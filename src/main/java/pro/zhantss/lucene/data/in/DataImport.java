package pro.zhantss.lucene.data.in;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.data.Resources;
import pro.zhantss.lucene.data.dbimport.AnalyzerType;
import pro.zhantss.lucene.data.dbimport.DataConfig;
import pro.zhantss.lucene.data.dbimport.DataSourceConfig;
import pro.zhantss.lucene.data.dbimport.DirectoryConfig;
import pro.zhantss.lucene.data.dbimport.EntityConfig;
import pro.zhantss.lucene.data.dbimport.FieldConfig;

public abstract class DataImport {

	private Log4JLogger logger = Resources.LOGGER;

	private DataSource ds;

	private DataConfig dc;

	private List<Directory> directories;

	private List<EntityIndex> entityIndexs;

	private ExecutorService service;

	private final Version version;

	public DataImport(Version version, DataConfig dc) {
		this.service = Executors.newCachedThreadPool();
		this.version = version;
		this.dc = dc;
		this.directories = new ArrayList<Directory>();
		this.entityIndexs = new ArrayList<DataImport.EntityIndex>();
	}

	public DataSource getDs() {
		return ds;
	}

	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	public Boolean overCheck() {
		if (entityIndexs.size() <= 0) {
			return true;
		}

		for (EntityIndex ei : entityIndexs) {
			if (!ei.over()) {
				return false;
			}
		}
		return true;
	}

	public void startIndex() throws DataImportException {
		if (!overCheck()) {
			return;
		}
		List<EntityConfig> ecs = dc.getEntities();
		for (EntityConfig ec : ecs) {
			try {
				entityIndex(ec);
			} catch (IOException e) {
				logger.debug(e);
			}
		}

		OverListener listener = new OverListener();
		service.execute(listener);
	}

	private void entityIndex(EntityConfig config) throws IOException {
		EntityIndex ei = null;
		try {
			ei = new EntityIndex(config);
			this.directories.add(ei.getDirectory());
			entityIndexs.add(ei);
			service.execute(ei);
		} catch (IOException e) {
			logger.debug("Error: write fail");
			logger.debug(ei);
			logger.debug(e);
			e.printStackTrace();
		}
	}

	private Connection getConnection()
			throws SQLException, ClassNotFoundException {
		if (ds != null) {
			return ds.getConnection();
		}

		if (dc != null) {
			DataSourceConfig dcs = dc.getDsc();
			Class.forName(dcs.getDriver());
			return DriverManager.getConnection(dcs.getUrl(), dcs.getUser(),
					dcs.getPassword());
		}

		return null;
	}

	protected abstract Directory getDirectory(DirectoryConfig dirc)
			throws IOException;

	class OverListener implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (overCheck()) {
					break;
				}
			}

			service.shutdown();
			System.out.println("DataImport End");
		}

	}

	class EntityIndex implements Runnable {

		private EntityConfig config;

		private IndexWriter writer;

		private Directory directory;

		private Boolean over;

		public EntityIndex(EntityConfig ec) throws IOException {
			this.config = ec;
			this.over = false;
			IndexWriterConfig iwc = null;
			AnalyzerType type = dc.getAnalyzerType();
			switch (type) {
				case COMPLEX :
					iwc = new IndexWriterConfig(version, Resources.complex);
					break;
				case MAXWORD :
					iwc = new IndexWriterConfig(version, Resources.maxWord);
					break;
				case SIMPLE :
					iwc = new IndexWriterConfig(version, Resources.simple);
					break;
				default :
					iwc = new IndexWriterConfig(version, Resources.complex);
					break;
			}
			if (config.getDirectory().getClean()) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			this.directory = DataImport.this
					.getDirectory(config.getDirectory());
			this.writer = new IndexWriter(this.directory, iwc);
		}

		public Boolean over() {
			return this.over;
		}

		public Directory getDirectory() {
			return directory;
		}

		@Override
		public void run() {
			Connection connection = null;
			Statement statement = null;
			ResultSet rs = null;

			try {
				connection = getConnection();
				statement = connection.createStatement(
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				if (statement.execute(this.config.getQuery())) {
					rs = statement.getResultSet();
					Integer submit = 0;
					Document document = null;
					while (rs.next()) {
						List<FieldConfig> fcs = this.config.getFields();
						document = new Document();
						for (FieldConfig fc : fcs) {
							String fcName = fc.getName();
							String fcCol = fc.getCloumn();
							String value = rs.getString(fcCol) == null
									? ""
									: rs.getString(fcCol).trim();
							Boolean segmented = fc.getSegmented();
							Boolean stored = fc.getStored();
							if (segmented) {
								if (stored) {
									document.add(new TextField(fcName, value,
											Store.YES));
								} else {
									document.add(new TextField(fcName, value,
											Store.NO));
								}
							} else {
								if (stored) {
									document.add(new StringField(fcName, value,
											Store.YES));
								} else {
									document.add(new StringField(fcName, value,
											Store.NO));
								}
							}
						}
						this.writer.addDocument(document);
						submit++;

						if (Resources.debug) {
							if (submit % 100000 == 0) {
								System.out.println(submit);
							}
						}
					}
					this.writer.commit();
					this.writer.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			over = true;
		}

	}

}

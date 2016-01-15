package pro.zhantss.lucene.data.merge;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.data.Resources;
import pro.zhantss.lucene.data.dbimport.AnalyzerType;
import pro.zhantss.lucene.data.dbimport.DirectoryConfig;
import pro.zhantss.lucene.data.dbimport.MergeConfig;

public abstract class IndexMerge {

	private Log4JLogger logger = Resources.LOGGER;

	private Version version;

	private MergeConfig mergeConfig;
	
	private MergeIndex mi;

	private Directory target;

	public IndexMerge(Version version, MergeConfig mc) throws IOException {
		this.mergeConfig = mc;
		this.version = version;
	}

	public abstract Directory getDirectory(DirectoryConfig dc) throws IOException;

	public Boolean overCheck() {
		if (this.mi == null) {
			return true;
		}
		
		return this.mi.over();
	}

	public void startMerge() {
		if (!overCheck()) {
			return;
		}
		try {
			IndexWriterConfig iwc = null;
			AnalyzerType type = mergeConfig.getAnalyzerType();
			switch (type) {
				case COMPLEX :
					iwc = new IndexWriterConfig(this.version, Resources.complex);
					break;
				case MAXWORD :
					iwc = new IndexWriterConfig(this.version, Resources.maxWord);
					break;
				case SIMPLE :
					iwc = new IndexWriterConfig(this.version, Resources.simple);
					break;
				default :
					iwc = new IndexWriterConfig(this.version, Resources.complex);
					break;
			}
			this.target = getDirectory(mergeConfig.getTarget());
			IndexWriter writer = new IndexWriter(this.target, iwc);
			this.mi = new MergeIndex(writer);
			new Thread(this.mi).start();
		} catch (IOException e) {
			logger.debug("Error: merge fail");
			logger.debug(this.mergeConfig);
			logger.debug(e);
			e.printStackTrace();
		}
		
	}

	class MergeIndex implements Runnable {
		
		private IndexWriter writer;
		
		private Boolean over;
		
		public MergeIndex(IndexWriter writer) {
			this.writer = writer;
			this.over = false;
		}
		
		public Boolean over() {
			return this.over;
		}

		@Override
		public void run() {
			List<DirectoryConfig> dcs = mergeConfig.getDcs();
			Integer dirsNum = dcs.size();
			if (dirsNum <= 0) {
				return;
			}

			Directory[] mds = new Directory[dirsNum];
			for (Integer i = 0; i < dirsNum; i++) {
				try {
					mds[i] = IndexMerge.this.getDirectory(dcs.get(i));
				} catch (IOException e) {
					logger.debug(e);
					logger.debug(dcs.get(i));
					e.printStackTrace();
				}
			}

			try {
				writer.addIndexes(mds);
				writer.commit();
				writer.close();
			} catch (IOException e) {
				logger.debug(e);
				e.printStackTrace();
			}
			
			this.over = true;
		}
	}

}

package pro.zhantss.lucene.data.in;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.store.database.DatabaseDirectory;
import pro.zhantss.lucene.store.database.config.DatabaseConfig;
import pro.zhantss.lucene.store.database.datasource.TransactionAwareDataSourceProxy;

import pro.zhantss.lucene.data.dbimport.DataConfig;
import pro.zhantss.lucene.data.dbimport.DirectoryConfig;

public class DataImportToDatabase extends DataImport {
	
	private DatabaseConfig databaseConfig;
	
	private TransactionAwareDataSourceProxy proxy;

	public DataImportToDatabase(Version version, DataConfig dc, DataSource ds, DataSource control, DatabaseConfig dbc) {
		super(version, dc);
		super.setDs(ds);
		this.databaseConfig = dbc;
		this.proxy = new TransactionAwareDataSourceProxy(control);
	}

	@Override
	protected Directory getDirectory(DirectoryConfig dirc) throws IOException {
		return new DatabaseDirectory(this.proxy, this.databaseConfig, dirc.getName());
	}

}

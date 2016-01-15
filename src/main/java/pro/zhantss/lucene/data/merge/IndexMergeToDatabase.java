package pro.zhantss.lucene.data.merge;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.store.database.DatabaseDirectory;
import pro.zhantss.lucene.store.database.config.DatabaseConfig;
import pro.zhantss.lucene.store.database.datasource.TransactionAwareDataSourceProxy;

import pro.zhantss.lucene.data.dbimport.DirectoryConfig;
import pro.zhantss.lucene.data.dbimport.MergeConfig;

public class IndexMergeToDatabase extends IndexMerge {
	
	private DatabaseConfig databaseConfig;
	
	private TransactionAwareDataSourceProxy proxy;

	public IndexMergeToDatabase(Version version, MergeConfig mc, DataSource ds, DataSource control, DatabaseConfig dbc)
			throws IOException {
		super(version, mc);
		this.databaseConfig = dbc;
		this.proxy = new TransactionAwareDataSourceProxy(control);
	}

	@Override
	public Directory getDirectory(DirectoryConfig dc) throws IOException {
		return new DatabaseDirectory(this.proxy, this.databaseConfig, dc.getName());
	}

}

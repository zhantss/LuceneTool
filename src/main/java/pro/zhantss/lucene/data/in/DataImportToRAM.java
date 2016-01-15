package pro.zhantss.lucene.data.in;

import javax.sql.DataSource;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.data.dbimport.DataConfig;
import pro.zhantss.lucene.data.dbimport.DirectoryConfig;

public class DataImportToRAM extends DataImport {
	
	private Directory directory;
	
	public DataImportToRAM(Version version, DataConfig dc, DataSource ds) {
		super(version, dc);
		super.setDs(ds);
		this.directory = new RAMDirectory();
	}

	@Override
	protected Directory getDirectory(DirectoryConfig dirc) {
		return this.directory;
	}
	
}

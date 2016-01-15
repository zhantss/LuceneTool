package pro.zhantss.lucene.data.in;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.data.dbimport.DataConfig;
import pro.zhantss.lucene.data.dbimport.DirectoryConfig;

public class DataImportToFileSystem extends DataImport {
	
	private File dir;
	
	public DataImportToFileSystem(Version version, DataConfig dc, File dir) {
		super(version, dc);
		this.dir = dir;
	}

	public DataImportToFileSystem(Version version, DataConfig dc, DataSource ds, File dir) {
		super(version, dc);
		super.setDs(ds);
		this.dir = dir;
	}

	@Override
	protected Directory getDirectory(DirectoryConfig dirc) throws IOException {
		return FSDirectory.open(new File(dir, dirc.getName()));
	}

}

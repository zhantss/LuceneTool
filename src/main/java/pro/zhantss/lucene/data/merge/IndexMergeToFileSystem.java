package pro.zhantss.lucene.data.merge;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.data.dbimport.DirectoryConfig;
import pro.zhantss.lucene.data.dbimport.MergeConfig;

public class IndexMergeToFileSystem extends IndexMerge {
	
	private File dir;

	public IndexMergeToFileSystem(Version version, MergeConfig mc, File dir)
			throws IOException {
		super(version, mc);
		this.dir = dir;
	}

	@Override
	public Directory getDirectory(DirectoryConfig dc) throws IOException {
		return FSDirectory.open(new File(dir, dc.getName()));
	}

}

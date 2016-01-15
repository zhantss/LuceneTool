package pro.zhantss.lucene.data.merge;

import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pro.zhantss.lucene.data.dbimport.DirectoryConfig;
import pro.zhantss.lucene.data.dbimport.MergeConfig;

public class IndexMergeToRAM extends IndexMerge {
	
	private Directory directory;

	public IndexMergeToRAM(Version version, MergeConfig mc) throws IOException {
		super(version, mc);
		directory = new RAMDirectory();
	}

	@Override
	public Directory getDirectory(DirectoryConfig dc) throws IOException {
		return this.directory;
	}

}

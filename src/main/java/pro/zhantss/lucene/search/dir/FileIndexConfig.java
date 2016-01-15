package pro.zhantss.lucene.search.dir;

import java.io.File;

public class FileIndexConfig extends IndexConfig {
	
	private File dirpath;
	
	public FileIndexConfig(File dirpath) {
		super();
		setLocation(DataLocation.FILESYSTEM);
		this.dirpath = dirpath;
	}
	
	public File getDirpath() {
		return dirpath;
	}
	
	public void setDirpath(File dirpath) {
		this.dirpath = dirpath;
	}
	
	@Override
	public String serialCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(getLocation().toString());
		if (dirpath != null) {
			builder.append(dirpath.getAbsolutePath());
		}
		return builder.toString();
	}

}

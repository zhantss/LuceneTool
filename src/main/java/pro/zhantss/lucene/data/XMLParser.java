package pro.zhantss.lucene.data;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pro.zhantss.lucene.data.dbimport.AnalyzerType;
import pro.zhantss.lucene.data.dbimport.DataConfig;
import pro.zhantss.lucene.data.dbimport.DataSourceConfig;
import pro.zhantss.lucene.data.dbimport.DirectoryConfig;
import pro.zhantss.lucene.data.dbimport.DirectoryType;
import pro.zhantss.lucene.data.dbimport.EntityConfig;
import pro.zhantss.lucene.data.dbimport.FieldConfig;
import pro.zhantss.lucene.data.in.DataImportException;

public class XMLParser {
	
	private static DataConfig documentParse(Document document) throws DataImportException {
		if (document == null) {
			return null;
		}
		Element root = document.getRootElement();
		
		String start = root.getName().trim();
		if (!start.equals("dataConfig")) {
			throw new DataImportException("dataConfig node is not found");
		}
		
		Element ds = root.element("dataSource");
		if (ds == null) {
			throw new DataImportException("dataSource node is not found");
		}
		Element doc = root.element("document");
		if (doc == null) {
			throw new DataImportException("document node is not found");
		}
		
		String driver = ds.attributeValue("driver");
		if (driver == null) {
			throw new DataImportException("dataSource node define error, The 'driver' attribute is must");
		}
		
		String url = ds.attributeValue("url");
		if (url == null) {
			throw new DataImportException("dataSource node define error, The 'url' attribute is must");
		}
		
		String user = ds.attributeValue("user");
		if (user == null) {
			throw new DataImportException("dataSource node define error, The 'user' attribute is must");
		}
		
		String password = ds.attributeValue("password");
		if (password == null) {
			throw new DataImportException("dataSource node define error, The 'password' attribute is must");
		}
		
		DataConfig dataConfig = new DataConfig();
		
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDriver(driver.trim());
		dsc.setUrl(url.trim());
		dsc.setUser(user.trim());
		dsc.setPassword(password.trim());
		dataConfig.setDsc(dsc);
		
		try {
			dataConfig.setAnalyzerType(Enum.valueOf(AnalyzerType.class, root.attributeValue("analyzer")));
		} catch (Exception e1) {
			dataConfig.setAnalyzerType(AnalyzerType.COMPLEX);
		}
		
		@SuppressWarnings("unchecked")
		List<Element> entities = doc.elements("entity");
		if (entities.size() <= 0) {
			throw new DataImportException("The import data define error, entity nodes is not found");
		}
		
		List<EntityConfig> ecs = new ArrayList<EntityConfig>();
		for (Element e : entities) {
			List<FieldConfig> fcs = new ArrayList<FieldConfig>();
			
			@SuppressWarnings("unchecked")
			List<Element> fields = e.elements("field");
			for (Element f : fields) {
				FieldConfig fc = new FieldConfig();
				fc.setCloumn(f.attributeValue("column"));
				fc.setIndexed(Boolean.parseBoolean(f.attributeValue("indexed")));
				fc.setSegmented(Boolean.parseBoolean(f.attributeValue("segmented")));
				fc.setStored(Boolean.parseBoolean(f.attributeValue("stored")));
				fc.setName(f.attributeValue("name"));
				fcs.add(fc);
			}
			
			Element dir = e.element("directory");
			DirectoryConfig dirc = new DirectoryConfig();
			if (dir == null) {
				throw new DataImportException("directory node is not found");
			}
			dirc.setName(dir.attributeValue("name"));
			try {
				dirc.setType(Enum.valueOf(DirectoryType.class, dir.attributeValue("type")));
			} catch (Exception e1) {
				dirc.setType(DirectoryType.FILESYSTEM);
			}
			dirc.setClean("true".equals(dir.attributeValue("clean")));
			
			EntityConfig entity = new EntityConfig();
			entity.setName(e.attributeValue("name"));
			entity.setQuery(e.attributeValue("query"));
			entity.setDirectory(dirc);
			entity.setFields(fcs);
			ecs.add(entity);
		}
		dataConfig.setEntities(ecs);
		return dataConfig;
	}
	
	public static DataConfig parse(Reader r) throws DataImportException {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(r);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new DataImportException(e.getMessage());
		}
	}
	
	public static DataConfig parse(InputStream is) throws DataImportException {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(is);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new DataImportException(e.getMessage());
		}
	}
	
	public static DataConfig parse(File path) throws DataImportException {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(path);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new DataImportException(e.getMessage());
		}
	}
	
	public static DataConfig parse(String xml) throws DataImportException {
		StringReader sr = new StringReader(xml);
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(sr);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new DataImportException(e.getMessage());
		}
	}
	
}

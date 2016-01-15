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
import pro.zhantss.lucene.data.dbimport.DirectoryConfig;
import pro.zhantss.lucene.data.dbimport.DirectoryType;
import pro.zhantss.lucene.data.dbimport.MergeConfig;
import pro.zhantss.lucene.data.merge.MergeException;

public class MergeParser {
	
	private static MergeConfig documentParse(Document document) throws MergeException {
		if (document == null) {
			return null;
		}
		Element root = document.getRootElement();
		
		String start = root.getName().trim();
		if (!start.equals("merge")) {
			throw new MergeException("merge node is not found");
		}
		
		Element target = root.element("target");
		if (target == null) {
			throw new MergeException("target node is not found");
		}
		DirectoryConfig tdc = new DirectoryConfig();
		tdc.setName(target.attributeValue("name"));
		tdc.setType(Enum.valueOf(DirectoryType.class, target.attributeValue("type")));
		
		@SuppressWarnings("unchecked")
		List<Element> dirs = root.elements("directory");
		List<DirectoryConfig> dcs = new ArrayList<DirectoryConfig>();
		for (Element dir : dirs) {
			DirectoryConfig dc = new DirectoryConfig();
			dc.setName(dir.attributeValue("name"));
			dc.setType(Enum.valueOf(DirectoryType.class, dir.attributeValue("type")));
			dcs.add(dc);
		}
		
		MergeConfig mc = new MergeConfig();
		mc.setAnalyzerType(Enum.valueOf(AnalyzerType.class, root.attributeValue("analyzer")));
		mc.setTarget(tdc);
		mc.setDcs(dcs);
		return mc;
	}
	
	public static MergeConfig parse(Reader r) throws MergeException {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(r);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new MergeException(e.getMessage());
		}
	}
	
	public static MergeConfig parse(InputStream is) throws MergeException {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(is);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new MergeException(e.getMessage());
		}
	}
	
	public static MergeConfig parse(File path) throws MergeException {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(path);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new MergeException(e.getMessage());
		}
	}
	
	public static MergeConfig parse(String xml) throws MergeException {
		StringReader sr = new StringReader(xml);
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(sr);
			return documentParse(document);
		} catch (DocumentException e) {
			throw new MergeException(e.getMessage());
		}
	}

}

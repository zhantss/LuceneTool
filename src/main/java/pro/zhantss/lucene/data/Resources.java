package pro.zhantss.lucene.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;

public class Resources {
	
	public static String dicPath = "";
	
	public static Analyzer complex = new ComplexAnalyzer();
	
	public static Analyzer maxWord = new MaxWordAnalyzer();
	
	public static Analyzer simple = new SimpleAnalyzer();
	
	public final static Integer stop = 5;
	
	public final static Log4JLogger LOGGER = new Log4JLogger("luceneTool");
	
	public final static Version DEFAULT_VERSION = Version.LUCENE_47;
	
	public final static String FNAME = "FNAME";
	
	public static Boolean debug = false;
	
	public final static Map<String, String> typeMap = new HashMap<String, String>();
	
	public static void refreshAnalyzer(String dicPath) {
		Resources.dicPath = dicPath;
		Resources.complex = new ComplexAnalyzer(Resources.dicPath);
		Resources.maxWord = new MaxWordAnalyzer(Resources.dicPath);
		Resources.simple = new SimpleAnalyzer(Resources.dicPath);
	}
	
	static {
		typeMap.put("String", "java.lang.String");
		typeMap.put("Integer", "java.lang.Integer");
		typeMap.put("Float", "java.lang.Float");
		typeMap.put("Double", "java.lang.Double");
		typeMap.put("Enum", "java.lang.Enum");
		typeMap.put("Date", "java.util.Date");
		typeMap.put("Blob", "[B");
		typeMap.put("Clob", "java.sql.String");
	}

}

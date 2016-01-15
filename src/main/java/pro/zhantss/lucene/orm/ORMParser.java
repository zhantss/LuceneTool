package pro.zhantss.lucene.orm;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.impl.Log4JLogger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import pro.zhantss.lucene.data.Resources;
import pro.zhantss.lucene.orm.convert.Converter;
import pro.zhantss.lucene.orm.convert.SuperConverter;

public class ORMParser {

	private static Log4JLogger logger = Resources.LOGGER;

	public static HashMap<String, Target> ORM = new HashMap<String, Target>();
	
	@SuppressWarnings("unchecked")
	public static void append(Document document) throws ORMException {
		Element root = document.getRootElement();
		if (root == null) {
			throw new ORMException("root node is not found");
		}
		String rootName = root.getName();
		if (!rootName.equals("ormConfig")) {
			throw new ORMException("ormConfig node is not found");
		}

		Element entities = root.element("entities");
		if (entities == null) {
			throw new ORMException("entities node is not found");
		}

		List<Element> alltarget = entities.elements("entity");
		if (alltarget == null || alltarget.size() <= 0) {
			logger.debug("no entity need convert");
			return;
		}

		for (Element tar : alltarget) {
			String className = tar.attributeValue("type");
			String entityName = tar.attributeValue("name");
			if (className == null || "".equals(className) || entityName == null
					|| "".equals(entityName)) {
				logger.warn("entity define error, entityName: " + entityName);
				continue;
			}
			try {
				Class.forName(className);
				Target target = new Target(entityName, className);

				List<Element> fields = tar.elements("field");
				if (fields == null || fields.size() <= 0) {
					continue;
				}

				for (Element v : fields) {
					String vName = v.attributeValue("name");
					String indexName = v.attributeValue("index");
					String type = v.attributeValue("type");
					String convert = v.attributeValue("convert");

					if (vName == null || "".equals(vName) || indexName == null
							|| "".equals(indexName) || type == null
							|| "".equals("type")) {
						logger.warn("field define error, entityName: "
								+ entityName);
						continue;
					}

					String typeClassName = null;
					if (Resources.typeMap.containsKey(type)) {
						typeClassName = Resources.typeMap.get(type);
					} else {
						typeClassName = type;
					}

					Value value = new Value();
					value.setName(vName);
					value.setIndexName(indexName);
					value.setClassName(typeClassName);
					if (convert == null || "".equals(convert)) {
						value.setConverter(new SuperConverter());
					} else {
						try {
							Class<?> converterClass = Class.forName(convert);
							Object directConverter = converterClass
									.newInstance();
							if (directConverter instanceof Converter) {
								value.setConverter((Converter) directConverter);
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							logger.warn("Converter Class " + className
									+ " is not found", e);
						} catch (InstantiationException e) {
							e.printStackTrace();
							logger.warn("Converter Class " + className
									+ " can't be instantiation", e);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							logger.warn(
									"Converter Class " + className
											+ " construction function is private",
									e);
						}
					}
					target.putInfo(value);
				}

				ORM.put(className, target);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				logger.warn("Entity Class " + className + " is not found", e);
				continue;
			}
		}
	}
	
	/*public static <T> T convert(Class<T> target, JSONObject object) {
		Class<T> c = target;
		T res = null;
		if (ORM.containsKey(c.getName())) {
			try {
				res = c.newInstance();
				Target ti = ORM.get(c.getName());
				Method[] methods = c.getMethods();
				for (Method method : methods) {
					String methodName = method.getName();
					if (methodName.startsWith("set")) {
						String name = methodName.replace("set", "")
								.toLowerCase();
						method.setAccessible(true);
						Field field = c.getDeclaredField(name);
						if (field == null) {
							continue;
						}
						Value info = ti.getInfoByName(name);
						
						if ("entity".equals(info.getClassName())) {
							Target ct = ORM.get(info.getIndexName());
							String entityClassName = ct.getClassName();
							Object value = convert(Class.forName(entityClassName), object);
							if (value != null) {
								method.invoke(res, value);
							}
						} else {
							Object v = object.get(info.getIndexName());
							Converter converter = info.getConverter();
							if (converter != null) {
								Object value = info.getConverter().convert(v, field);
								method.invoke(res, value);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.debug(e);
				e.printStackTrace();
			}
		}
		return (T) res;
	}*/
	
	public static void clear() {
		ORM.clear();
	}

	public static void init(Reader r) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(r);
			init(document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (ORMException e) {
			e.printStackTrace();
		}
	}

	public static void init(InputStream is) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(is);
			init(document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (ORMException e) {
			e.printStackTrace();
		}
	}

	public static void init(File path) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(path);
			init(document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (ORMException e) {
			e.printStackTrace();
		}
	}

	public static void init(String xml) {
		StringReader sr = new StringReader(xml);
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(sr);
			init(document);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (ORMException e) {
			e.printStackTrace();
		}
	}
	
	public static void init(Document document) throws ORMException {
		clear();
		append(document);
	}

}

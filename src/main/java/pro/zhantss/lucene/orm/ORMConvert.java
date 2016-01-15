package pro.zhantss.lucene.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.impl.Log4JLogger;

import com.alibaba.fastjson.JSONObject;

import pro.zhantss.lucene.data.Resources;
import pro.zhantss.lucene.orm.convert.Converter;

public class ORMConvert<T> {
	
	private Log4JLogger logger = Resources.LOGGER;
	
	@SuppressWarnings({"unchecked"})
	public T convert(Class<?> target, JSONObject object) {
		if (target == null || object == null) {
			return null;
		}
		
		Class<T> c = null;
		try {
			c = (Class<T>) target;
		} catch (Exception err) {
			err.printStackTrace();
			return null;
		}
		T res = null;
		if (ORMParser.ORM.containsKey(c.getName())) {
			try {
				res = c.newInstance();
				Target ti = ORMParser.ORM.get(c.getName());
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
						if (info == null) {
							continue;
						}
						if ("entity".equals(info.getClassName())) {
							Target ct = ORMParser.ORM.get(info.getIndexName());
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
	}

}

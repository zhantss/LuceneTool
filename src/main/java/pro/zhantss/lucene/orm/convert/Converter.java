package pro.zhantss.lucene.orm.convert;

import java.lang.reflect.Field;

public interface Converter {
	
	public <T> T convert(Object value, Field field);

}

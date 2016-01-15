package pro.zhantss.lucene.orm.convert;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;

public class SuperConverter implements Converter {

	@SuppressWarnings({"unchecked"})
	public <T> T convert(Object value, Field field) {
		if (value == null) {
			return null;
		}
		
		Class<?> t = field.getType();
		if (List.class.isAssignableFrom(t)) {
			return (T) listConvert(value, field);
		}
		return (T) convert(value, t);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private <T> T convert(Object value, Class<T> t) {
		if (value == null) {
			return null;
		}
		
		if (t.equals(String.class)) {
			return (T) value;
		}
		
		if (t.equals(Integer.class) || t.equals(int.class)) {
			try {
				Integer res = Integer.parseInt(value.toString());
				return (T) res;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if (t.equals(Float.class) || t.equals(float.class)) {
			try {
				Float res = Float.parseFloat(value.toString());
				return (T) res;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if (t.equals(Double.class) || t.equals(double.class)) {
			try {
				Double res = Double.parseDouble(value.toString());
				return (T) res;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		if (t.equals(Date.class)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			try {
				Date res = sdf.parse(value.toString());
				return (T) res;
			} catch (ParseException e) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date res = sdf2.parse(value.toString());
					return (T) res;
				} catch (ParseException e1) {
					return null;
				}
			}
		}
		
		if (t.equals(byte[].class)) {
			return (T) value.toString().getBytes();
		}
		
		if (Enum.class.isAssignableFrom(t)) {
			try {
				Enum res = Enum.valueOf((Class<? extends Enum>) t, value.toString());
				return (T) res;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			return JSON.parseObject(value.toString(), t, Feature.DisableCircularReferenceDetect);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private List listConvert(Object value, Field field) {
		Class<?> t = field.getType();
		
		if (List.class.isAssignableFrom(t)) {
			if (!(value instanceof JSONArray)) {
				try {
					value = JSONArray.parse(value.toString());
				} catch (Exception e) {
					return null;
				}
			}
			Type gt = field.getGenericType();
			ArrayList<Object> list = new ArrayList<Object>();
			JSONArray ja = (JSONArray) value;
			Iterator<Object> it = ja.iterator();
			while(it.hasNext()) {
				Object jo = it.next();
					if (gt instanceof ParameterizedType) {
						ParameterizedType pt = (ParameterizedType) gt;
						Class ptc = (Class) pt.getActualTypeArguments()[0];
						Object item = convert(jo, ptc);
						if (item != null) {
							list.add(item);
						}
					}
			}
			return list;
		}
		
		return null;
	}
	
}

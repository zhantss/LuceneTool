package test.orm.convert;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pro.zhantss.lucene.orm.convert.Converter;

public class IDCardToBirthConvert implements Converter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object value, Field field) {
		if (value == null) {
			return null;
		}
		
		String idcard = value.toString();
		String dateStr = idcard.substring(6, 14);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = sdf.parse(dateStr);
			return (T) date;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}

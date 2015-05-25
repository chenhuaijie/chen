package com.utils;

import java.util.Arrays;
import java.util.List;

import com.license.number.enums.enumi.IEnum;
import com.license.number.enums.enumi.ValueIEnum;

/**
 * 枚举类的工具类
 * 我是作者 啦啦啦啦啦拉阿拉蕾
 */
public class EnumUtil{
	
	//========================================getNameByValue----String=======================================
	public static String getNameByValue(Class<? extends IEnum<String>> enumClass, String value){
		IEnum<String> em = (IEnum<String>)getEnumByValue(enumClass, value);
		if (em == null) return null;
		return em.getEnName();
	}
	@SuppressWarnings("unchecked")
	public static <T extends ValueIEnum<String>> T getEnumByValue(Class<T> enumClass, String value){
	    List<T> list = getEnums(enumClass);
	    if(list.size()==0||value==""){return null;}
	    for (ValueIEnum<String> em : list) {
	      if (((String)em.getEnValue()).equals(value)) {
	        return (T)em;
	      }    
	    }
	    return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> List<T> getEnums(Class<T> enumClass){
	    if (enumClass == null) return null;
	    Object[] enumArr = enumClass.getEnumConstants();
	    if(enumArr.length==0){
	    	return null;
	    }
	    return (List<T>) Arrays.asList(enumArr);
	}
	//========================================getNameByValue----String=====================================

	//=======================================getNameByValue-----Number=====================================
	public static String getNameByValue(Class<? extends IEnum<? extends Number>> enumClass, Number value){
		IEnum em = (IEnum)getEnumByValue(enumClass, value);
		if(em == null) return null;
		return em.getEnName();
	}
	public static <T extends ValueIEnum<? extends Number>> T getEnumByValue(Class<T> enumClass, Number value){
	    List<T> list = getEnums(enumClass);
//	    if ((CollectionUtil.isEmpty(list)) || (value == null)) return null;
	    if(list.size()==0||value==null){
	    	return null;
	    }
	    for (ValueIEnum em : list) {
//	      if (NumberUtil.isEQ((Number)em.getEnValue(), value)) {
//	        return (T)em;
//	      }
    	if((Number)em.getEnValue()==value){
    		return (T)em;
    	}
	    }
	    return null;
	}
}

package com.license.number.enums.enumc;

import com.license.number.enums.enumi.IEnum;

public enum ISEmpireEnum implements IEnum<String>{
	N("未过期","N"),
	Y("已过期","Y"),;


	/**
	 * 属性名
	 */
	private String enName;
	/**属性值*/
	private String enValue;
	private ISEmpireEnum(String enName,String enValue){
		this.enName = enName;
		this.enValue = enValue;
	}
	@Override
	public String getEnName() {
		return enName;
	}

	@Override
	public String getEnValue() {
		return enValue;
	}

}

package com.license.number.enums.enumc;

import com.license.number.enums.enumi.IEnum;

public enum CarStatusEnum implements IEnum<String>{
	N("未租用","N"),
	Y("已租用","Y"),;

	/**
	 * 属性名
	 */
	private String enName;
	/**属性值*/
	private String enValue;
	
	private CarStatusEnum(String enName,String enValue){
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

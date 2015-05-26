package com.license.number.entity;
import java.io.Serializable;
import java.util.List;

/**
 * AppEJB实体类
 * @author Administrator
 *
 */
public class AppEJB implements Serializable{
	private static final long serialVersionUID = -360620372587821399L;
	private boolean exists;
	private List<AppPOJO> cars;
	
	public boolean isExists() {
		return exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}
	public List<AppPOJO> getCars() {
		return cars;
	}
	public void setCars(List<AppPOJO> cars) {
		this.cars = cars;
	}
	
}
package com.license.number.entity;
import java.io.Serializable;
import java.util.Date;

public class AppPOJO implements Serializable{
	private static final long serialVersionUID = 6536966593199759428L;
	private boolean exists;
	private Car car;
	private SBook book;
	private User user;
	
	private Date createDate ;
	
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public SBook getBook() {
		return book;
	}
	public void setBook(SBook book) {
		this.book = book;
	}
	
	public boolean isExists() {
		return exists;
	}
	public void setExists(boolean exists) {
		this.exists = exists;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
package com.license.number.entity;
import java.io.Serializable;

public class AppPOJO implements Serializable{
	private static final long serialVersionUID = 6536966593199759428L;
	private Car car;
	private SBook book;
	private User user;
	
	
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
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
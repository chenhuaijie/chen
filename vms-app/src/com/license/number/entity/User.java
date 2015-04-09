package com.license.number.entity;
import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 937801390089260836L;
	//用户ID
	private String userid;
	//用户名称
	private String name;
	//用户昵称
	private String nickname;
	//邮箱
	private String email;
	//手机号码
	private String phonenumber;
	//角色:super:超级管理员  admin:管理员 user:维护人
	private String role;
	//部门
	private String dept;
	//密码
	private String password;
	//公司
	private String company;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}

	
}
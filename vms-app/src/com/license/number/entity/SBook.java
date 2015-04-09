package com.license.number.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.license.number.enums.enumc.ISEmpireEnum;
import com.utils.EnumUtil;

/**
 * 台账表
 * @author Administrator
 *
 */
public class SBook implements Serializable{
	private static final long serialVersionUID = 6089946439235125417L;
	//流水号，主键
	private String idsbook;
	//经办人id
	private String userid;
	//汽车id
	private String carid;
	//乘租方
	private String lessee;
	//出租放方\n租出车辆的公司
	private String lease;
	//起租时间
	private Date startdate;
	//到期时间
	private Date enddate;
	private String startDateString;
	private String endDateString;
	//使用部门
	private String usedept;
	//是否过期
	private String isexpire;
	private String isexpireString;
	public String getIdsbook() {
		return idsbook;
	}
	public void setIdsbook(String idsbook) {
		this.idsbook = idsbook;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getCarid() {
		return carid;
	}
	public void setCarid(String carid) {
		this.carid = carid;
	}
	public String getLessee() {
		return lessee;
	}
	public void setLessee(String lessee) {
		this.lessee = lessee;
	}
	public String getLease() {
		return lease;
	}
	public void setLease(String lease) {
		this.lease = lease;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Date getEnddate() {
		return enddate;
	}
	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
	public String getUsedept() {
		return usedept;
	}
	public void setUsedept(String usedept) {
		this.usedept = usedept;
	}
	public String getIsexpire() {
		return isexpire;
	}
	public void setIsexpire(String isexpire) {
		this.isexpire = isexpire;
	}
	public String getStartDateString() {
		SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyyMMdd");
		return sdfDateFormat.format(this.getStartdate());
	}
	public void setStartDateString(String startDateString) throws Exception{
		this.startDateString = startDateString;
		SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyyMMdd");
		this.setStartdate(sdfDateFormat.parse(startDateString));
	}
	public String getEndDateString() {
		SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyyMMdd");
		return sdfDateFormat.format(this.getEnddate());
	}
	public void setEndDateString(String endDateString) throws Exception {
		this.endDateString = endDateString;
		SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyyMMdd");
		this.setEnddate(sdfDateFormat.parse(endDateString));
	}
	public String getIsexpireString() {
		return EnumUtil.getNameByValue(ISEmpireEnum.class, isexpire);
	}
	public void setIsexpireString(String isexpireString) {
		this.isexpireString = isexpireString;
	}
	
}

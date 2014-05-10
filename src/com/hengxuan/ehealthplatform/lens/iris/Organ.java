package com.hengxuan.ehealthplatform.lens.iris;

import java.io.Serializable;

public class Organ implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4627619647812458837L;
	//该器官的id
	private int organId;
	//该器官的名称
	private String name;
	//该器官所属的环（共8个环）
	private int ringId;
	//环的内径大小
	private float inRaduis;
	//环的外径大小
	private float outRaduis;
	//环的起始角度的大小
	private float startAngle;
	//扇环的夹角大小
	private float angle;
	//扇环的结束角度的大小
	private float endAngle;
	public int getOrganId() {
		return organId;
	}
	public void setOrganId(int organId) {
		this.organId = organId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRingId() {
		return ringId;
	}
	public void setRingId(int ringId) {
		this.ringId = ringId;
	}
	public float getInRaduis() {
		return inRaduis;
	}
	public void setInRaduis(float inRaduis) {
		this.inRaduis = inRaduis;
	}
	public float getOutRaduis() {
		return outRaduis;
	}
	public void setOutRaduis(float outRaduis) {
		this.outRaduis = outRaduis;
	}
	public float getStartAngle() {
		return startAngle;
	}
	public void setStartAngle(float startAngle) {
		this.startAngle = startAngle;
	}
	public float getAngle() {
		return angle;
	}
	public void setAngle(float angle) {
		this.angle = angle;
		this.setEndAngle(this.getStartAngle() + angle);
	}
	public float getEndAngle() {
		return endAngle;
	}
	public void setEndAngle(float endAngle) {
		this.endAngle = endAngle;
	}
	@Override
	public String toString() {
		return "Organ [organId=" + organId + ", name=" + name + ", ringId="
				+ ringId + ", inRaduis=" + inRaduis + ", outRaduis="
				+ outRaduis + ", startAngle=" + startAngle + ", angle=" + angle
				+ ", endAngle=" + endAngle + "]";
	}
	
	
	

}

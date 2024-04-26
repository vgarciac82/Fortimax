package com.syc.fortimax.hibernate;

public class Order {
	String property;
	String direction;
	
	public Order() {
	}
	public Order(String property, String direction) {
		this.property = property;
		this.direction = direction;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
}

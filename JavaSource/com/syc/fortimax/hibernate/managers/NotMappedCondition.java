package com.syc.fortimax.hibernate.managers;

import org.hibernate.type.Type;

public class NotMappedCondition {

	private String field;
	private String condition;
	private Object value;
	private Type type;
	
	public NotMappedCondition(String field, String condition, Object value, Type type) {
		this.field = field;
		this.condition = condition;
		this.value = value;
		this.type = type;
	}
	public NotMappedCondition(String field, String condition, Object value) {
		this(field, condition, value, null);
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
}

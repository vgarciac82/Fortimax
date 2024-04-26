package com.syc.fortimax.hibernate.entities;

import java.io.Serializable;

public class imx_config implements Serializable {

	private static final long serialVersionUID = -755741259060970178L;
	private String category;
	private String description;
	private int ID;
	private String name;
	private String value;

	public imx_config() {
		super();
	}

	public imx_config(int iD, String name, String value, String category,
			String description) {
		super();
		ID = iD;
		this.name = name;
		this.value = value;
		this.category = category;
		this.description = description;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "imx_config [ID=" + ID + ", name=" + name + ", value=" + value
				+ ", category=" + category + ", description=" + description
				+ "]";
	}

}

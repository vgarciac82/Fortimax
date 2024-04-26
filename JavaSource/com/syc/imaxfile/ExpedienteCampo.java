package com.syc.imaxfile;

/**
 * @author bubuntux
 * 
 */
public class ExpedienteCampo {

	private int idTipoDato = -1;
	private boolean primaryKey = false;
	private String columnName;
	private String value;

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIdTipoDato() {
		return idTipoDato;
	}

	public void setIdTipoDato(int idTipoDato) {
		this.idTipoDato = idTipoDato;
	}

}

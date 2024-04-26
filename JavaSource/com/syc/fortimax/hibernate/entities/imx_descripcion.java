package com.syc.fortimax.hibernate.entities;

public class imx_descripcion implements java.io.Serializable  {
	private static final long serialVersionUID = 6091343155725676631L;
	private imx_descripcion_id id;
	private String nombreColumna;
	private int posicionCampo;
	private String nombreTipoDatos;
	private int idTipoDatos;
	private int longitudCampo;
	private String valorDefCampo;
	private String mascaraCampo;
	private String nombreIndice;
	private int indiceTipo;
	private char multivaluado;
	private char requerido;
	private char editable;
	private char lista;
	public imx_descripcion(){
		
	}
	public imx_descripcion(imx_descripcion_id id,String nombreColumna,int posicionCampo,String nombreTipoDatos,int idTipoDatos,int longitudCampo,
			String valorDefCampo, String mascaraCampo, String nombreIndice,int indiceTipo, char multivaluado, char requerido,char editable,char lista){
		this.id =id;
		this.nombreColumna=nombreColumna;
		this.posicionCampo=posicionCampo;
		this.nombreTipoDatos=nombreTipoDatos;
		this.idTipoDatos=idTipoDatos;
		this.longitudCampo=longitudCampo;
		this.valorDefCampo=valorDefCampo;
		this.mascaraCampo=mascaraCampo;
		this.nombreIndice=nombreIndice;
		this.indiceTipo=indiceTipo;
		this.multivaluado=multivaluado;
		this.requerido=requerido;
		this.editable=editable;
		this.lista=lista;
	}
	public imx_descripcion_id getId() {
		return id;
	}
	public void setId(imx_descripcion_id id) {
		this.id = id;
	}
	public String getNombreColumna() {
		return nombreColumna;
	}
	public void setNombreColumna(String nombreColumna) {
		this.nombreColumna = nombreColumna;
	}
	public int getPosicionCampo() {
		return posicionCampo;
	}
	public void setPosicionCampo(int poscionCampo) {
		this.posicionCampo = poscionCampo;
	}
	public String getNombreTipoDatos() {
		return nombreTipoDatos;
	}
	public void setNombreTipoDatos(String nombreTipoDatos) {
		this.nombreTipoDatos = nombreTipoDatos;
	}
	public int getIdTipoDatos() {
		return idTipoDatos;
	}
	public void setIdTipoDatos(int idTipoDatos) {
		this.idTipoDatos = idTipoDatos;
	}
	public int getLongitudCampo() {
		return longitudCampo;
	}
	public void setLongitudCampo(int longitudCampo) {
		this.longitudCampo = longitudCampo;
	}
	public String getValorDefCampo() {
		return valorDefCampo;
	}
	public void setValorDefCampo(String valorDefCampo) {
		this.valorDefCampo = valorDefCampo;
	}
	public String getMascaraCampo() {
		return mascaraCampo;
	}
	public void setMascaraCampo(String mascaraCampo) {
		this.mascaraCampo = mascaraCampo;
	}
	public String getNombreIndice() {
		return nombreIndice;
	}
	public void setNombreIndice(String nombreIndice) {
		this.nombreIndice = nombreIndice;
	}
	public int getIndiceTipo() {
		return indiceTipo;
	}
	public void setIndiceTipo(int indiceTipo) {
		this.indiceTipo = indiceTipo;
	}
	public char getMultivaluado() {
		return multivaluado;
	}
	public void setMultivaluado(char multivaluado) {
		this.multivaluado = multivaluado;
	}
	public char getRequerido() {
		return requerido;
	}
	public void setRequerido(char requerido) {
		this.requerido = requerido;
	}
	public char getEditable() {
		return editable;
	}
	public void setEditable(char editable) {
		this.editable = editable;
	}
	public char getLista() {
		return lista;
	}
	public void setLista(char lista) {
		this.lista = lista;
	}
}

package com.syc.imaxfile;

import java.io.Serializable;
import java.util.ArrayList;

public class Descripcion implements Serializable {

	private static final long serialVersionUID = -3985680808178943774L;
	private String titulo_aplicacion = null;
	private String nombre_campo = null;
	private String nombre_columna = null;
	private int posicion_campo = -1;
	private String nombre_tipo_datos = null;
	private int id_tipo_datos = -1;
	private int longitud_campo = -1;
	private String valor_def_campo = null;
	private String mascara_campo = null;
	private String nombre_indice = null;
	private int indice_tipo = -1;
	private String multivaluado = "N";
	private String requerido = "N";
	private String editable = "N";
	private String lista = "N";
	private ArrayList<String> valoresLista = new ArrayList<String>();

	public Descripcion(String titulo_aplicacion, String nombre_campo) {
		this.titulo_aplicacion = titulo_aplicacion;
		this.nombre_campo = nombre_campo;
	}

	public Descripcion(String titulo_aplicacion, String nombre_campo,
			String nombre_columna, int posicion_campo,
			String nombre_tipo_datos, int id_tipo_datos, int longitud_campo,
			String valor_def_campo, String mascara_campo, String nombre_indice,
			int indice_tipo, String multivaluado, String requerido,
			String editable, String lista) {
		this(titulo_aplicacion, nombre_campo);
		this.nombre_columna = nombre_columna;
		this.posicion_campo = posicion_campo;
		this.nombre_tipo_datos = nombre_tipo_datos;
		this.id_tipo_datos = id_tipo_datos;
		this.longitud_campo = longitud_campo;
		this.valor_def_campo = valor_def_campo;
		this.mascara_campo = mascara_campo;
		this.nombre_indice = nombre_indice;
		this.indice_tipo = indice_tipo;
		this.multivaluado = multivaluado;
		this.requerido = requerido;
		this.editable = editable;
		this.lista = lista;
	}

	public String getTituloAplicacion() {
		return titulo_aplicacion;
	}

	public void setTituloAplicacion(String titulo_aplicacion) {
		this.titulo_aplicacion = titulo_aplicacion;
	}

	public String getNombreCampo() {
		return nombre_campo;
	}

	public String getNombreCampoLower() {
		return nombre_campo.toLowerCase();
	}

	public void setNombreCampo(String nombre_campo) {
		this.nombre_campo = nombre_campo;
	}

	public String getNombreColumna() {
		return nombre_columna;
	}

	public void setNombreColumna(String nombre_columna) {
		this.nombre_columna = nombre_columna;
	}

	public int getPosicionCampo() {
		return posicion_campo;
	}

	public void setPosicionCampo(int posicion_campo) {
		this.posicion_campo = posicion_campo;
	}

	public String getNombreTipoDatos() {
		return nombre_tipo_datos;
	}

	public void setNombreTipoDatos(String nombre_tipo_datos) {
		this.nombre_tipo_datos = nombre_tipo_datos;
	}

	public int getIdTipoDatos() {
		return id_tipo_datos;
	}

	public void setIdTipoDatos(int id_tipo_datos) {
		this.id_tipo_datos = id_tipo_datos;
	}

	public int getLongitudCampo() {
		return longitud_campo;
	}

	public void setLongitudCampo(int longitud_campo) {
		this.longitud_campo = longitud_campo;
	}

	public String getValorDefCampo() {
		return valor_def_campo;
	}

	public void setValorDefCampo(String valor_def_campo) {
		this.valor_def_campo = valor_def_campo;
	}

	public String getMascaraCampo() {
		return mascara_campo;
	}

	public void setMascaraCampo(String mascara_campo) {
		this.mascara_campo = mascara_campo;
	}

	public String getNombreIndice() {
		return nombre_indice;
	}

	public void setNombreIndice(String nombre_indice) {
		this.nombre_indice = nombre_indice;
	}

	public int getIndiceTipo() {
		return indice_tipo;
	}

	public void setIndiceTipo(int indice_tipo) {
		this.indice_tipo = indice_tipo;
	}

	public String getMultivaluado() {
		return multivaluado;
	}

	public void setMultivaluado(String multivaluado) {
		this.multivaluado = multivaluado;
	}

	public String getRequerido() {
		return requerido;
	}

	public boolean getRequeridoBoolean() {
		return "S".equals(requerido);
	}

	public void setRequerido(String requerido) {
		this.requerido = requerido;
	}

	public String getEditable() {
		return editable;
	}

	public void setEditable(String editable) {
		this.editable = editable;
	}

	public String getLista() {
		return lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}

	public void setValoresLista(ArrayList<String> valores) {
		this.valoresLista.addAll(valores);
	}

	public ArrayList<String> getValoresLista() {
		return this.valoresLista;
	}
}

package com.syc.fortimax.hibernate.entities;

public class imx_unidad_volumen implements java.io.Serializable {
			private static final long serialVersionUID = -7714194179796667764L;
			public imx_unidad_volumen_id Id;
			public int EstadoUnidad;
			public char TipoDispositivo;
			
public imx_unidad_volumen(){
		super();
}
public imx_unidad_volumen(imx_unidad_volumen_id Id, int EstadoUnidad, char TipoDispositivo){
		this.Id=Id;
		this.EstadoUnidad=EstadoUnidad;
		this.TipoDispositivo=TipoDispositivo;
}
	
public imx_unidad_volumen_id getId(){
	return this.Id;
}
public void setId(imx_unidad_volumen_id Id){
	this.Id=Id;
}
public int getEstadoUnidad(){
	return this.EstadoUnidad;
}
public void setEstadoUnidad(int EstadoUnidad){
	this.EstadoUnidad=EstadoUnidad;
}
public char getTipoDispositivo(){
	return this.TipoDispositivo;
}
public void setTipoDispositivo(char TipoDispositivo){
	this.TipoDispositivo=TipoDispositivo;
}
}

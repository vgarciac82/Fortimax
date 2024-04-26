package com.syc.servlets.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.syc.gavetas.Gaveta;
import com.syc.gavetas.GavetaCampo;

public class GavetaCampoModel
{     
        String nombre = "";
        String etiqueta = "";
        int longitud = 10;
        String valor = "";
        String tipo = "Texto";
        String indice = "Ninguno";
        Boolean requerido = false;
        Boolean editable = true;
        int orden = 0;
        String lista = "-Ninguna-";
        List<GavetaCampoModel> hijos = new ArrayList<GavetaCampoModel>(); 
       
        public GavetaCampoModel() {
		}
        
        public GavetaCampoModel(String nombre, String etiqueta) {
			this.nombre = nombre;
			this.etiqueta = etiqueta;
		}
		public String getNombre() {
                return nombre;
        }
        public void setNombre(String nombre) {
                this.nombre = nombre;
        }
        public String getEtiqueta() {
                return etiqueta;
        }
        public void setEtiqueta(String etiqueta) {
                this.etiqueta = etiqueta;
        }
        public int getTamano() {
                return longitud;
        }
        public GavetaCampoModel setTamano(int tamano) {
                longitud = tamano;
                return this;
        }
        public String getValor() {
                return valor;
        }
        public GavetaCampoModel setValor(String valor) {
                this.valor = valor;
                return this;
        }
        public String getTipo() {
                return tipo;
        }
        public GavetaCampoModel setTipo(String tipo) {
                this.tipo = tipo;
                return this;
        }
        public String getIndice() {
                return indice;
        }
        public void setIndice(String indice) {
                this.indice = indice;
        }
        public Boolean getRequerido() {
                return requerido;
        }
        public void setRequerido(Boolean requerido) {
                this.requerido = requerido;
        }
        public Boolean getEditable() {
                return editable;
        }
        public void setEditable(Boolean editable) {
                this.editable = editable;
        }
        public String getLista() {
                return lista;
        }
        public GavetaCampoModel setLista(String lista) {
                this.lista = lista;
                return this;
        }
        public int getOrden() {
            return orden ;
        }
        public void setOrden(int orden) {
        	this.orden  = orden;
        }
        
       	public static GavetaCampoModel[] getGavetaCamposModel(ArrayList<GavetaCampo> gavetaCampos) {
    		GavetaCampoModel[] gavetaCamposModel = new GavetaCampoModel[gavetaCampos.size()];
    		
    		for(int i=0;i<gavetaCamposModel.length;i++){
    			GavetaCampo gavetaCampo = gavetaCampos.get(i);
    			gavetaCamposModel[i] = new GavetaCampoModel();
    			
    			gavetaCamposModel[i].setOrden(gavetaCampo.getPosicionCampo());
    			gavetaCamposModel[i].setNombre(gavetaCampo.getNombreCampo());
    			gavetaCamposModel[i].setEtiqueta(gavetaCampo.getNombreDesplegar());
    			gavetaCamposModel[i].setValor(gavetaCampo.getValPredefinido());
    			gavetaCamposModel[i].setTamano((int)gavetaCampo.getTamano());
    			gavetaCamposModel[i].setTipo((String)getTipos().get(gavetaCampo.getTipoDato()));
    			gavetaCamposModel[i].setIndice((String)getIndices().get(gavetaCampo.getIndice()));
    			gavetaCamposModel[i].setRequerido(gavetaCampo.getRequerido().equals("S"));
    			gavetaCamposModel[i].setEditable(gavetaCampo.getEditable().equals("S"));
    			if(gavetaCampo.getLista().equals("S"))
    				gavetaCamposModel[i].setLista(gavetaCampo.getNombreCatalogo());
    			else
    				gavetaCamposModel[i].setLista(CatalogoModel.getBlankCatalogo());
    		}	
    		return gavetaCamposModel;	
    	}
    
    	public static GavetaCampoModel[] getGavetaCamposModel(Gaveta gaveta) {
    		return getGavetaCamposModel(gaveta.getCampos());
    	}

    	public static Gaveta getGaveta(String nombre, String descripcion, GavetaCampoModel[] gavetaCamposModel) {
    		Gaveta gaveta = new Gaveta(nombre, descripcion);
    		GavetaCampo[] campos = getCampos(gavetaCamposModel);
    		for(int i=0;i<campos.length;i++){
    			gaveta.addCampo(campos[i]);
    		}
    		return gaveta;
    	}
    	
    	public static String[] getNombresCampos(Gaveta gaveta) {
    		ArrayList<GavetaCampo> campos = gaveta.getCampos();
    		String[] nombresCampos = new String[campos.size()];
    		for(int i=0;i<nombresCampos.length;i++) {
    			nombresCampos[i]=campos.get(i).getNombreCampo();
    		}
    		return nombresCampos;
    	}

    	public static GavetaCampo[] getCampos(GavetaCampoModel[] gavetaCamposModel) {
    		GavetaCampo[] campos = new GavetaCampo[gavetaCamposModel.length];		
    		Object tipo = null;
    		Object indice = null;
    		for(int i=0;i<gavetaCamposModel.length;i++){
    			tipo = getTipos().getKey(gavetaCamposModel[i].getTipo());
    			indice = getIndices().getKey(gavetaCamposModel[i].getIndice());
    			GavetaCampo campo = new GavetaCampo(
    					gavetaCamposModel[i].getNombre(),
    					gavetaCamposModel[i].getEtiqueta(),
    					gavetaCamposModel[i].getValor(),
    					gavetaCamposModel[i].getTamano()+"",
    					tipo!=null?tipo+"":null,
    					indice!=null?indice+"":null,
    					gavetaCamposModel[i].getRequerido()?"S":"N",
    					gavetaCamposModel[i].getEditable()?"S":"N",
    					gavetaCamposModel[i].getLista().equals(CatalogoModel.getBlankCatalogo())?"N":"S",
    					gavetaCamposModel[i].getLista().equals(CatalogoModel.getBlankCatalogo())?"":gavetaCamposModel[i].getLista()		
    			);
    			campos[i] = campo;
    		}
    		return campos;
    	}
    	
    	public static BidiMap getIndices() {
    		BidiMap indices = new DualHashBidiMap();
    		indices.put(0, "Ninguno");
    		indices.put(2, "Unico");
    		indices.put(1, "No-Unico");
    		return indices;
    	}
    	
    	public static BidiMap getTipos() {
    		BidiMap tipos = new DualHashBidiMap();
    		tipos.put(3,"Entero (Smallint)");
    		tipos.put(4,"Entero Largo (Int)");
    		tipos.put(5,"Decimal");
    		tipos.put(7,"Doble");
    		tipos.put(8,"Fecha");
    		tipos.put(10,"Texto (VarChar)");
    		//TODO:Corregir bugs al crear tipos de datos memo y check
    		//tipos.put(12,"Memo");
    		//tipos.put(13, "Check");
    		return tipos;
    	}

		public List<GavetaCampoModel> getHijos() {
			return hijos;
		}

		public void setHijos(List<GavetaCampoModel> hijos) {
			this.hijos = hijos;
		}
}

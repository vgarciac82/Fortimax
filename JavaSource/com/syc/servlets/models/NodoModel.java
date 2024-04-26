package com.syc.servlets.models;

import java.util.ArrayList;
import java.util.List;

import com.jenkov.prizetags.tree.itf.ITreeNode;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_carpeta;
import com.syc.fortimax.hibernate.entities.imx_documento;
import com.syc.imaxfile.Carpeta;
import com.syc.imaxfile.Documento;
import com.syc.imaxfile.GetDatosNodo;

public class NodoModel {
	public String id = null;
	public String text = null;
	public String type = null;
	public Integer tipo_documento = null;
	public Integer cantidad_paginas = null;
	public boolean compartido = false;
	public boolean protegido = false;
	public String iconCls = null;
	public String qtip = null; 
	public boolean leaf = false;
	public boolean expanded = false;
	public List<AtributosModel> atributos = null;
	public List<NodoModel> children = null;
	
	public NodoModel() {
	}
	
	public NodoModel(NodoModel nodoModel) {
		this.id = nodoModel.id;
		this.text = nodoModel.text;
		this.type = nodoModel.type;
		this.tipo_documento = nodoModel.tipo_documento;
		this.cantidad_paginas = nodoModel.cantidad_paginas;
		this.compartido = nodoModel.compartido;
		this.protegido = nodoModel.protegido;
		this.iconCls = nodoModel.iconCls;
		this.qtip = nodoModel.qtip; 
		this.leaf = nodoModel.leaf;
		this.expanded = nodoModel.expanded;
		this.children = nodoModel.children;
	}
	
	public NodoModel(ITreeNode node) {
		NodoModel nodoModel = getNodoModel(node);
		this.id = nodoModel.id; //Nodo Fortimax
		this.text = nodoModel.text; //Nombre Documento
		this.type = nodoModel.type; //tipo_nodo: Tipos propios nuestros: gaveta.root, gaveta.hija, folder, document
		this.tipo_documento = nodoModel.tipo_documento; //tipo_documento: imx en imaxfile, y en externos nunca regresara nada.
		this.cantidad_paginas = nodoModel.cantidad_paginas;
		this.compartido = nodoModel.compartido; //compartido: false default, true si es un documento compartido.
		this.protegido = nodoModel.protegido; //protegido: false default, true si es carpeta compartida.
		this.iconCls = nodoModel.iconCls; //
		this.qtip = nodoModel.qtip; //
		this.leaf = nodoModel.leaf; //
		this.expanded = nodoModel.expanded; //
		this.children = nodoModel.children; //
	}
	
	public NodoModel(List<imx_aplicacion> gavetas) {
		this.id = "";
		this.text = "Gavetas";
		this.type = "gaveta.root";
		this.iconCls = "nodo_gaveta.root";
		this.qtip = "Gavetas"; 
		this.leaf = false;
		this.expanded = true;
		List<NodoModel> children = new ArrayList<NodoModel>();
		for(imx_aplicacion imx_aplicacion : gavetas) 
			children.add(getNodoModel(imx_aplicacion));
		this.children = children;
	}
	
	public NodoModel(Documento documento) {
		this(getNodoModel(documento));
	}

	public NodoModel(Carpeta carpeta) {
		this(getNodoModel(carpeta));
	}

	private static NodoModel getNodoModel(imx_aplicacion imx_aplicacion) {
		NodoModel nodoModel = new NodoModel();
		nodoModel.id = imx_aplicacion.getTituloAplicacion();
		nodoModel.text = imx_aplicacion.getDescripcion();
		nodoModel.type = "gaveta.hija";
		nodoModel.iconCls = "nodo_gaveta";
		nodoModel.qtip = imx_aplicacion.getDescripcion();
		nodoModel.leaf = true;
		return nodoModel;
	}
	
	private static NodoModel getNodoModel(Documento documento) {
		NodoModel nodo = new NodoModel();
		nodo.id = documento.getNodo();
		nodo.text = documento.getNombreDocumento();
		nodo.type = "document";
		nodo.qtip = documento.getDescripcionPaginas();
		nodo.leaf = true;
		String flags = "";
		
		if(documento.getTipoDocumento()==Documento.SIN_TIPO)
			nodo.iconCls="nodo_empty";
		else if(documento.getTipoDocumento()==Documento.IMAX_FILE)
			nodo.iconCls="nodo_imx";
		else
			nodo.iconCls="nodo_ext"; 
		
		nodo.tipo_documento = documento.getTipoDocumento();
		nodo.cantidad_paginas = documento.getNumeroPaginas();
		nodo.compartido = "S".equals(documento.getCompartir());
		
		if (nodo.compartido) {
			flags+="s";
		}
		if(!flags.isEmpty())
			flags += "_";
		nodo.iconCls = flags+nodo.iconCls;
		return nodo;
	}
	
	private static NodoModel getNodoModel(Carpeta carpeta) {
		NodoModel nodo = new NodoModel();
		nodo.id = carpeta.getNodo();
		nodo.text = carpeta.getNombreCarpeta();
		nodo.type = "folder";
		nodo.iconCls = "nodo_dir";
		nodo.qtip = carpeta.getContenidoCarpeta();
		nodo.protegido = carpeta.isProtected();
		
		String flags = "";
		if(carpeta.isProtected())
			flags+="p";
		if (nodo.compartido) {
			flags+="s";
		}
		if(!flags.isEmpty())
			flags += "_";
		nodo.iconCls = flags+nodo.iconCls;
		return nodo;
	}
	
	private static NodoModel getNodoModel(ITreeNode node) {
		if(node.getObject() instanceof Documento){
			return getNodoModel((Documento)node.getObject());
		} else if(node.getObject() instanceof Carpeta){
			NodoModel nodoModel = getNodoModel((Carpeta)node.getObject());
			//if(!nodoModel.protegido)
				nodoModel.children = getChildren(node);
			return nodoModel;
		}
		return null;
	}
	
	private static List<NodoModel> getChildren(ITreeNode iTreeNode){
		List<NodoModel> nodos = new ArrayList<NodoModel>();
		for(ITreeNode node : (List<ITreeNode>)iTreeNode.getChildren()) {
			nodos.add(getNodoModel(node));
		}
		if(nodos.isEmpty())
			return null;
		else
			return nodos;
	}
	
	
	//El siguiente código viene de Fortimax_MVC y es redundante con lo anterior pero para IfimaxDocumentos se requiere lo que proporciona,
	//en realidad lo que hay que eliminar es lo anterior
	//TODO: Eliminar la redundancia
	
	public NodoModel(imx_documento imx_documento) {
		this(getNodoModel(imx_documento));
	}

	public NodoModel(imx_carpeta imx_carpeta) {
		this(getNodoModel(imx_carpeta));
	}
	
	private static NodoModel getNodoModel(imx_documento imx_documento) {
		NodoModel nodo = new NodoModel();
		nodo.id = new GetDatosNodo(imx_documento.getId().getTituloAplicacion(),imx_documento.getId().getIdGabinete(), imx_documento.getId().getIdCarpetaPadre(), imx_documento.getId().getIdDocumento()).toString();
		nodo.text = imx_documento.getNombreDocumento();
		nodo.type = "document";
		nodo.qtip = imx_documento.getNumeroPaginas() + " páginas";
		nodo.leaf = true;
		String flags = "";
		
		if(imx_documento.getIdTipoDocumento()==-1)
			nodo.iconCls="nodo_empty";
		else if(imx_documento.getIdTipoDocumento()==2)
			nodo.iconCls="nodo_imx";
		else
			nodo.iconCls="nodo_ext"; 
		
		nodo.tipo_documento = imx_documento.getIdTipoDocumento();
		nodo.cantidad_paginas = imx_documento.getNumeroPaginas();
		nodo.compartido = "S".equals(imx_documento.getCompartir());
		
		if (nodo.compartido) {
			flags+="s";
		}
		if(!flags.isEmpty())
			flags += "_";
		nodo.iconCls = flags+nodo.iconCls;
		return nodo;
	}
	
	private static NodoModel getNodoModel(imx_carpeta imx_carpeta) {
		NodoModel nodo = new NodoModel();
		nodo.id = new GetDatosNodo(imx_carpeta.getId().getTituloAplicacion(),imx_carpeta.getId().getIdGabinete(), imx_carpeta.getId().getIdCarpeta()).toString();
		nodo.text = imx_carpeta.getNombreCarpeta();
		nodo.type = "folder";
		nodo.iconCls = "nodo_dir";
		nodo.qtip = imx_carpeta.getNumeroCarpetas()+" carpetas, "+imx_carpeta.getNumeroDocumentos()+" documentos";
		nodo.protegido = imx_carpeta.getPassword() != null && !"-1".equals(imx_carpeta.getPassword());
		
		String flags = "";
		if(nodo.protegido)
			flags+="p";
		if (nodo.compartido) {
			flags+="s";
		}
		if(!flags.isEmpty())
			flags += "_";
		nodo.iconCls = flags+nodo.iconCls;
		return nodo;
	}
}

package com.syc.utils;
import com.jenkov.prizetags.tree.itf.ICollapseListener;
import com.jenkov.prizetags.tree.itf.IExpandListener;
import com.jenkov.prizetags.tree.itf.ISelectListener;
import com.jenkov.prizetags.tree.itf.IUnSelectListener;
import com.syc.tree.CollapseListener;
import com.syc.tree.ExpandListener;
import com.syc.tree.SelectListener;
import com.syc.tree.UnselectListener;

public interface ParametersInterface {

//Variables para el GeneradorWeb.
	public static final int TIPO_SELECT = 1;
	public static final int TIPO_TABLA = 2;

	public static final String NAME_KEY = "image.name";
	public static final String HEIGHT_KEY = "image.height";
	public static final String WIDTH_KEY = "image.width";
	public static final String ROTATE_KEY = "image.rotate";
	public static final String INDEX_KEY = "image.index";
	public static final String THUMBNAIL_KEY = "image.thumbnail";
	public static final String DELETE_KEY = "image.delete";
	public static final String LOAD_KEY = "image.load";

	public static final String TREE_KEY = "arbol.modelo";
	public static final String TREE_MDC_KEY = "arbol.mydocs";
	public static final String TREE_APP_KEY = "arbol.locker";
	public static final String TREE_EXP_KEY = "arbol.lkfile";
	public static final String TREE_TYPE_KEY = "arbol.tipo";

	public static final String USER_KEY = "usr.objeto";

	public static ICollapseListener MyDocCollapseListener = new CollapseListener();
	public static IExpandListener MyDocExpandListener = new ExpandListener();
	public static ISelectListener MyDocSelectListener = new SelectListener();
	public static IUnSelectListener MyDocUnselectListener = new UnselectListener();

	public static ICollapseListener GavCollapseListener = new CollapseListener();
	public static IExpandListener GavExpandListener = new ExpandListener();
	public static ISelectListener GavSelectListener = new SelectListener();
	public static IUnSelectListener GavUnselectListener = new UnselectListener();

	public static ICollapseListener ExpCollapseListener = new CollapseListener();
	public static IExpandListener ExpExpandListener = new ExpandListener();
	public static ISelectListener ExpSelectListener = new SelectListener();
	public static IUnSelectListener ExpUnselectListener = new UnselectListener();
	
	public static final String CARPETA_CORREO = "Correos recibidos";
	
	public static final int DBMS_SQLSERVER = 0;
	//public static final int DBMS_WATCOM = 2;
	public static final int DBMS_ORACLE = 1;
	public static final int DBMS_INFORMIX = 2;
	public static final int DBMS_SYBASE = 3;
	public static final int DBMS_SQLANYWHERE = 4;
	//public static final int DBMS_ACCESS = 7;
	public static final int DBMS_ANTS = 5;
	public static final int DBMS_DB2 = 6;
	public static final int DBMS_ISERIES = 7;
	public static final int DBMS_MYSQL = 8;
	
	//esto es para los idiomas
	public static final String USER_LOCALE = "user.locale";
	
	public static final String vAPLICACION = "vAplicacion";

	public static final String vLISTADOCUMENTOS = "vListaDocumentos"; 
}

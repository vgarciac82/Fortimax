package com.syc.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.log4j.Logger;

import com.syc.fortimax.config.Config;
import com.syc.fortimax.config.HibernateUtils;
import com.syc.fortimax.hibernate.entities.imx_aplicacion;
import com.syc.fortimax.hibernate.entities.imx_catalogo_tipo_documento;
import com.syc.fortimax.hibernate.managers.imx_aplicacion_manager;
import com.syc.fortimax.hibernate.managers.imx_catalogo_tipo_documento_manager;

public class Utils {
	
	private static final Logger log = Logger.getLogger(Utils.class);
	
	public static java.sql.Date getDate(Object object) {
		if(object == null)
			return null;
		else if(object instanceof java.sql.Timestamp)
			return new java.sql.Date(((java.sql.Timestamp)object).getTime());
		return (java.sql.Date)object;
	}
	
	public static String getString(Object object) {
		if(object == null)
			return null;
		else if(object instanceof byte[])
			return new String((byte[])object);
		return object.toString();
	}
	
	public static Boolean getBoolean(Object object) {
		if(object == null)
			return null;
		else if(object instanceof byte[])
			return !getString(object).equals("0");
		try {
			return getInteger(object)!=0;
		} catch (Exception e) {
		}
		return (Boolean)object;
	}
	
	//El siguiente metodo existe por transición de GGP que tiene un versión mixta de fortimax, eliminar cuando se les actualize
	public static Integer getInt(Object object) {
		return getInteger(object);
	}
	
	public static Integer getInteger(Object object) {
		if(object == null)
			return null;
		else if(object instanceof BigDecimal)
			return ((BigDecimal)object).intValue();
		else if (object instanceof BigInteger)
			return ((BigInteger)object).intValue();
		else if (object instanceof Double)
			return ((Double)object).intValue();
		else if (object instanceof Long)
			return ((Long)object).intValue();
		else if (object instanceof Short)
			return ((Short)object).intValue();
		return (Integer)object; 
	}
	
	public static List<Integer> getListInteger(List<?> list) {
		List<Integer> listInteger = new ArrayList<Integer>();
		for(Object object : list) {
			listInteger.add(getInteger(object));
		}
		return listInteger; 
	}
	
	@SuppressWarnings("unchecked")
	public static <Clase> List<Clase> castList(Class<Clase> clase, List<?> list) {
		List<Clase> listClase = new ArrayList<Clase>();
		for(Object object : list) {
			listClase.add((Clase)object);
		}
		return listClase; 
	}
	
	public static Long getLong(Object object) {
		if(object == null)
			return null;
		else if(object instanceof BigDecimal)
			return ((BigDecimal)object).longValue();
		else if (object instanceof BigInteger)
			return ((BigInteger)object).longValue();
		else if (object instanceof Integer)
			return ((Integer)object).longValue(); 
		else if (object instanceof Double)
			return ((Double)object).longValue();
		else if (object instanceof Short)
			return ((Short)object).longValue();
		return (Long)object;
	}
	
	public static Double getDouble(Object object) {
		if(object == null)
			return null;
		else if(object instanceof BigDecimal)
			return ((BigDecimal)object).doubleValue();
		else if (object instanceof BigInteger)
			return ((BigInteger)object).doubleValue();
		else if (object instanceof Integer)
			return ((Integer)object).doubleValue(); 
		else if (object instanceof Long)
			return ((Long)object).doubleValue();
		else if (object instanceof Short)
			return ((Short)object).doubleValue();
		return (Double)object;
	}
	
	public static void write(InputStream inputStream, OutputStream outputStream) throws IOException {
        // buffer size
        byte[] b = new byte[4096];
        int count;
        while ((count = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, count);
        }
	}
	
	public static String readFile(File file, String charset) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    /* Instead of using default, pass in a decoder. */
		    return Charset.forName(charset).decode(bb).toString();
		 } finally {
			 IOUtils.closeQuietly(stream);
		 }
	}
	
	public static String getContentType(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		try {
			BufferedInputStream bis = new BufferedInputStream(fis);
			try {
				return URLConnection.guessContentTypeFromStream(bis);
			} finally {
				IOUtils.closeQuietly(bis);
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
	
    public static String encodeNonAsciiCharacters(String string) {
        StringBuilder sb = new StringBuilder();
        for( char c : string.toCharArray() ) {
            if(c>127) {
            	 String encodedValue = "\\u" + Integer.toHexString(c | 0x10000).substring(1);
                 sb.append( encodedValue );
            } else {
                sb.append( c );
            }
        }
        return sb.toString();
    }
    
	public static HashSet<Integer> rangeToHashSet(String range) throws NumberFormatException{
		HashSet<Integer> pages = new HashSet<Integer>();
		String[] csv = range.split("\\s*,\\s*");
		for(String cadenaIntervalo : csv) {
			String[] intervalo = cadenaIntervalo.split("\\s*-\\s*");
			if(intervalo.length==1) {
				try{
					pages.add(Integer.parseInt(intervalo[0]));
				} catch (NumberFormatException e) {
					throw new NumberFormatException("\""+intervalo[0]+"\" no es un número de página válido");
				}
			} else if(intervalo.length==2) {
				int inicio = -1;
				int fin = -1;
				try {
					inicio = Integer.parseInt(intervalo[0]);
				} catch (NumberFormatException e) {
					throw new NumberFormatException("\""+intervalo[0]+"\" en \""+cadenaIntervalo+"\" no es un número de página válido");
				}
				try {
					fin = Integer.parseInt(intervalo[1]);
				} catch (NumberFormatException e) {
					throw new NumberFormatException("\""+intervalo[1]+"\" en \""+cadenaIntervalo+"\" no es un número de página válido");
				}
				IntRange intRange = new IntRange(inicio , fin);
				int[] paginas = intRange.toArray();
				for (int pagina : paginas) {
					pages.add(pagina);
				}
			} else if(intervalo.length>2) {
				throw new NumberFormatException("\""+cadenaIntervalo+"\" no es un intervalo de páginas válido");
			}
		}
		if(pages.isEmpty()) {
			throw new NumberFormatException("Su selección de páginas esta vacía");
		}
		return pages;
	}
	
	public static Date getDateExtJS(String stringDate) throws ParseException {
		String regex = "([A-z]{3}) ([A-z]{3}) ([0-9]*?) (.*?)([0-9]{4})(.*?)";
		Matcher m=Pattern.compile(regex).matcher(stringDate);
		if (m.matches()) 
			stringDate=m.group(1)+" "+m.group(2)+" "+m.group(3)+" "+m.group(5);
		else
			log.error("La fecha "+stringDate+" no es parseable");
			
		SimpleDateFormat sdfExtJS = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
		return sdfExtJS.parse(stringDate);
		
	}
	
	public static String getDateString(Date date,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static String getDateExtJSString(String stringDate, String format) throws ParseException {
		return getDateString(getDateExtJS(stringDate),format);
	}

	public static String guessContentTypeFromName(String filename) {
		String extension = FilenameUtils.getExtension(filename);
		if(extension==null)
			return null;
		else
			return Config.mimeTypes.get("."+extension.toLowerCase());
	}
	
	public static boolean isImaxFileFromName(String filename) {
		String extension = FilenameUtils.getExtension(filename);
		if(extension==null)
			return false;
		else
			return Config.imaxExtensions.contains("."+extension.toLowerCase());
	}
	
	public static List<Map<String,String>> getColumnsFromClass(Class<?> clase) throws IllegalArgumentException, IllegalAccessException {
		List<Map<String,String>> columns = new ArrayList<Map<String,String>>(); 
		for (Field field : clase.getDeclaredFields()) {
	        //field.setAccessible(true); // if you want to modify private fields
			Map<String,String> column = new HashMap<String,String>();
			column.put("name", field.getName());
			column.put("type", field.getType().getName());
			column.put("label", field.getName());
			columns.add(column);
	    }
		return columns;
	}
	
	public static List<Map<String,String>> getColumnsFromEntity(Class<?> clase) throws IllegalArgumentException, IllegalAccessException {
		List<String> propertyNames = HibernateUtils.getPropertyNames(clase);
		List<Map<String,String>> columns = new ArrayList<Map<String,String>>();
		for(Map<String,String> column : getColumnsFromClass(clase)) {
			if(propertyNames.contains(column.get("name")))
				columns.add(column);
		}
		return columns;
	}
	
	public static List<Map<String,Object>> getFMXList(String name) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(name.equals("_fmx_gavetas")) {
			for(imx_aplicacion imx_aplicacion : imx_aplicacion_manager.get()) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", imx_aplicacion.getTituloAplicacion());
				map.put("nombre", imx_aplicacion.getTituloAplicacion()+" ("+imx_aplicacion.getDescripcion()+")");
				list.add(map);
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", null);
			map.put("nombre", "Todas las gavetas...");
			list.add(0,map);
		} else if(name.equals("_fmx_plantillas")) {
			ArrayList<imx_catalogo_tipo_documento> plantillas = new imx_catalogo_tipo_documento_manager().getTiposDocumento();
			for(imx_catalogo_tipo_documento plantilla : plantillas) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", plantilla.getId());
				map.put("nombre", plantilla.getNombre()+" ("+plantilla.getDescripcion()+")");
				list.add(map);
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id", null);
			map.put("nombre", "Todas las plantillas...");
			list.add(0,map);
		}
		return list;
	}

	public static Map<String,?> getMap(Map<String,?>[] maps) {
		HashMap<String, Object> returnMap = new HashMap<String,Object>();
		for(Map<String, ?> map : maps) {
			for(Entry<String, ?> entry : map.entrySet()) {
				returnMap.put(entry.getKey(), entry.getValue());
			}
		}
		return returnMap;
	}
}

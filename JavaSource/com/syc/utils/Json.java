package com.syc.utils;

import java.io.BufferedReader;
import java.io.IOException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Json {
	
	private static final Logger log = Logger.getLogger(Json.class);
	private HashMap<String, Object> hashmap = new HashMap<String, Object>();
	private Gson gson = new Gson();
	private String stringJson=null;
	
	/* TODO: convertir un Objeto a Json y posteriormente añadir nuevos datos
	 * con la funcion add(String header, Object data), como esto aún no funciona
	 * añadir nueva información eliminara el objeto previamente convertido.
	 * Realmente no debería ser ningún problema, ya que la necesidad de ambos
	 * procesos simultaneamente es trivial.
	 */
	
	public Json() {	
	}
	
	public Json(Object object) {
		objectToJson(object);
	}
	
	public void clear() {
		hashmap.clear();
		stringJson=null;
	}
	
	public void add(String header, Object data) {
		/* Cuidado con poner cosas no seguras en el hashmap como excepciones */
		if (stringJson!=null)
			stringJson=null;
		hashmap.put(header, data);
	}
	
	public String objectToJson(Object object){
		stringJson = gson.toJson(object); 
		return stringJson;
	}
	
	public String returnJson() {
			if (stringJson==null)
				return objectToJson(hashmap); //Utils.encodeNonAsciiCharacters(objectToJson(hashmap));
			else
				return stringJson; //Utils.encodeNonAsciiCharacters(stringJson);
	}
	
	
	public void returnJson(HttpServletResponse response) {	
		returnJson(response, "application/json");
	}
	
	public void returnJson(HttpServletResponse response, String contentType) {
		try {
			response.setContentType(contentType);
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().write(returnJson());
		} catch (IOException e) {
			log.error(e, e);
		}  
	}
	
	public static String getFromRequest(HttpServletRequest request, String parameter) {
		String json=null;
		try {
			BufferedReader reader = request.getReader();
			String line = reader.readLine();
		
			String data=null;
			while (line!=null&&data==null) {
				String[] parameters = line.split("&");
				for (int i=0;i<parameters.length&&data==null;i++) {
					if (parameters[i].startsWith(parameter+"=")) {
						data=parameters[i].substring(parameter.length()+1);
					}
				}
				line = reader.readLine();
			}
			reader.close();

			json = URLDecoder.decode(data, "UTF-8");
		} catch (IOException e) {
			log.error(e, e);
		}
		return json;
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T getObject(String json, Class<T> classOfT) {
		Gson gson = new Gson();
		Object object = gson.fromJson(json, (Type) classOfT);
		return (T)object;
	}
	
	public static HashMap<String,Object> getHashMap(String json) {
		return (HashMap<String,Object>)getMap(json);
	}
	
	public static Map<String,Object> getMap(String json) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Object.class, new NaturalDeserializer());
		Gson gson = gsonBuilder.create();
		Object natural = gson.fromJson(json, Object.class);
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String,Object>)natural;
		return map;
	}
	
    public static List<Map<String,Object>> getList(String json){
    	JsonParser jsonParser = new JsonParser();
        JsonArray array = jsonParser.parse(json).getAsJsonArray();
        List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
        for(JsonElement jsonElement : array){
            entityList.add(getMap(jsonElement.toString()));
        }
        return entityList;
    }
	
	private static class NaturalDeserializer implements JsonDeserializer<Object> {
		
		  public Object deserialize(JsonElement json, Type typeOfT, 
		      JsonDeserializationContext context) {
		    if(json.isJsonNull()) return null;
		    else if(json.isJsonPrimitive()) return handlePrimitive(json.getAsJsonPrimitive());
		    else if(json.isJsonArray()) return handleArray(json.getAsJsonArray(), context);
		    else return handleObject(json.getAsJsonObject(), context);
		  }
		  
		  private Object handlePrimitive(JsonPrimitive json) {
		    if(json.isBoolean())
		      return json.getAsBoolean();
		    else if(json.isString())
		      return json.getAsString();
		    else {
		      BigDecimal bigDec = json.getAsBigDecimal();
		      // Find out if it is an int type
		      try {
		        bigDec.toBigIntegerExact();
		        try { return bigDec.intValueExact(); }
		        catch(ArithmeticException e) {}
		        return bigDec.longValue();
		      } catch(ArithmeticException e) {}
		      // Just return it as a double
		      return bigDec.doubleValue();
		    }
		  }
		  
		  private Object handleArray(JsonArray json, JsonDeserializationContext context) {
		    Object[] array = new Object[json.size()];
		    for(int i = 0; i < array.length; i++)
		      array[i] = context.deserialize(json.get(i), Object.class);
		    return array;
		  }
		  
		  private Object handleObject(JsonObject json, JsonDeserializationContext context) {
		    Map<String, Object> map = new HashMap<String, Object>();
		    for(Map.Entry<String, JsonElement> entry : json.entrySet())
		      map.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));
		    return map;
		  }
		}
}

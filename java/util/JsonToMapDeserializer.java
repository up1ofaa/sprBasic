package com.hanwhalife.nbm.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class JsonToMapDeserializer implements JsonDeserializer<Map<String, Object>> {
  
  /* (non-Javadoc)
   * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
   */
  @Override
  public Map<String, Object> deserialize(final JsonElement arg0, final Type arg1, final JsonDeserializationContext arg2)
      throws JsonParseException {
    return (Map<String, Object>) read(arg0); 
  }
  
  public Object read(final JsonElement in) {
    if (in.isJsonArray()) {
      List<Object> list = new ArrayList<Object>();
      JsonArray arr = in.getAsJsonArray();
      for (JsonElement anArr : arr) {
        list.add(read(anArr));
      }
      return list;
    } else if (in.isJsonObject()) {
      Map<String, Object> map = new HashMap<String, Object>();
      JsonObject obj = in.getAsJsonObject();
      Set<Entry<String, JsonElement>> entitySet = obj.entrySet();
      for (Map.Entry<String, JsonElement> entry: entitySet) {
        map.put(entry.getKey(), read(entry.getValue()));
      }
      return map;
    } else if ( in.isJsonPrimitive()) {
      JsonPrimitive prim = in.getAsJsonPrimitive();
      if (prim.isBoolean()) {
        return prim.getAsBoolean();
      } else if (prim.isString()) {
        return prim.getAsString();
      } else if (prim.isNumber()) {
        Number num = prim.getAsNumber();
        if (num.toString().indexOf('.') > -1) {
          return num.doubleValue();
        } else {
          return num.intValue();
        }
      }
    }
    return null;
  }
  
}


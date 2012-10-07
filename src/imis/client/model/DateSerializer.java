package imis.client.model;

import java.lang.reflect.Type;
import java.sql.Date;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateSerializer implements JsonSerializer<java.sql.Date>{

  @Override
  public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
    return new JsonPrimitive(Util.df.format(date));
  }

}

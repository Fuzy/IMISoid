package imis.client.model;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Util {
  private static GsonBuilder gsonBuilder = new GsonBuilder();
  public static Gson gson;
  private static final Type listType = new TypeToken<List<Event>>() {
  }.getType();
  public static final JsonParser parser = new JsonParser();
  public static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

  static {
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    gsonBuilder.registerTypeAdapter(java.sql.Date.class, new DateDeserializer());
    gson = gsonBuilder.create();
  }

}

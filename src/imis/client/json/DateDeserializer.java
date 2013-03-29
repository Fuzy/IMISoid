package imis.client.json;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.ParseException;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateDeserializer implements JsonDeserializer<java.sql.Date> {
  //private static final String TAG = "DateDeserializer";

  @Override
  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    Date date = null;

    try {
      date = new java.sql.Date((Util.df.parse(json.getAsString())).getTime());
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return date;
  }

}

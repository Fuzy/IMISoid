package imis.client.model;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Util {
  private static final String TAG = Util.class.getSimpleName();
  private static GsonBuilder gsonBuilder = new GsonBuilder();
  public static Gson gson;
  public static final Type listType = new TypeToken<List<Event>>() {
  }.getType();
  public static final JsonParser parser = new JsonParser();
  @SuppressLint("SimpleDateFormat")
  public static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

  static {
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    gsonBuilder.registerTypeAdapter(java.sql.Date.class, new DateDeserializer());
    gsonBuilder.registerTypeAdapter(java.sql.Date.class, new DateSerializer());
    gsonBuilder.setExclusionStrategies(new MyExclusionStrategy());
    gson = gsonBuilder.create();
  }

  public static String formatDate(java.sql.Date date) {
    String dateS = null;
    if (date != null) {
      dateS = df.format(date);
    }
    return dateS;
  }

  public static java.sql.Date stringToDate(String str) {
    java.sql.Date date = null;
    try {
      date = new java.sql.Date((df.parse(str)).getTime());
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return date;
  }
  
  /**
   * @param sinceEpoch
   * @return Time since midnight.
   */
  public static long timeFromEpochMsToDayMs() {
    Log.d(TAG, "timeFromEpochMsToDayMs");
    Calendar c = Calendar.getInstance();
    long now = c.getTimeInMillis();
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    long sinceMidnight = now - c.getTimeInMillis();
    return sinceMidnight;
  }

}

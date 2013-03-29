package imis.client.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class MyExclusionStrategy implements ExclusionStrategy {

  @Override
  public boolean shouldSkipClass(Class<?> arg0) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean shouldSkipField(FieldAttributes atr) {
    String name = atr.getName();    
    return name.equals("dirty") || name.equals("deleted") || name.equals("_id");
  }
}

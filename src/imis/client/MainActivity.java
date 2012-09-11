package imis.client;

import imis.client.customviews.BlockView;
import imis.client.customviews.BlocksLayout;
import imis.client.persistent.EventDatabaseHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;

public class MainActivity extends Activity {
  private static final String TAG = "MainActivity";
  public static final String AUTHORITY = "imis.client.events.contentprovider";
  public static final String PROVIDERS_AUTHORITY = AUTHORITY + "/";
  public static final String SCHEME = "content://";
  public static final String TABLE_TODOS = EventDatabaseHelper.TABLE_EVENTS;
  public static final Uri CONTENT_URI = Uri.parse(SCHEME + PROVIDERS_AUTHORITY + TABLE_TODOS);
 

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.blocks_content);

    final BlocksLayout blocks = (BlocksLayout) findViewById(R.id.blocks);
    long start = System.currentTimeMillis() - 2 * 60 * 60 * 1000;
    // long end = System.currentTimeMillis() - 1 * 59 * 60 * 1000 - 1 * 60 * 60
    // * 1000; 1 min
    long end = System.currentTimeMillis() + 1 * 60 * 60 * 1000;
    BlockView block = new BlockView(getApplicationContext(), "id1", "Blok číslo 1", start, end,
        false, 1);
    blocks.addBlock(block);
    
    ContentResolver resolver = getContentResolver();
    Uri uri = resolver.insert(CONTENT_URI, null);
    Log.d(TAG, "uri: " + uri);
    
    Cursor cursor = resolver.query(CONTENT_URI, null, null, null, null);
    if (cursor.moveToFirst()) {
      //TODO mapovani
    }
  }
}

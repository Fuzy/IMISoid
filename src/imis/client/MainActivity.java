package imis.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import imis.client.customviews.BlockView;
import imis.client.customviews.BlocksLayout;
import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventDatabaseHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
    StrictMode.setThreadPolicy(policy);

    final BlocksLayout blocks = (BlocksLayout) findViewById(R.id.blocks);
    //long start = System.currentTimeMillis() - 2 * 60 * 60 * 1000;
    // long end = System.currentTimeMillis() - 1 * 59 * 60 * 1000 - 1 * 60 * 60
    // * 1000; 1 min long end = System.currentTimeMillis() + 1 * 60 * 60 * 1000;
    // BlockView block = new BlockView(getApplicationContext(), "id1",
    // "Blok číslo 1", start, end, false, 1);

    // ContentResolver resolver = getContentResolver(); Uri uri =
    // resolver.insert(CONTENT_URI, null); Log.d(TAG, "uri: " + uri);

    // Cursor cursor = resolver.query(CONTENT_URI, null, null, null, null);

    List<Event> events = NetworkUtilities.getEvents();
    // System.out.println(TAG + " events: " + events);
    List<BlockView> blockViews = blocksFromEvents(events);
    for (BlockView blockView : blockViews) {
      blocks.addBlock(blockView);
    }
    //System.out.println(TAG + "blocksFromEvents: " + blockViews);
  }

  private List<BlockView> blocksFromEvents(List<Event> events) {
    List<BlockView> blocks = new ArrayList<BlockView>();
    final String arrival = "P";
    final String leave = "O";
    // TODO upozornit ze data nejsou v poradku

    String next = arrival;
    boolean error;
    BlockView block = null;
    for (Event event : events) {
      if (event.getDruh().equals(arrival) && next.equals(arrival)) {
        // ocekavany prichod
        block = new BlockView(getApplicationContext());
        next = leave;
        block.setStartTime(timeFromDayDoubleToDayMs(event.getCas()));
      }
      else if (event.getDruh().equals(leave) && next.equals(leave)) {
        // ocekavany odchod
        next = arrival;
        block.setEndTime(timeFromDayDoubleToDayMs(event.getCas()));
        blocks.add(block);
      }
      else {
        // TODO nepocita s prvni denni udalost = O
        error = true;// chyba v datech, neni sekvence P,O,P..
      }
    }

    if (block != null && block.getEndTime() == 0) {
      // nastavim end time na akt cas
      block.setEndTime(timeFromEpochMsToDayMs(System.currentTimeMillis()));
      blocks.add(block);
    }

    return blocks;
  }

  /**
   * Time is <0.0-24.0>h
   * 
   * @return Time since midnight.
   */
  private long timeFromDayDoubleToDayMs(double time) {
    final double msInHourD = 60 * 60 * 1000;
    final long msInHourL = 60 * 60 * 1000;

    long m = (long) time;
    double dec = time % 1;

    long thisHourMs = (long) (dec * msInHourD);
    long todayMs = m * msInHourL + thisHourMs;

    return todayMs;
  }

  /**
   * @param sinceEpoch
   * @return Time since midnight.
   */
  private long timeFromEpochMsToDayMs(long sinceEpoch) {
    Calendar rightNow = Calendar.getInstance();
    // offset to add since we're not UTC
    long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
    long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
    return sinceMidnight;
  }
}

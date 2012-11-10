package imis.client.ui.adapter;

import imis.client.model.Event;
import imis.client.ui.BlockView;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.util.Log;

public class EventsAdapter extends CursorAdapter { // Adapter
  private static final String TAG = EventsAdapter.class.getSimpleName();

  public EventsAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    Log.d(TAG, "EventsAdapter()");
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    // Log.d(TAG, "bindView()");

  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    // Log.d(TAG, "newView()");
    Event event = Event.cursorToEvent(cursor);
    // System.out.println(event);

    BlockView block = new BlockView(context, String.valueOf(event.get_id()), "Title",
        event.getCas(), event.getCas() + 1000 * 60 * 60);

    return block;
  }
  
  //TODO upravit 2->1 komponentu: getCount, mapovaci kolekce

  /*
   * public EventsAdapter(Context context, Cursor c, int flags) { Log.d(TAG,
   * "EventsAdapter()"); }
   * 
   * @Override public void bindView(View view, Context context, Cursor cursor) {
   * Log.d(TAG, "bindView()"); }
   * 
   * @Override public View newView(Context context, Cursor cursor, ViewGroup
   * parent) { Log.d(TAG, "newView()"); Event event =
   * Event.cursorToEvent(cursor); System.out.println(event);
   * 
   * BlockView block = new BlockView(context, String.valueOf(event.get_id()),
   * "Title", event.getCas(), event.getCas() + 1000, false, 1);
   * 
   * return block; }
   */
}

package imis.client.ui.adapter;

import imis.client.model.Event;
import imis.client.ui.BlockView;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.util.Log;

/**
 * Adapter mappings 2 cursor rows to 1 view object.
 * 
 * @author Martin Kadlec, A11N0109P
 * 
 */
public class EventsAdapter extends CursorAdapter {
  private static final String TAG = EventsAdapter.class.getSimpleName();
  @SuppressWarnings("unused")
  private Context context;

  public EventsAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
    this.context = context;
    Log.d(TAG, "EventsAdapter()");
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    // Log.d(TAG, "bindView()");

  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    int initPos = cursor.getPosition();

    Event event, leaveEvent;
    event = Event.cursorToEvent(cursor);
    if (event.isDruhLeave()) {
      Log.d(TAG, "newView() isDruhLeave");
      return null;
    }
    // Log.d(TAG, event.toString());

    // Mam prichozi udalost, hledam zda existuje i odchozi
    leaveEvent = getNextLeaveEvent(cursor);
    long leaveTime = (leaveEvent == null) ? System.currentTimeMillis() : leaveEvent.getCas();// TODO
                                                                                             // test,
                                                                                             // timeFromEpochMsToDayMs

    int leaveId = (leaveEvent == null) ? -1 : leaveEvent.get_id();// TODO log
    Log.d(TAG, "newView() arrive id: " + event.get_id() + " leave id: " + leaveId);
    BlockView block = new BlockView(context, event.get_id(), leaveId, event.getCas(), leaveTime, event.isDeleted());

    cursor.moveToPosition(initPos);
    return block;
  }

  private Event getNextLeaveEvent(Cursor cursor) {
    int initPos = cursor.getPosition();

    Event event = null;
    while (cursor.moveToNext()) {
      event = Event.cursorToEvent(cursor);
      if (event.isDruhLeave())
        break;
      event = null;
    }
    cursor.moveToPosition(initPos);
    return event;
  }

  // TODO upravit 2->1 komponentu: getCount, mapovaci kolekce

  /*
   * private List<BlockView> blocksFromEvents(List<Event> events) {
   * List<BlockView> blocks = new ArrayList<BlockView>(); final String arrival =
   * "P"; final String leave = "O"; // TODO upozornit ze data nejsou v poradku
   * 
   * String next = arrival;
   * 
   * @SuppressWarnings("unused") boolean error; BlockView block = null; for
   * (Event event : events) { if (event.getDruh().equals(arrival) &&
   * next.equals(arrival)) { // ocekavany prichod block = new
   * BlockView(context); next = leave; block.setStartTime(event.getCas()); }
   * else if (event.getDruh().equals(leave) && next.equals(leave)) { //
   * ocekavany odchod next = arrival; block.setEndTime(event.getCas());
   * blocks.add(block); } else { // TODO nepocita s prvni denni udalost = O
   * error = true;// chyba v datech, neni sekvence P,O,P.. } }
   * 
   * if (block != null && block.getEndTime() == 0) { // nastavim end time na akt
   * cas block.setEndTime(timeFromEpochMsToDayMs()); blocks.add(block); }
   * 
   * return blocks; }
   */
}

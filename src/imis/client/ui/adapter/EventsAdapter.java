package imis.client.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import imis.client.model.Event;
import imis.client.ui.BlockView;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.util.Log;
import static imis.client.model.Util.timeFromEpochMsToDayMs;

/**
 * Adapter mappings 2 cursor rows to 1 view object.
 * 
 * @author Martin Kadlec, A11N0109P
 * 
 */
public class EventsAdapter extends CursorAdapter {
  private static final String TAG = EventsAdapter.class.getSimpleName();
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
    Log.d(TAG, "newView()");
    Event event = Event.cursorToEvent(cursor);
    Log.d(TAG, event.toString());

    BlockView block = new BlockView(context, String.valueOf(event.get_id()), event.getPoznamka(),
        event.getCas() - 5 * 1000 * 60 * 60, event.getCas()- 2 * 1000 * 60 * 60);// TODO mozna
                                                             // predavat event

    return block;
  }

  // TODO upravit 2->1 komponentu: getCount, mapovaci kolekce

  private List<BlockView> blocksFromEvents(List<Event> events) {
    List<BlockView> blocks = new ArrayList<BlockView>();
    final String arrival = "P";
    final String leave = "O";
    // TODO upozornit ze data nejsou v poradku

    String next = arrival;
    @SuppressWarnings("unused")
    boolean error;
    BlockView block = null;
    for (Event event : events) {
      if (event.getDruh().equals(arrival) && next.equals(arrival)) {
        // ocekavany prichod
        block = new BlockView(context);
        next = leave;
        block.setStartTime(event.getCas());
      }
      else if (event.getDruh().equals(leave) && next.equals(leave)) {
        // ocekavany odchod
        next = arrival;
        block.setEndTime(event.getCas());
        blocks.add(block);
      }
      else {
        // TODO nepocita s prvni denni udalost = O
        error = true;// chyba v datech, neni sekvence P,O,P..
      }
    }

    if (block != null && block.getEndTime() == 0) {
      // nastavim end time na akt cas
      block.setEndTime(timeFromEpochMsToDayMs());
      blocks.add(block);
    }

    return blocks;
  }
}

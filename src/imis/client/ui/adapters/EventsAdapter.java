package imis.client.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import imis.client.model.Event;
import imis.client.ui.BlockView;

/**
 * Adapter mappings 2 cursor rows to 1 view object.
 *
 * @author Martin Kadlec, A11N0109P
 */
public class EventsAdapter extends CursorAdapter {
    private static final String TAG = EventsAdapter.class.getSimpleName();
    @SuppressWarnings("unused")
    private Context context;
    //TODO kolekce s barvami podle klice

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

        Event event, nextEvent = null;
        event = Event.cursorToEvent(cursor);
        if (event.isDruhArrival()) {
            Log.d(TAG, "newView() isDruhArrival");
            nextEvent = getNextEvent(cursor, Event.DRUH_LEAVE);
        } else if (event.isDruhLeave()) {
            Log.d(TAG, "newView() isDruhLeave");
            String kod_po = event.getKod_po();
            if (kod_po.equals(Event.KOD_PO_LEAVE_SERVICE) || kod_po.equals(Event.KOD_PO_LEAVE_LUNCH)
                    || kod_po.equals(Event.KOD_PO_LEAVE_SUPPER)) {
                Log.d(TAG, "newView() isDruhLeave kod_po " + kod_po);
                nextEvent = getNextEvent(cursor, Event.DRUH_ARRIVAL);
            } else {
                return null;
            }

        }

        // Mam prichozi udalost, hledam zda existuje i odchozi

        long endTime = (nextEvent == null) ? System.currentTimeMillis() : nextEvent.getCas();// TODO aktualizovat cas, round min

        int nextId = (nextEvent == null) ? -1 : nextEvent.get_id();// TODO log
        Log.d(TAG, "newView() id: " + event.get_id() + " nextId id: " + nextId);
        BlockView block = new BlockView(context, event.get_id(), nextId, event.getCas(), endTime, event.getKod_po(), event.isDirty());
        //TODO barevnost
        cursor.moveToPosition(initPos);
        return block;
    }

    private Event getNextEvent(Cursor cursor, String druh) {
        int initPos = cursor.getPosition();

        Event event = null;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
            if (event.getDruh().equals(druh))
                break;
            event = null;
        }
        cursor.moveToPosition(initPos);
        return event;
    }
}

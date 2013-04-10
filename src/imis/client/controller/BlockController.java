package imis.client.controller;

import android.database.Cursor;
import imis.client.model.Block;
import imis.client.model.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 14:15
 */
public class BlockController {
    private static final String TAG = BlockController.class.getSimpleName();

    public static final String[] VALUES = new String[]
            {Event.KOD_PO_LEAVE_SERVICE, Event.KOD_PO_LEAVE_LUNCH, Event.KOD_PO_LEAVE_SUPPER};

    public static List<Block> eventsToMapOfBlocks(Cursor cursor) {
        Event startEvent, endEvent = null;
        Block block;
        List<Block> blocks = new ArrayList<>();

        while (cursor.moveToNext()) {
            startEvent = Event.cursorToEvent(cursor);


            if (startEvent.isDruhArrival()) {
                endEvent = getNextEvent(cursor, Event.DRUH_LEAVE);
            } else if (startEvent.isDruhLeave()) {
                if (Arrays.asList(VALUES).contains(startEvent.getKod_po())) {
                    endEvent = getNextEvent(cursor, Event.DRUH_ARRIVAL);
                } else {
                    endEvent = null;
                }

            }

            if (endEvent != null) {
                block = new Block();
                block.setDate(startEvent.getDatum());
                block.setStartTime(startEvent.getCas());
                block.setArriveId(startEvent.get_id());
                block.setEndTime((endEvent == null) ? -1 : endEvent.getCas());
                block.setLeaveId((endEvent == null) ? -1 : endEvent.get_id());
                block.setKod_po(startEvent.getKod_po());
                block.setDirty(startEvent.isDirty() || endEvent.isDirty());
                blocks.add(block);
            }
        }


        return blocks;
    }

    private static Event getNextEvent(Cursor cursor, String druh) {
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

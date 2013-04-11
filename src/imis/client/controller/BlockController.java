package imis.client.controller;

import android.database.Cursor;
import imis.client.model.Block;
import imis.client.model.Event;
import imis.client.model.EventsGraphSerie;
import imis.client.ui.ColorUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 14:15
 */
public class BlockController {
    private static final String TAG = BlockController.class.getSimpleName();
    private static final long MS_IN_HOUR = 60 * 60 * 1000;

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
        cursor.moveToPosition(-1);

        return blocks;
    }

    public static List<EventsGraphSerie> countBlocksStatistics(List<Block> blocks) {

        Map<String, Double> statistics = new HashMap<>();
        for (Block block : blocks) {
            double amount = (double) ((block.getEndTime() - block.getStartTime()) / MS_IN_HOUR);
            double count = statistics.containsKey(block.getKod_po()) ? statistics.get(block.getKod_po()) : 0;
            statistics.put(block.getKod_po(), count + amount);
        }

        EventsGraphSerie serie;
        List<EventsGraphSerie> eventsGraphSeries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : statistics.entrySet()) {
            serie = new EventsGraphSerie(entry.getKey(), entry.getValue());
            serie.setColor(ColorUtil.getColorForType(entry.getKey()));
            eventsGraphSeries.add(serie);
        }

        return eventsGraphSeries;
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

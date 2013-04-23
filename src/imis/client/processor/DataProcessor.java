package imis.client.processor;

import android.database.Cursor;
import android.util.Log;
import imis.client.AppUtil;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Block;
import imis.client.model.Event;
import imis.client.model.Record;
import imis.client.ui.ColorUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 14:15
 */
public class DataProcessor {
    private static final String TAG = DataProcessor.class.getSimpleName();
    private static final long MS_IN_HOUR = 60L * 60L * 1000L;
    private static final long MS_IN_MIN = 60L * 1000L;
    private static final long MS_IN_DAY = MS_IN_HOUR * 24L;

    public static final String[] VALUES = new String[]
            {Event.KOD_PO_LEAVE_SERVICE, Event.KOD_PO_LEAVE_LUNCH, Event.KOD_PO_LEAVE_SUPPER};

    public static List<Block> eventsToMapOfBlocks(Cursor cursor) {
        Log.d(TAG, "eventsToMapOfBlocks()");
        Event startEvent, endEvent = null;
        Block block;
        List<Block> blocks = new ArrayList<>();

        //TODO odladit, nektere jsou tam dvakrat
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
                int index = Arrays.asList(Event.KOD_PO_VALUES).indexOf(startEvent.getKod_po());
                if (index == -1) index = 6;
                block.setKod_po(Event.KOD_PO_VALUES[index]);
                block.setDirty(startEvent.isDirty() || endEvent.isDirty());
                block.setError(startEvent.isError() || endEvent.isError());
                Log.d(TAG, "eventsToMapOfBlocks() block " + block);
                blocks.add(block);
            }
        }
        Log.d(TAG, "eventsToMapOfBlocks() blocks " + blocks );
        /*Event last = blocks.get(blocks.size() - 1);
        if ()*/
        cursor.moveToPosition(-1);

        return blocks;
    }


    public static PieChartData countEventsPieChartData(List<Block> blocks, List<String> codes, Map<String, String> kody_po) {
        PieChartData pieChartData = new PieChartData();
        long total = 0L;

        Map<String, Long> statistics = new HashMap<>();
        for (Block block : blocks) {
            if (codes.contains(block.getKod_po()) == false) continue; // Exclude not checked in checkbox
            long amount = (block.getEndTime() - block.getStartTime());
            total += amount;
            long count = statistics.containsKey(block.getKod_po()) ? statistics.get(block.getKod_po()) : 0;
            statistics.put(block.getKod_po(), count + amount);
            Log.d(TAG, "countPieChartData() count + amount " + (count + amount));
        }


        long value;
        PieChartSerie serie;
        for (Map.Entry<String, Long> entry : statistics.entrySet()) {
            value = entry.getValue();
            Log.d(TAG, "countPieChartData() value " + value);


            serie = new PieChartSerie(kody_po.get(entry.getKey()), (double) (value / MS_IN_HOUR));
            serie.setColor(ColorUtil.getColor(entry.getKey()));
            serie.setTime(AppUtil.formatTime(value));
            serie.setPercent((int) (((double) value / (double) total) * 100));
            pieChartData.addSerie(serie);
        }
        Log.d(TAG, "countPieChartData() total " + total);
        pieChartData.setTotal(AppUtil.formatTime(total));
        return pieChartData;
    }

    public static PieChartData countRecordsPieChartData(List<Record> records, List<String> codes) {
        Log.d(TAG, "countRecordsPieChartData()");
        //TODO // Exclude not checked in checkbox
        PieChartData pieChartData = new PieChartData();
        long total = 0L;

        Map<String, Long> statistics = new HashMap<>();
        for (Record record : records) {
            if (codes.contains(record.recordType()) == false) continue; // Exclude not checked in checkbox
            long amount = record.getMnozstvi_odved();
            total += amount;
            long count = statistics.containsKey(record.recordType()) ? statistics.get(record.recordType()) : 0;
            statistics.put(record.recordType(), count + amount);
        }

        long value;
        PieChartSerie serie;
        for (Map.Entry<String, Long> entry : statistics.entrySet()) {
            value = entry.getValue();
            serie = new PieChartSerie(entry.getKey(), (double) (value / MS_IN_HOUR));//TODO
            serie.setColor(ColorUtil.getColor(entry.getKey()));
            serie.setTime(AppUtil.formatTime(value));
            serie.setPercent((int) (((double) value / (double) total) * 100));
            pieChartData.addSerie(serie);
        }

        pieChartData.setTotal(AppUtil.formatTime(total));
        Log.d(TAG, "countRecordsPieChartData() pieChartData " + pieChartData);
        return pieChartData;
    }

    public static StackedBarChartData countEventsStackedBarChartData(List<Block> blocks, List<String> codes,
                                                                     Map<String, String> kody_po) {
        StackedBarChartData chartData = new StackedBarChartData();
        for (Block block : blocks) {
            if (block.getDate() > chartData.getMaxDay()) chartData.setMaxDay(block.getDate());
            if (block.getDate() < chartData.getMinDay()) chartData.setMinDay(block.getDate());
        }

        final int numOfDays = (int) ((chartData.getMaxDay() - chartData.getMinDay()) / MS_IN_DAY) + 1;
        Log.d(TAG, "countEventsStackedBarChartData() numOfDays " + numOfDays);

        Map<String, double[]> map = new HashMap<>();

        for (Block block : blocks) {
            String kod_po = block.getKod_po();
            if (codes.contains(kod_po) == false) continue; // Exclude not checked in checkbox
            int index = (int) ((block.getDate() - chartData.getMinDay()) / MS_IN_DAY);
            double amount = (double) ((block.getEndTime() - block.getStartTime()) / MS_IN_HOUR);

            // If exists update
            boolean contains = map.containsKey(kod_po);
            if (contains == false) {
                double[] values = new double[numOfDays];
                map.put(kod_po, values);
            }

            // Update value
            double[] vaDoubles = map.get(kod_po);
            double oldValue = vaDoubles[index];
            vaDoubles[index] += oldValue + amount;
            if (vaDoubles[index] > chartData.getyMax()) chartData.setyMax(vaDoubles[index]);

        }

        int size = map.size();
        int ind = 0;
        List<double[]> values = new ArrayList<>(size);
        String[] titles = new String[size];
        int[] colors = new int[size];

        for (Map.Entry<String, double[]> stringEntry : map.entrySet()) {
            Log.d(TAG, "countEventsStackedBarChartData() " + Arrays.toString(stringEntry.getValue()));
            values.add(stringEntry.getValue());
            titles[ind] = kody_po.get(stringEntry.getKey());
            colors[ind] = ColorUtil.getColor(stringEntry.getKey());
            ind++;
        }

        chartData.setValues(values);
        chartData.setTitles(titles);
        chartData.setColors(colors);

        // order titles correctly
        /*String[] titles = new String[kody_po.size()];
        for (int i = 0; i < titles.length; i++) {
            titlesStr[i] = kody_po.get(titles[i]);
        }
        chartData.setKody_po(kody_po);
        Log.d(TAG, "prepareGraph() titles " + titles);
        Log.d(TAG, "prepareGraph() titlesStr " + titlesStr);*/

        Log.d(TAG, "countEventsStackedBarChartData() titles " + Arrays.toString(titles));
        for (double[] value : values) {
            Log.d(TAG, "countEventsStackedBarChartData() value " + Arrays.toString(value));
        }
        Log.d(TAG, "countEventsStackedBarChartData() min " + chartData.getMinDay() + " max" + chartData.getMaxDay());
        return chartData;
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

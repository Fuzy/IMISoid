package imis.client.processor;

import android.database.Cursor;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Block;
import imis.client.model.Event;
import imis.client.model.Record;
import imis.client.ui.ColorUtil;
import imis.client.ui.activities.ControlActivity;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 14:15
 */
public class DataProcessor {
    private static final String TAG = DataProcessor.class.getSimpleName();


    public static final String[] VALUES = new String[]
            {Event.KOD_PO_LEAVE_SERVICE, Event.KOD_PO_LEAVE_LUNCH, Event.KOD_PO_LEAVE_SUPPER};

    public static String[] eventsCodesInBlocks(List<Block> blocks) {
        Set<String> codes = new HashSet<>();
        for (Block block : blocks) {
            codes.add(block.getKod_po());
        }
        return codes.toArray(new String[]{});
    }

    public static String[] recordsCodesInRecords(List<Record> records) {
        Set<String> codes = new HashSet<>();
        for (Record record : records) {
            codes.add(record.recordType());
        }
        return codes.toArray(new String[]{});
    }

    public static List<Block> eventsToMapOfBlocks(Cursor cursor) {
        Log.d(TAG, "eventsToMapOfBlocks()");
        Event startEvent, endEvent = null;
        Block block;
        List<Block> blocks = new ArrayList<>();
        //TODO kazdou minutu by mel prijit refresh
        //TODO odladit, nektere jsou tam dvakrat
        cursor.moveToPosition(-1);
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

            boolean isTodayUnfinishedPresence = startEvent.isDruhArrival() && endEvent == null
                    && startEvent.getCas() <= AppUtil.currentTimeInLong();//TODO pouze pro dnesek
            Log.d(TAG, "eventsToMapOfBlocks() isUnfinishedPresence " + isTodayUnfinishedPresence);
            if (isTodayUnfinishedPresence || endEvent != null) {
                block = new Block();
                block.setDate(startEvent.getDatum());
                block.setStartTime(startEvent.getCas());
                block.setArriveId(startEvent.get_id());
                if (endEvent != null) {
                    block.setEndTime(endEvent.getCas());
                    block.setLeaveId(endEvent.get_id());
                } else {
                    block.setEndTime(AppUtil.currentTimeInLong());
                    block.setLeaveId(-1);
                }
                int index = Arrays.asList(Event.KOD_PO_VALUES).indexOf(startEvent.getKod_po());
                if (index == -1) index = 6;
                block.setKod_po(Event.KOD_PO_VALUES[index]);
                block.setDirty(startEvent.isDirty() || (endEvent != null && endEvent.isDirty()));
                block.setError(startEvent.isError() || (endEvent != null && endEvent.isError()));
                Log.d(TAG, "eventsToMapOfBlocks() block " + block);
                blocks.add(block);
            }
        }
        Log.d(TAG, "eventsToMapOfBlocks() blocks " + blocks);
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


            serie = new PieChartSerie(kody_po.get(entry.getKey()), (double) (value / AppConsts.MS_IN_HOUR));
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
            serie = new PieChartSerie(entry.getKey(), (double) (value / AppConsts.MS_IN_HOUR));
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
                                                                     Map<String, String> kody_po, Map<String, String> selectionArgs) {
        StackedBarChartData chartData = new StackedBarChartData();
        chartData.setMinDay(Long.valueOf(selectionArgs.get(ControlActivity.PAR_FROM)));
        chartData.setMaxDay(Long.valueOf(selectionArgs.get(ControlActivity.PAR_TO)));

        final int numOfDays = (int) ((chartData.getMaxDay() - chartData.getMinDay()) / AppConsts.MS_IN_DAY) + 1;
        Log.d(TAG, "countEventsStackedBarChartData() numOfDays " + numOfDays);

        Map<String, double[]> map = new HashMap<>();

        for (Block block : blocks) {
            String kod_po = block.getKod_po();
            if (codes.contains(kod_po) == false) continue; // Exclude not checked in checkbox
            int index = (int) ((block.getDate() - chartData.getMinDay()) / AppConsts.MS_IN_DAY);
            double amount = (double) ((block.getEndTime() - block.getStartTime()) / AppConsts.MS_IN_HOUR);

            // If not exists yet, create new array
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

        Log.d(TAG, "countEventsStackedBarChartData() titles " + Arrays.toString(titles));
        for (double[] value : values) {
            Log.d(TAG, "countEventsStackedBarChartData() value " + Arrays.toString(value));
        }
        Log.d(TAG, "countEventsStackedBarChartData() min "
                + AppUtil.formatAbbrDate(chartData.getMinDay()) + " max" + AppUtil.formatAbbrDate(chartData.getMaxDay()));
        return chartData;
    }

    public static StackedBarChartData countRecordsStackedBarChartData(List<Record> records, List<String> codes, Map<String, String> selectionArgs) {
        StackedBarChartData chartData = new StackedBarChartData();
        // count mix/ max

        chartData.setMinDay(Long.valueOf(selectionArgs.get(ControlActivity.PAR_FROM)));
        chartData.setMaxDay(Long.valueOf(selectionArgs.get(ControlActivity.PAR_TO)));

        final int numOfDays = (int) ((chartData.getMaxDay() - chartData.getMinDay()) / AppConsts.MS_IN_DAY) + 1;
        Log.d(TAG, "countRecordsStackedBarChartData() numOfDays " + numOfDays);

        Map<String, double[]> map = new HashMap<>();
        String type;
        for (Record record : records) {
            type = record.recordType();
            if (codes.contains(type) == false) continue; // Exclude not checked in checkbox
            int index = (int) ((record.getDatum() - chartData.getMinDay()) / AppConsts.MS_IN_DAY);
            double amount = (double) (record.getMnozstvi_odved() / AppConsts.MS_IN_HOUR);

            // If not exists yet, create new array
            boolean contains = map.containsKey(type);
            if (contains == false) {
                double[] values = new double[numOfDays];
                map.put(type, values);
            }

            // Update value
            double[] vaDoubles = map.get(type);
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
            titles[ind] = stringEntry.getKey();
            colors[ind] = ColorUtil.getColor(stringEntry.getKey());
            ind++;
        }

        chartData.setValues(values);
        chartData.setTitles(titles);
        chartData.setColors(colors);

        Log.d(TAG, "countRecordsStackedBarChartData() titles " + Arrays.toString(titles));
        for (double[] value : values) {
            Log.d(TAG, "countRecordsStackedBarChartData() value " + Arrays.toString(value));
        }
        Log.d(TAG, "countRecordsStackedBarChartData() min "
                + AppUtil.formatAbbrDate(chartData.getMinDay()) + " max" + AppUtil.formatAbbrDate(chartData.getMaxDay()));

        return chartData;
    }


    public static Event getNextEvent(Cursor cursor, String druh) {
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

    public static Event getPrevEvent(Cursor cursor, String druh) {
        int initPos = cursor.getPosition();

        Event event = null;
        while (cursor.moveToPrevious()) {
            event = Event.cursorToEvent(cursor);
            if (event.getDruh().equals(druh))
                break;
            event = null;
        }
        cursor.moveToPosition(initPos);
        return event;
    }
}

package imis.client.processor;

import android.database.Cursor;
import android.util.Log;
import imis.client.AppUtil;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Block;
import imis.client.model.Event;
import imis.client.ui.ColorUtil;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 14:15
 */
public class BlockProcessor {
    private static final String TAG = BlockProcessor.class.getSimpleName();
    private static final long MS_IN_HOUR = 60L * 60L * 1000L;
    private static final long MS_IN_MIN = 60L * 1000L;
    private static final long MS_IN_DAY = MS_IN_HOUR * 24L;

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
                block.setIndexKod_po(Arrays.asList(Event.KOD_PO_VALUES).indexOf(startEvent.getKod_po()));
                block.setDirty(startEvent.isDirty() || endEvent.isDirty());
                blocks.add(block);
            }
        }
        cursor.moveToPosition(-1);

        return blocks;
    }

    public static PieChartData countPieChartData(List<Block> blocks) {
        PieChartData pieChartData = new PieChartData();
        long total = 0L;

        Map<String, Long> statistics = new HashMap<>();
        for (Block block : blocks) {
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

            serie = new PieChartSerie(entry.getKey(), (double) (value / MS_IN_HOUR));
            serie.setColor(ColorUtil.getColor(entry.getKey()));
            serie.setTime(AppUtil.formatTime(value));
            serie.setPercent((int) (((double) value / (double) total) * 100));
            pieChartData.addSerie(serie);
        }
        Log.d(TAG, "countPieChartData() total " + total);
        pieChartData.setTotal(AppUtil.formatTime(total));
        return pieChartData;
    }

    public static StackedBarChartData countStackedBarChartData(List<Block> blocks) {
        StackedBarChartData chartData = new StackedBarChartData();
        for (Block block : blocks) {
            if (block.getDate() > chartData.getMaxDay()) chartData.setMaxDay(block.getDate());
            if (block.getDate() < chartData.getMinDay()) chartData.setMinDay(block.getDate());
        }
        Log.d(TAG, "countStackedBarChartData() min " + chartData.getMinDay() + " max" + chartData.getMaxDay());
        final int numOfDays = (int) ((chartData.getMaxDay() - chartData.getMinDay()) / MS_IN_DAY) + 1;
        Log.d(TAG, "countStackedBarChartData() numOfDays " + numOfDays);
        double[] normal = null, privat = null, service = null, lunch = null, supper = null, medic = null;

        for (Block block : blocks) {
            int index = (int) ((block.getDate() - chartData.getMinDay()) / MS_IN_DAY);
            int indexKod_po = block.getIndexKod_po();
            double amount = (double) ((block.getEndTime() - block.getStartTime()) / MS_IN_HOUR);

            Log.d(TAG, "countStackedBarChartData() index " + index + " indexKod_po" + indexKod_po);

            switch (indexKod_po) {
                case Event.IND_NORMAL:
                    if (normal == null) normal = new double[numOfDays];
                    normal[index] += amount;
                    if (normal[index] > chartData.getyMax()) chartData.setyMax(normal[index]);
                    break;
                case Event.IND_PRIVAT:
                    if (privat == null) privat = new double[numOfDays];
                    privat[index] += amount;
                    break;
                case Event.IND_SERVICE:
                    if (service == null) service = new double[numOfDays];
                    service[index] += amount;
                    break;
                case Event.IND_LUNCH:
                    if (lunch == null) lunch = new double[numOfDays];
                    lunch[index] += amount;
                    break;
                case Event.IND_SUPPER:
                    if (supper == null) supper = new double[numOfDays];
                    supper[index] += amount;
                    break;
                case Event.IND_MEDIC:
                    if (medic == null) medic = new double[numOfDays];
                    medic[index] += amount;
                    break;
            }
        }

        int distinct = 0, ind_titles = 0, ind_colors = 0;
        if (normal != null) distinct++;
        if (privat != null) distinct++;
        if (service != null) distinct++;
        if (lunch != null) distinct++;
        if (supper != null) distinct++;
        if (medic != null) distinct++;

        List<double[]> values = new ArrayList<>(distinct);
        String[] titles = new String[distinct];
        int[] colors = new int[distinct];
        if (normal != null) {
            titles[ind_titles++] = Event.KOD_PO_VALUES[Event.IND_NORMAL];
            values.add(normal);
            colors[ind_colors++] = ColorUtil.getColor(Event.KOD_PO_VALUES[Event.IND_NORMAL]);
        }
        if (service != null) {
            titles[ind_titles++] = Event.KOD_PO_VALUES[Event.IND_SERVICE];
            values.add(service);
            colors[ind_colors++] = ColorUtil.getColor(Event.KOD_PO_VALUES[Event.IND_SERVICE]);
        }
        if (lunch != null) {
            titles[ind_titles++] = Event.KOD_PO_VALUES[Event.IND_LUNCH];
            values.add(lunch);
            colors[ind_colors++] = ColorUtil.getColor(Event.KOD_PO_VALUES[Event.IND_LUNCH]);
        }

        Log.d(TAG, "countStackedBarChartData() normal " + Arrays.toString(normal));
        Log.d(TAG, "countStackedBarChartData() lunch " + Arrays.toString(lunch));
        Log.d(TAG, "countStackedBarChartData() service " + Arrays.toString(service));
        chartData.setValues(values);
        chartData.setTitles(titles);
        chartData.setColors(colors);
        //TODO dokoncit pro ostatni typy
        return chartData;
    }

    public List<String> countEventsStatistics(List<Block> blocks) {


        return null;
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

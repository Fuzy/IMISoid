package imis.client.ui.activities;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.asynctasks.GetListOfEmployees;
import imis.client.asynctasks.result.Result;
import imis.client.model.Event;
import imis.client.model.Record;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import imis.client.persistent.RecordManager;
import imis.client.ui.ColorUtil;
import imis.client.ui.activities.util.ActivityConsts;
import imis.client.ui.dialogs.ColorPickerDialog;
import imis.client.ui.fragments.DayTimelineBlocksFragment;
import imis.client.ui.fragments.DayTimelineListFragment;

import java.util.Map;

import static imis.client.AppConsts.*;
import static imis.client.AppUtil.showAccountNotExistsError;
import static imis.client.AppUtil.showNetworkAccessUnavailable;
import static imis.client.persistent.EventManager.EventQuery;


public class DayTimelineActivity extends AsyncActivity implements LoaderManager.LoaderCallbacks<Cursor>, ColorPickerDialog.OnColorChangedListener {
    private static final String TAG = DayTimelineActivity.class.getSimpleName();

    private long date;// = 1364428800000L; //1364166000000L;//1364169600000L;
    protected final DataSetObservable mDataSetObservable = new DataSetObservable();
    private Cursor mCursor;

    private static final int LOADER_EVENTS = 0x02;
    private static final int CALENDAR_ACTIVITY_CODE = 1;

    protected static final String FRAG_LIST = "DayTimelineListFragment",
            FRAG_BLOCKS = "DayTimelineBlocksFragment";

    private static final String FRAG_TAG = "fragment";
    private String currentFragment;
    //TODO save, restore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        getSupportLoaderManager().initLoader(LOADER_EVENTS, null, this);

        // init UI
        setContentView(R.layout.daytimeline);

        changeDate(AppUtil.todayInLong());
        Log.d(TAG, "onCreate() date: " + AppUtil.formatDate(date));

        loadNetworkSharedPreferences();
        loadColorSharedPreferences();

        // delete old data
        deleteOldData();
    }

    private void deleteOldData() {
        long milestone = AppUtil.getStartDateOfPreviousMonth();
        int countOfEvents = EventManager.deleteEventsOlderThan(this, milestone);
        int countOfRecords = RecordManager.deleteRecordsOlderThan(this, milestone);
        Log.d(TAG, "deleteOldData() count " + countOfEvents);
        Log.d(TAG, "deleteOldData() countOfRecords " + countOfRecords);
    }

    private void setDateTitle(long date) {
        setTitle(AppUtil.formatDate(date));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() Events:\n" + EventManager.getAllEvents(getApplicationContext()));
        initFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DayTimelineActivity", "onPause()");
        saveColorSharedPreferences();
    }

    private void initFragment() {
        Log.d(TAG, "initFragment() current " + currentFragment);
        if (currentFragment == null) {
            switchToDayTimelineBlocksFragment();
            return;
        }

        if (currentFragment.equals(FRAG_LIST)) {
            switchToDayTimelineBlocksFragment();
        } else if (currentFragment.equals(FRAG_BLOCKS)) {
            switchToDayTimelineListFragment();
        }
    }

    private void switchToDayTimelineListFragment() {
        Log.d(TAG, "switchToDayTimelineListFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DayTimelineListFragment listFragment = new DayTimelineListFragment();
        ft.replace(R.id.dayTimeline, listFragment, "DayTimelineListFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        if (getSupportLoaderManager().getLoader(LOADER_EVENTS) != null) {
            getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
        }
    }

    private void switchToDayTimelineBlocksFragment() {
        Log.d(TAG, "switchToDayTimelineBlocksFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DayTimelineBlocksFragment listFragment = new DayTimelineBlocksFragment();
        ft.replace(R.id.dayTimeline, listFragment, "DayTimelineBlocksFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        if (getSupportLoaderManager().getLoader(LOADER_EVENTS) != null) {
            getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
        }
    }

    private void switchFragment() {
        Log.d(TAG, "switchFragment() num of existing ");
        if (getSupportFragmentManager().findFragmentByTag(FRAG_LIST) != null) {
            removeFragment(FRAG_LIST);
            switchToDayTimelineBlocksFragment();
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_BLOCKS) != null) {
            removeFragment(FRAG_BLOCKS);
            switchToDayTimelineListFragment();
        }
    }

    private void removeFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        Log.d(TAG, "removeFragment() fragment " + fragment.getTag());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                startInsertActivity();
                return true;
            case R.id.sync_button:
                performSync();
                return true;
            case R.id.network_settings:
                startNetworkSettingActivity();
                return true;
            case R.id.menu_calendar:
                startCalendarActivity();
                return true;
            case R.id.menu_alt_view:
                switchFragment();
                return true;
            case R.id.menu_records:
                startRecordsListActivity();
                return true;
            case R.id.menu_employeesList:
                refreshListOfEmployees();
                return true;
            case R.id.menu_employeesPresent:
                startPresentEmployeesActivity();
                return true;
            case R.id.menu_eventsChart:
                startEventsChartActivity();
                return true;
            case R.id.menu_recordsChart:
                startRecordsChartActivity();
                return true;
            case R.id.info_color:
                startColorInfoActivity();
                return true;
            case R.id.sync_settings:
                startSyncSettingsActivity();
                return true;
            case R.id.location_settings:
                startLocationSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void processAsyncTask() {
        Log.d(TAG, "processAsyncTask()");
    }

    private void refreshListOfEmployees() {
        Log.d(TAG, "refreshListOfEmployees()");
        try {
            String icp = AppUtil.getUserICP(this);
            Log.d(TAG, "refreshListOfEmployees() icp " + icp);
            createTaskFragment(new GetListOfEmployees(this, icp));
        } catch (Exception e) {
            //TODO err msg
            showAccountNotExistsError(this);
        }
    }

    private void performSync() {
        Log.d(TAG, "onOptionsItemSelected sync request");

        if (!NetworkUtilities.isOnline(getApplication())) {
            showNetworkAccessUnavailable(getApplication());
            return;
        }
        Bundle extras = new Bundle();
        extras.putLong(Event.KEY_DATE, date);
        try {
            Account account = AppUtil.getUserAccount(this);
            /*int isSyncable = ContentResolver.getIsSyncable(account, AppConsts.AUTHORITY1);
            Log.d(TAG, "performSync() isSyncable " + isSyncable);
            ContentResolver.setSyncAutomatically(account, AppConsts.AUTHORITY1, true);
            boolean syncAutomatically = ContentResolver.getSyncAutomatically(account, AppConsts.AUTHORITY1);
            Log.d(TAG, "performSync() syncAutomatically " + syncAutomatically);*/
            ContentResolver.requestSync(account, AppConsts.AUTHORITY1, extras);
        } catch (Exception e) {
            e.printStackTrace();
            showAccountNotExistsError(getApplication());//TODO err msg
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader() date " + date);
        switch (i) {
            case LOADER_EVENTS:
                try {
                    String icp = AppUtil.getUserICP(this);
                    Log.d(TAG, "onCreateLoader() icp " + icp);
                    return new CursorLoader(getApplicationContext(), EventQuery.CONTENT_URI, null,
                            EventQuery.SELECTION_DAY_USER_UNDELETED, new String[]{String.valueOf(date), icp}, EventQuery.ORDER_BY_DATE_TIME_ASC);
                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtil.showAccountNotExistsError(this);
                    return null;
                }
            default:
                return null;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset()");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished() rows: " + cursor.getCount() + " positon: " + cursor.getPosition());
        mCursor = cursor;
        mDataSetObservable.notifyChanged();
    }

    private void startInsertActivity() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/event.imisoid");
        intent.putExtra(Event.KEY_DATE, date);
        startActivity(intent);
    }

    public void startEditActivity(int arriveID, int leaveID) {   //TODO frag
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(ActivityConsts.ID_ARRIVE, arriveID);
        intent.putExtra(ActivityConsts.ID_LEAVE, leaveID);
        intent.setType("vnd.android.cursor.item/event.imisoid");
        //intent.putExtra(Event.KEY_DATE, date);
        startActivity(intent);
    }

    private void startNetworkSettingActivity() {
        Intent intent = new Intent(this, NetworkSettingsActivity.class);
        startActivity(intent);
    }

    private void startCalendarActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra(Event.KEY_DATE, date);
        startActivityForResult(intent, CALENDAR_ACTIVITY_CODE);
    }

    private void startRecordsChartActivity() {
        Intent intent = new Intent(this, RecordsChartActivity.class);
        Log.d("DayTimelineActivity", "startRecordsChartActivity() intent " + intent);
        //intent.putExtra("date", date);
        startActivity(intent);
    }

    private void startRecordsListActivity() {
        Intent intent = new Intent(this, RecordListActivity.class);
        Log.d("DayTimelineActivity", "startRecordsChartActivity() intent " + intent);
        //intent.putExtra("date", date);
        startActivity(intent);
    }

    private void startPresentEmployeesActivity() {
        Intent intent = new Intent(this, PresentEmployeesActivity.class);
        Log.d("DayTimelineActivity", "startPresentEmployeesActivity() intent " + intent);
        startActivity(intent);
    }

    private void startEventsChartActivity() {
        Intent intent = new Intent(this, EventsChartActivity.class);
        Log.d("DayTimelineActivity", "startEventsChartActivity() intent " + intent);
        startActivity(intent);
    }

    private void startColorInfoActivity() {
        Intent intent = new Intent(this, InfoColorActivity.class);
        startActivity(intent);
    }

    private void startSyncSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startLocationSettingsActivity() {
        Intent intent = new Intent(this, LocationSettingsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CALENDAR_ACTIVITY_CODE:
                if (resultCode == RESULT_OK) {
                    changeDate(data.getLongExtra(Event.KEY_DATE, -1));
                    Log.d("DayTimelineActivity", "onActivityResult() date: " + date);
                }
                break;
        }
    }

    @Override
    public void colorChanged() {
        mDataSetObservable.notifyChanged();
    }

    private void loadNetworkSharedPreferences() {
        Log.d(TAG, "loadNetworkSharedPreferences()");
        SharedPreferences settings = getSharedPreferences(AppConsts.PREFS_NAME, Context.MODE_PRIVATE);
        String domain = settings.getString(KEY_DOMAIN, NetworkUtilities.DOMAIN_DEFAULT);
        int port = (settings.getInt(KEY_PORT, NetworkUtilities.PORT_DEFAULT));
        NetworkUtilities.resetDomainAndPort(domain, port);
    }

    private void loadColorSharedPreferences() {
        Log.d("DayTimelineActivity", "loadColorSharedPreferences()");
        SharedPreferences settings = getSharedPreferences(PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        loadEventColors(settings);
        loadRecordColors(settings);
    }

    private void loadEventColors(SharedPreferences settings) {
        ColorUtil.setColor(Event.KOD_PO_ARRIVE_NORMAL, settings.getInt(Event.KOD_PO_ARRIVE_NORMAL,
                getResources().getColor(R.color.COLOR_PRESENT_NORMAL_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_ARRIVE_PRIVATE, settings.getInt(Event.KOD_PO_ARRIVE_PRIVATE,
                getResources().getColor(R.color.COLOR_PRESENT_PRIVATE_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_OTHERS, settings.getInt(Event.KOD_PO_OTHERS,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_SERVICE, settings.getInt(Event.KOD_PO_LEAVE_SERVICE,
                getResources().getColor(R.color.COLOR_ABSENCE_SERVICE_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_LUNCH, settings.getInt(Event.KOD_PO_LEAVE_LUNCH,
                getResources().getColor(R.color.COLOR_ABSENCE_LUNCH_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_SUPPER, settings.getInt(Event.KOD_PO_LEAVE_SUPPER,
                getResources().getColor(R.color.COLOR_ABSENCE_SUPPER_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_MEDIC, settings.getInt(Event.KOD_PO_LEAVE_MEDIC,
                getResources().getColor(R.color.COLOR_ABSENCE_MEDIC_DEFAULT)));
    }

    private void loadRecordColors(SharedPreferences settings) {
        ColorUtil.setColor(Record.TYPE_A, settings.getInt(Record.TYPE_A,
                getResources().getColor(R.color.COLOR_RECORD_A)));
        ColorUtil.setColor(Record.TYPE_I, settings.getInt(Record.TYPE_I,
                getResources().getColor(R.color.COLOR_RECORD_I)));
        ColorUtil.setColor(Record.TYPE_J, settings.getInt(Record.TYPE_J,
                getResources().getColor(R.color.COLOR_RECORD_J)));
        ColorUtil.setColor(Record.TYPE_K, settings.getInt(Record.TYPE_K,
                getResources().getColor(R.color.COLOR_RECORD_K)));
        ColorUtil.setColor(Record.TYPE_O, settings.getInt(Record.TYPE_O,
                getResources().getColor(R.color.COLOR_RECORD_O)));
        ColorUtil.setColor(Record.TYPE_R, settings.getInt(Record.TYPE_R,
                getResources().getColor(R.color.COLOR_RECORD_R)));
        ColorUtil.setColor(Record.TYPE_S, settings.getInt(Record.TYPE_S,
                getResources().getColor(R.color.COLOR_RECORD_S)));
        ColorUtil.setColor(Record.TYPE_V, settings.getInt(Record.TYPE_V,
                getResources().getColor(R.color.COLOR_RECORD_V)));
        ColorUtil.setColor(Record.TYPE_W, settings.getInt(Record.TYPE_W,
                getResources().getColor(R.color.COLOR_RECORD_W)));
    }

    private void saveColorSharedPreferences() {
        Log.d("DayTimelineActivity", "saveColorSharedPreferences()");
        SharedPreferences settings = getSharedPreferences(PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Map<String, Integer> colorsEvents = ColorUtil.getColors();
        for (Map.Entry<String, Integer> entry : colorsEvents.entrySet()) {
            editor.putInt(entry.getKey(), entry.getValue().intValue());
        }
        editor.commit();
    }


    private void changeDate(long date) {
        Log.d(TAG, "changeDate() date " + AppUtil.formatDate(date));
        this.date = date;
        setDateTitle(date);
        if (getSupportLoaderManager().getLoader(LOADER_EVENTS) != null) {
            getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
        }
    }

    @Override
    public void onTaskFinished(Result result) {
        Log.d(TAG, "onTaskFinished()");

    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public Cursor getCursor() {
        return mCursor;
    }
}

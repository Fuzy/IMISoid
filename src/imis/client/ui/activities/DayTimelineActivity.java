package imis.client.ui.activities;

import android.accounts.Account;
import android.content.*;
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
import com.appkilt.client.AppKilt;
import imis.client.*;
import imis.client.asynctasks.GetListOfEmployees;
import imis.client.asynctasks.result.Result;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import imis.client.persistent.RecordManager;
import imis.client.processor.EventsProcessor;
import imis.client.sync.eventssync.EventsSync;
import imis.client.ui.dialogs.ColorPickerDialog;
import imis.client.ui.fragments.DayTimelineBlocksFragment;
import imis.client.ui.fragments.DayTimelineListFragment;

import static imis.client.AppUtil.showAccountNotExistsError;
import static imis.client.persistent.EventManager.EventQuery;


public class DayTimelineActivity extends AsyncActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ColorPickerDialog.OnColorChangedListener {
    private static final String TAG = DayTimelineActivity.class.getSimpleName();

    private long date;
    protected final DataSetObservable mDataSetObservable = new DataSetObservable();
    private volatile Cursor mCursor;
    private BroadcastReceiver minuteTickReceiver;
    private BroadcastReceiver syncResultReceiver;

    private static final int LOADER_EVENTS = 0x02;
    private static final int CALENDAR_ACTIVITY_CODE = 1;

    protected static final String FRAG_LIST = "DayTimelineListFragment",
            FRAG_BLOCKS = "DayTimelineBlocksFragment", KEY_FRAGMENT = "key_fragment",
            KEY_DATE = "key_date";
    private String currentFragment;
    private EventsProcessor processor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // init UI
        setContentView(R.layout.daytimeline);

        changeDate(TimeUtil.todayDateInLong());

        getSupportLoaderManager().initLoader(LOADER_EVENTS, null, this);
        processor = new EventsProcessor(getApplicationContext());

        // delete old data
        deleteOldData();
    }


    private void deleteOldData() {
        long milestone = TimeUtil.getStartDateOfPreviousMonth();
        int countOfEvents = EventManager.deleteEventsOlderThan(this, milestone);
        int countOfRecords = RecordManager.deleteRecordsOlderThan(this, milestone);
//        Log.d(TAG, "deleteOldData() countOfEvents " + countOfEvents + " countOfRecords " + countOfRecords);
    }

    private void setDateTitle(long date) {
        setTitle(TimeUtil.formatDate(date));
    }


    @Override
    public void onStart() {
        super.onStart();
        minuteTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
//                    Log.d(TAG, "onReceive()");
                    if (getSupportLoaderManager().getLoader(LOADER_EVENTS) != null) {
                        getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, DayTimelineActivity.this);
                    }
                    performScroll();
                }
            }
        };
        registerReceiver(minuteTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        syncResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String stats = intent.getStringExtra(EventsSync.KEY_SYNC_RESULT);
//                Log.d(TAG, "onReceive() stats " + stats);
                AppUtil.showInfo(DayTimelineActivity.this, stats);
            }
        };
        registerReceiver(syncResultReceiver, new IntentFilter(AppConsts.SYNC_RESULT_ACTION));
    }

    public void performScroll() {
        DayTimelineBlocksFragment fragment = (DayTimelineBlocksFragment) getSupportFragmentManager().findFragmentByTag(FRAG_BLOCKS);
        if (fragment != null) {
            fragment.scrollTo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppKilt.onUpdateableActivityPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() Events:\n" + EventManager.getAllEvents(getApplicationContext()));
        initFragment();
        AppKilt.onUpdateableActivityResume(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            unregisterReceiver(minuteTickReceiver);
            unregisterReceiver(syncResultReceiver);
        } catch (Exception e) {
        }
    }

    private void initFragment() {
        Log.d(TAG, "initFragment() current " + currentFragment);
        if (currentFragment == null) {
            switchToDayTimelineBlocksFragment();
        } else if (currentFragment.equals(FRAG_LIST)) {
            switchToDayTimelineListFragment();
        } else if (currentFragment.equals(FRAG_BLOCKS)) {
            switchToDayTimelineBlocksFragment();
        }

        if (getSupportLoaderManager().getLoader(LOADER_EVENTS) != null) {
            getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
        }
    }

    private void switchToDayTimelineListFragment() {
        Log.d(TAG, "switchToDayTimelineListFragment()");
        currentFragment = FRAG_LIST;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DayTimelineListFragment listFragment = new DayTimelineListFragment();
        ft.replace(R.id.dayTimeline, listFragment, "DayTimelineListFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void switchToDayTimelineBlocksFragment() {
        Log.d(TAG, "switchToDayTimelineBlocksFragment()");
        currentFragment = FRAG_BLOCKS;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DayTimelineBlocksFragment listFragment = new DayTimelineBlocksFragment();
        ft.replace(R.id.dayTimeline, listFragment, "DayTimelineBlocksFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
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
        if (getSupportLoaderManager().getLoader(LOADER_EVENTS) != null) {
            getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
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
                processAsyncTask();
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
            case R.id.settings:
                startSyncSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void processAsyncTask() {
        String icp = null;
        try {
            icp = AccountUtil.getUserICP(this);
            createTaskFragment(new GetListOfEmployees(this, icp));
        } catch (Exception e) {
            showAccountNotExistsError(getSupportFragmentManager());

        }
    }

    private void performSync() {
        Log.d(TAG, "onOptionsItemSelected sync request");

        Bundle extras = new Bundle();
        extras.putLong(Event.KEY_DATE, date);
        try {
            Account account = AccountUtil.getUserAccount(this);
            ContentResolver.requestSync(account, AppConsts.AUTHORITY1, extras);
        } catch (Exception e) {
            Log.d(TAG, "performSync() showAccountNotExistsError");
            showAccountNotExistsError(getSupportFragmentManager());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader() date " + date);
        switch (i) {
            case LOADER_EVENTS:
                try {
                    String icp = AccountUtil.getUserICP(this);
                    return new CursorLoader(getApplicationContext(), EventQuery.CONTENT_URI, null,
                            EventQuery.SELECTION_DAY_USER_UNDELETED, new String[]{String.valueOf(date), icp}, EventQuery.ORDER_BY_DATE_TIME_ASC);
                } catch (Exception e) {
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
        setCursor(cursor);
        mDataSetObservable.notifyChanged();
    }

    private void startInsertActivity() {
        // Check if user exists
        try {
            AccountUtil.getUserICP(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            AppUtil.showAccountNotExistsError(getSupportFragmentManager());
            return;
        }

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/event.imisoid");
        intent.putExtra(Event.KEY_DATE, date);
        Event event = getLastEvent();
        if (event != null && event.isDruhArrival()) {
            intent.putExtra(AppConsts.ID_ARRIVE, event.get_id());
            intent.putExtra(EventEditorActivity.KEY_ENABLE_ADD_LEAVE, true);
        } else {
            intent.putExtra(EventEditorActivity.KEY_ENABLE_ADD_ARRIVE, true);
        }
        startActivity(intent);
        Log.d(TAG, "startInsertActivity() event " + event);
    }

    public void startEditActivity(int arriveID, int leaveID) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(AppConsts.ID_ARRIVE, arriveID);
        intent.putExtra(AppConsts.ID_LEAVE, leaveID);
        intent.setType("vnd.android.cursor.item/event.imisoid");
        startActivity(intent);
    }

    private void startCalendarActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra(Event.KEY_DATE, date);
        startActivityForResult(intent, CALENDAR_ACTIVITY_CODE);
    }

    private void startRecordsChartActivity() {
        Intent intent = new Intent(this, RecordsChartActivity.class);
        startActivity(intent);
    }

    private void startRecordsListActivity() {
        Intent intent = new Intent(this, RecordsListActivity.class);
        startActivity(intent);
    }

    private void startPresentEmployeesActivity() {
        Intent intent = new Intent(this, PresentEmployeesActivity.class);
        startActivity(intent);
    }

    private void startEventsChartActivity() {
        Intent intent = new Intent(this, EventsChartActivity.class);
        startActivity(intent);
    }

    private void startSyncSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString(KEY_FRAGMENT, currentFragment);
            outState.putLong(KEY_DATE, date);
            Log.d(TAG, "onSaveInstanceState() outState " + outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getString(KEY_FRAGMENT);
            date = savedInstanceState.getLong(KEY_DATE, date);
            changeDate(date);
            Log.d(TAG, "onRestoreInstanceState() savedInstanceState " + savedInstanceState);
        }
    }

    private void changeDate(long date) {
        Log.d(TAG, "changeDate() date " + TimeUtil.formatDate(date));
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

    public synchronized Cursor getCursor() {
        return mCursor;
    }

    private synchronized void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
    }

    private synchronized Event getLastEvent() {
        if (mCursor == null) return null;
        boolean success = mCursor.moveToLast();
        Event event = (success) ? Event.cursorToEvent(mCursor) : null;
        return event;
    }

    public long getDate() {
        return date;
    }

    public EventsProcessor getProcessor() {
        return processor;
    }

}

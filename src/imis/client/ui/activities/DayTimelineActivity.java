package imis.client.ui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.asynctasks.GetListOfEmployees;
import imis.client.authentication.AuthenticationConsts;
import imis.client.model.Block;
import imis.client.model.Event;
import imis.client.model.Record;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import imis.client.processor.DataProcessor;
import imis.client.ui.BlockView;
import imis.client.ui.BlocksLayout;
import imis.client.ui.ColorUtil;
import imis.client.ui.ObservableScrollView;
import imis.client.ui.adapters.EventsArrayAdapter;
import imis.client.ui.dialogs.ColorPickerDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static imis.client.AppConsts.*;
import static imis.client.AppUtil.showAccountNotExistsError;
import static imis.client.AppUtil.showNetworkAccessUnavailable;
import static imis.client.authentication.AuthenticationConsts.ACCOUNT_TYPE;
import static imis.client.authentication.AuthenticationConsts.AUTHORITY;
import static imis.client.persistent.EventManager.EventQuery;

public class DayTimelineActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, AdapterView.OnItemLongClickListener, ColorPickerDialog.OnColorChangedListener {

    private static final String TAG = DayTimelineActivity.class.getSimpleName();
    BroadcastReceiver _broadcastReceiver;
    private AccountManager accountManager;
    private BlocksLayout blocks;
    private ObservableScrollView scroll;
    private List<Block> blockList;
    private EventsArrayAdapter adapter;
    private long date = 1364166000000L;//1364169600000L;

    private static final int LOADER_ID = 0x02;
    private static final int CALENDAR_ACTIVITY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate()");
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        // create account manager
        accountManager = AccountManager.get(this);

        // init UI
        setContentView(R.layout.blocks_content);
        scroll = (ObservableScrollView) findViewById(R.id.blocks_scroll);
        blocks = (BlocksLayout) findViewById(R.id.blocks);
        blocks.setOnItemClickListener(this);
        blocks.setOnItemLongClickListener(this);


        // init adapter and underlying list
        blockList = new ArrayList<>();
        adapter = new EventsArrayAdapter(getApplicationContext(), -1, blockList);
        blocks.setAdapter(adapter);

        // init today date and loader

        //changeDate(1364169600000L); //TODO toto je pro ladici ucely
        Log.d(TAG, "onCreate() date: " + AppUtil.formatDate(date));


        // EventManager.deleteAllEvents(getApplicationContext());
        //Log.d(TAG, "Events:\n" + EventManager.getAllEvents(getApplicationContext()));

        loadNetworkSharedPreferences();
        loadColorSharedPreferences();
    }

    private void setDateTitle(long date) {
        setTitle(AppUtil.formatDate(date));
    }

    @Override
    protected void onStart() {

        super.onStart();
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    blocks.setVisibility(View.GONE);
                    blocks.setVisibility(View.VISIBLE);
                }
            }
        };
        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        Log.d(TAG, "Events:\n" + EventManager.getAllEvents(getApplicationContext()));
        super.onResume();

        setDateTitle(date);
        scroll.post(new Runnable() {
            public void run() {
                //Log.d(TAG, "onResume() scroll.getBottom(): " + scroll.getBottom());
                scroll.scrollTo(0, blocks.getBottom());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("DayTimelineActivity", "onPause()");
        saveColorSharedPreferences();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void refreshListOfEmployees() {
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        try {
            String icp = accountManager.getUserData(accounts[0], AuthenticationConsts.KEY_ICP);
            Log.d(TAG, "refreshListOfEmployees() icp " + icp);
            new GetListOfEmployees(this).execute(icp);//1493913
        } catch (Exception e) {
            showAccountNotExistsError(getApplication());
        }
    }

    private void startInsertActivity() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/event.imisoid");
        intent.putExtra(Event.KEY_DATE, date);
        startActivity(intent);
    }

    private void performSync() {
        Log.d(TAG, "onOptionsItemSelected sync request");
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        Bundle extras = new Bundle();
        extras.putLong(Event.KEY_DATE, date);
        if (accounts.length > 0) {
            Log.d(TAG, "performSync()");
            if (!NetworkUtilities.isOnline(getApplication())) showNetworkAccessUnavailable(getApplication());
            ContentResolver.requestSync(accounts[0], AUTHORITY, extras);
        } else {
            showAccountNotExistsError(getApplication());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader() date " + date);
        return new CursorLoader(getApplicationContext(), EventQuery.CONTENT_URI, EventQuery.PROJECTION_ALL,
                EventQuery.SELECTION_DAY_UNDELETED, new String[]{String.valueOf(date)}, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset()");
        //getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished() rows: " + cursor.getCount() + " positon: " + cursor.getPosition());
        resfreshAdaptersDataList(cursor);
    }

    private void resfreshAdaptersDataList(Cursor cursor) {
        adapter.clear();
        blockList = null;
        blockList = DataProcessor.eventsToMapOfBlocks(cursor);
        adapter.addAll(blockList);
        adapter.notifyDataSetChanged();
        blocks.setVisibility(View.GONE);
        blocks.setVisibility(View.VISIBLE);
        //Log.d(TAG, "onLoadFinished() blockList: " + blockList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlockView block = (BlockView) view;
        int arriveID = block.getArriveId(), leaveID = block.getLeaveId();
        Log.d(TAG, "onItemClick() position: " + position + " id: " + id + " arriveID: " + arriveID
                + " leaveID: " + leaveID);
        startEditActivity(arriveID, leaveID);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("DayTimelineActivity", "onItemLongClick() position: " + position);
        BlockView block = (BlockView) view;
        DialogFragment dialog = new ColorPickerDialog(block.getType());
        dialog.show(getSupportFragmentManager(), "ColorPickerDialog"); //TODO [rpc nejde support verze
        return true;
    }

    private void startEditActivity(int arriveID, int leaveID) {
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

    /*private void showAccountNotExistsError(Context context) {
        Toast toast = Toast.makeText(context, R.string.no_account_set, Toast.LENGTH_LONG);
        toast.show();
    }*/


    @Override
    public void colorChanged() {
        //ColorUtil.setColor_present_normal(color);
        blocks.setVisibility(View.GONE);
        blocks.setVisibility(View.VISIBLE);
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

       /* ColorUtil.setColor(Event.KOD_PO_LEAVE_ILL, settings.getInt(Event.KOD_PO_LEAVE_ILL,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_VAC, settings.getInt(Event.KOD_PO_LEAVE_VAC,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_TREAT, settings.getInt(Event.KOD_PO_LEAVE_TREAT,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_STUDY, settings.getInt(Event.KOD_PO_LEAVE_STUDY,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_LEAVE_REFUND, settings.getInt(Event.KOD_PO_LEAVE_REFUND,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));
        ColorUtil.setColor(Event.KOD_PO_ARRIVE_EMERGENCY, settings.getInt(Event.KOD_PO_ARRIVE_EMERGENCY,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));*/

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
        adapter.setDate(date);
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}

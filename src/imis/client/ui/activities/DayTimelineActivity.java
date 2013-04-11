package imis.client.ui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DialogFragment;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.controller.BlockController;
import imis.client.json.Util;
import imis.client.model.Block;
import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import imis.client.services.RefreshListOfEmployees;
import imis.client.ui.BlockView;
import imis.client.ui.BlocksLayout;
import imis.client.ui.ColorUtil;
import imis.client.ui.ObservableScrollView;
import imis.client.ui.adapters.EventsArrayAdapter;
import imis.client.ui.dialogs.ColorPickerDialog;

import java.util.ArrayList;
import java.util.List;

import static imis.client.AppConsts.*;
import static imis.client.authentication.AuthenticationConsts.ACCOUNT_TYPE;
import static imis.client.authentication.AuthenticationConsts.AUTHORITY;

public class DayTimelineActivity extends NetworkingActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, AdapterView.OnItemLongClickListener, ColorPickerDialog.OnColorChangedListener {

    private static final String TAG = DayTimelineActivity.class.getSimpleName();
    BroadcastReceiver _broadcastReceiver;
    private AccountManager accountManager;
    private BlocksLayout blocks;
    private ObservableScrollView scroll;
    private List<Block> blockList;
    private EventsArrayAdapter adapter;
    private long date = 0L;

    private static final int LOADER_ID = 0x02;
    private static final int CALENDAR_ACTIVITY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

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
        changeDate(1364169600000L); //TODO toto je pro ladici ucely
        Log.d(TAG, "onCreate() date: " + date);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);


        // EventManager.deleteAllEvents(getApplicationContext());
        //Log.d(TAG, "Events:\n" + EventManager.getAllEvents(getApplicationContext()));

        loadNetworkSharedPreferences();
        loadColorSharedPreferences();
    }

    private void setDateTitle(long date) {
        setTitle(Util.formatDate(date));
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
        Log.d(TAG, "onOptionsItemSelected");
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
                startRecordsChartActivity();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshListOfEmployees() {
        new RefreshListOfEmployees(this).execute("1493913");
    }

    private void startInsertActivity() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/event.imisoid");
        intent.putExtra(Event.KEY_DATE, date);
        startActivity(intent);
    }

    private void performSync() {
        Log.d(TAG, "onOptionsItemSelected sync request");
        Account[] acc = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (acc.length > 0) {
            ContentResolver.requestSync(acc[0], AUTHORITY, new Bundle());
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_account_set, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        return new CursorLoader(getApplicationContext(), EventManager.EventQuery.CONTENT_URI, EventManager.EventQuery.PROJECTION_ALL,
                EventManager.EventQuery.SELECTION_DATUM, new String[]{String.valueOf(date)}, null);
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
        blockList = BlockController.eventsToMapOfBlocks(cursor);
        adapter.addAll(blockList);
        adapter.notifyDataSetChanged();
        blocks.setVisibility(View.GONE);
        blocks.setVisibility(View.VISIBLE);
        Log.d(TAG, "onLoadFinished() blockList: " + blockList);
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
        DialogFragment dialog = new ColorPickerDialog(ColorUtil.getColorForType(block.getType()));
        dialog.show(getFragmentManager(), "ColorPickerDialog"); //TODO [rpc nejde support verze
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
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
                }
                break;
        }
    }


    @Override
    public void colorChanged(int color) {
        ColorUtil.setColor_present_normal(color);
        blocks.setVisibility(View.GONE);
        blocks.setVisibility(View.VISIBLE);
    }

    private void loadNetworkSharedPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String domain = settings.getString(KEY_DOMAIN, NetworkUtilities.DOMAIN_DEFAULT);
        int port = (settings.getInt(KEY_PORT, NetworkUtilities.PORT_DEFAULT));
        NetworkUtilities.resetDomainAndPort(domain, port);
    }

    private void loadColorSharedPreferences() {
        //Log.d("DayTimelineActivity", "loadColorSharedPreferences()");
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int color_present_normal = settings.getInt(ColorUtil.KEY_COLOR_PRESENT_NORMAL,
                getResources().getColor(R.color.COLOR_PRESENT_NORMAL_DEFAULT));
        ColorUtil.setColor_present_normal(color_present_normal);
        int color_present_private = settings.getInt(ColorUtil.KEY_COLOR_PRESENT_PRIVATE,
                getResources().getColor(R.color.COLOR_PRESENT_PRIVATE_DEFAULT));
        ColorUtil.setColor_present_private(color_present_private);
        int color_present_others = settings.getInt(ColorUtil.KEY_COLOR_PRESENT_OTHERS,
                getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT));
        ColorUtil.setColor_present_others(color_present_others);
        int color_absence_service = settings.getInt(ColorUtil.KEY_COLOR_ABSENCE_SERVICE,
                getResources().getColor(R.color.COLOR_ABSENCE_SERVICE_DEFAULT));
        ColorUtil.setColor_absence_service(color_absence_service);
        int color_absence_meal = settings.getInt(ColorUtil.KEY_COLOR_ABSENCE_MEAL,
                getResources().getColor(R.color.COLOR_ABSENCE_MEAL_DEFAULT));
        ColorUtil.setColor_absence_meal(color_absence_meal);
        int color_absence_medic = settings.getInt(ColorUtil.KEY_COLOR_ABSENCE_MEDIC,
                getResources().getColor(R.color.COLOR_ABSENCE_MEDIC_DEFAULT));
        ColorUtil.setColor_absence_medic(color_absence_medic);
    }

    private void saveColorSharedPreferences() {
        //Log.d("DayTimelineActivity", "saveColorSharedPreferences()");
        SharedPreferences settings = getSharedPreferences(AppConsts.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ColorUtil.KEY_COLOR_PRESENT_NORMAL, ColorUtil.getColor_present_normal());
        editor.putInt(ColorUtil.KEY_COLOR_PRESENT_PRIVATE, ColorUtil.getColor_present_private());
        editor.putInt(ColorUtil.KEY_COLOR_PRESENT_OTHERS, ColorUtil.getColor_present_others());
        editor.putInt(ColorUtil.KEY_COLOR_ABSENCE_SERVICE, ColorUtil.getColor_absence_service());
        editor.putInt(ColorUtil.KEY_COLOR_ABSENCE_MEAL, ColorUtil.getColor_absence_meal());
        editor.putInt(ColorUtil.KEY_COLOR_ABSENCE_MEDIC, ColorUtil.getColor_absence_medic());
        editor.commit();
    }

    private void changeDate(long date) {
        this.date = date;
        adapter.setDate(date);
    }
}

package imis.client.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import imis.client.R;
import imis.client.authentication.AuthenticationConsts;
import imis.client.json.Util;
import imis.client.persistent.EventManager;
import imis.client.persistent.EventManager.DataQuery;
import imis.client.ui.BlockView;
import imis.client.ui.BlocksLayout;
import imis.client.ui.ObservableScrollView;
import imis.client.ui.adapter.EventsAdapter;
import imis.client.ui.dialogs.ColorPickerDialog;

import static imis.client.json.Util.todayInLong;
import static imis.client.persistent.Consts.URI;

//import imis.client.ui.activity.ActivityConsts;

public class DayTimelineActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, AdapterView.OnItemLongClickListener, ColorPickerDialog.OnColorChangedListener {// extends
    // Activity
    private static final String TAG = DayTimelineActivity.class.getSimpleName();
    private static final String ACCOUNT_TYPE = AuthenticationConsts.ACCOUNT_TYPE;
    private static final String AUTHORITY = AuthenticationConsts.AUTHORITY;
    private String text = "Neni nastaven ucet pro synchronizaci";
    BroadcastReceiver _broadcastReceiver;
    private AccountManager accountManager;
    private BlocksLayout blocks;
    private ObservableScrollView scroll;
    private EventsAdapter adapter;
    private long date = 0;

    private static final int LOADER_ID = 0x02;
    private static final int CALENDAR_ACTIVITY_CODE = 1;
    //private static final int NETWORK_SETTINGS_ACTIVITY_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        // Vytvori manazer uctu
        accountManager = AccountManager.get(this);
        setContentView(R.layout.blocks_content);

        getLoaderManager().initLoader(LOADER_ID, null, this);
        scroll = (ObservableScrollView) findViewById(R.id.blocks_scroll);
        blocks = (BlocksLayout) findViewById(R.id.blocks);
        adapter = new EventsAdapter(getApplicationContext(), null, -1);
        blocks.setAdapter(adapter);
        blocks.setOnItemClickListener(this);
        blocks.setOnItemLongClickListener(this);

        date = todayInLong();

        Log.d(TAG, "onCreate() date: " + date);
        // EventManager.deleteAllEvents(getApplicationContext());
        Log.d(TAG, "Events:\n" + EventManager.getAllEvents(getApplicationContext()));
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
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");

        super.onResume();

        setDateTitle(date);
        scroll.post(new Runnable() {
            public void run() {
                Log.d(TAG, "onResume() scroll.getBottom(): " + scroll.getBottom());
                scroll.scrollTo(0, blocks.getBottom());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Ziska menu z XML zdroje
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startInsertActivity() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/event.imisoid");
        startActivity(intent);
    }

    private void performSync() {
        Log.d(TAG, "onOptionsItemSelected sync request");
        Account[] acc = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (acc.length > 0) {
            ContentResolver.requestSync(acc[0], AUTHORITY, new Bundle());
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Log.d(TAG, "onCreateLoader() date: " + date);
        return new CursorLoader(getApplicationContext(), URI, DataQuery.PROJECTION_ALL,
                DataQuery.SELECTION_DATUM, new String[]{String.valueOf(date)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        Log.d(TAG, "onLoadFinished() rows: " + data.getCount());
        adapter.swapCursor(data);
        //adapter.notifyDataSetInvalidated();
        blocks.setVisibility(View.GONE);
        blocks.setVisibility(View.VISIBLE);
        //blocks.requestLayout();
        //blocks.invalidate();//TODO test
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        Log.d(TAG, "onLoaderReset()");
        adapter.swapCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // TODO ziskat id
        BlockView block = (BlockView) view;
        int arriveID = block.getArriveId(), leaveID = block.getLeaveId();
        Log.d(TAG, "onItemClick() position: " + position + " id: " + id + " arriveID: " + arriveID
                + " leaveID: " + leaveID);
        startEditActivity(arriveID, leaveID);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("DayTimelineActivity", "onItemLongClick() position: " + position);
        //ColorPickerDialog dialog = new ColorPickerDialog(getApplicationContext(), this, 10);
        //showDialog();
        //dialog.show();
        DialogFragment dialog = new ColorPickerDialog();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");//getSupportFragmentManager()
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void startEditActivity(int arriveID, int leaveID) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(ActivityConsts.ID_ARRIVE, arriveID);
        intent.putExtra(ActivityConsts.ID_LEAVE, leaveID);
        intent.setType("vnd.android.cursor.item/event.imisoid");
        startActivity(intent);
        //TODO mazani polozky?
    }

    private void startNetworkSettingActivity() {
        Intent intent = new Intent(this, NetworkSettingsActivity.class);
        startActivity(intent);
        Log.d("DayTimelineActivity", "startNetworkSettingActivity() intent " + intent);
        //startActivityForResult(intent, NETWORK_SETTINGS_ACTIVITY_CODE);
    }

    private void startCalendarActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        Log.d("DayTimelineActivity", "startCalendarActivity() intent " + intent);
        intent.putExtra("date", date);
        startActivityForResult(intent, CALENDAR_ACTIVITY_CODE);
    }

    private void startRecordsChartActivity() {
        Intent intent = new Intent(this, RecordsChartActivity.class);
        Log.d("DayTimelineActivity", "startRecordsChartActivity() intent " + intent);
        //intent.putExtra("date", date);
        startActivity(intent, null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CALENDAR_ACTIVITY_CODE:
                Log.d("DayTimelineActivity", "onActivityResult() CALENDAR_ACTIVITY_CODE");
                if (resultCode == RESULT_OK) {
                    long result = data.getLongExtra("millis", -1);
                    Log.d("DayTimelineActivity", "onActivityResult() result: " + result);
                    date = result;
                    getLoaderManager().restartLoader(LOADER_ID, null, this);
                } else {
                    Log.d("DayTimelineActivity", "onActivityResult() resultCode: " + resultCode);
                }
                break;
           /* case NETWORK_SETTINGS_ACTIVITY_CODE:
                Log.d("DayTimelineActivity", "onActivityResult() NETWORK_SETTINGS_ACTIVITY_CODE");
                if (resultCode == RESULT_OK) {
                    //TODO dat do shared preferences
                    String domain = data.getStringExtra("domain");
                    int port = data.getIntExtra("port", -1);
                    Log.d("DayTimelineActivity", "onActivityResult() domain: " + domain + " port: " + port);
                } else {
                    Log.d("DayTimelineActivity", "onActivityResult() resultCode: " + resultCode);
                }
                break;*/
        }
    }

    @Override
    public void colorChanged(int color) {
        Log.d("DayTimelineActivity", "colorChanged()");
    }


    //TODO zmena polohy

}

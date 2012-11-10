package imis.client.ui.activity;

import imis.client.R;
import imis.client.authentication.Consts;
import static imis.client.persistent.Consts.URI;
import imis.client.model.Event;
import imis.client.persistent.EventManager.DataQuery;
import imis.client.ui.BlockView;
import imis.client.ui.BlocksLayout;
import imis.client.ui.ObservableScrollView;
import imis.client.ui.adapter.EventsAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.app.LoaderManager;

public class DayTimelineActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {// extends
                                                                                                    // Activity
  private static final String TAG = DayTimelineActivity.class.getSimpleName();
  private static final String ACCOUNT_TYPE = Consts.ACCOUNT_TYPE;
  private static final String AUTHORITY = Consts.AUTHORITY;
  private String text = "Neni nastaven ucet pro synchronizaci";
  private AccountManager accountManager;
  private BlocksLayout blocks;
  private ObservableScrollView scroll;
  private EventsAdapter adapter;

  private static final int LOADER_ID = 0x02;

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
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume()");
    super.onResume();
    // getContentResolver().delete(ContentUris.withAppendedId(URI, 1), null,
    // null);
    
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
      // Spusti aktivitu pro pridani noveho ukolu
      startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
      return true;
    case R.id.sync_button:
      Log.d(TAG, "onOptionsItemSelected sync request");
      Account[] acc = accountManager.getAccountsByType(ACCOUNT_TYPE);
      if (acc.length > 0) {
        ContentResolver.requestSync(acc[0], AUTHORITY, new Bundle());
      }
      else {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
      }
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  @Deprecated
  private List<BlockView> blocksFromEvents(List<Event> events) {
    List<BlockView> blocks = new ArrayList<BlockView>();
    final String arrival = "P";
    final String leave = "O";
    // TODO upozornit ze data nejsou v poradku

    String next = arrival;
    @SuppressWarnings("unused")
    boolean error;
    BlockView block = null;
    for (Event event : events) {
      if (event.getDruh().equals(arrival) && next.equals(arrival)) {
        // ocekavany prichod
        block = new BlockView(getApplicationContext());
        next = leave;
        block.setStartTime(event.getCas());
      }
      else if (event.getDruh().equals(leave) && next.equals(leave)) {
        // ocekavany odchod
        next = arrival;
        block.setEndTime(event.getCas());
        blocks.add(block);
      }
      else {
        // TODO nepocita s prvni denni udalost = O
        error = true;// chyba v datech, neni sekvence P,O,P..
      }
    }

    if (block != null && block.getEndTime() == 0) {
      // nastavim end time na akt cas
      block.setEndTime(timeFromEpochMsToDayMs(System.currentTimeMillis()));
      blocks.add(block);
    }

    return blocks;
  }

  /* *//**
   * Time is <0.0-24.0>h
   * 
   * @return Time since midnight.
   */
  /*
   * private long timeFromDayDoubleToDayMs(double time) { final double msInHourD
   * = 60 * 60 * 1000; final long msInHourL = 60 * 60 * 1000;
   * 
   * long m = (long) time; double dec = time % 1;
   * 
   * long thisHourMs = (long) (dec * msInHourD); long todayMs = m * msInHourL +
   * thisHourMs;
   * 
   * return todayMs; }
   */

  /**
   * @param sinceEpoch
   * @return Time since midnight.
   */
  private long timeFromEpochMsToDayMs(long sinceEpoch) {
    Calendar rightNow = Calendar.getInstance();
    // offset to add since we're not UTC
    long offset = rightNow.get(Calendar.ZONE_OFFSET) + rightNow.get(Calendar.DST_OFFSET);
    long sinceMidnight = (rightNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
    return sinceMidnight;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    Log.d(TAG, "onCreateLoader()");
    return new CursorLoader(getApplicationContext(), URI, DataQuery.PROJECTION, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
    Log.d(TAG, "onLoadFinished() rows: " + data.getCount());
    adapter.swapCursor(data);
    blocks.setVisibility(View.GONE);
    blocks.setVisibility(View.VISIBLE);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> arg0) {
    Log.d(TAG, "onLoaderReset()");
    adapter.swapCursor(null);
  }

}

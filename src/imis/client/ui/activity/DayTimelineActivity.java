package imis.client.ui.activity;

import imis.client.R;
import imis.client.authentication.Consts;
import static imis.client.persistent.Consts.URI;
import imis.client.persistent.EventManager;
import imis.client.persistent.EventManager.DataQuery;
import imis.client.ui.BlocksLayout;
import imis.client.ui.ObservableScrollView;
import imis.client.ui.adapter.EventsAdapter;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.app.LoaderManager;

public class DayTimelineActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>,
    OnItemClickListener {// extends
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
    blocks.setOnItemClickListener(this);

    // EventManager.deleteAllEvents(getApplicationContext());
    Log.d(TAG, "Events:\n" + EventManager.getAllEvents(getApplicationContext()));
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume()");
    super.onResume();

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
    }
    else {
      Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
      toast.show();
    }
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
    blocks.setVisibility(View.GONE);// TODO k cemu to je?
    blocks.setVisibility(View.VISIBLE);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> arg0) {
    Log.d(TAG, "onLoaderReset()");
    adapter.swapCursor(null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub
    super.onActivityResult(requestCode, resultCode, data);
    // TODO refresh view
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Log.d(TAG, "onItemClick() position: " + position + " id: " + id);
    startEditActivity(id);
  }

  private void startEditActivity(long id) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.putExtra("id", id);
    intent.setType("vnd.android.cursor.item/event.imisoid");
    startActivity(intent);
  }

}

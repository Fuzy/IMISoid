package imis.client.ui.activity;

import java.util.Arrays;

import imis.client.R;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import static imis.client.model.Util.timeFromEpochMsToDayMs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class EventEditor extends Activity implements OnItemSelectedListener {// View.OnClickListener,
  private static final String TAG = EventEditor.class.getSimpleName();

  // Ulozena data v pripade preruseni aktivity (onSaveInstanceState)
  private static final String ORIG_KOD_PO = "orig_kod_po";
  private static final String ORIG_POZNAMKA = "orig_poznamka";
  private String orig_kod_po;
  private String orig_poznamka;

  // Identifikace aktualniho ukolu
  private Event event = null;
  private long id = -1;//TODO zaroven id odchoyi udalosti

  // Ruzne stavy ve kterych muze aktivita bezet.
  private static final int STATE_EDIT = 0;
  private static final int STATE_INSERT = 1;
  private static final int STATE_VIEWING = 2;

  private int state;

  // UI polozky
  private Spinner spinnerKod_po;
  int selected = 0;
  private String[] kody_po_values;
  private EditText textPoznamka;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_editor);
    Log.d(TAG, "onCreate " + EventManager.getAllEvents(getApplicationContext()));

    final Intent intent = getIntent();

    // Provede nastaveni na zaklade akce o kterou se jedna (insert/edit).
    final String action = intent.getAction();
    if (Intent.ACTION_VIEW.equals(action)) {
      // Editace: nastavi stav a URI ke zdroji.
      state = STATE_VIEWING;
      id = Long.valueOf(intent.getExtras().getLong("id"));
      Log.d(TAG, "onCreate id " + id);
      // Ziska cursor pro pristup k ukolu
      event = EventManager.getEvent(getApplicationContext(), id);
    }
    else if (Intent.ACTION_INSERT.equals(action)) {
      // Vkladani: nastavi stav a vytvori novy vstup ke zdroji dat.
      state = STATE_INSERT;
      event = new Event();
    }
    else {
      Log.e(TAG, "Unknown action, exiting");
      finish();
      return;
    }

    init();
    restorePreviousValues(savedInstanceState);
  }

  private void init() {
    textPoznamka = (EditText) this.findViewById(R.id.edit_poznamka_arrive);
    TimePicker arriveTime = (TimePicker)this.findViewById(R.id.time_arrive);
    arriveTime.setIs24HourView(true);
    TimePicker leaveTime = (TimePicker)this.findViewById(R.id.time_leave);
    leaveTime.setIs24HourView(true);
    prepareSpinner();

  }

  private void prepareSpinner() {
    // Nacte hodnoty kodu dochazky
    kody_po_values = getResources().getStringArray(R.array.kody_po_values);

    // Vyber kodu dochazky
    spinnerKod_po = (Spinner) this.findViewById(R.id.spinner_kod_po_arrive);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.kody_po_desc, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerKod_po.setAdapter(adapter);
    spinnerKod_po.setOnItemSelectedListener(this);
  }

  private void disableChanges() {
    Log.d(TAG, "disableChanges");
    spinnerKod_po.setEnabled(false);

    textPoznamka.setEnabled(false);
    textPoznamka.setInputType(InputType.TYPE_NULL);
    textPoznamka.setFocusable(false);
    textPoznamka.setFocusableInTouchMode(false);
  }

  private void enableChanges() {
    Log.d(TAG, "enableChanges");
    spinnerKod_po.setEnabled(true);

    textPoznamka.setEnabled(true);
    textPoznamka.setInputType(InputType.TYPE_CLASS_TEXT);
    textPoznamka.setFocusable(true);
    textPoznamka.setFocusableInTouchMode(true);
  }

  private void restorePreviousValues(Bundle savedInstanceState) {
    // Ziskani ulozenych vstupu z drivejska
    if (savedInstanceState != null) {
      orig_kod_po = savedInstanceState.getString(ORIG_KOD_PO);
      orig_poznamka = savedInstanceState.getString(ORIG_POZNAMKA);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");

    // Pokud se podarilo ziskat event s daty lze pokracovat dal.
    if (event != null) {

      // Nastavi titulek podle modu, pripadne vyplni pole
      if (state == STATE_VIEWING) {
        setTitle(getText(R.string.title_view));
        populateFields();
        disableChanges();
      }
      else if (state == STATE_INSERT) {
        setTitle(getText(R.string.title_create));
      }

    }
    else {
      // TODO muze nastat?
      setTitle(getText(R.string.error_title));
    }

  }

  @Override
  protected void onPause() {
    super.onPause();
    /*
     * if (isFinishedByUser) { saveEvent(); }
     */
    Log.d(TAG, "onPause" + EventManager.getAllEvents(getApplicationContext()));
  }

  private void populateFields() {
    String kod_po = "";
    String poznamka = "";
    kod_po = event.getKod_po();
    selected = Arrays.asList(kody_po_values).indexOf(kod_po);
    spinnerKod_po.setSelection(selected);
    poznamka = event.getPoznamka();
    textPoznamka.setText(poznamka);
    if (orig_kod_po == null) {
      orig_kod_po = kod_po;
    }
    if (orig_poznamka == null) {
      orig_poznamka = poznamka;
    }
  }

  private void revertEvent() {
    Log.d(TAG, "revertEvent");
    if (event != null) {
      if (state == STATE_EDIT) {
        populateFields();
      }
    }
  }

  private void deleteEvent() {
    // TODO dialog
    Log.d(TAG, "deleteEvent");
    if (event != null) {
      event = null;
      spinnerKod_po.setSelection(0);
      textPoznamka.setText("");
      if (state == STATE_EDIT || state == STATE_VIEWING) {
        EventManager.markEventAsDeleted(getApplicationContext(), id);
      }
    }
    finish();
  }

  private void saveEvent() {
    Log.d(TAG, "saveEvent state " + state);
    if (event != null) {
      // Ulozi aktualizovane hodnoty
      event.setKod_po(kody_po_values[selected]);
      event.setPoznamka(textPoznamka.getText().toString());
      event.setCas(timeFromEpochMsToDayMs());
      if (state == STATE_EDIT) {
        EventManager.updateEvent(getApplicationContext(), event);
      }
      else if (state == STATE_INSERT) {
        event.setDruh(Event.DRUH_ARRIVAL);//TODO
        id = EventManager.addEvent(getApplicationContext(), true, event);
      }

    }

    finish();
  }

  private void makeEventEditable() {
    Log.d(TAG, "makeEventEditable");
    state = STATE_EDIT;
    enableChanges();
    invalidateOptionsMenu();
    // TODO dokoncit
  }

  /*
   * @Override public void onClick(View arg0) { Log.d(TAG, "onClick"); // Pokud
   * uzivatel stiskne tlacitko, aktivita se ukonci // a probehne onPause metoda,
   * kde se data ulozi isFinishedByUser = true; finish(); }
   */

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    Log.d(TAG, "onItemSelected pos: " + pos + " id: " + id);
    selected = pos;
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "onCreateOptionsMenu");
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.editor_options_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    Log.d(TAG, "onPrepareOptionsMenu");

    if (state == STATE_EDIT) {
      setActionsToEditState(menu);

      // Aktivuje/deaktivuje volbu revert podle toho zda se udaje zmenily
      // TODO aktivace/deaktivace jako posluchac stisku klaves
      /*
       * String savedKod_po = event.getKod_po(); String savedPoznamka =
       * event.getPoznamka(); String currentKod_po = kody_po_values[selected];
       * String currentPoznamka = textPoznamka.getText().toString();
       * 
       * if (savedKod_po.equals(currentKod_po) &&
       * savedPoznamka.equals(currentPoznamka)) {
       * menu.findItem(R.id.menu_revert).setEnabled(false); } else {
       * menu.findItem(R.id.menu_revert).setEnabled(true); }
       */
    }
    else if (state == STATE_INSERT) {
      setActionsToInsertState(menu);
    }
    else if (state == STATE_VIEWING) {
      setActionsToViewState(menu);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  private void setActionsToEditState(Menu menu) {
    menu.findItem(R.id.menu_delete).setVisible(true);
    menu.findItem(R.id.menu_edit).setVisible(false);
    menu.findItem(R.id.menu_revert).setVisible(false);
    menu.findItem(R.id.menu_save).setVisible(true);
  }

  private void setActionsToInsertState(Menu menu) {
    menu.findItem(R.id.menu_delete).setVisible(true);
    menu.findItem(R.id.menu_edit).setVisible(false);
    menu.findItem(R.id.menu_revert).setVisible(false);
    menu.findItem(R.id.menu_save).setVisible(true);
  }

  private void setActionsToViewState(Menu menu) {
    menu.findItem(R.id.menu_delete).setVisible(true);
    menu.findItem(R.id.menu_edit).setVisible(true);
    menu.findItem(R.id.menu_revert).setVisible(false);
    menu.findItem(R.id.menu_save).setVisible(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Log.d(TAG, "onOptionsItemSelected");
    // Obslouzi akce z menu
    switch (item.getItemId()) {
    case R.id.menu_save:
      saveEvent();
      break;
    case R.id.menu_delete:
      deleteEvent();
      break;
    case R.id.menu_revert:
      revertEvent();
    case R.id.menu_edit:
      makeEventEditable();
      break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    Log.d(TAG, "onSaveInstanceState");
    outState.putString(orig_kod_po, orig_kod_po);
    outState.putString(orig_poznamka, orig_poznamka);
  }
}

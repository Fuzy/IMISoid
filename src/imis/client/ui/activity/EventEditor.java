package imis.client.ui.activity;

import java.util.Arrays;

import imis.client.R;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import static imis.client.model.Util.timeFromEpochMsToDayMs;
import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Time;
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
  private static final String ORIG_KOD_PO = "orig_kod_po", ORIG_POZNAMKA = "orig_poznamka";
  private String orig_kod_po, orig_poznamka;// TODO to samy pro leave

  // Identifikace aktualniho ukolu
  private Event arriveEvent = null, leaveEvent = null;
  private int arriveId = -1, leaveId = -1;

  // Ruzne stavy ve kterych muze aktivita bezet.
  private static final int STATE_EDIT = 0, STATE_INSERT = 1, STATE_VIEWING = 2;

  private int state;

  // UI polozky
  private Spinner spinnerKod_poArrive, spinnerKod_poLeave;
  int selectedArrive = 0, selectedLeave = 0;
  private String[] kody_po_values;
  private EditText textPoznamkaArrive, textPoznamkaLeave;
  private TimePicker arriveTime, leaveTime;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_editor);
    Log.d(TAG, "onCreate " + EventManager.getAllEvents(getApplicationContext()));

    final Intent intent = getIntent();

    // Provede nastaveni na zaklade akce o kterou se jedna (view/insert).
    final String action = intent.getAction();
    if (Intent.ACTION_VIEW.equals(action)) {
      // Prohlizeni: nacte udalosti
      state = STATE_VIEWING;
      loadEvents(arriveId, leaveId, intent);
    }
    else if (Intent.ACTION_INSERT.equals(action)) {
      // Vkladani: nastavi stav a vytvori novy vstup ke zdroji dat.
      state = STATE_INSERT;
      arriveEvent = new Event();
    }
    else {
      Log.e(TAG, "Unknown action, exiting");
      finish();
      return;
    }

    init();
    restorePreviousValues(savedInstanceState);
  }

  private void loadEvents(int arriveId, int leaveId, Intent intent) {
    arriveId = Integer.valueOf(intent.getExtras().getInt(ActivityConsts.ID_ARRIVE));
    leaveId = Integer.valueOf(intent.getExtras().getInt(ActivityConsts.ID_LEAVE));
    Log.d(TAG, "onCreate arriveId: " + arriveId + " leaveId: " + leaveId);
    // Ziska cursor pro pristup k ukolu
    arriveEvent = EventManager.getEvent(getApplicationContext(), arriveId);
    if (leaveId != -1) {
      leaveEvent = EventManager.getEvent(getApplicationContext(), leaveId);
    }
  }

  private void init() {
    prepareNoteFields();
    prepareTimePickers();
    prepareSpinners();
  }

  private void prepareTimePickers() {
    arriveTime = (TimePicker) this.findViewById(R.id.time_arrive);
    arriveTime.setIs24HourView(true);
    leaveTime = (TimePicker) this.findViewById(R.id.time_leave);
    leaveTime.setIs24HourView(true);
  }

  private void prepareSpinners() {
    // Nacte hodnoty kodu dochazky
    kody_po_values = getResources().getStringArray(R.array.kody_po_values);

    // Vyber kodu dochazky
    spinnerKod_poArrive = (Spinner) this.findViewById(R.id.spinner_kod_po_arrive);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.kody_po_desc, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerKod_poArrive.setAdapter(adapter);
    spinnerKod_poArrive.setOnItemSelectedListener(this);

    // Leave spinner
    spinnerKod_poLeave = (Spinner) this.findViewById(R.id.spinner_kod_po_leave);
    spinnerKod_poLeave.setAdapter(adapter);
    spinnerKod_poLeave.setOnItemSelectedListener(this);
  }

  private void prepareNoteFields() {
    textPoznamkaArrive = (EditText) this.findViewById(R.id.edit_poznamka_arrive);
    textPoznamkaLeave = (EditText) this.findViewById(R.id.edit_poznamka_leave);
  }

  private void disableChanges() {
    Log.d(TAG, "disableChanges");
    spinnerKod_poArrive.setEnabled(false);
    spinnerKod_poLeave.setEnabled(false);
    arriveTime.setEnabled(false);
    leaveTime.setEnabled(false);
    disableNoteField(textPoznamkaArrive);
    disableNoteField(textPoznamkaLeave);

  }

  private void disableNoteField(EditText field) {
    field.setEnabled(false);// TODO prostudovat
    field.setInputType(InputType.TYPE_NULL);
    field.setFocusable(false);
    field.setFocusableInTouchMode(false);
  }

  private void enableChanges() {
    Log.d(TAG, "enableChanges");
    spinnerKod_poArrive.setEnabled(true);
    arriveTime.setEnabled(true);
    leaveTime.setEnabled(true);
    enableNoteField(textPoznamkaArrive);
    enableNoteField(textPoznamkaLeave);

  }

  private void enableNoteField(EditText field) {
    field.setEnabled(true);
    field.setInputType(InputType.TYPE_CLASS_TEXT);
    field.setFocusable(true);
    field.setFocusableInTouchMode(true);
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

    if (state == STATE_VIEWING) {
      setTitle(getText(R.string.title_view));
    }
    else if (state == STATE_INSERT) {
      setTitle(getText(R.string.title_create));
    }

    // Pokud se podarilo ziskat event s daty lze pokracovat dal.
    if (arriveEvent != null) {
      populateArriveFields();
      disableChanges();
    }

    if (leaveEvent != null) {
      populatLeaveFields();
    }

    /*
     * else { // TODO muze nastat? setTitle(getText(R.string.error_title)); }
     */

  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause" + EventManager.getAllEvents(getApplicationContext()));
  }

  private void populateArriveFields() {
    Log.d(TAG, "populateArriveFields event " + arriveEvent);
    String kod_po = "", poznamka = "";
    // Type
    kod_po = arriveEvent.getKod_po();
    selectedArrive = Arrays.asList(kody_po_values).indexOf(kod_po);
    spinnerKod_poArrive.setSelection(selectedArrive);
    // Time
    Time time = new Time();
    time.set(arriveEvent.getCas());
    arriveTime.setCurrentHour(time.hour);
    arriveTime.setCurrentMinute(time.minute);
    // Note
    poznamka = arriveEvent.getPoznamka();
    textPoznamkaArrive.setText(poznamka);
    if (orig_kod_po == null) {
      orig_kod_po = kod_po;
    }
    if (orig_poznamka == null) {
      orig_poznamka = poznamka;
    }
  }

  private void populatLeaveFields() {
    String kod_po = "", poznamka = "";
    // Type
    selectedLeave = Arrays.asList(kody_po_values).indexOf(kod_po);
    spinnerKod_poLeave.setSelection(selectedLeave);
    kod_po = leaveEvent.getKod_po();
    // Time
    Time time = new Time();
    time.set(leaveEvent.getCas());
    leaveTime.setCurrentHour(time.hour);
    leaveTime.setCurrentMinute(time.minute);
    // Note
    poznamka = leaveEvent.getPoznamka();
    textPoznamkaLeave.setText(poznamka);
  }

  private void revertEvent() {
    Log.d(TAG, "revertEvent");
    if (arriveEvent != null && state == STATE_EDIT) {
      populateArriveFields();
    }
  }

  private void deleteEvent() {
    // TODO dialog
    Log.d(TAG, "deleteEvent");
    if (arriveEvent != null) {
      arriveEvent = null;
      leaveEvent = null;
      spinnerKod_poArrive.setSelection(0);
      spinnerKod_poLeave.setSelection(0);
      textPoznamkaArrive.setText("");
      textPoznamkaLeave.setText("");
      if (state == STATE_EDIT || state == STATE_VIEWING) {
        EventManager.markEventAsDeleted(getApplicationContext(), arriveId);
        if (leaveId != -1) {
          EventManager.markEventAsDeleted(getApplicationContext(), leaveId);
        }
      }
    }
    finish();
  }

  private void saveEvent() {
    Log.d(TAG, "saveEvent state " + state);
    if (arriveEvent != null) {
      // Ulozi aktualizovane hodnoty
      arriveEvent.setKod_po(kody_po_values[selectedArrive]);      
      arriveEvent.setCas(getPickerCurrentTimeInMs(arriveTime));
      arriveEvent.setPoznamka(textPoznamkaArrive.getText().toString());
      if (state == STATE_EDIT) {
        EventManager.updateEvent(getApplicationContext(), arriveEvent);
      }
      else if (state == STATE_INSERT) {
        arriveEvent.setDruh(Event.DRUH_ARRIVAL);
        arriveId = EventManager.addEvent(getApplicationContext(), true, arriveEvent);
      }
    }
    if (leaveEvent != null) {
      // Ulozi aktualizovane hodnoty
      leaveEvent.setKod_po(kody_po_values[selectedLeave]); 
      leaveEvent.setCas(getPickerCurrentTimeInMs(leaveTime));
      leaveEvent.setPoznamka(textPoznamkaLeave.getText().toString());
      if (state == STATE_EDIT) {
        EventManager.updateEvent(getApplicationContext(), leaveEvent);
      }
      else if (state == STATE_INSERT) {
        leaveEvent.setDruh(Event.DRUH_LEAVE);
        leaveId = EventManager.addEvent(getApplicationContext(), true, leaveEvent);
      }
    }

    finish();
  }

  private long getPickerCurrentTimeInMs(TimePicker picker) {
    Time time = new Time();
    time.hour = picker.getCurrentHour();
    time.minute = picker.getCurrentMinute();
    return time.toMillis(true);
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

    int spinnerId = ((Spinner) parent).getId();

    switch (spinnerId) {
    case R.id.spinner_kod_po_arrive:
      Log.d(TAG, "onItemSelected pos: " + pos + " id: " + id + " spinner: spinner_kod_po_arrive");
      selectedArrive = pos;
      break;
    case R.id.spinner_kod_po_leave:
      Log.d(TAG, "onItemSelected pos: " + pos + " id: " + id + " spinner: spinnerKod_poLeave");
      selectedLeave = pos;
      break;
    default:
      break;
    }

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

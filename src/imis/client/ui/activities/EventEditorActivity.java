package imis.client.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import imis.client.ui.dialogs.DeleteEventDialog;

import java.util.Arrays;

public class EventEditorActivity extends FragmentActivity implements OnItemSelectedListener,
        View.OnClickListener, DeleteEventDialog.OnDeleteEventListener {
    private static final String TAG = EventEditorActivity.class.getSimpleName();

    // Ulozena data v pripade preruseni aktivity (onSaveInstanceState)
    private static final String ORIG_KOD_PO = "orig_kod_po", ORIG_POZNAMKA = "orig_poznamka";
    private String orig_kod_po, orig_poznamka;// TODO to samy pro leave

    // Actual event
    private Event arriveEvent = null, leaveEvent = null;
    private int arriveId = -1, leaveId = -1;
    private long date;

    // States this activities could be in
    private static final int STATE_EDIT = 0, STATE_INSERT = 1, STATE_VIEWING = 2;

    private int state;

    // UI units
    private Spinner spinnerKod_poArrive, spinnerKod_poLeave;
    int selectedArrive = 0, selectedLeave = 0;
    private String[] kody_po_values;
    private EditText textPoznamkaArrive, textPoznamkaLeave;
    private TimePicker arriveTime, leaveTime;
    private Button leaveBtn;
    private LinearLayout leaveLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_editor);
        final Intent intent = getIntent();
        date = intent.getLongExtra(Event.KEY_DATE, -1);
        Log.d(TAG, "onCreate date : " + date + "  " + EventManager.getAllEvents(getApplicationContext()));


        // Provede nastaveni na zaklade akce o kterou se jedna (view/insert).
        final String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            // Prohlizeni: nacte udalosti
            state = STATE_VIEWING;
            arriveId = Integer.valueOf(intent.getExtras().getInt(ActivityConsts.ID_ARRIVE));
            leaveId = Integer.valueOf(intent.getExtras().getInt(ActivityConsts.ID_LEAVE));
            loadEvents(arriveId, leaveId);
        } else if (Intent.ACTION_INSERT.equals(action)) {
            // Vkladani: nastavi stav a vytvori novy vstup ke zdroji dat.
            state = STATE_INSERT;
            arriveEvent = new Event();
        } else {
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

        init();
        restorePreviousValues(savedInstanceState);
    }

    private void loadEvents(int arriveId, int leaveId) {
        Log.d(TAG, "onCreate arriveId: " + arriveId + " leaveId: " + leaveId);
        // Ziska cursor pro pristup k ukolu
        arriveEvent = EventManager.getEvent(getApplicationContext(), arriveId);
        if (leaveId != -1) {
            leaveEvent = EventManager.getEvent(getApplicationContext(), leaveId);
        }
    }

    private void init() {
        prepareSpinners();
        prepareTimePickers();
        prepareNoteFields();
        leaveLayout = (LinearLayout) findViewById(R.id.leave_layout);
        leaveBtn = (Button) findViewById(R.id.leave_add_btn);
        leaveBtn.setOnClickListener(this);
        // leaveEvent = null;//TODO
        if (leaveEvent == null) {
            Log.d(TAG, "init leaveEvent == null");
            leaveLayout.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "init leaveEvent != null");
            leaveBtn.setVisibility(View.GONE);
        }

    }

    private void prepareTimePickers() {
        arriveTime = (TimePicker) this.findViewById(R.id.time_arrive);
        arriveTime.setIs24HourView(true);
        if (state == STATE_INSERT) {
            setTimePickerToNow(arriveTime);
        }
        leaveTime = (TimePicker) this.findViewById(R.id.time_leave);
        leaveTime.setIs24HourView(true);
    }

    private void setTimePickerToNow(TimePicker timePicker) {
        Log.d("EventEditorActivity", "setTimePickerToNow() " + timePicker.getId());
        Time time = new Time();
        time.setToNow();
        timePicker.setCurrentHour(time.hour);
        timePicker.setCurrentMinute(time.minute);
    }

    private void setTimePickerToTime(TimePicker timePicker, long millis) {
        Log.d("EventEditorActivity", "setTimePickerToNow() " + timePicker.getId());
        Time time = new Time();
        time.set(millis);
        timePicker.setCurrentHour(time.hour);
        timePicker.setCurrentMinute(time.minute);
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
        spinnerKod_poLeave.setEnabled(true);
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
        } else if (state == STATE_INSERT) {
            setTitle(getText(R.string.title_create));
        }

        // Pokud se podarilo ziskat event s daty lze pokracovat dal.
        if (arriveEvent != null) {
            if (state != STATE_INSERT) {
                populateArriveFields();
            }
            if (state == STATE_VIEWING) {
                disableChanges();
            }
        }

        if (leaveEvent != null) {
            populateLeaveFields();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause" + EventManager.getAllEvents(getApplicationContext()));
    }

    private void populateArriveFields() {
        Log.d(TAG, "populateArriveFields event " + arriveEvent);
        // Type
        String kod_po = arriveEvent.getKod_po();
        selectedArrive = Arrays.asList(kody_po_values).indexOf(kod_po);
        spinnerKod_poArrive.setSelection(selectedArrive);
        // Time
        setTimePickerToTime(arriveTime, arriveEvent.getCas());
        // Note
        String poznamka = arriveEvent.getPoznamka();
        textPoznamkaArrive.setText(poznamka);
        if (orig_kod_po == null) {
            orig_kod_po = kod_po;
        }
        if (orig_poznamka == null) {
            orig_poznamka = poznamka;
        }
    }

    private void populateLeaveFields() {
        Log.d(TAG, "populateArriveFields event " + leaveEvent);
        // Type
        String kod_po = leaveEvent.getKod_po();
        selectedLeave = Arrays.asList(kody_po_values).indexOf(kod_po);
        spinnerKod_poLeave.setSelection(selectedLeave);
        // Time
        setTimePickerToTime(leaveTime, leaveEvent.getCas());
        // Note
        String poznamka = leaveEvent.getPoznamka();
        textPoznamkaLeave.setText(poznamka);
    }

    private void revertEvent() {
        Log.d(TAG, "revertEvent");
        if (arriveEvent != null && state == STATE_EDIT) {
            populateArriveFields();
        }
    }

    @Override
    public void deleteEvent(int deleteCode) {
        Log.d(TAG, "deleteEvent() deleteCode " + deleteCode);
        if (deleteCode == -1) return;
        switch (deleteCode) {
            case DeleteEventDialog.DEL_ARRIVE:
                deleteEvent(arriveEvent);
                break;
            case DeleteEventDialog.DEL_LEAVE:
                deleteEvent(leaveEvent);
                break;
            case DeleteEventDialog.DEL_BOTH:
                deleteEvent(arriveEvent);
                deleteEvent(leaveEvent);
                break;
        }
    }

    private void deleteEvent(Event event) {
        if (event != null) {
            if (event.isDirty()) {
                EventManager.deleteEvent(getApplicationContext(), event.get_id());
            } else {
                EventManager.markEventAsDeleted(getApplicationContext(), event.get_id());
            }
        }
        finish();
    }

   /* private void deleteEvent2() {
        // TODO dialog
        Log.d(TAG, "deleteEvent");
        if (arriveEvent != null) {
            spinnerKod_poArrive.setSelection(0);
            spinnerKod_poLeave.setSelection(0);
            textPoznamkaArrive.setText("");
            textPoznamkaLeave.setText("");
            if (state == STATE_EDIT || state == STATE_VIEWING) {
                if (arriveEvent.isDirty()) {
                    EventManager.deleteEvent(getApplicationContext(), arriveId);
                } else {
                    EventManager.markEventAsDeleted(getApplicationContext(), arriveId);
                }
                if (leaveId != -1) {
                    if (arriveEvent.isDirty()) {
                        EventManager.deleteEvent(getApplicationContext(), leaveId);
                    } else {
                        EventManager.markEventAsDeleted(getApplicationContext(), leaveId);
                    }
                }
            }
            //TODO oprava, otestovat
            arriveEvent = null;
            leaveEvent = null;
        }
        finish();
    }*/

    private void saveEvent() {
        Log.d(TAG, "saveEvent()");
        saveArriveEvent();
        saveLeaveeEvent();
        finish();
    }

    private void saveArriveEvent() {
        if (arriveEvent != null) {
            // Ulozi aktualizovane hodnoty
            setImplicitEventValues(arriveEvent);
            arriveEvent.setKod_po(kody_po_values[selectedArrive]);
            arriveEvent.setCas(getPickerCurrentTimeInMs(arriveTime));
            arriveEvent.setPoznamka(textPoznamkaArrive.getText().toString());
            if (state == STATE_EDIT) {
                EventManager.updateEvent(getApplicationContext(), arriveEvent);
            } else if (state == STATE_INSERT) {
                arriveEvent.setDruh(Event.DRUH_ARRIVAL);
                arriveEvent.setDatum(date);
                arriveId = EventManager.addEvent(getApplicationContext(), arriveEvent);
            }
        }
    }

    private void saveLeaveeEvent() {
        if (leaveEvent != null) {
            // Ulozi aktualizovane hodnoty
            setImplicitEventValues(leaveEvent);
            leaveEvent.setKod_po(kody_po_values[selectedLeave]);
            leaveEvent.setCas(getPickerCurrentTimeInMs(leaveTime));
            leaveEvent.setPoznamka(textPoznamkaLeave.getText().toString());
            if (state == STATE_EDIT) {
                EventManager.updateEvent(getApplicationContext(), leaveEvent);
            } else if (state == STATE_INSERT) {
                leaveEvent.setDruh(Event.DRUH_LEAVE);
                leaveEvent.setDatum(date);
                leaveId = EventManager.addEvent(getApplicationContext(), leaveEvent);
            }
        }
    }

    private void setImplicitEventValues(Event event) {
        event.setDirty(true);
        event.setDatum_zmeny(AppUtil.getTodayInLong());
        event.setIcp("123");
        event.setTyp(Event.TYPE_ORIG);
    }


    private long getPickerCurrentTimeInMs(TimePicker picker) {
        Time time = new Time();
        time.hour = picker.getCurrentHour();
        time.minute = picker.getCurrentMinute();
        return time.toMillis(true);
    }

    private void makeEventsEditable() {
        Log.d(TAG, "makeEventsEditable");
        state = STATE_EDIT;
        setTitle(getText(R.string.title_editing));
        enableChanges();
        invalidateOptionsMenu();
        // TODO dokoncit
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        int spinnerId = parent.getId();//((Spinner) parent).getId();

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
        } else if (state == STATE_INSERT) {
            setActionsToInsertState(menu);
        } else if (state == STATE_VIEWING) {
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

        switch (item.getItemId()) {
            case R.id.menu_save:
                saveEvent();
                break;
            case R.id.menu_delete:
                showDeleteDialog();
                break;
            /*case R.id.menu_revert:
                revertEvent();*/
            case R.id.menu_edit:
                makeEventsEditable();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        DialogFragment deleteEventDialog = new DeleteEventDialog();
        deleteEventDialog.show(getSupportFragmentManager(), "DeleteEventDialog");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putString(orig_kod_po, orig_kod_po);
        outState.putString(orig_poznamka, orig_poznamka);
    }

    @Override
    public void onClick(View arg) {
        Log.d(TAG, "onClick");
        leaveBtn.setVisibility(View.GONE);
        leaveLayout.setVisibility(View.VISIBLE);
        leaveEvent = new Event();// TODO test
        setTimePickerToNow(leaveTime);
    }


}

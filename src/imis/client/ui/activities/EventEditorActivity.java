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
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import imis.client.ui.activities.util.ActivityConsts;
import imis.client.ui.dialogs.AddEventDialog;
import imis.client.ui.dialogs.DeleteEventDialog;
import imis.client.widget.ShortcutWidgetProvider;

import java.util.Arrays;
import java.util.Set;

public class EventEditorActivity extends FragmentActivity implements OnItemSelectedListener,
        View.OnClickListener, DeleteEventDialog.OnDeleteEventListener, AddEventDialog.AddEventDialogListener {
    private static final String TAG = EventEditorActivity.class.getSimpleName();

    private static final String KEY_KOD_PO_ARR = "key_kod_po_arr", KEY_KOD_PO_LEA = "key_kod_po_lea";
    private static final String KEY_POZN_ARR = "key_pozn_arr", KEY_POZN_LEA = "key_pozn_lea";
    private String pozn_arr, pozn_lea;
    private int spinner_arr = -1, spinner_lea = -1;
    // TODO instance stav, hodiny

    // Actual event
    private Event arriveEvent = null, leaveEvent = null;
    private int arriveId = -1, leaveId = -1;//, widgetID = -1;
    private long date;
    private boolean widgetIsSource = false;

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
        date = intent.getLongExtra(Event.KEY_DATE, AppUtil.todayInLong());
        widgetIsSource = intent.getBooleanExtra(AppConsts.KEY_WIDGET_IS_SOURCE, false);

//        Log.d(TAG, "onCreate date : " + date + "  " + EventManager.getAllEvents(getApplicationContext()));
        Log.d(TAG, "onCreate() intent " + intent.getAction() + " date: " + date + " date: " + AppUtil.formatAbbrDate(date));

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Set<String> set = extras.keySet();
            Log.d(TAG, "onCreate() extras " + extras.keySet());
            for (String s : set) {
                Log.d(TAG, "onCreate() " + s + ": " + extras.get(s));
            }
        }

        final String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            state = STATE_VIEWING;
        } else if (Intent.ACTION_INSERT.equals(action)) {
            state = STATE_INSERT;
        }

        arriveId = intent.getIntExtra(ActivityConsts.ID_ARRIVE, -1);
        leaveId = intent.getIntExtra(ActivityConsts.ID_LEAVE, -1);
        loadEvents(arriveId, leaveId);//TODO varianta pouze leave

        init();
        if (widgetIsSource) {
            showAddEventDialog(arriveId == -1);
        }
    }

    private void loadEvents(int arriveId, int leaveId) {
        Log.d(TAG, "loadEvents arriveId: " + arriveId + " leaveId: " + leaveId);
        String arriveMsg = null, leaveMsg = null;
        if (arriveId == -1) {
            arriveEvent = new Event();
        } else {
            arriveEvent = EventManager.getEvent(getApplicationContext(), arriveId);
        }
        if (arriveEvent.getMsg() != null) arriveMsg = arriveEvent.getMsg();
        if (leaveId != -1) {
            leaveEvent = EventManager.getEvent(getApplicationContext(), leaveId);
            if (leaveEvent.getMsg() != null) leaveMsg = leaveEvent.getMsg();
        } else if (widgetIsSource && arriveId != -1) {
            leaveEvent = new Event();
        }

        showToastIfErrors(arriveMsg, leaveMsg);
    }

    private void showToastIfErrors(String arriveMsg, String leaveMsg) {
        StringBuilder errMsg = new StringBuilder();
        if (arriveMsg != null) errMsg.append(getString(R.string.title_arrive_err) + arriveMsg + "\n");
        if (leaveMsg != null) errMsg.append(getString(R.string.title_leave_err) + leaveMsg + "\n");
        if (errMsg.length() != 0) Toast.makeText(getApplication(), errMsg, Toast.LENGTH_LONG).show();
    }

    private void showAddEventDialog(boolean isArrive) {
        String title, time, desc = "";
        StringBuilder message = new StringBuilder();

        if (isArrive) {
            title = getString(R.string.add_arrive);
            time = AppUtil.formatTime(arriveEvent.getCas());
            desc = spinnerKod_poArrive.getSelectedItem().toString();
        } else {
            title = getString(R.string.add_leave);
            time = AppUtil.formatTime(leaveEvent.getCas());
            desc = spinnerKod_poLeave.getSelectedItem().toString();

        }
        message.append(getString(R.string.dialog_add_time));
        message.append(time + "\n");
        message.append(getString(R.string.dialog_add_type));
        message.append(desc);

        DialogFragment deleteEventDialog = new AddEventDialog();
        Bundle bundle = new Bundle();
        bundle.putString(AppConsts.KEY_TITLE, title);
        bundle.putString(AppConsts.KEY_MSG, message.toString());
        deleteEventDialog.setArguments(bundle);
        deleteEventDialog.show(getSupportFragmentManager(), "AddEventDialog");
    }

    private void init() {
        prepareSpinners();
        prepareTimePickers();
        prepareNoteFields();
        leaveLayout = (LinearLayout) findViewById(R.id.leave_layout);
        leaveBtn = (Button) findViewById(R.id.leave_add_btn);
        leaveBtn.setOnClickListener(this);
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
        setTimePickerToNow(arriveTime);
        leaveTime = (TimePicker) this.findViewById(R.id.time_leave);
        leaveTime.setIs24HourView(true);
        setTimePickerToNow(leaveTime);
    }

    private void setTimePickerToNow(TimePicker timePicker) {
        Time time = new Time();
        time.setToNow();
        timePicker.setCurrentHour(time.hour);
        timePicker.setCurrentMinute(time.minute);
    }

    private void setTimePickerToTime(TimePicker timePicker, long millis) {
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
        spinnerKod_poArrive.setEnabled(false);
        spinnerKod_poLeave.setEnabled(false);
        arriveTime.setEnabled(false);
        leaveTime.setEnabled(false);
        disableNoteField(textPoznamkaArrive);
        disableNoteField(textPoznamkaLeave);

    }

    private void disableNoteField(EditText field) {
        field.setEnabled(false);
        field.setInputType(InputType.TYPE_NULL);
        field.setFocusable(false);
        field.setFocusableInTouchMode(false);
    }

    private void enableChanges() {
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        if (state == STATE_VIEWING) {
            setTitle(getText(R.string.title_view));
            disableChanges();
        } else if (state == STATE_INSERT) {
            setTitle(getText(R.string.title_create));
        }

        populateArriveFields();
        populateLeaveFields();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause" + EventManager.getAllEvents(getApplicationContext()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        refreshShortcutWidgets();
    }

    private void refreshShortcutWidgets() {
        ShortcutWidgetProvider.updateAllWidgets(this);
    }

    private void populateArriveFields() {
        // Type
        if (spinner_arr != -1) {
            selectedArrive = spinner_arr;
        } else if (arriveEvent != null) {
            selectedArrive = Arrays.asList(kody_po_values).indexOf(arriveEvent.getKod_po());
        }
        spinnerKod_poArrive.setSelection(selectedArrive);

        // Time
        setTimePickerToTime(arriveTime, arriveEvent.getCas());

        // Note
        if (pozn_arr != null) {
            textPoznamkaArrive.setText(pozn_arr);
        } else if (arriveEvent != null) {
            textPoznamkaArrive.setText(arriveEvent.getPoznamka());
        }
    }

    private void populateLeaveFields() {
        Log.d(TAG, "populateArriveFields event " + leaveEvent);
        // Type
        if (spinner_lea != -1) {
            selectedLeave = spinner_lea;
        } else if (leaveEvent != null) {
            selectedLeave = Arrays.asList(kody_po_values).indexOf(leaveEvent.getKod_po());
        }
        spinnerKod_poLeave.setSelection(selectedLeave);

        // Time
        setTimePickerToTime(leaveTime, leaveEvent.getCas());

        // Note
        if (pozn_lea != null) {
            textPoznamkaLeave.setText(pozn_lea);
        } else if (leaveEvent != null) {
            textPoznamkaLeave.setText(leaveEvent.getPoznamka());
        }
    }

    @Override
    public void deleteEvent(int deleteCode) {
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
                EventManager.deleteEventOnId(this, event.get_id());
            } else {
                EventManager.markEventAsDeleted(this, event.get_id());
            }
        }
        finish();
    }

    private void saveEvents() {
        Log.d(TAG, "saveEvents()");
        saveArriveEvent();
        saveLeaveEvent();

    }

    private void saveArriveEvent() {
        if (arriveEvent != null) {
            Log.d(TAG, "saveArriveEvent() arriveEvent != null");
            setImplicitEventValues(arriveEvent);
            arriveEvent.setKod_po(kody_po_values[selectedArrive]);
            arriveEvent.setCas(getPickerCurrentTimeInMs(arriveTime));
            arriveEvent.setPoznamka(textPoznamkaArrive.getText().toString());
            if (state == STATE_EDIT) {
                EventManager.updateEvent(getApplicationContext(), arriveEvent);
            } else if (state == STATE_INSERT && arriveEvent.get_id() == 0) {
                arriveEvent.setDruh(Event.DRUH_ARRIVAL);
                arriveEvent.setDatum(date);
                Log.d(TAG, "saveArriveEvent() vkladam prichod " + arriveEvent);
                arriveId = EventManager.addEvent(getApplicationContext(), arriveEvent);//TODO nepridavat z widgety
            }
        }
    }

    private void saveLeaveEvent() {
        if (leaveEvent != null) {
            Log.d(TAG, "saveLeaveEvent() leaveEvent " + leaveEvent);
            setImplicitEventValues(leaveEvent);
            leaveEvent.setKod_po(kody_po_values[selectedLeave]);
            leaveEvent.setCas(getPickerCurrentTimeInMs(leaveTime));
            leaveEvent.setPoznamka(textPoznamkaLeave.getText().toString());

            if (state == STATE_INSERT || (state == STATE_EDIT && leaveEvent.get_id() == 0)) {
                Log.d(TAG, "saveLeaveEvent() vkladam odchod");//TODO predelat
                leaveEvent.setDruh(Event.DRUH_LEAVE);
                leaveEvent.setDatum(date);
                leaveId = EventManager.addEvent(getApplicationContext(), leaveEvent);
            } else if (state == STATE_EDIT) {
                EventManager.updateEvent(getApplicationContext(), leaveEvent);
            }
        }
    }

    private void setImplicitEventValues(Event event) {
        event.setDirty(true);
        event.setDatum_zmeny(AppUtil.todayInLong());
        event.setTyp(Event.TYPE_ORIG);

        try {
            String kod = AppUtil.getUserUsername(this);
            String icp = AppUtil.getUserICP(this);
            event.setIcp(icp);
            event.setIc_obs(kod);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            AppUtil.showAccountNotExistsError(getSupportFragmentManager());
            finish();
        }

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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.spinner_kod_po_arrive:
                selectedArrive = pos;
                break;
            case R.id.spinner_kod_po_leave:
                selectedLeave = pos;
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
        menu.findItem(R.id.menu_save).setVisible(true);
    }

    private void setActionsToInsertState(Menu menu) {
        menu.findItem(R.id.menu_delete).setVisible(true);
        menu.findItem(R.id.menu_edit).setVisible(false);
        menu.findItem(R.id.menu_save).setVisible(true);
    }

    private void setActionsToViewState(Menu menu) {
        menu.findItem(R.id.menu_delete).setVisible(true);
        menu.findItem(R.id.menu_edit).setVisible(true);
        menu.findItem(R.id.menu_save).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveEvents();
                finish();
                break;
            case R.id.menu_delete:
                showDeleteDialog();
                break;
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
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putInt(KEY_KOD_PO_ARR, spinnerKod_poArrive.getSelectedItemPosition());
            outState.putInt(KEY_KOD_PO_LEA, spinnerKod_poLeave.getSelectedItemPosition());
            outState.putString(KEY_POZN_ARR, textPoznamkaArrive.getText().toString());
            outState.putString(KEY_POZN_LEA, textPoznamkaLeave.getText().toString());
            Log.d(TAG, "onSaveInstanceState() savedInstanceState " + outState.keySet());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, "onRestoreInstanceState() savedInstanceState " + savedInstanceState.keySet());
            spinner_arr = savedInstanceState.getInt(KEY_KOD_PO_ARR);
            spinner_lea = savedInstanceState.getInt(KEY_KOD_PO_LEA);
            pozn_arr = savedInstanceState.getString(KEY_POZN_ARR);
            pozn_lea = savedInstanceState.getString(KEY_POZN_LEA);
        }
    }

    @Override
    public void onClick(View arg) {
        Log.d(TAG, "onClick");
        leaveBtn.setVisibility(View.GONE);
        leaveLayout.setVisibility(View.VISIBLE);
        leaveEvent = new Event();
    }

    @Override
    public void onAddEventDialogPositiveClick() {
        Log.d(TAG, "onConfirmClickPositiveClick()");
        saveEvents();
        finish();
    }

    @Override
    public void onAddEventDialogNegativeClick() {
        Log.d(TAG, "onAddEventDialogNegativeClick()");
        finish();
    }

    @Override
    public void onAddEventDialogNeutralClick() {
        Log.d(TAG, "onAddEventDialogNeutralClick()");
    }
}

package imis.client.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import imis.client.*;
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

    public static final String KEY_ENABLE_ADD_ARRIVE = "key_enable_add_arrive",
            KEY_ENABLE_ADD_LEAVE = "key_enable_add_leave";

    // Activity state
    private static final String KEY_KOD_PO_ARR = "key_kod_po_arr", KEY_KOD_PO_LEA = "key_kod_po_lea",
            KEY_POZN_ARR = "key_pozn_arr", KEY_POZN_LEA = "key_pozn_lea",
            KEY_TIME_ARR = "key_time_arr", KEY_TIME_LEA = "key_time_lea";
    private String poznArr, poznLea;
    private int spinnerArr = -1, spinnerLea = -1;
    private long timeArr = -1, timeLea = -1;

    // Actual event
    private Event origArriveEvent = null, arriveEvent = null, origLeaveEvent = null, leaveEvent = null;
    private int arriveId = -1, leaveId = -1;
    private long date;
    private boolean widgetIsSource = false;

    // UI units
    private Spinner spinnerKod_poArrive, spinnerKod_poLeave;
    int selectedArrive = 0, selectedLeave = 0;
    private String[] kody_po_values;
    private EditText textPoznamkaArrive, textPoznamkaLeave;
    private TimePicker arriveTime, leaveTime;
    private Button arriveBtn, leaveBtn;
    private LinearLayout arriveLayout, leaveLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_editor);
        final Intent intent = getIntent();
        date = intent.getLongExtra(Event.KEY_DATE, TimeUtil.todayInLong());
        widgetIsSource = intent.getBooleanExtra(AppConsts.KEY_WIDGET_IS_SOURCE, false);

//        Log.d(TAG, "onCreate date : " + date + "  " + EventManager.getAllEvents(getApplicationContext()));
        Log.d(TAG, "onCreate() intent " + intent.getAction() + " date: " + date + " date: " + TimeUtil.formatAbbrDate(date));

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Set<String> set = extras.keySet();
            Log.d(TAG, "onCreate() extras " + extras.keySet());
            for (String s : set) {
                Log.d(TAG, "onCreate() " + s + ": " + extras.get(s));
            }
        }

        arriveId = intent.getIntExtra(ActivityConsts.ID_ARRIVE, -1);
        leaveId = intent.getIntExtra(ActivityConsts.ID_LEAVE, -1);
        loadEvents(arriveId, leaveId);
        showToastIfErrors();

        init();
        boolean enableAddArrive = intent.getBooleanExtra(KEY_ENABLE_ADD_ARRIVE, false);
        if (enableAddArrive) enableAddArrive();
        boolean enableAddLeave = intent.getBooleanExtra(KEY_ENABLE_ADD_LEAVE, false);
        if (enableAddLeave) enableAddLeave();
        Log.d(TAG, "onCreate() enableAddArrive " + enableAddArrive + " enableAddLeave " + enableAddLeave);

        if (widgetIsSource) {
            showAddEventDialog(arriveId == -1);
        }
    }

    private void loadEvents(int arriveId, int leaveId) {
        Log.d(TAG, "loadEvents arriveId: " + arriveId + " leaveId: " + leaveId);
        if (arriveId != -1) {
            arriveEvent = EventManager.getEvent(getApplicationContext(), arriveId);
            origArriveEvent = new Event(arriveEvent);
        }
        if (leaveId != -1) {
            leaveEvent = EventManager.getEvent(getApplicationContext(), leaveId);
            origLeaveEvent = new Event(leaveEvent);
        }
    }

    private void showToastIfErrors() {
        StringBuilder errMsg = new StringBuilder();
        if (arriveEvent != null && arriveEvent.getMsg() != null)
            errMsg.append(getString(R.string.title_arrive_err) + arriveEvent.getMsg() + "\n");
        if (leaveEvent != null && leaveEvent.getMsg() != null)
            errMsg.append(getString(R.string.title_leave_err) + leaveEvent.getMsg() + "\n");
        if (errMsg.length() != 0) Toast.makeText(getApplication(), errMsg, Toast.LENGTH_LONG).show();
    }

    private void showAddEventDialog(boolean isArrive) {
        String title, time, desc;
        StringBuilder message = new StringBuilder();

        if (isArrive) {
            title = getString(R.string.add_arrive);
            time = TimeUtil.formatTime(arriveEvent.getCas());
            desc = spinnerKod_poArrive.getSelectedItem().toString();
        } else {
            title = getString(R.string.add_leave);
            time = TimeUtil.formatTime(leaveEvent.getCas());
            desc = spinnerKod_poLeave.getSelectedItem().toString();

        }
        message.append(getString(R.string.dialog_add_time) + time + "\n");
        message.append(getString(R.string.dialog_add_type) + desc);

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
        arriveLayout = (LinearLayout) findViewById(R.id.arrive_layout);
        leaveLayout = (LinearLayout) findViewById(R.id.leave_layout);
        arriveBtn = (Button) findViewById(R.id.arrive_add_btn);
        arriveBtn.setOnClickListener(this);
        leaveBtn = (Button) findViewById(R.id.leave_add_btn);
        leaveBtn.setOnClickListener(this);
        if (arriveEvent == null) {
            Log.d(TAG, "init leaveEvent == null");
            arriveLayout.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "init leaveEvent != null");
            arriveBtn.setVisibility(View.GONE);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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
        new ShortcutWidgetProvider().updateAllWidgets(this);
    }

    private void populateArriveFields() {
        // Type
        if (spinnerArr != -1) {
            selectedArrive = spinnerArr;
        } else if (arriveEvent != null) {
            selectedArrive = Arrays.asList(kody_po_values).indexOf(arriveEvent.getKod_po());
        }
        spinnerKod_poArrive.setSelection(selectedArrive);

        // Time
        if (timeArr != -1) {
            setTimePickerToTime(arriveTime, timeArr);
        } else if (arriveEvent != null) {
            setTimePickerToTime(arriveTime, arriveEvent.getCas());
        }

        // Note
        if (poznArr != null) {
            textPoznamkaArrive.setText(poznArr);
        } else if (arriveEvent != null) {
            textPoznamkaArrive.setText(arriveEvent.getPoznamka());
        }
    }

    private void populateLeaveFields() {
        // Type
        if (spinnerLea != -1) {
            selectedLeave = spinnerLea;
        } else if (leaveEvent != null) {
            selectedLeave = Arrays.asList(kody_po_values).indexOf(leaveEvent.getKod_po());
        }
        spinnerKod_poLeave.setSelection(selectedLeave);

        // Time
        if (timeLea != -1) {
            setTimePickerToTime(leaveTime, timeLea);
        } else if (leaveEvent != null) {
            setTimePickerToTime(leaveTime, leaveEvent.getCas());
        }

        // Note
        if (poznLea != null) {
            textPoznamkaLeave.setText(poznLea);
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
        finish();
    }

    private void deleteEvent(Event event) {
        if (event != null) {
            if (event.isDirty()) {
                EventManager.deleteEventOnId(this, event.get_id());
            } else {
                EventManager.markEventAsDeleted(this, event.get_id());
            }
        }
    }

    private void saveEvents() {
        Log.d(TAG, "saveEvents()");
        saveArriveEvent();
        saveLeaveEvent();
    }

    private void saveArriveEvent() {
        if (arriveEvent != null) {
            actualizeArriveEvent();

            if (origArriveEvent == null || !arriveEvent.equals(origArriveEvent)) {
                Log.d(TAG, "saveArriveEvent() ukladam");

                if (arriveEvent.get_id() == 0) {
                    Log.d(TAG, "saveArriveEvent() new");
                    setImplicitEventValues(arriveEvent);
                    arriveEvent.setDruh(Event.DRUH_ARRIVAL);
                    arriveId = EventManager.addEvent(getApplicationContext(), arriveEvent);
                } else {
                    Log.d(TAG, "saveArriveEvent() update");
                    EventManager.updateEvent(getApplicationContext(), arriveEvent);
                }
            }
        }
    }

    private void actualizeArriveEvent() {
        arriveEvent.setKod_po(kody_po_values[selectedArrive]);
        arriveEvent.setCas(getPickerCurrentTimeInMs(arriveTime));
        arriveEvent.setPoznamka(textPoznamkaArrive.getText().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void saveLeaveEvent() {
        if (leaveEvent != null) {
            actualizeLeaveEvent();

            if (origLeaveEvent == null || !leaveEvent.equals(origLeaveEvent)) {
                Log.d(TAG, "saveLeaveEvent() ukladam");

                if (leaveEvent.get_id() == 0) {
                    Log.d(TAG, "saveLeaveEvent() new");
                    setImplicitEventValues(leaveEvent);
                    leaveEvent.setDruh(Event.DRUH_LEAVE);
                    leaveId = EventManager.addEvent(getApplicationContext(), leaveEvent);
                } else {
                    Log.d(TAG, "saveLeaveEvent() update");
                    EventManager.updateEvent(getApplicationContext(), leaveEvent);
                }
            }
        }
    }

    private void actualizeLeaveEvent() {
        leaveEvent.setKod_po(kody_po_values[selectedLeave]);
        leaveEvent.setCas(getPickerCurrentTimeInMs(leaveTime));
        leaveEvent.setPoznamka(textPoznamkaLeave.getText().toString());
    }

    private void setImplicitEventValues(Event event) {
        event.setDirty(true);
        event.setDatum_zmeny(TimeUtil.todayInLong());
        event.setTyp(Event.TYPE_ORIG);
        event.setDatum(date);
        try {
            String kod = AccountUtil.getUserUsername(this);
            String icp = AccountUtil.getUserICP(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveEvents();
                finish();
                break;
            case R.id.menu_delete:
                showDeleteDialog();
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
            outState.putLong(KEY_TIME_ARR, getPickerCurrentTimeInMs(arriveTime));
            outState.putLong(KEY_TIME_LEA, getPickerCurrentTimeInMs(leaveTime));
            Log.d(TAG, "onSaveInstanceState() savedInstanceState " + outState.keySet());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            spinnerArr = savedInstanceState.getInt(KEY_KOD_PO_ARR);
            spinnerLea = savedInstanceState.getInt(KEY_KOD_PO_LEA);
            poznArr = savedInstanceState.getString(KEY_POZN_ARR);
            poznLea = savedInstanceState.getString(KEY_POZN_LEA);
            timeArr = savedInstanceState.getLong(KEY_TIME_ARR);
            timeLea = savedInstanceState.getLong(KEY_TIME_LEA);
            Log.d(TAG, "onRestoreInstanceState() savedInstanceState " + savedInstanceState.keySet());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == arriveBtn.getId()) {
            enableAddArrive();
        } else if (view.getId() == leaveBtn.getId()) {
            enableAddLeave();
        }
    }

    private void enableAddArrive() {
        arriveBtn.setVisibility(View.GONE);
        arriveLayout.setVisibility(View.VISIBLE);
        arriveEvent = new Event();
    }

    private void enableAddLeave() {
        leaveBtn.setVisibility(View.GONE);
        leaveLayout.setVisibility(View.VISIBLE);
        leaveEvent = new Event();
    }

    @Override
    public void onAddEventDialogPositiveClick() {
        saveEvents();
        finish();
    }

    @Override
    public void onAddEventDialogNegativeClick() {
        finish();
    }

    @Override
    public void onAddEventDialogNeutralClick() {
    }
}

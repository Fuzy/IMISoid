package imis.client.ui.fragments;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import imis.client.AccountUtil;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.services.AttendanceGuardService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.5.13
 * Time: 22:28
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static Map<String, String> eventsFreq = new HashMap<>();
    private static Map<String, String> widgetsFreq = new HashMap<>();
    private static Map<String, String> employeesFreq = new HashMap<>();
    private static Map<String, String> networkType = new HashMap<>();
    private static Map<String, String> notificationDelay = new HashMap<>();
    private static String KEY_PREF_NETWORK_TYPE;
    private static String KEY_PREF_NETWORK_URL;
    private static String KEY_PREF_SYNC_EVENTS;
    private static String KEY_PREF_SYNC_WIDGETS;
    private static String KEY_PREF_SYNC_EMPLOYEES;
    private static String KEY_PREF_NOTIFI_ARRIVE;
    private static String KEY_PREF_NOTIFI_LEAVE;
    private static String KEY_PREF_NOTIFI_FREQ;
    private static String KEY_PREF_LOCATION;
    private static String[] KEYS_WITH_SUMMARIES;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);

        //load resources
        loadKeys();
        populateMaps();

        // init
        initSummaries();
        setListeners();
    }

    private void setListeners() {
        Preference pref = findPreference(KEY_PREF_NETWORK_URL);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(preference.getIntent(), 1234);
                return true;
            }
        });
        pref = findPreference(KEY_PREF_LOCATION);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(preference.getIntent(), 1235);
                return true;
            }
        });
    }

    private void loadKeys() {
        KEY_PREF_NETWORK_TYPE = getString(R.string.prefSyncOnNetworkType);
        KEY_PREF_NETWORK_URL = getString(R.string.prefNetworkUrl);
        KEY_PREF_SYNC_EVENTS = getString(R.string.prefSyncEventsFrequency);
        KEY_PREF_SYNC_WIDGETS = getString(R.string.prefSyncWidgetsFrequency);
        KEY_PREF_SYNC_EMPLOYEES = getString(R.string.prefSyncEmployeesFrequency);
        KEY_PREF_NOTIFI_ARRIVE = getString(R.string.prefNotificationArrive);
        KEY_PREF_NOTIFI_LEAVE = getString(R.string.prefNotificationLeave);
        KEY_PREF_NOTIFI_FREQ = getString(R.string.prefNotificationFrequency);
        KEY_PREF_LOCATION = getString(R.string.prefLocation);
        KEYS_WITH_SUMMARIES = new String[]{KEY_PREF_NETWORK_TYPE, KEY_PREF_NETWORK_URL, KEY_PREF_SYNC_EVENTS,
                KEY_PREF_SYNC_WIDGETS, KEY_PREF_SYNC_EMPLOYEES, KEY_PREF_NOTIFI_FREQ, KEY_PREF_LOCATION};
    }

    private void populateMaps() {
        String[] eventsFreqAr = getResources().getStringArray(R.array.syncEventsFrequency);
        String[] eventsFreqValuesAr = getResources().getStringArray(R.array.syncEventsFrequencyValues);
        populateMap(eventsFreqValuesAr, eventsFreqAr, eventsFreq);
        String[] widgetsFreqAr = getResources().getStringArray(R.array.prefSyncWidgetsFrequency);
        String[] widgetsFreqValuesAr = getResources().getStringArray(R.array.prefSyncWidgetsFrequencyValues);
        populateMap(widgetsFreqValuesAr, widgetsFreqAr, widgetsFreq);
        String[] employeesFreqAr = getResources().getStringArray(R.array.prefSyncEmployeesFrequency);
        String[] employeesFreqValuesAr = getResources().getStringArray(R.array.prefSyncEmployeesFrequencyValues);
        populateMap(employeesFreqValuesAr, employeesFreqAr, employeesFreq);
        String[] networkTypeAr = getResources().getStringArray(R.array.prefsyncNetworkType);
        String[] networkTypeValuesAr = getResources().getStringArray(R.array.prefsyncNetworkTypeValues);
        populateMap(networkTypeValuesAr, networkTypeAr, networkType);
        String[] notification = getResources().getStringArray(R.array.prefNotificationDelay);
        String[] notificationValues = getResources().getStringArray(R.array.prefNotificationDelayValues);
        populateMap(notificationValues, notification, notificationDelay);
    }

    private void populateMap(String[] keys, String[] values, Map<String, String> map) {
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
    }

    private void initSummaries() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Map<String, ?> all = sharedPref.getAll();
        for (String s : all.keySet()) {
            if (Arrays.asList(KEYS_WITH_SUMMARIES).contains(s)) {
                setSummary(sharedPref, s);
            }
        }
        Log.d(TAG, "initSummaries() all " + all);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        applySettings();
    }

    private void applySettings() {
        Log.d(TAG, "applySettings()");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String value = settings.getString(KEY_PREF_SYNC_EVENTS, "0");
        int periodEvents = Integer.valueOf(value);
        value = settings.getString(KEY_PREF_SYNC_WIDGETS, "0");
        int periodWidgets = Integer.valueOf(value);
        value = settings.getString(KEY_PREF_SYNC_EMPLOYEES, "0");
        int periodEmployees = Integer.valueOf(value);
        applySyncSetting(periodEvents, periodWidgets, periodEmployees);
        boolean notifyArrive = settings.getBoolean(KEY_PREF_NOTIFI_ARRIVE, false);
        boolean notifyLeave = settings.getBoolean(KEY_PREF_NOTIFI_LEAVE, false);
        value = settings.getString(KEY_PREF_NOTIFI_FREQ, "60");
        int periodNotification = Integer.valueOf(value);
        applyNotificationSetting(notifyArrive, notifyLeave, periodNotification);
    }

    private void applySyncSetting(int periodEvents, int periodWidgets, int periodEmployees) {
        Log.d(TAG, "applySyncSetting()" + "periodEvents = [" + periodEvents + "], periodWidgets = ["
                + periodWidgets + "], periodEmployees = [" + periodEmployees + "]");
        try {
            Account account = AccountUtil.getUserAccount(getActivity());

            if (periodEvents != 0) {
                ContentResolver.addPeriodicSync(account, AppConsts.AUTHORITY1, new Bundle(), periodEvents);
            } else {
                ContentResolver.removePeriodicSync(account, AppConsts.AUTHORITY1, new Bundle());
            }
            if (periodWidgets != 0) {
                ContentResolver.addPeriodicSync(account, AppConsts.AUTHORITY2, new Bundle(), periodWidgets);
            } else {
                ContentResolver.removePeriodicSync(account, AppConsts.AUTHORITY2, new Bundle());
            }
            if (periodEmployees != 0) {
                ContentResolver.addPeriodicSync(account, AppConsts.AUTHORITY3, new Bundle(), periodEmployees);
            } else {
                ContentResolver.removePeriodicSync(account, AppConsts.AUTHORITY3, new Bundle());
            }
        } catch (Exception e) {
            AppUtil.showError(getActivity(), getString(R.string.no_account_set_prev));
        }
    }

    private void applyNotificationSetting(boolean notifyArrive, boolean notifyLeave, int periodNotification) {
        Log.d(TAG, "applyNotificationSetting()" + "notifyArrive = [" + notifyArrive + "], notifyLeave = [" + notifyLeave + "], periodNotification = [" + periodNotification + "]");
        AttendanceGuardService.cancelAllIntents(getActivity());
        AttendanceGuardService.startAttendanceCheck(getActivity(), notifyArrive, notifyLeave, periodNotification);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Arrays.asList(KEYS_WITH_SUMMARIES).contains(key)) {
            setSummary(sharedPreferences, key);
        }
    }

    private void setSummary(SharedPreferences sharedPref, String key) {
        Preference connectionPref = findPreference(key);
        String summary = null;

        if (key.equals(KEY_PREF_NETWORK_TYPE)) {
            summary = networkType.get(sharedPref.getString(key, ""));
        } else if (key.equals(KEY_PREF_SYNC_EVENTS)) {
            summary = eventsFreq.get(sharedPref.getString(key, ""));
        } else if (key.equals(KEY_PREF_SYNC_WIDGETS)) {
            summary = widgetsFreq.get(sharedPref.getString(key, ""));
        } else if (key.equals(KEY_PREF_NOTIFI_FREQ)) {
            summary = notificationDelay.get(sharedPref.getString(key, ""));
        } else if (key.equals(KEY_PREF_SYNC_EMPLOYEES)) {
            summary = employeesFreq.get(sharedPref.getString(key, ""));
        } else if (key.equals(KEY_PREF_NETWORK_URL)) {
            summary = sharedPref.getString(key, "");
        } else if (key.equals(KEY_PREF_LOCATION)) {
            summary = getString(R.string.location_saved);
        }
        Log.d(TAG, "setSummary() key " + key + " summary " + summary);
        connectionPref.setSummary(summary);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initSummaries();
    }
}

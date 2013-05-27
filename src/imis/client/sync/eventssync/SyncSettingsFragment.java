package imis.client.sync.eventssync;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import imis.client.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.5.13
 * Time: 22:28
 */
public class SyncSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SyncSettingsFragment.class.getSimpleName();

    private static Map<String, String> eventsFreq = new HashMap<>();
    private static Map<String, String> widgetsFreq = new HashMap<>();
    private static Map<String, String> networkType = new HashMap<>();
    private static String KEY_PREF_NETWORK_TYPE;
    private static String KEY_PREF_SYNC_EVENTS;
    private static String KEY_PREF_SYNC_WIDGETS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the preferences from an XML resource
        addPreferencesFromResource(R.xml.sync_prefs);

        //load resources
        loadKeys();
        populateMaps();

        // set all summaries
        initSummaries();
    }

    private void loadKeys() {
        KEY_PREF_NETWORK_TYPE = getResources().getString(R.string.prefSyncOnNetworkType);
        KEY_PREF_SYNC_EVENTS = getResources().getString(R.string.prefSyncEventsFrequency);
        KEY_PREF_SYNC_WIDGETS = getResources().getString(R.string.prefSyncWidgetsFrequency);
    }

    private void populateMaps() {
        String[] eventsFreqAr = getResources().getStringArray(R.array.syncEventsFrequency);
        String[] eventsFreqValuesAr = getResources().getStringArray(R.array.syncEventsFrequencyValues);
        populateMap(eventsFreqValuesAr, eventsFreqAr, eventsFreq);
        String[] widgetsFreqAr = getResources().getStringArray(R.array.prefSyncWidgetsFrequency);
        String[] widgetsFreqValuesAr = getResources().getStringArray(R.array.prefSyncWidgetsFrequencyValues);
        populateMap(widgetsFreqValuesAr, widgetsFreqAr, widgetsFreq);
        String[] networkTypeAr = getResources().getStringArray(R.array.prefsyncNetworkType);
        String[] networkTypeValuesAr = getResources().getStringArray(R.array.prefsyncNetworkTypeValues);
        populateMap(networkTypeValuesAr, networkTypeAr, networkType);
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
            setSummary(sharedPref, s);
        }
        Log.d(TAG, "onCreate() all " + all);
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
        //TODO aplikovat zmeny nastaveni
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged()" + "key = [" + key + "]");
        setSummary(sharedPreferences, key);
    }

    private void setSummary(SharedPreferences sharedPref, String key) {
        Preference connectionPref = findPreference(key);
        String value = sharedPref.getString(key, "");
        String summary = null;
        if (key.equals(KEY_PREF_NETWORK_TYPE)) {
            summary = networkType.get(value);
        } else if (key.equals(KEY_PREF_SYNC_EVENTS)) {
            summary = eventsFreq.get(value);
        } else if (key.equals(KEY_PREF_SYNC_WIDGETS)) {
            summary = widgetsFreq.get(value);
        }
        connectionPref.setSummary(summary);
    }
}

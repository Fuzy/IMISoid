package imis.client.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import imis.client.R;
import imis.client.exceptions.PositionNotSetException;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import imis.client.ui.activities.util.Notifications;

import static imis.client.AppConsts.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 12.6.13
 * Time: 20:39
 */
public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    private Context context;
    private Bundle bundle;
    private LocationManager locationManager;
    private LatLng workplace, current;
    private double radius;
    private final double MULTIPLIER = 0.00001;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        this.bundle = intent.getExtras();
        listenForLocation();
        return START_NOT_STICKY;
    }

    private void listenForLocation() {
        try {
            loadWorkplaceSetting();
        } catch (PositionNotSetException e) {
            e.printStackTrace();
            Log.d(TAG, "listenForLocation() PositionNotSetException");
            //TODO return a err notifikace
        }
        loadCurrentPosition();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged()");
            current = new LatLng(location.getLatitude(), location.getLongitude());
            locationManager.removeUpdates(locationListener);
            notifyIfEventMissing();
            //TODO jsem nebo nejsem na pracovisti
            stopSelf();
            //TODO cekat ne lepsi odhad presnost
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private void notifyIfEventMissing() {
        Log.d(TAG, "notifyIfEventMissing()");
        Event event = EventManager.getLastEvent(getApplicationContext());
        if (event == null) {
            Log.d(TAG, "notifyIfEventMissing() event " + event);
            return;
        }

        boolean onWorkplace = isCurrentPositionOnWorkplace();

        if (bundle.getBoolean(AttendanceGuardService.ARRIVE)) {
            // check for missing arrive event
            if (onWorkplace && event.isDruhLeave()) Notifications.showMissingArriveNotification(context);
        }

        if (bundle.getBoolean(AttendanceGuardService.LEAVE)) {
            // check for missing leave event
            if (!onWorkplace && event.isDruhArrival()) Notifications.showMissingLeaveNotification(context);

        }
        Log.d(TAG, "notifyIfEventMissing() currentPositionOnWorkplace " + onWorkplace);
       /* for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            Log.d(TAG, String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }*/
    }

    private boolean isCurrentPositionOnWorkplace() {
        double maxLatitude = workplace.latitude + MULTIPLIER * radius;
        double minLatitude = workplace.latitude - MULTIPLIER * radius;
        if (current.latitude > maxLatitude || current.latitude < minLatitude) {
            return false;
        }
        double maxLongitude = workplace.longitude + MULTIPLIER * radius;
        double minLongitude = workplace.longitude - MULTIPLIER * radius;
        if (current.longitude > maxLongitude || current.longitude < minLongitude) {
            return false;
        }
        return true;
    }

    private void loadWorkplaceSetting() throws PositionNotSetException {
        Log.d(TAG, "loadWorkplaceSetting()");
//        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (!settings.contains(KEY_LATITUDE) || !settings.contains(KEY_LONGITUDE) || !settings.contains(KEY_RADIUS)) {
            throw new PositionNotSetException(context.getString(R.string.no_position_set));
        }
        double latitude = (double) settings.getFloat(KEY_LATITUDE, 0);
        double longitude = (double) settings.getFloat(KEY_LONGITUDE, 0);
        workplace = new LatLng(latitude, longitude);
        radius = settings.getFloat(KEY_RADIUS, 0);
        Log.d(TAG, "loadWorkplaceSetting() workplace " + workplace);
        Log.d(TAG, "loadWorkplaceSetting() radius " + radius);
    }

    private void loadCurrentPosition() {
        Log.d(TAG, "loadCurrentPosition()");
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

}

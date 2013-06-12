package imis.client.position;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import imis.client.R;
import imis.client.exceptions.PositionNotSetException;

import static imis.client.AppConsts.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 12.6.13
 * Time: 16:40
 */
public class MyLocationManager {
    private static final String TAG = MyLocationManager.class.getSimpleName();
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng workplace, current;
    private double radius;

    public MyLocationManager(Context context, LocationListener locationListener) {
        Log.d(TAG, "MyLocationManager()");
        this.context = context;
        this.locationListener = locationListener;
    }

    //TODO
    public boolean isUserOnWorkplace() throws PositionNotSetException {
        Log.d(TAG, "isUserOnWorkplace()");
        loadWorkplaceSetting();
        loadCurrentPosition();
        return false;
    }

    private void loadWorkplaceSetting() throws PositionNotSetException {
        Log.d(TAG, "loadWorkplaceSetting()");
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!sharedPref.contains(KEY_LATITUDE) || !sharedPref.contains(KEY_LONGITUDE) || !sharedPref.contains(KEY_RADIUS)) {
            throw new PositionNotSetException(context.getString(R.string.no_position_set));
        }
        double latitude = (double) sharedPref.getFloat(KEY_LATITUDE, 0);
        double longitude = (double) sharedPref.getFloat(KEY_LONGITUDE, 0);
        workplace = new LatLng(latitude, longitude);
        radius = sharedPref.getFloat(KEY_RADIUS, 0);
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

    // Define a listener that responds to location updates
    /*private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged()");
            current = new LatLng(location.getLatitude(), location.getLongitude());
            locationManager.removeUpdates(locationListener);
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
    };*/

}

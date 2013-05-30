package imis.client.ui.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import imis.client.R;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 30.5.13
 * Time: 14:37
 */
public class LocationSettingsActivity extends FragmentActivity {
    private static final String TAG = LocationSettingsActivity.class.getSimpleName();
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_settings);
        Log.d(TAG, "onCreate()");


        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                makeUseOfNewLocation(location);
                Log.d(TAG, "onLocationChanged() location getAccuracy " + location.getAccuracy() + " " + location.getProvider());
                Log.d(TAG, "onLocationChanged() location pos " + location.getLatitude() + " " + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        //MapFragment map;
        GoogleMap map;
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Log.d(TAG, "onCreate() map " + map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() deregister");
        // Remove the listener you previously added
        locationManager.removeUpdates(locationListener);
    }
}

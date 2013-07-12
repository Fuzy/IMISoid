package imis.client.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SeekBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import imis.client.AppUtil;
import imis.client.R;

import static imis.client.AppConsts.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 30.5.13
 * Time: 14:37
 */
public class LocationSettingsActivity extends FragmentActivity {
    private static final String TAG = LocationSettingsActivity.class.getSimpleName();
    private LocationManager locationManager;
    private GoogleMap map;
    private LatLng position;
    private Circle circle;
    private Marker marker;

    private final float ZOOM = 18f;
    private final double RADIUS = 10.0, MIN_RADIUS = 5.0;
    private int seekPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_settings);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Log.d(TAG, "onCreate() map " + map);
        map.setOnMapClickListener(myMapClickListener);

        SeekBar seekBar = (SeekBar) findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekPos = loadSeekPosition();
        seekBar.setProgress(seekPos);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private final GoogleMap.OnMapClickListener myMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            position = latLng;
            drawMarker();
            drawCircle();
        }
    };

    // Define a listener that responds to location updates
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            Log.d(TAG, "onLocationChanged() latitude " + latitude);
            double longitude = location.getLongitude();
            Log.d(TAG, "onLocationChanged() longitude " + longitude);

            position = new LatLng(location.getLatitude(), location.getLongitude());
            drawMarker();
            drawCircle();

            // Move the camera instantly to hamburg with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomTo(ZOOM), 1000, null);
            // Remove the listener you previously added
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
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the listener you previously added
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                savePosition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void savePosition() {
        if (position == null || circle == null) {
            AppUtil.showError(this, getString(R.string.position_error));
            return;
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(KEY_LATITUDE, (float) position.latitude);
        editor.putFloat(KEY_LONGITUDE, (float) position.longitude);
        editor.putFloat(KEY_RADIUS, (float) circle.getRadius());
        editor.putInt(KEY_SEEK_POS, seekPos);
        editor.commit();
        finish();
    }

    private int loadSeekPosition() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int anInt = settings.getInt(KEY_SEEK_POS, 0);
        return anInt;
    }

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            seekPos = i;
            Log.d(TAG, "onProgressChanged() i " + i);
            drawCircle();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void drawMarker() {
        if (marker != null) marker.remove();
        marker = map.addMarker(new MarkerOptions().position(position));
    }

    private void drawCircle() {
        if (circle != null) circle.remove();
        if (position != null) {
            circle = map.addCircle(new CircleOptions()
                    .center(position)
                    .radius(RADIUS * seekPos + MIN_RADIUS)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(5.0F));
        }
        if (circle != null) {
            Log.d(TAG, "drawCircle() getRadius " + circle.getRadius());
        }
    }
}

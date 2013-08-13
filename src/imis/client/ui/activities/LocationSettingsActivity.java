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

import java.util.Map;

import static imis.client.AppConsts.*;

/**
 * Activity for settings location of workplace.
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
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_settings);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        map.setOnMapClickListener(myMapClickListener);

        seekBar = (SeekBar) findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        loadPreviousLocation();
        if (position == null) {
            requestLocationUpdates();
        }
    }

    private void loadPreviousLocation() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Map<String, ?> all = settings.getAll();
        Log.d(TAG, "loadPreviousLocation() all " + all);

        if (settings.contains(KEY_LATITUDE) && settings.contains(KEY_LONGITUDE)) {
            double latitude = settings.getFloat(KEY_LATITUDE, 0f);
            double longitude = settings.getFloat(KEY_LONGITUDE, 0f);
            position = new LatLng(latitude, longitude);
            drawMarker(position);
            arrangeCamera();
            Log.d(TAG, "loadPreviousLocation() position loaded");
        }

        seekPos = settings.getInt(KEY_SEEK_POS, 0);
        seekBar.setProgress(seekPos);
        drawCircle(position, seekPos);
    }


    private void requestLocationUpdates() {
        Log.d(TAG, "requestLocationUpdates()");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    private final GoogleMap.OnMapClickListener myMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            position = latLng;
            drawMarker(position);
            drawCircle(position, seekPos);
        }
    };

    private void arrangeCamera() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));
        map.animateCamera(CameraUpdateFactory.zoomTo(ZOOM), 1000, null);
    }


    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            Log.d(TAG, "onLocationChanged() latitude " + latitude);
            double longitude = location.getLongitude();
            Log.d(TAG, "onLocationChanged() longitude " + longitude);

            position = new LatLng(location.getLatitude(), location.getLongitude());
            drawMarker(position);
            drawCircle(position, seekPos);
            arrangeCamera();

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
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_options_menu, menu);
        inflater.inflate(R.menu.save_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                savePosition();
                return true;
            case R.id.requestPosition:
                requestLocationUpdates();
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


    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            seekPos = i;
            Log.d(TAG, "onProgressChanged() i " + i);
            drawCircle(position, seekPos);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void drawMarker(LatLng position) {
        if (marker != null) marker.remove();
        marker = map.addMarker(new MarkerOptions().position(position));
    }

    private void drawCircle(LatLng position, int seekPos) {
        if (circle != null) circle.remove();
        if (position != null) {
            circle = map.addCircle(new CircleOptions().center(position).radius(RADIUS * seekPos + MIN_RADIUS)
                    .strokeColor(Color.BLACK).strokeWidth(5.0F));
        }
    }
}

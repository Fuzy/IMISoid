package imis.client.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.5.13
 * Time: 14:56
 */
public class EmployeeDetailActivity extends Activity {
    private static final String TAG = EmployeeDetailActivity.class.getSimpleName();
    private Employee employee;
    private ImageButton favButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.employee_profile);

        long id = getIntent().getLongExtra(Employee.COL_ID, -1);
        employee = EmployeeManager.getEmployee(this, id);
        Log.d(TAG, "onCreate() employee " + employee);
        populateEmployeeFields();
    }

    private void populateEmployeeFields() {
        TextView kodPra = (TextView) findViewById(R.id.emp_kodpra);
        kodPra.setText(employee.getKodpra());
        TextView name = (TextView) findViewById(R.id.emp_name);
        name.setText(employee.getName());

        favButton = (ImageButton) findViewById(R.id.emp_favorite);
        if (employee.isFav()) {
            setImageIsFavorite();
        } else {
            setImageIsNotFavorite();
        }

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIsFavorite();
            }
        });
    }

    private void changeIsFavorite() {
        if (employee.isFav()) {
            setImageIsNotFavorite();
            employee.setFav(false);
        } else {
            setImageIsFavorite();
            employee.setFav(true);
        }
    }

    private void setImageIsFavorite() {
        favButton.setImageResource(R.drawable.btn_rating_star_on_pressed);
    }

    private void setImageIsNotFavorite() {
        favButton.setImageResource(R.drawable.btn_rating_star_off_normal);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        EmployeeManager.updateEmployeeIsFav(this, employee.get_id(), employee.isFav());
    }
}

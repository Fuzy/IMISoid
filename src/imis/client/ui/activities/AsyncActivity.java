package imis.client.ui.activities;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import imis.client.asynctasks.NetworkingAsyncTask;
import imis.client.ui.fragments.TaskFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 27.4.13
 * Time: 19:51
 */
public abstract class AsyncActivity extends FragmentActivity implements TaskFragment.OnAsyncActionCompletedListener {
    // Tag so we can find the task fragment again, in another instance of this fragment after rotation.
    static final String TASK_FRAGMENT_TAG = "task";
    private static final String TAG = AsyncActivity.class.getSimpleName();

    protected void createTaskFragment(NetworkingAsyncTask task) {
        Log.d(TAG, "createTaskFragment()");

        // We will create a new TaskFragment.
        TaskFragment taskFragment = new TaskFragment();
        taskFragment.setTask(task);
        // Show the fragment.
        taskFragment.show(getSupportFragmentManager(), TASK_FRAGMENT_TAG);
    }

}

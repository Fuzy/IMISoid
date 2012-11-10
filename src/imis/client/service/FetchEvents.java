package imis.client.service;

import imis.client.model.Event;
import imis.client.ui.activity.DayTimelineActivity;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

public class FetchEvents extends AsyncTask<Void, Void, List<Event>>{
  private static final String TAG = FetchEvents.class.getSimpleName();

  public FetchEvents(DayTimelineActivity activity) {
    super();
  }

  @Override
  protected List<Event> doInBackground(Void... params) {
    Log.d(TAG, "doInBackground()");
    return null;
  }
  
  @Override
  protected void onPostExecute(List<Event> result) {
    Log.d(TAG, "onPostExecute()");
    //activity.setEvents(result);
    //activity.refresh();
    //TODO ulozi je do content provider
  }

}

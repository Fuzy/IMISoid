package imis.client.customviews;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Vlastní widget zobrazující docházku pro daný den. (Třída dědí od ViewGroup
 * protože obsahuje další View komponenty).
 * 
 * @author Martin Kadlec
 * 
 */
public class DayTimelineBar extends ViewGroup {
  private static final String TAG = "DayTimelineBar";
  private List<Event> dayEvents = new ArrayList<Event>();

  private Paint backgroundPaint;
  private Paint eventPaint;
  private Rect barBackground = new Rect();
  private Rect barEvent = new Rect();

  private int barHeight = 20;// TODO presunout jinam
  private int barWidth = 1000;// TODO presunout jinam
  private int hoursInDay = 24;// TODO presunout jinam

  /**
   * Class constructor taking only a context. Use this constructor to create
   * {@link DayTimelineBar} objects from your own code.
   * 
   * @param context
   */
  public DayTimelineBar(Context context) {
    super(context);
    Log.d(TAG, "DayTimelineBar(Context context)");
    init();
  }

  /**
   * Class constructor taking a context and an attribute set. This constructor
   * is used by the layout engine to construct a {@link DayTimelineBar} from a
   * set of XML attributes.
   * 
   * @param context
   * @param attrs
   *          An attribute set which can contain attributes from
   *          {@link imis.client.customviews.DayTimelineBar.styleable.DayTimeline}
   *          as well as attributes inherited from {@link android.view.View}.
   */
  public DayTimelineBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    Log.d(TAG, "DayTimelineBar(Context context, AttributeSet attrs)");
    setWillNotDraw(false);
    init();
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

  }

  /**
   * Přidá položku do seznamu událostí.
   */
  public void addEvent(float startTime, float endTime) {
    Event event = new Event();
    event.startTime = startTime;
    event.endTime = endTime;

    dayEvents.add(event);
  }

  /**
   * Vytvoří kreslící komponenty.
   */
  private void init() {
    Log.d(TAG, "init()");
    backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    backgroundPaint.setColor(Color.BLUE);
    eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    eventPaint.setColor(Color.GREEN);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Log.d(TAG, "onDraw()");
    super.onDraw(canvas);

    // vykresli obdelnik na pozadi
    canvas.drawRect(barBackground, backgroundPaint);
    // vykresli obdelniky pro jednotlive udalosti

    for (Event event : dayEvents) {
      barEvent.left = barWidth * (int) event.startTime / hoursInDay;
      barEvent.right = barWidth * (int) event.endTime / hoursInDay;
      barEvent.offset(getPaddingLeft(), getPaddingTop());
      canvas.drawRect(barEvent, eventPaint);
      // Posunout zpět do počátku souřadnic před další iterací
      barEvent.offset(-getPaddingLeft(), -getPaddingTop());
    }
    // co s casy?, asi jen cele hodiny, presnejsi v detailu udalosti
    // TODO
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    Log.d(TAG, "onMeasure()");
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    // Try for a width based on our minimum
    int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
    int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

    // Whatever the width ends up being, ask for a height that would let the pie
    // get as big as it can
    int minh = barHeight + getPaddingBottom() + getPaddingTop();
    int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

    Log.d(TAG, "onMeasure() w: " + w + " h: " + h);
    setMeasuredDimension(w, h);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    Log.d(TAG, "onSizeChanged()");
    super.onSizeChanged(w, h, oldw, oldh);

    // Úbytek šířky kvůli okrajům
    // int xpad = (getPaddingLeft() + getPaddingRight());

    // Velikost okna bez okrajů
    // int ww = w - xpad;

    // Nastaví velikost a odsazení lišty
    barBackground.right = barWidth;
    barBackground.bottom = barHeight;
    barBackground.offset(getPaddingLeft(), getPaddingTop());

    barEvent.bottom = barHeight;
  }

  /**
   * Položka v denním výkazu práce.
   * 
   */
  private class Event {
    public float startTime;
    public float endTime;
    // nejaka reprezentace typu udalosti, kvuli barevnemu rozliseni,
    // nezapomenout na
    // typ pridany nekdy v budoucnu

  }

  @Override
  protected int getSuggestedMinimumWidth() {
    return barWidth;
  }

  @Override
  protected int getSuggestedMinimumHeight() {
    return barHeight;
  }

}

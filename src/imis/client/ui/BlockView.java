/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package imis.client.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import imis.client.R;

/**
 * Custom view that represents a {Blocks#BLOCK_ID} instance, including its
 * title and time span that it occupies. Usually organized automatically by
 * {@link BlocksLayout} to match up against a {@link TimeRulerView} instance.
 */
public class BlockView extends View {// TODO je nutne aby to byl button?Button
    private static final String TAG = BlockView.class.getSimpleName();
    /*private static final int TIME_STRING_FLAGS = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY
            | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_TIME;// TODO co to
    // je?*/

    // private String blockId;
    private int arriveId;
    private int leaveId;
    private long startTime;
    private long endTime;
    private String type;

    private boolean dirty;
    LayerDrawable buttonDrawable;

    // TODO dalsi atributy

    public BlockView(Context context) {
        this(context, -1, -1, 0, 0, "", false);
    }

    public BlockView(Context context, int arriveId, int leaveId, long startTime, long endTime, String type, boolean dirty) {
        super(context);
        // Log.d(TAG, "BlockView()");

        this.arriveId = arriveId;
        this.leaveId = leaveId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.dirty = dirty;

        int accentColor = ColorUtil.getColorForType(type);
        /*if (dirty) {
            Log.d("BlockView", "BlockView() dirty");
            accentColor = ColorUtil.getColor_present_normal();
            accentColor |= 0x80000000;
            accentColor = 0;
        } else {
            accentColor = ColorUtil.getColor_present_normal();//getResources().getColor(R.color.block_column_2);
        }*/
        //TODO barva podle typu
        LayerDrawable buttonDrawable;

        //TODO nesync rozlisit
        if (dirty) {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.btn_block_dirty);
        }  else {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.btn_block_not_dirty);
        }
        buttonDrawable.getDrawable(0).setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
        // setTextColor(textColor);
        setBackground(buttonDrawable);
        //setBackgroundColor(accentColor);
    }


    /*public void setAsDeleted() {
        int accentColor = getResources().getColor(R.color.block_column_2_del);
        buttonDrawable.getDrawable(0).setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
        // setTextColor(textColor);
        setBackgroundDrawable(buttonDrawable);
    }*/

    public int getArriveId() {
        return arriveId;
    }

    public void setArriveId(int arriveId) {
        this.arriveId = arriveId;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    /*public String getBlockTimeString() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-2"));
        return DateUtils.formatDateTime(getContext(), startTime, TIME_STRING_FLAGS);
    }*/

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BlockView{" +
                "arriveId=" + arriveId +
                ", leaveId=" + leaveId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", dirty=" + dirty +
                '}';
    }
}

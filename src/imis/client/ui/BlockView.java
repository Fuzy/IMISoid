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

public class BlockView extends View {
    private static final String TAG = BlockView.class.getSimpleName();

    private int arriveId;
    private int leaveId;
    private long startTime;
    private long endTime;
    private String type;

    private boolean dirty;
    private boolean error;
    private boolean presence;

    public BlockView(Context context, int arriveId, int leaveId, long startTime, long endTime,
                     String type, boolean dirty, boolean error, boolean presence) {
        super(context);

        this.arriveId = arriveId;
        this.leaveId = leaveId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.dirty = dirty;
        this.error = error;
        this.presence = presence;

        int accentColor = ColorConfig.getColor(context, type);
        LayerDrawable buttonDrawable;

        if (error) {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.btn_block_error);
        } else if (dirty) {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.btn_block_dirty);
        } else {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.btn_block_not_dirty);
        }
        buttonDrawable.getDrawable(0).setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(buttonDrawable);
        } else {
            setBackground(buttonDrawable);
        }
    }


    public int getArriveId() {
        return arriveId;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }

    public boolean isPresence() {
        return presence;
    }

    @Override
    public String toString() {
        return "BlockView{" +
                "arriveId=" + arriveId +
                ", leaveId=" + leaveId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", type='" + type + '\'' +
                ", dirty=" + dirty +
                ", error=" + error +
                '}';
    }
}

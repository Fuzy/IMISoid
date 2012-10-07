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

package imis.client.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.format.DateUtils;
import android.widget.Button;

import imis.client.R;

import java.util.TimeZone;

/**
 * Custom view that represents a {@link Blocks#BLOCK_ID} instance, including its
 * title and time span that it occupies. Usually organized automatically by
 * {@link BlocksLayout} to match up against a {@link TimeRulerView} instance.
 */
public class BlockView extends Button {
  private static final int TIME_STRING_FLAGS = DateUtils.FORMAT_SHOW_DATE
      | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY
      | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_TIME;

  private String blockId;
  private String title;
  private long startTime;
  private long endTime;
  private boolean containsStarred;
  private int column;

  public BlockView(Context context) {
    this(context, null, null, 0, 0, true, 0);
  }

  public BlockView(Context context, String blockId, String title, long startTime, long endTime,
      boolean containsStarred, int column) {
    super(context);

    this.blockId = blockId;
    this.title = title;
    this.startTime = startTime;
    this.endTime = endTime;
    this.containsStarred = containsStarred;
    this.column = column;

    setText(title);

    // TODO: turn into color state list with layers?
    int textColor = Color.WHITE;
    int accentColor = -1;
    switch (column) {
    case 0:
      accentColor = getResources().getColor(R.color.block_column_1);
      break;
    case 1:
      accentColor = getResources().getColor(R.color.block_column_2);
      break;
    case 2:
      accentColor = getResources().getColor(R.color.block_column_3);
      break;
    }

    LayerDrawable buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
        R.drawable.btn_block);
    buttonDrawable.getDrawable(0).setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
    buttonDrawable.getDrawable(1).setAlpha(containsStarred ? 255 : 0);

    setTextColor(textColor);
    setBackgroundDrawable(buttonDrawable);
  }

  public String getBlockId() {
    return blockId;
  }

  public String getBlockTimeString() {
    TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-2"));
    return DateUtils.formatDateTime(getContext(), startTime, TIME_STRING_FLAGS);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

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

  public boolean isContainsStarred() {
    return containsStarred;
  }

  public void setContainsStarred(boolean containsStarred) {
    this.containsStarred = containsStarred;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  @Override
  public String toString() {
    return "BlockView [blockId=" + blockId + ", title=" + title + ", startTime=" + startTime
        + ", endTime=" + endTime + ", containsStarred=" + containsStarred + ", column=" + column
        + "]";
  }

 
  
}

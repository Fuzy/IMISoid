package imis.client.model;

import imis.client.TimeUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 14:17
 */
public class Block {
    private long date;
    private long startTime;
    private long endTime;
    private int arriveId;
    private int leaveId;
    private String kod_po;
    private boolean dirty;
    private boolean error;
    private boolean presence;

    public Block() {
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public String getKod_po() {
        return kod_po;
    }

    public void setKod_po(String kod_po) {
        this.kod_po = kod_po;
    }

    public int getArriveId() {
        return arriveId;
    }

    public void setArriveId(int arriveId) {
        this.arriveId = arriveId;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    @Override
    public String toString() {
        return "Block{" +
                "date=" + date +
                ", startTime=" + TimeUtil.formatTimeInNonLimitHour(startTime) +
                ", endTime=" + TimeUtil.formatTimeInNonLimitHour(endTime) +
                ", arriveId=" + arriveId +
                ", leaveId=" + leaveId +
                ", kod_po='" + kod_po + '\'' +
                ", dirty=" + dirty +
                ", error=" + error +
                ", presence=" + presence +
                '}';
    }
}

package imis.client.model;

import static imis.client.TimeUtil.formatTimeInNonLimitHour;

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
    boolean dirty;
    boolean error;

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

    @Override
    public String toString() {
        return "Block{" +
                "date=" + date +
                ", startTime=" + formatTimeInNonLimitHour(startTime) +
                ", endTime=" + formatTimeInNonLimitHour(endTime) +
                ", arriveId=" + arriveId +
                ", leaveId=" + leaveId +
                ", kod_po='" + kod_po + '\'' +
                ", dirty=" + dirty +
                '}';
    }
}

package imis.client.sync.eventssync;

/**
 * Class contains result of stats.
 */
public class SyncStats {
    private int created = 0;
    private int createdAttempt = 0;
    private int updated = 0;
    private int updateAttempt = 0;
    private int deleted = 0;
    private int deleteAttempt = 0;
    private int downloaded = 0;
    private boolean isDownloadErr = false;
    private String downErrMsg;

    public void incCreated() {
        created++;
    }

    public void incCreatedAttempt() {
        createdAttempt++;
    }

    public void incUpdated() {
        updated++;
    }

    public void incUpdatedAttempt() {
        updateAttempt++;
    }

    public void incDeleted() {
        deleted++;
    }

    public void incDeletedAttempt() {
        deleteAttempt++;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getCreatedAttempt() {
        return createdAttempt;
    }

    public void setCreatedAttempt(int createdAttempt) {
        this.createdAttempt = createdAttempt;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getUpdateAttempt() {
        return updateAttempt;
    }

    public void setUpdateAttempt(int updateAttempt) {
        this.updateAttempt = updateAttempt;
    }

    public int getDeleteAttempt() {
        return deleteAttempt;
    }

    public void setDeleteAttempt(int deleteAttempt) {
        this.deleteAttempt = deleteAttempt;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isDownloadErr() {
        return isDownloadErr;
    }

    public void setDownloadErr(boolean downloadErr) {
        isDownloadErr = downloadErr;
    }

    public String getDownErrMsg() {
        return downErrMsg;
    }

    public void setDownErrMsg(String downErrMsg) {
        this.downErrMsg = downErrMsg;
    }
}

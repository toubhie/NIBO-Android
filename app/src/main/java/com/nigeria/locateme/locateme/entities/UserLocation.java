package com.nigeria.locateme.locateme.entities;

/**
 * Created by Theophilus on 12/21/2016.
 */
public class UserLocation {
    private String longitude;
    private String latitude;
    private long userId;
    private String description;
    private String isSyncedToOnlineDBStatus;
    private NiboUser niboUser;
    private int sqliteId;
    private int mysqlId;
    private int isDeleted;

    public int getSqliteId() {
        return sqliteId;
    }

    public void setSqliteId(int sqliteId) {
        this.sqliteId = sqliteId;
    }

    public int getMysqlId() {
        return mysqlId;
    }

    public void setMysqlId(int mysqlId) {
        this.mysqlId = mysqlId;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getIsSyncedToOnlineDBStatus() {
        return isSyncedToOnlineDBStatus;
    }

    public void setIsSyncedToOnlineDBStatus(String isSyncedToOnlineDBStatus) {
        this.isSyncedToOnlineDBStatus = isSyncedToOnlineDBStatus;
    }

    public NiboUser getNiboUser() {
        return niboUser;
    }

    public void setNiboUser(NiboUser niboUser) {
        this.niboUser = niboUser;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitdue(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

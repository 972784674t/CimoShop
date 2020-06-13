package com.example.cimoshop.entity;

import androidx.annotation.NonNull;

/**
 * @author 谭海山
 */
public class UserWareHouses {

    private int userId;

    private String wareHouseItemUrl;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWareHouseItemUrl() {
        return wareHouseItemUrl;
    }

    public void setWareHouseItemUrl(String wareHouseItemUrl) {
        this.wareHouseItemUrl = wareHouseItemUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserWareHouses{" +
                "userId=" + userId +
                ", wareHouseItemUrl='" + wareHouseItemUrl + '\'' +
                '}';
    }

}

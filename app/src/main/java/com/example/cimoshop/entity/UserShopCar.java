package com.example.cimoshop.entity;

import androidx.annotation.NonNull;

/**
 * @author 谭海山
 */
class UserShopCar {

    private int userId;

    private String shopCarItemUrl;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getShopCarItemUrl() {
        return shopCarItemUrl;
    }

    public void setShopCarItemUrl(String shopCarItemUrl) {
        this.shopCarItemUrl = shopCarItemUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserShopCar{" +
                "userId=" + userId +
                ", shopCarItemUrl='" + shopCarItemUrl + '\'' +
                '}';
    }

}

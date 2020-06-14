package com.example.cimoshop.entity;

/**
 * 用户收藏信息实体
 *
 * @author 谭海山
 */
public class UserFavorites {

    private int userId;

    private String userFavoriteUrl;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFavoriteUrl() {
        return userFavoriteUrl;
    }

    public void setUserFavoriteUrl(String userFavoriteUrl) {
        this.userFavoriteUrl = userFavoriteUrl;
    }

    @Override
    public String toString() {
        return "UserFavorites{" +
                "userId=" + userId +
                ", userFavoriteUrl='" + userFavoriteUrl + '\'' +
                '}';
    }

}

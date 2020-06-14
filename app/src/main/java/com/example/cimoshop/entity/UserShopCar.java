package com.example.cimoshop.entity;

/**
 * 用户购物车信息实体
 *
 * @author 谭海山
 */
public class UserShopCar {

    private boolean isCheck = false;

    private int userId;

    private String userName;

    private String shopCarItemUrl;

    private String size;

    private String price;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getShopCarItemUrl() {
        return shopCarItemUrl;
    }

    public void setShopCarItemUrl(String shopCarItemUrl) {
        this.shopCarItemUrl = shopCarItemUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "UserShopCar{" +
                "isCheck=" + isCheck +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", shopCarItemUrl='" + shopCarItemUrl + '\'' +
                ", size='" + size + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

}

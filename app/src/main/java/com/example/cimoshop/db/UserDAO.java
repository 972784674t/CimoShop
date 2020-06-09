package com.example.cimoshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.cimoshop.entity.User;
import com.example.cimoshop.entity.UserShopCar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 谭海山
 */
public class UserDAO {

    private static final String TAG = "UserDAO";

    private static final String DATABASE_NAME = "db_cimoShop";

    private static final int DATABASE_VERSION = 1;

    /**
     * 数据库表
     */
    private static final String USER_INFO = "userInfo";
    private static final String USER_FAVORITES = "userFavorites";
    private static final String USER_SHOP_CAR = "userShopCar";

    private SQLiteDatabase db;

    private static UserDAO INSTANCE;

    private UserDAO(SQLiteDatabase db){
        this.db = db;
    }

    public static UserDAO getInstance(Context context){
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        if ( INSTANCE == null ){
            INSTANCE = new UserDAO(db);
        }
        return INSTANCE;
    }

    /**
     * 通过用户名查询用户信息
     * @param userName 用户名
     * @return  User
     */
    public User findUserByUserName(String userName){
        User user = new User();
        String sql = "select * from userInfo where userName = ?";
        Cursor cursor = db.rawQuery(sql, new  String[]{userName});
        if ( cursor.moveToNext() ){
            user.setUserId( cursor.getInt(cursor.getColumnIndex("id")) );
            user.setUserName( cursor.getString(cursor.getColumnIndex("userName")) );
        }
        return user;
    }

    /**
     * 插入用户
     * @return boolean 是否成功插入用户
     */
    public boolean insertUser(User user){
        boolean flag = false;
        Log.d(TAG,"即将插入的用户："+user.getUserName());
        Log.d(TAG,"数据库是否存在该用户："+findUserByUserName(user.getUserName()).getUserName());
        if ( findUserByUserName(user.getUserName()).getUserName() == null ){
            ContentValues values = new ContentValues();
            values.put("userName",user.getUserName());
            if ( db.insert(USER_INFO,null,values) != -1){
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 将用户收藏的图片url插入到数据库
     * @param userName 用户名
     * @param imageUrl 图片url
     * @return  是否成功
     */
    public boolean insertUserFavoriteImage(String userName,String imageUrl){
        boolean flag = false;
        int uid = findUserByUserName(userName).getUserId();
        ContentValues values = new ContentValues();
        values.put("id",uid);
        values.put("userFavoriteUrl",imageUrl);
        if ( db.insert(USER_FAVORITES,null,values) != -1){
            flag = true;
        }
        return flag;
    }

    /**
     * 将收藏的图片从数据库中删除
     * @param imageUrl 图片url
     * @return  是否成功
     */
    public boolean delUserFavoriteImage(String imageUrl){
        boolean flag = false;
        if ( db.delete(USER_FAVORITES, "userFavoriteUrl=?",new String[]{imageUrl}) > 0 ){
            flag = true ;
        }
        return flag;
    }

    /**
     * 是否已经点赞此图片
     * @param userName 用户名
     * @param imageUrl 图片url
     * @return boolean
     */
    public boolean isFavoriteImage(String userName,String imageUrl){
        boolean flag = false;
        int uid = findUserByUserName(userName).getUserId();
        String sql = "select * from userFavorites where id=? and userFavoriteUrl=?";
        Cursor cursor = db.rawQuery(sql, new  String[]{""+uid,imageUrl});
        if ( cursor.moveToNext() ){
            flag = true;
        }
        return flag;
    }

    /**
     * 通过用户id获取收藏列表
     * @param userId 用户id
     * @return 收藏列表
     */
    public ArrayList<String> getUserFavoriteImageList(int userId){
        ArrayList<String> list = new ArrayList<>();
        String sql = "select * from userFavorites where id = ?";
        Cursor cursor = db.rawQuery(sql, new  String[]{String.valueOf(userId)});
        while ( cursor.moveToNext() ){
            list.add(cursor.getString(cursor.getColumnIndex("userFavoriteUrl")));
        }
        return list;
    }

    /**
     * 将图片加入到购物车
     * @param userShopCar 带有相关信息的购物车item对象
     * @return 是否成功
     */
    public boolean addImageToShopCar(UserShopCar userShopCar){
        boolean flag = false;
        int uid = findUserByUserName(userShopCar.getUserName()).getUserId();
        ContentValues values = new ContentValues();
        values.put("id",uid);
        values.put("shopCarItemUrl",userShopCar.getShopCarItemUrl());
        values.put("imageSize",userShopCar.getSize());
        values.put("price",userShopCar.getPrice());
        if ( db.insert(USER_SHOP_CAR,null,values) != -1){
            flag = true;
        }
        return flag;
    }

    /**
     * 删除购物车中的图片
     * @param imageUrl 图片链接
     * @param price 图片尺寸
     * @return  是否成功
     */
    public boolean delImageFromShopCar(String imageUrl,String price){
        boolean flag = false;
        if ( db.delete(USER_FAVORITES, "shopCarItemUrl=? and price=?",new String[]{imageUrl,price}) > 0 ){
            flag = true ;
        }
        return flag;
    }

    /**
     * 通过用户id获取购物车列表
     * @param userName 用户名
     * @return 购物车列表
     */
    public ArrayList<UserShopCar> getShopCarList(String userName){
        ArrayList<UserShopCar> list = new ArrayList<>();
        int uid = findUserByUserName(userName).getUserId();
        String sql = "select * from userShopCar where id = ?";
        Cursor cursor = db.rawQuery(sql, new  String[]{""+uid});
        while ( cursor.moveToNext() ){
            UserShopCar userShopCar = new UserShopCar();
            userShopCar.setUserId( cursor.getInt(cursor.getColumnIndex("id")));
            userShopCar.setShopCarItemUrl( cursor.getString(cursor.getColumnIndex("shopCarItemUrl")));
            userShopCar.setSize( cursor.getString(cursor.getColumnIndex("imageSize")));
            userShopCar.setPrice( cursor.getString(cursor.getColumnIndex("price")));
            list.add(userShopCar);
        }
        return list;
    }
}

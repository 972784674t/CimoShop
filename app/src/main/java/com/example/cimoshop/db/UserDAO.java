package com.example.cimoshop.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.cimoshop.entity.User;

/**
 * @author 谭海山
 */
public class UserDAO {

    private SQLiteDatabase db;

    private static UserDAO INSTANCE;

    private UserDAO(SQLiteDatabase db){
        this.db = db;
    }

    public static UserDAO getInstance(SQLiteDatabase db){
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
        ContentValues values = new ContentValues();
        values.put("userName","cimo");

        if ( db.insert("userInfo",null,values) != -1){
            flag = true;
        }
        return false;
    }

}

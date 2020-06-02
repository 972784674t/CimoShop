package com.example.cimoshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.cimoshop.entity.User;
import com.example.cimoshop.utils.SharedPrefsTools;

/**
 * @author 谭海山
 */
public class UserDAO {

    private static final String TAG = "UserDAO";

    private static final String DATABASE_NAME = "db_cimoShop";

    private static final int DATABASE_VERSION = 1;

    private static final String USER_INFO = "userInfo";
    private static final String USER_FAVORITES = "userFavorites";

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

}

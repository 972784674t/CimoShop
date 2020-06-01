package com.example.cimoshop.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.cimoshop.R;

/**
 * @author 谭海山
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private Context context;

    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String initTableUserInfo = context.getString(R.string.initTable_userInfo);
        String initTableUserFavorites = context.getString(R.string.initTable_userFavorites);
        String initTableUserShopCar = context.getString(R.string.initTable_userShopCar);
        String initTableUserWareHouses = context.getString(R.string.initTable_userWareHouses);
        db.execSQL(initTableUserInfo);
        db.execSQL(initTableUserFavorites);
        db.execSQL(initTableUserShopCar);
        db.execSQL(initTableUserWareHouses);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}

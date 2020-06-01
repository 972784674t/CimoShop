package com.example.cimoshop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.cimoshop.db.DataBaseHelper;
import com.example.cimoshop.db.UserDAO;
import com.example.cimoshop.entity.User;

/**
 * @author 谭海山
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "cimoshopMainActivity";

    public static final String DATABASE_NAME = "db_cimoShop";
    public static final int DATABASE_VERSION = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this,DATABASE_NAME,null,DATABASE_VERSION);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        User user = new User();
        UserDAO.getInstance(db).insertUser(user);
        User user1 = UserDAO.getInstance(db).findUserByUserName("cimo");
        Log.d(TAG,user1.toString());
    }

    /**
     * 获取权限
     */
    public void checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("读写权限获取"," ： "+isGranted);
            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }
        }
    }


}

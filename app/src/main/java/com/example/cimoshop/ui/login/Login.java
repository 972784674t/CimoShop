package com.example.cimoshop.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.cimoshop.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * @author 谭海山
 */
public class Login extends AppCompatActivity {

    EditText username;
    EditText password;
    MaterialButton loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.loginbtn);
        username = findViewById(R.id.usernameEdit);
        password = findViewById(R.id.passwordEdit);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals("123456") && password.getText().toString().equals("123456")){
                    Snackbar.make(v, "登录成功", Snackbar.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(v);
                } else {
                    Snackbar.make(v, "用户名或密码错误", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}

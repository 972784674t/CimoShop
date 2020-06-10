package com.example.cimoshop.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.R;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.utils.UITools;
import com.example.cimoshop.utils.SharedPrefsTools;
import com.google.android.material.button.MaterialButton;

/**
 * @author 谭海山
 */
public class Login extends AppCompatActivity {

    private static final String TAG = "cimoLogin";
    private static final String GITHUB_LOGON_URL = "https://github.com/login/oauth/authorize?client_id=c92a401aa4c16f767b32";

    /**
     * 带有code参数的回调
     */
    private String githubCallbackUrl;

    /**
     * github response
     */
    private String githubResponse;

    /**
     * github token
     */
    private String githubToken;

    ConstraintLayout constraintLayout;
    EditText username;
    EditText password;
    MaterialButton loginBtn;
    ImageView baiduIcon;
    ImageView githubIcon;
    WebView logonWebView;
    ProgressBar logonWebViewProgressBar;
    TextView logonWebProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

    }

    /**
     * 控件初始化
     */
    private void initView() {
        constraintLayout = findViewById(R.id.logopage);
        loginBtn = findViewById(R.id.loginbtn);
        username = findViewById(R.id.usernameEdit);
        password = findViewById(R.id.passwordEdit);
        baiduIcon = findViewById(R.id.logonbaiduicon);
        githubIcon = findViewById(R.id.logongithubicon);
        logonWebView = findViewById(R.id.loginWebView);
        logonWebViewProgressBar = findViewById(R.id.logonWebVeiwProgressbar);
        logonWebProgress = findViewById(R.id.logonWebProgress);
        UITools.makeStatusBarTransparent(this);
        UITools.MIUISetStatusBarLightMode(this, true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "作者很懒，暂时只支持github登录哦", Toast.LENGTH_SHORT).show();
            }
        });

        baiduIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "作者很懒，没有做百度账号登录", Toast.LENGTH_SHORT).show();
            }
        });

        githubIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogonWebView();
            }
        });

    }

    /**
     * 初始化登录webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initLogonWebView() {
        Toast.makeText(getApplication(), "使用github账号登录", Toast.LENGTH_SHORT).show();

        loginBtn.setVisibility(View.GONE);
        logonWebView.setVisibility(View.VISIBLE);

        //是否允许JavaScript
        logonWebView.getSettings().setJavaScriptEnabled(true);
        //将图片调整到适合webView的大小
        logonWebView.getSettings().setUseWideViewPort(true);
        //是否使用预览模式加载界面
        logonWebView.getSettings().setLoadWithOverviewMode(true);
        //是否开启网页缓存
        logonWebView.getSettings().setAppCacheEnabled(false);
        //缩放至屏幕的大小
        logonWebView.getSettings().setLoadWithOverviewMode(true);
        //是否开启DOMStorage
        logonWebView.getSettings().setDomStorageEnabled(false);
        logonWebView.getProgress();

        //网页加载事件监听
        logonWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG,"WebView : newProgress - >"+newProgress);
                logonWebProgress.setVisibility(View.VISIBLE);
                logonWebViewProgressBar.setVisibility(View.VISIBLE);
                //显示加载中提示
                logonWebProgress.setText("正在前往Github：" + newProgress + "%");
                logonWebViewProgressBar.setProgress(newProgress);
                if ( 80 < newProgress ) {
                    //加载中提示消失
                    constraintLayout.removeView(logonWebProgress);
                    constraintLayout.removeView(logonWebViewProgressBar);
                }
            }

        });

        //网页加载后的操作
        logonWebView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                //获取浏览器Url
                githubCallbackUrl = request.getUrl().toString();
                Log.d(TAG, githubCallbackUrl);

                //如果加载的url为下面路径则拦截
                if ( githubCallbackUrl.contains("http://incimo.xyz:8080/cimowebshop/GithubLogingCallBack") ) {
                    //通过拦截的url获取token
                    getGithubToken();

                    //隐藏webView
                    logonWebView.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                    clearWebView();
                    //返回true则不加载这个链接
                    return true;
                }
                return false;
            }

        });
        logonWebView.loadUrl(GITHUB_LOGON_URL);

    }


    /**
     * 获取code后，从服务器inicmo.xyz获取github的token
     */
    void getGithubToken() {
        Toast.makeText(getApplicationContext(), "登录github成功,正在获取授权", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                githubCallbackUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        githubResponse = response;
                        Log.d(TAG, "result：" + githubResponse);

                        githubToken = UITools.getMap(githubResponse).get("access_token");
                        Log.d(TAG, "token：" + githubToken);

                        //将token存入Sharepreferences
                        SharedPrefsTools.getInstance(getApplication()).saveToken("github",githubToken);
                        Toast.makeText(getApplicationContext(), "github授权成功", Toast.LENGTH_SHORT).show();

                        //返回token到LogonActivity
                        Intent intent = new Intent();
                        intent.putExtra("token",githubToken);
                        setResult(2, intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"error："+ error);
                        Toast.makeText(getApplicationContext(), "github授权失败", Toast.LENGTH_SHORT).show();
                        VolleySingleton.errorMessage(error,getApplication().getApplicationContext());
                    }
                });
        VolleySingleton.getInstance(getApplication()).addToRequestQueue(stringRequest);
    }

    /**
     * 清空webView为下次登录做准备
     */
    private void clearWebView() {

        if (logonWebView != null) {
            //清除表单数据
            logonWebView.clearFormData();
            //清除浏览历史
            logonWebView.clearHistory();
            //清除匹配文本
            logonWebView.clearMatches();
            logonWebView.clearSslPreferences();
        }
        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
        logonWebView.setWebChromeClient(null);
        logonWebView.setWebViewClient(null);
        logonWebView.getSettings().setJavaScriptEnabled(false);
        //清除缓存
        logonWebView.clearCache(true);
    }

    @Override
    public void onBackPressed() {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("info","null");
        //设置返回数据
        setResult(1, intent);
        finish();
    }


}

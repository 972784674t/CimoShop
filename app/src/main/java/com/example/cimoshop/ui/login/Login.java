package com.example.cimoshop.ui.login;

import android.annotation.SuppressLint;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.cimoshop.R;
import com.example.cimoshop.api.VolleySingleton;
import com.example.cimoshop.mytools.MyTools;
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
        loginBtn = findViewById(R.id.loginbtn);
        username = findViewById(R.id.usernameEdit);
        password = findViewById(R.id.passwordEdit);
        baiduIcon = findViewById(R.id.logonbaiduicon);
        githubIcon = findViewById(R.id.logongithubicon);
        logonWebView = findViewById(R.id.loginWebView);
        logonWebViewProgressBar = findViewById(R.id.logonWebVeiwProgressbar);
        logonWebProgress = findViewById(R.id.logonWebProgress);
        MyTools.makeStatusBarTransparent(this);
        MyTools.MIUISetStatusBarLightMode(this, true);
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
                //显示加载中提示
                logonWebProgress.setVisibility(View.VISIBLE);
                logonWebProgress.setText("正在前往Github："+newProgress+"%");
                logonWebViewProgressBar.setVisibility(View.VISIBLE);
                logonWebViewProgressBar.setProgress(newProgress);
                if ( newProgress > 90){
                    //加载中提示消失
                    logonWebProgress.setVisibility(View.GONE);
                    logonWebViewProgressBar.setVisibility(View.GONE);
                }
            }
        });

        //网页加载后的操作
        logonWebView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                githubCallbackUrl = request.getUrl().toString();
                Log.d(TAG, githubCallbackUrl);
                //如果加载的url为下面路径则拦截
                if (githubCallbackUrl.contains("http://incimo.xyz:8080/cimowebshop/GithubLogingCallBack")) {
                    getGithubToken();
                    Toast.makeText(getApplicationContext(), "登录github成功,正在获取授权", Toast.LENGTH_SHORT).show();
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
     * 获取github的token
     */
    void getGithubToken() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                githubCallbackUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        githubResponse = response;
                        Log.d(TAG, "result：" + githubResponse);
                        githubToken = MyTools.getMap(githubResponse).get("access_token");
                        Log.d(TAG, "token：" + githubToken);
                        Toast.makeText(getApplicationContext(), "github授权成功", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "loadMore: " + error.getClass());
                        Toast.makeText(getApplicationContext(), "github授权失败", Toast.LENGTH_SHORT).show();
                        switch (error.getClass().toString()) {
                            case "class com.android.volley.NoConnectionError":
                                Log.d(TAG, "onErrorResponse: " + error);
                                Toast.makeText(getApplication(),
                                        "Oops. 网络连接出错了！",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case "class com.android.volley.ClientError":
                                Toast.makeText(getApplication(),
                                        "Oops. 服务器出错了!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case "class com.android.volley.ParseError":
                                Toast.makeText(getApplication(),
                                        "Oops. 数据解析出错了!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case "class com.android.volley.TimeoutError":
                                Toast.makeText(getApplication(),
                                        "Oops. 请求超时了!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
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


}

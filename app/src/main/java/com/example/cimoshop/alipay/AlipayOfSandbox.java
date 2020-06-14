package com.example.cimoshop.alipay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.example.cimoshop.R;
import com.example.cimoshop.utils.UITools;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Map;

/**
 *  重要说明：
 *
 *  本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 *  （包括 OrderInfoUtil2_0_HK 和 OrderInfoUtil2_0）。
 *
 *  在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 *  否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 *
 *  Warning:
 *
 *  For demonstration purpose, the assembling and signing of the request parameters are done on
 *  the client side in this demo application.
 *
 *  However, in practice, both assembling and signing must be carried out on the server side.
 *
 *  @author 谭海山
 */
public class AlipayOfSandbox extends AppCompatActivity {

    public static final String TAG = "AlipayOfSandbox";

    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2016101600698448";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088612667638409";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "";

    /**
     *  pkcs8 格式的商户私钥。
     *
     * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * 	RSA2_PRIVATE。
     *
     * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCfmIlSmbWk6PUblA+Idg7W+qZnQ7Bg/8L425TERuA3fKyCWZVvFDuYgROQg2xVs9Z+ZyEd88rSA0Krjftsm7of6k8Lb1lI1F+sNpqoyCLWxAvQ567ZEiqvXIuX7Tbi+uIAS/aKvcPypt6/kviCeE9ZwWOwRGW32yCR85isz+asnJXDlWgALI9spwzbkl/INVjvbvFXJL+rcK2Le1Qeuz5m39jx9r9AdKYWjxYRW+Niohf1X6TJH/z3DY+8/rFpGK4JB+vVUPDa7INn1JoozqgR0LKvAHBkNlFBUaSf9dBsUlzJXAM6N66Y2r2XFvV0hF7NQEnA0sbHIlugMG4AVNB/AgMBAAECggEBAIbDm1Gwkf3K6Q58nBEy/5zw0oroUYvW7KCParPC1ZqFIHaBhvnhL+G7PPYOw/Aqh7j9BK3yKxXjwuxyXBh991Dz3gNd5xrkmlUlVrunJCQOL5wbxz1tZbXmg8mqeWp5R1XQz0Kgs1G3Lui+BjLFvNuhvbgeSXZTBtG3zPhAJh0iTwut0NIaaLbDCIU+dSEtucW/urndkgJts5GBBBELlnqrxE+5aMoOq6hAI86JUO6oH3bknV3yKkT9Zj+kk17rYQsB9i9rt3IubqwKgvtaB/tA5/4qe4B2QR4Zk2mI/Xtzv+6kVC3bpdm8nIc/3/UZ2Yv+O5cVNQYdRowfZD5Y2QECgYEA+fgJD4VghcXWbTYzWY2mFrfutmScH+hsBJeTM7RLFxvD9XF7LKCqNV3ue9AMPxnDJTZ92p39HU7MgW1zfmqSJT/GVIlXiyltkHhnEAmzkDn8RGSf1Hr0tZa2BCF7OcwFalwWnKHC4RRkBv/obMpuBruz+pmT9X8BbPEz1uP4VKECgYEAo3JM6Yy/z+oT0HBvwmCEJIoFWChSpioC/EKxdYrNhp1Pl84imqhEnOnK+doSv4p2Qy+piXemzgIvIRZV4DXpxTVg5Nb8AvLEwoSU3VfL+VfHiYOPqA31rgCG7sTrwxt0xR5VK+Qxboyvv/26WijsJKNSbmZuoX1VvTdn/A838R8CgYEAv7A+uoFMmKkzG0LyM92VHVzlmUckFq/0jbzy7DZ5Uvo+1qy0iW+Xtx8WMCpGCemmpZXZA1oebWgtGZ+mbq4o7pmubMt5r8UQOTaryje2VIay9nu3uYikudAeOqK2TYcwK/T8fhbqTBKH9noucab2owE0LpuxRBSmMjXxy/u8RqECgYB+32LEh/T8t37Z9e1KvRDWLC0p2WMOiN34YuzNiQkwzTvTYreJxcQ86V4dxCF5fUl/rae0haR9lURwMp5GhJMJ+f1UpWzJ/RiuxauohVZYHsNNA0qn8O4cO1yCS2mW8eEaggkrIN8eKSbjqVjWU526cuyxteF6iYfccP3OsyGBkQKBgEOi+1F/ttw+ArE0daAg6988SIh2Il9fP4xhZKvGUy2sRUcar0+MDOj7r9e07gSmvD/S9mtzGYXElES5gKdicM4eY/fd0amPoZv39rPxLrAboEdP52HHFKy0WZlomFXZmrsELQqNVqOfelf0Kh3/JC+DeLfe0bTz+Vh8ppBqZSk2";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    private static String TOTAL_PRICE = null;
    private static String IMAGE_NUMBER = null;

    private static final int RESULT_CODE_ALIPAY = 10086;

    private MaterialToolbar toolbar;
    private TextView payTotalPrice;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Log.d(TAG, "handleMessage: 支付成功 -> "+payResult);
                        Toast.makeText(getApplicationContext(),"支付成功，感谢您的支持",Toast.LENGTH_SHORT).show();
                        //返回支付结果
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("payResult",resultInfo);
                        bundle.putString("resultStatus",resultStatus);
                        intent.putExtras(bundle);
                        setResult(RESULT_CODE_ALIPAY,intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Log.d(TAG, "handleMessage: 支付失败 -> "+payResult);
                        Toast.makeText(getApplicationContext(),"支付失败请稍后再试",Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Log.d(TAG, "handleMessage: 授权成功 -> "+authResult);
                    } else {
                        // 其他状态值则为授权失败
                        Log.d(TAG, "handleMessage: 授权失败 -> "+authResult);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //使用沙箱模式
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay_of_sandbox);

        toolbar = findViewById(R.id.selectPayToobar);
        payTotalPrice = findViewById(R.id.payTotalPrice);
        //状态栏文字透明
        UITools.makeStatusBarTransparent(this);
        //修复标题栏与状态栏重叠
        UITools.fitTitleBar(this, toolbar);

        TOTAL_PRICE = getIntent().getExtras().getString("totalPrice");
        IMAGE_NUMBER = getIntent().getExtras().getString("imageNumber");

        Log.d(TAG, "onCreate: 总价 -> "+TOTAL_PRICE);
        payTotalPrice.setText("¥ "+TOTAL_PRICE);

    }

    /**
     * 支付宝支付业务示例
     */
    public void payV2(View v) {
        
        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2,TOTAL_PRICE,IMAGE_NUMBER);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(AlipayOfSandbox.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}

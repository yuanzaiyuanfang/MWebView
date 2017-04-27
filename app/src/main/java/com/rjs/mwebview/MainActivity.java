package com.rjs.mwebview;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ValueCallback;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rjs.mywebview.browse.CallBackFunction;
import com.rjs.mywebview.browse.JsWeb.CustomWebViewClient;
import com.rjs.mywebview.browse.JsWeb.JavaCallHandler;
import com.rjs.mywebview.browse.JsWeb.JsHandler;
import com.rjs.mywebview.view.ProgressBarWebView;

import java.util.ArrayList;
import java.util.Map;

/**
 * 直接在布局文件中添加了一个WebView，发现也没什么问题啊，一样可以显示，什么都是正常的啊。在重复打开有WebView的页面时，
 * 你会发现，应用的内存会不断升高，销毁了之后也不会降下来，点击GC也降不下来，这样就出现了内存泄漏了，这时你就会发现，
 * 这样使用WebView是不正确的，那么最好方式是如何使用呢？那就是在代码中动态添加。
 * <p>
 * 常用设置
 * webView.loadUrl("www.baidu.com");//WebView加载的网页使用loadUrl
 * WebSettings webSettings = webView.getSettings();//获得WebView的设置
 * webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
 * webSettings.setLoadWithOverviewMode(true);//适配
 * webSettings.setJavaScriptEnabled(true);  //支持js
 * webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
 * webSettings.setDomStorageEnabled(true);// 开启 DOM storage API 功能
 * webSettings.setDatabaseEnabled(true);//开启 database storage API 功能
 * webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//HTTPS，注意这个是在LOLLIPOP以上才调用的
 * webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
 * webSettings.setBlockNetworkImage(true);//关闭加载网络图片，在一开始加载的时候可以设置为true，当加载完网页的时候再设置为false
 */
public class MainActivity extends AppCompatActivity {

    private LinearLayout root_layout;

    // UI references.
    private ProgressBarWebView mProgressBarWebView;

    private ArrayList<String> mHandlers = new ArrayList<>();

    private ValueCallback<Uri> mUploadMessage;

    private static CallBackFunction mfunction;

    private int RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
    }

    /**
     * 初始化
     */
    private void initWebView() {
        root_layout = (LinearLayout) findViewById(R.id.root_layout);
        //=== 动态添加（把WebView当做其子View添加进去）
        //如果你的WebView需要弹出一个dialog呢？还有其他的不可预估的问题的，最好还是用当前的activity的Context是最合适的
        mProgressBarWebView = new ProgressBarWebView(MainActivity.this);
        root_layout.addView(mProgressBarWebView);

        mProgressBarWebView.setWebViewClient(new CustomWebViewClient(mProgressBarWebView.getWebView()) {
            @Override
            public String onPageError(String url) {
                //指定网络加载失败时的错误页面
                return "file:///android_asset/error.html";
            }

            @Override
            public Map<String, String> onPageHeaders(String url) {
                // 可以加入header
                return null;
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                pickFile();
            }
        });

        // 打开页面，也可以支持网络url
        mProgressBarWebView.loadUrl("file:///android_asset/demo.html");
//        mProgressBarWebView.loadUrl("http://www.baidu.com");

        mHandlers.add("login");
        mHandlers.add("callNative");
        mHandlers.add("callJs");
        mHandlers.add("open");
        //回调js的方法
        mProgressBarWebView.registerHandlers(mHandlers, new JsHandler() {
            @Override
            public void OnHandler(String handlerName, String responseData, CallBackFunction function) {
                if (handlerName.equals("login")) {
                    Toast.makeText(MainActivity.this, responseData, Toast.LENGTH_SHORT).show();
                } else if (handlerName.equals("callNative")) {
                    Toast.makeText(MainActivity.this, responseData, Toast.LENGTH_SHORT).show();
                    function.onCallBack("我在上海");
                } else if (handlerName.equals("callJs")) {
                    Toast.makeText(MainActivity.this, responseData, Toast.LENGTH_SHORT).show();
                    // 想调用你的方法：
                    function.onCallBack("好的 这是图片地址 ：xxxxxxx");
                }
                if (handlerName.equals("open")) {
                    mfunction = function;
                    pickFile();
                }
            }
        });

        // 调用js
        mProgressBarWebView.callHandler("callNative", "hello H5, 我是java", new JavaCallHandler() {
            @Override
            public void OnHandler(String handlerName, String jsResponseData) {
                Toast.makeText(MainActivity.this, "h5返回的数据：" + jsResponseData, Toast.LENGTH_SHORT).show();
            }
        });

        //发送消息给js
        mProgressBarWebView.send("哈喽", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 相册中选择图片
     */
    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RESULT_CODE) {
//            if (null == mUploadMessage) {
//                return;
//            } else {
//                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
//                mUploadMessage.onReceiveValue(result);
//                mUploadMessage = null;
//            }
            if (intent == null || intent.getData() == null) {
                mfunction.onCallBack("没有选择");
            } else {
                mfunction.onCallBack(intent.getData().toString());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressBarWebView.getWebView() != null) {
            mProgressBarWebView.getWebView().destroy();
        }
    }
}
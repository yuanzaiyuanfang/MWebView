# MWebView
根据 [Tamicer/JsWebView](https://github.com/Tamicer/JsWebView) 修改定制

# 为什么要使用WebView

#### 随着app业务的不断深入发展，只靠着原生代码来堆砌功能是不现实，毕竟开发的时长会增加，而且同时需要开发iOS和Android两套，并且，如果在UI上改变了一丁点，都需要提包（虽然Android现在可以进行热更新，但是热更新不是100%能生效的，其中的原理只要了解过的人都会知道的），最终我们会选择使用原生嵌套H5的方式进行开发，这样，既可以随时更改UI，也可以无限制的进行功能扩展，然后，我们就要使用到Android的WebView了，这个让我们痛并快乐着的控件。

Android基于JsBridge封装的高效带加载进度的WebView
可用作简单应用内置浏览器，帮你快速开发Hybrid APP

主要功能：

- 支持header
- 支持进度
- 支持自定义错误页面
- 支持h5和native的快速交互，简单易学
- 支持cookie同步
- 

#UI

![](http://img.blog.csdn.net/20161209180646623?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2s3MTk4ODc5MTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast
)
#Dependencies

**Gradle:**  

root：

    repositories {
    maven { url "https://jitpack.io" }
    jcenter()
      }

Module:

       dependencies {
       .....
       compile 'com.tamic:browse:1.0.0'
    
       }

# Function
  
    **XMl**

     <com.rjs.mywebview.view.ProgressBarWebView
             android:id="@+id/login_progress_webview"
             style="@style/NumberProgressBar_Default"
             android:layout_width="match_parent"
             android:layout_height="match_parent" />
     
  **建议：代码中动态生成(防止重复打开导致的内容泄露)**
  ```java
   mProgressBarWebView = new ProgressBarWebView(MainActivity.this);
   root_layout.addView(mProgressBarWebView);
```
           
  **初始化**
    **XMl**
    
    ProgressBarWebView  mProgressBarWebView = (ProgressBarWebView) findViewById(R.id.login_progress_webview);
      
  **设置WebViewClient**
  
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

            
        });

        // 打开页面，也可以支持网络url
        mProgressBarWebView.loadUrl("file:///android_asset/demo.html");
        
**回调js的方法**

        // 添加hander方法名 
        mHandlers.add("login");
       // 订阅此方法key
        mProgressBarWebView.registerHandlers(mHandlers, new JsHandler() {
            @Override
            public void OnHandler(String handlerName, String responseData, CallBackFunction function) {

                    String resquestData = "this native data"
                    
                     // 返回数据给js
                    function.onCallBack(resquestData);
               
            }
        });
**调用js**

        mProgressBarWebView.callHandler("callNative", "hello H5, 我是java", new JavaCallHandler() {
            @Override
            public void OnHandler(String handlerName, String jsResponseData) {
                Toast.makeText(MainActivity.this, "h5返回的数据：" + jsResponseData, Toast.LENGTH_SHORT).show();
            }
        });
        
**发送消息给js**

        mProgressBarWebView.send("hello world!", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
            
            // data 为js回传数据

                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();

            }
        });

**属性配置**

```java
webView.loadUrl("www.baidu.com");//WebView加载的网页使用loadUrl
WebSettings webSettings = webView.getSettings();//获得WebView的设置
webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
webSettings.setLoadWithOverviewMode(true);//适配
webSettings.setJavaScriptEnabled(true);  //支持js
webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
webSettings.setDomStorageEnabled(true);// 开启 DOM storage API 功能
webSettings.setDatabaseEnabled(true);//开启 database storage API 功能
webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//HTTPS，注意这个是在LOLLIPOP以上才调用的
webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
webSettings.setBlockNetworkImage(true);//关闭加载网络图片，在一开始加载的时候可以设置为true，当加载完网页的时候再设置为false
```

**WebChromeClient和WebViewClient**

```java
webView.setWebChromeClient(new WebChromeClient() {    
     @Override   
     public void onProgressChanged(WebView view, int newProgress) {
         //加载的进度
     }
     @Override
     public void onReceivedTitle(WebView view, String title) {   
         //获取WebView的标题
     }
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {    
        return super.onJsAlert(view, url, message, result);
        //Js 弹框
    }
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {   
        AlertDialog.Builder b = new AlertDialog.Builder(IllegalQueryActivity.this);    
        b.setTitle("删除");    
        b.setMessage(message);    
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {        
            @Override        
            public void onClick(DialogInterface dialog, int which) {            
                result.confirm();        
            }    
        });    
        b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {        
            @Override        
            public void onClick(DialogInterface dialog, int which) {            
                result.cancel();        
            }    
        });    
        b.create().show();    
        return true;
    }
});

webView.setWebViewClient(new WebViewClient() {    
    @Override    
    public boolean shouldOverrideUrlLoading(WebView view, String url) {        
       //需要设置在当前WebView中显示网页，才不会跳到默认的浏览器进行显示
       return true;   
    }    
    @Override    
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        //加载出错了
    }   
    @Override    
    public void onPageFinished(WebView view, String url) {        
        super.onPageFinished(view, url);
        //加载完成
    }
});
webView.setDownloadListener(new DownLoadListener());//下载监听
private class DownLoadListener implements DownloadListener {   
    @Override   
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {      
    }
}
```

**然后就是WebView跟JS的交互了**

```java
webView.addJavascriptInterface(new WebAppInterface(this), "WebJs");
public class WebAppInterface { 
    Context mContext;    
    public WebAppInterface(Context c) {        
        mContext = c;    
    }    
    @JavascriptInterface    
    public void method() {
    }
}
webView.loadUrl("javascript:jsMethod()");//这是WebView最简单的调用JS的方法
```

**当activity执行生命周期的时候，这里需要注意的是在onDestroy的时候，需要销毁WebView，不然也会出现内存泄漏的**
```java
@Overrideprotected void onPause() {    
    super.onPause();    
    if (webView != null) {        
        webView.onPause();    
    }
}
@Override
protected void onResume() {    
    super.onResume();    
    if (webView != null) {        
        webView.onResume();    
    }
}
@Override
protected void onDestroy() {        
    if (webView != null) {        
        webView.clearCache(true); //清空缓存   
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {            
            if (webViewLayout != null) {                
                webViewLayout.removeView(webView);            
            }            
        webView.removeAllViews();            
        webView.destroy();        
    }else {            
        webView.removeAllViews();            
        webView.destroy();            
        if (webViewLayout != null) {                
            webViewLayout.removeView(webView);           
        }        
     }  
     webView = null;    
  }   
}
```

#### 可以看到上面的onDestroy方法中对系统的版本进行了判断，那是因为我在不同的版本中进行了测试，如果低于5.0版本的WebView中，如果先在parent中remove了WebView，那WebView将无法进行destroy了，这样就会造成内存的泄漏，下来你们可以自己去尝试一下这个说法是不是正确的。

#### 现在还遇到的一个问题就是，当WebView嵌套在ScrollView中时，某些机型会出现闪屏的问题，单独WebView的时候是不会出现的，把硬件加速关闭了之后，对用户的体验又不好，所以暂时还未想到比较好的解决方案，所以还是建议不要在ScrollView中嵌套WebView这样的控件。

----------------------------
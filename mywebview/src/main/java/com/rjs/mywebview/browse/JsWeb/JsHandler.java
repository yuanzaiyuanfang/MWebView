package com.rjs.mywebview.browse.JsWeb;


import com.rjs.mywebview.browse.CallBackFunction;

/**
 * Class description
 *
 * @author YEZHENNAN220
 * @date 2016-07-08 16:24
 */
public interface JsHandler {

    public void OnHandler(String handlerName, String responseData, CallBackFunction function);

}

package com.bingor.browserlib.util;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * Created by HXB on 2017/11/13.
 */

public class WebViewUtil {
    /**
     * 要过滤的方法数组
     */
    private static final String[] mFilterMethods = {
            "getClass",
            "hashCode",
            "notify",
            "notifyAll",
            "equals",
            "toString",
            "wait",
    };
    private static final String VAR_ARG_PREFIX = "arg";
    private static final String MSG_PROMPT_HEADER = "JmtDemo:";
    /**
     * 对象名
     */
    private static final String KEY_INTERFACE_NAME = "obj";
    /**
     * 函数名
     */
    private static final String KEY_FUNCTION_NAME = "func";
    /**
     * 参数数组
     */
    private static final String KEY_ARG_ARRAY = "args";

    private WebSettings webSettings;
    private WebView webView;

    private String cacheDir;

    /**
     * 缓存addJavascriptInterface的注册对象
     */
    private static HashMap<String, Object> mJsInterfaceMap;
    private static String mJsStringCache;

    public static JSONArray JS_PARAMS_JSON;


    public WebViewUtil(WebView webView) {
        this.webView = webView;
        webSettings = webView.getSettings();
        mJsInterfaceMap = new HashMap<String, Object>();
    }

    public WebViewUtil initWebView() {
        //设置允许执行JavaScript
        webSettings.setJavaScriptEnabled(true);
        //允许使用DOM存储
        webSettings.setDomStorageEnabled(true);

        // 设置允许JS弹窗
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //        webSettings.setLightTouchEnabled(true);
        //        //支持放大缩小
        //        webSettings.setSupportZoom(true);
        //        webSettings.setUseWideViewPort(true);
        //        webSettings.setLoadWithOverviewMode(true);
        //设置放大缩小
//        webSettings.setBuiltInZoomControls(true);
        //隐藏放大缩小的控件
//        webSettings.setDisplayZoomControls(false);
        //        webSettings.setPluginState(WebSettings.PluginState.ON);
        //        webSettings.setDatabaseEnabled(true);
        //        webSettings.setBlockNetworkImage(true);
        //
        ////        webSettings.setPluginsEnabled(WebSettings.LayoutAlgorithm.NORMAL);
        //
        //        //启用地理定位
        //        webSettings.setGeolocationEnabled(true);

//        webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
//        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能

//        webSettings.setSavePassword(false);//关闭密码保存，防止明文密码被窃取

//        cacheDir = activity.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
//        webSettings.setAppCachePath(cacheDir); //设置  Application Caches 缓存目录

//        webSettings.setUseWideViewPort(true);//让webview读取网页设置的viewport，pc版网页
//        webSettings.setLoadWithOverviewMode(true);

        //设置渲染效果优先级，高
//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

//        webSettings.setUserAgentString("ZSjmt/"
//                + BaseGlobal.versionName
//                + " ("
//                + DeviceUtil.getPhoneModel()
//                + "; "
//                + DeviceUtil.getSystemVersion()
//                + ")" + "linkmessenger " + webSettings.getUserAgentString());


        /**
         *  Webview在安卓5.0之前默认允许其加载混合网络协议内容
         *  在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }

//        if (Build.VERSION.SDK_INT >= 17) {
//            webView.addJavascriptInterface(new JSInterface(activity, new JSCallbackHandler(webView)), "NativePlugins");
//            //            webView.addJavascriptInterface(new JSInterface(activity, new JSCallbackHandler(webView)), "jsInterface");
//        } else {
//            webView.removeJavascriptInterface("searchBoxJavaBridge_");
//            mJsInterfaceMap.put("NativePlugins", new JSInterface(activity, new JSCallbackHandler(webView)));
//        }
        return this;
    }

    public WebViewUtil setWebViewClient(WebViewClient webViewClient) {
        webView.setWebViewClient(webViewClient);
        return this;
    }

    public WebViewUtil setWebChromeClient(WebChromeClient webChromeClient) {
        webView.setWebChromeClient(webChromeClient);
        return this;
    }


    public static void clearCookie(Context context) {
        //清空所有Cookie
        CookieSyncManager.createInstance(context);  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now
    }


    public static String getJsCode(String funName, Object... params) {
        String code = "javascript:" + funName + "(";
        if (params != null && params.length > 0) {
            for (Object obj : params) {
                if (obj == null) {
                    continue;
                } else {
                    if (obj instanceof String) {
                        String temp = (String) obj;
                        temp = temp.replace("\"", "\\\"");
                        code += "\"" + temp + "\",";
                    } else {
                        code += "\"" + obj + "\",";

                    }
                }
            }
            code = code.substring(0, code.length() - 1);
        }
        code += ")";
        return code;
    }


    public static String genJavascriptInterfacesString() {
//        if (!NEED_JS) {
//            return null;
//        }
        if (mJsInterfaceMap.size() == 0) {
            mJsStringCache = null;
            return null;
        }

        /*
         * 要注入的JS的格式，其中XXX为注入的对象的方法名，例如注入的对象中有一个方法A，那么这个XXX就是A
         * 如果这个对象中有多个方法，则会注册多个window.XXX_js_interface_name块，我们是用反射的方法遍历
         * 注入对象中的所有带有@JavaScripterInterface标注的方法
         *
         * javascript:(function JsAddJavascriptInterface_(){
         *   if(typeof(window.XXX_js_interface_name)!='undefined'){
         *       console.log('window.XXX_js_interface_name is exist!!');
         *   }else{
         *       window.XXX_js_interface_name={
         *           XXX:function(arg0,arg1){
         *               return prompt('MyApp:'+JSON.stringify({obj:'XXX_js_interface_name',func:'XXX_',args:[arg0,arg1]}));
         *           },
         *       };
         *   }
         * })()
         */

        Iterator<Entry<String, Object>> iterator = mJsInterfaceMap.entrySet().iterator();
        // Head
        StringBuilder script = new StringBuilder();
        script.append("javascript:(function JsAddJavascriptInterface_(){");

        // 遍历待注入java对象，生成相应的js对象
        try {
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                String interfaceName = entry.getKey();
                Object obj = entry.getValue();
                // 生成相应的js方法
                createJsMethod(interfaceName, obj, script);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // End
        script.append("})()");
        return script.toString();
    }


    /**
     * 根据待注入的java对象，生成js方法
     *
     * @param interfaceName 对象名
     * @param obj           待注入的java对象
     * @param script        js代码
     */
    private static void createJsMethod(String interfaceName, Object obj, StringBuilder script) {
        if (TextUtils.isEmpty(interfaceName) || (null == obj) || (null == script)) {
            return;
        }

        Class<? extends Object> objClass = obj.getClass();

        script.append("if(typeof(window.").append(interfaceName).append(")!='undefined'){");
//        if (ConfigXMLHelper.isDebug()) {
//            script.append("    console.log('window." + interfaceName + "_js_interface_name is exist!!');");
//        }

        script.append("}else {");
        script.append("    window.").append(interfaceName).append("={");

        // 通过反射机制，添加java对象的方法
        Method[] methods = objClass.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            // 过滤掉Object类的方法，包括getClass()方法，因为在Js中就是通过getClass()方法来得到Runtime实例
            if (filterMethods(methodName)) {
                continue;
            }

            script.append("        ").append(methodName).append(":function(");
            // 添加方法的参数
            int argCount = method.getParameterTypes().length;
            if (argCount > 0) {
                int maxCount = argCount - 1;
                for (int i = 0; i < maxCount; ++i) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(argCount - 1);
            }

            script.append(") {");

            // Add implementation
            if (method.getReturnType() != void.class) {
                script.append("            return ").append("prompt('").append(MSG_PROMPT_HEADER).append("'+");
            } else {
                script.append("            prompt('").append(MSG_PROMPT_HEADER).append("'+");
            }

            // Begin JSON
            script.append("JSON.stringify({");
            script.append(KEY_INTERFACE_NAME).append(":'").append(interfaceName).append("',");
            script.append(KEY_FUNCTION_NAME).append(":'").append(methodName).append("',");
            script.append(KEY_ARG_ARRAY).append(":[");
            //  添加参数到JSON串中
            if (argCount > 0) {
                int max = argCount - 1;
                for (int i = 0; i < max; i++) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(max);
            }

            // End JSON
            script.append("]})");
            // End prompt
            script.append(");");
            // End function
            script.append("        }, ");
        }

        // End of obj
        script.append("    };");
        // End of if or else
        script.append("}");
    }

    /**
     * 检查是否是被过滤的方法
     *
     * @param methodName
     * @return
     */
    private static boolean filterMethods(String methodName) {
        for (String method : mFilterMethods) {
            if (method.equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析JavaScript调用prompt的参数message，提取出对象名、方法名，以及参数列表，再利用反射，调用java对象的方法。
     *
     * @param view
     * @param url
     * @param message      MyApp:{"obj":"jsInterface","func":"onButtonClick","args":["从JS中传递过来的文本！！！"]}
     * @param defaultValue
     * @param result
     * @return
     */
    public static boolean handleJsInterface(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        String prefix = MSG_PROMPT_HEADER;
        if (!message.startsWith(prefix)) {
            return false;
        }

        String jsonStr = message.substring(prefix.length());
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            // 对象名称
            String interfaceName = jsonObj.getString(KEY_INTERFACE_NAME);
            // 方法名称
            String methodName = jsonObj.getString(KEY_FUNCTION_NAME);
            // 参数数组
            JSONArray argsArray = jsonObj.getJSONArray(KEY_ARG_ARRAY);
            Object[] args = null;
            if (null != argsArray) {
                int count = argsArray.length();
                if (count > 0) {
                    args = new Object[count];

                    for (int i = 0; i < count; ++i) {
                        args[i] = argsArray.get(i);
                    }
                }
            }

            if (invokeJSInterfaceMethod(result, interfaceName, methodName, args)) {
                //                if (argsArray.length() > 1) {
                //                    JS_PARAMS_JSON = argsArray.getJSONArray(1);
                //                } else {
                //                    JS_PARAMS_JSON = null;
                //                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.cancel();
        return false;
    }

    /**
     * 利用反射，调用java对象的方法。
     * <p>
     * 从缓存中取出key=interfaceName的java对象，并调用其methodName方法
     *
     * @param result
     * @param interfaceName 对象名
     * @param methodName    方法名
     * @param args          参数列表
     * @return
     */
    private static boolean invokeJSInterfaceMethod(JsPromptResult result, String interfaceName, String methodName, Object[] args) {

        boolean succeed = false;
        final Object obj = mJsInterfaceMap.get(interfaceName);
        if (null == obj) {
            result.cancel();
            return false;
        }

        Class<?>[] parameterTypes = null;
        int count = 0;
        if (args != null) {
            count = args.length;
        }

        if (count > 0) {
            parameterTypes = new Class[count];
            for (int i = 0; i < count; ++i) {
                parameterTypes[i] = getClassFromJsonObject(args[i]);
                args[i] = transformType(args[i], parameterTypes[i]);
            }
        }

        try {
            //            Method[] methods = obj.getClass().getMethods();
            //            for (Method m : methods) {
            //                Log.d(m.getName());
            //            }
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            Object returnObj = method.invoke(obj, args); // 执行接口调用
            boolean isVoid = returnObj == null || returnObj.getClass() == void.class;
            String returnValue = isVoid ? "" : returnObj.toString();
            result.confirm(returnValue); // 通过prompt返回调用结果
            succeed = true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.cancel();
        return succeed;
    }

    /**
     * 解析出参数类型
     *
     * @param obj
     * @return
     */
    private static Class<?> getClassFromJsonObject(Object obj) {
        if (obj == null) {
            return JSONArray.class;
        }
        // js对象只支持int boolean string三种类型
        if (obj instanceof Integer) {
            return Integer.TYPE;
        } else if (obj instanceof Boolean) {
            return Boolean.TYPE;
        } else {
            if (obj.toString().equals("null")) {
                return JSONArray.class;
            }
            try {
                new JSONArray(obj.toString());
            } catch (JSONException e) {
                try {
                    new JSONObject(obj.toString());
                } catch (JSONException e1) {
                    return String.class;
                }
                return JSONObject.class;
            }
            return JSONArray.class;
        }

    }


    private static Object transformType(Object obj, Class<?> type) {
        if (obj == null) {
            return null;
        }
        // js对象只支持int boolean string三种类型
        if (type == Integer.class) {
            return Integer.parseInt(obj.toString());
        } else if (type == Boolean.class) {
            return Boolean.parseBoolean(obj.toString());
        } else if (type == JSONArray.class) {
            if (obj.toString().equals("null")) {
                return null;
            }
            try {
                return new JSONArray(obj.toString());
            } catch (JSONException e) {
                return obj.toString();
            }
        } else {
            return obj.toString();
        }
    }
}

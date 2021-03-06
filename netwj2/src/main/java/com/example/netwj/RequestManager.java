package com.example.netwj;

/**
 * Created by wangjian on 2017/7/21.
 */

import android.app.Application;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 *
 * Response.ErrorListener-----1\string 2\对象
 * handleVolleyError()处理ErrorListener的提示
 */
public class RequestManager {

    private RequestQueue mRequestQueue;
    private Application mApplication;

    public RequestManager(RequestQueue mRequestQueue, Application application) {
        this.mRequestQueue = mRequestQueue;
        mApplication = application;

    }

    /**
     * get 返回String
     *
     * @param url           连接
     * @param tag           上下文
     * @param progressTitle 进度条文字
     * @param showLoading   true带加载
     * @param listener      回调
     */
    public void get(String url, Object tag, String progressTitle, boolean showLoading, RequestListener listener) {
        ByteArrayRequest request = new ByteArrayRequest(Request.Method.GET,
                url, null, responseListener(listener, showLoading, url),
                responseError(listener, showLoading, url));
        //设置超时时间重连次数
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(request, tag);
    }

    /**
     * get 返回对象
     *
     * @param url           连接
     * @param tag           上下文
     * @param classOfT      类对象
     * @param progressTitle 进度条文字
     * @param showLoading   true带加载
     * @param listener      回调
     */
    public <T> void get(String url, Object tag, Class<T> classOfT,
                        String progressTitle, boolean showLoading, RequestJsonListener<T> listener) {

        ByteArrayRequest request = new ByteArrayRequest(Request.Method.GET,
                url, null, responseListener(listener, classOfT, showLoading, url),
                responseError(listener, showLoading, url));
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(request, tag);
    }

    /**
     * 上传图片
     *
     * @param url      接口
     * @param tag      上下文
     * @param params   post需要传的参数
     * @param listener 回调
     *//*
    public static void postImg(String url, Object tag, RequestParams params,
                               RequestListener listener) {
        ByteArrayRequest request = new ByteArrayRequest(Request.Method.POST,
                url, params, responseListener(listener, false),
                responseError(listener, false, null));
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(request, tag);
    }

    public static void post(String url, Object tag, RequestParams params,
                            RequestListener listener) {
        LoadingFragment dialog = new LoadingFragment();
        dialog.show(((FragmentActivity) tag).getSupportFragmentManager(),
                "");
        dialog.setMsg("");
        ByteArrayRequest request = new ByteArrayRequest(Request.Method.POST,
                url, params, responseListener(listener, true, dialog),
                responseError(listener, true, dialog));
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(request, tag);
    }

    */

    /**
     * post 返回String 带进度条 nodata
     *
     * @param url           接口
     * @param tag           上下文
     * @param params        post需要传的参数
     * @param progressTitle 进度条文字
     * @param showLoading   true (显示进度) false (不显示进度)
     * @param listener      回调
     */
    public void post(String url, Object tag, RequestParams params,
                     String progressTitle, boolean showLoading, RequestListener listener) {
        ByteArrayRequest request = new ByteArrayRequest(Request.Method.POST,
                url, params, responseListener(listener, showLoading, url),
                responseError(listener, showLoading, url));
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(request, tag);
    }


    /**
     * post返回对象 带进度条  带进度条 nodata
     *
     * @param url           接口
     * @param tag           上下文
     * @param classOfT      类对象
     * @param params        post需要传的参数
     * @param progressTitle 进度条文字
     * @param showLoading   true (显示进度) false (不显示进度)
     * @param listener      回调
     */
    public <T> void post(String url, Object tag, Class<T> classOfT,
                         RequestParams params, String progressTitle, boolean showLoading,
                         RequestJsonListener<T> listener) {

        ByteArrayRequest request = new ByteArrayRequest(Request.Method.POST,
                url, params,
                responseListener(listener, classOfT, showLoading, url),
                responseError(listener, showLoading, url));
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addRequest(request, tag);
    }

    /**
     * 成功回调   返回string
     *
     * @param l           activity中的回调
     * @param showLoading boolean true 带进度条 flase不带进度条
     * @param url         链接,用于打log
     * @return 回调:加载成功时  string
     */
    protected Response.Listener<byte[]> responseListener(final RequestListener l, final boolean showLoading
            , final String url) {
        return new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] arg0) {
                String data;//全部数据
                String data_request;//请求message
                try {
                    data = new String(arg0, "UTF-8");
                    JSONObject a = new JSONObject(data);
                  /*  data_request = a.getString("msgContent");
                    int code = Integer.parseInt(a.get("msgFlag").toString());*/
                    l.requestSuccess(data);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d("RequestManager", "访问:" + url + "时:数据解码失败");
                    Toast.makeText(mApplication, "数据解码失败", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mApplication, "没有msgContent或msgFlag", Toast.LENGTH_SHORT).show();
                    Log.d("RequestManager", "访问:" + url + "时:没有msgContent/msgFlag");
                }

            }
        };
    }

    /**
     * 成功 返回对象
     *
     * @param l           json版 activity中的回调
     * @param classOfT    gsonbean类型
     * @param showLoading true带进度条
     * @param <T>         都成
     */
    protected <T> Response.Listener<byte[]> responseListener(
            final RequestJsonListener<T> l, final Class<T> classOfT,
            final boolean showLoading, final String url) {
        return new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] arg0) {
                String data;
                String data_request;
                try {
                    data = new String(arg0, "UTF-8");
                    JSONObject a = new JSONObject(data);
                    data_request = a.getString("msgContent");
                    int code = Integer.parseInt(a.get("msgFlag").toString());
                    l.requestSuccess(JsonUtils.object(data, classOfT));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.d("RequestManager", "访问:" + url + "时:数据解码失败");
                    Toast.makeText(mApplication, "数据解码失败", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mApplication, "json解析失败/没有msgContent或msgFlag", Toast.LENGTH_SHORT).show();
                    Log.d("RequestManager", "访问:" + url + "时:json解析失败/没有msgContent或msgFlag");
                }

            }
        };
    }

    /**
     * 错误回调 string
     *
     * @param l               回调
     * @param showLoading     true显示进度条  flase不显示进度条
     * @param url             链接地址
     */
    protected Response.ErrorListener responseError(
            final RequestListener l, final boolean showLoading, final String url) {
        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                if (!VolleyUtils.isNetworkAvailable(mApplication)) {
                    l.noInternet(e, "无网络请检查网络连接");
                }else{
                    String error = handleVolleyError(e);
                    l.requestError(e, error);
                }
            }
        };
    }

    /**
     * 错误回调 对象
     *
     * @param l               回调
     * @param showLoading     true 显示加载  flase不显示加载
     * @param url             链接地址
     */
    protected <T> Response.ErrorListener responseError(
            final RequestJsonListener<T> l, final boolean showLoading,
            final String url) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                if (!VolleyUtils.isNetworkAvailable(mApplication)) {
                    l.noInternet(e, "无网络请检查网络连接");
                }else{
                    String error = handleVolleyError(e);
                    l.requestError(e, error);
                }
            }
        };
    }

    /**
     * @param request 请求
     * @param tag     上下文
     */
    public void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }

    /**
     * 当主页面调用协议 在结束该页面调用此方法
     *
     * @param context 上下文
     */
    public void cancelAll(Object context) {
        mRequestQueue.cancelAll(context);
    }


    /**
     * 请求失败处理
     *
     * @param error VolleyError
     * @return 错误提示
     */
    public String handleVolleyError(VolleyError error) {
        if (error == null) {
            return "error == null";
        } else if (error.networkResponse == null) {
            return "服务器没开\nerror.networkResponse == null";
        } else if (error.networkResponse.statusCode == 500) {
            return "因为意外情况，服务器不能完成请求\ncode码:500";
        } else if (error.networkResponse.statusCode == 404) {
            return "服务器找不到给定的资源/文档不存在\n" +
                    "code码:404";
        } else if (error.networkResponse.statusCode == 401) {
            return "未授权客户机访问数据\n" +
                    "code码:401";
        } else if (error.networkResponse.statusCode == 406) {
            return "无法接受 \n" +
                    "code码:406";
        } else if (error.networkResponse.statusCode == 400) {
            return "请求中有语法问题，或不能满足请求\n" +
                    "code码:400";
        } else {
            return "我也不知道为啥了,错误为:\n" +
                    "code码:" + error.toString();
        }
    }


}

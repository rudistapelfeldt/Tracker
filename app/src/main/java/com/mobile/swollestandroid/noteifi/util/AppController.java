package com.mobile.swollestandroid.noteifi.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/*
* App: DiaperDad
* Name: AppController
* Author: Rudolph Stapelfeldt
* Date: 2016-07-09
* Summary: Controls app requests to webservice
*/
public class AppController extends Application {
    private static final String TAG="AppController";
    private static AppController mInstance;

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    private RequestQueue mRequestQueue;
    private static ImageLoader.ImageCache mCache;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SHOPFRAGMENT", "Appcontroller running");
        mInstance = this;
        mCache = new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                    cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        };
        mImageLoader = new ImageLoader(getRequestQueue(),mCache);
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req){
        Log.i("SHOPFRAGMENT", "IN ADD TO REQUESTQUEUE");
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if (mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }
    }

    public static  ImageLoader.ImageCache getCache(){
        return mCache;
    }
    public ImageLoader getImageLoader(Context context) {
        return mImageLoader;
    }
}

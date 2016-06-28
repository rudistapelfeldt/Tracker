package com.lavalamp.assessment.tracker_v11;

/**
 * Created by Rudolph on 2016/06/27.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Rudolph on 2016/06/09.
 */
public class SearchLocationTask extends AsyncTask<String, Void, String []> {
    private HashMap<Integer, String> h;
    private List<String> list;
    public AsyncLocationResponse delegate = null;

    public SearchLocationTask(AsyncLocationResponse response){
        super();
        delegate = response;
    }

    @Override
    protected void onPostExecute(String [] h) {
        super.onPostExecute(h);
        delegate.processFinish(h);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String [] doInBackground(String... params) {
        String url = params[0];
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.i("APPLICATIONRESPONSE", "RESPONSE = " + response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // handle error response
                    }
                }
        );
            AppController.getInstance().addToRequestQueue(request);

        return params;
    }
}


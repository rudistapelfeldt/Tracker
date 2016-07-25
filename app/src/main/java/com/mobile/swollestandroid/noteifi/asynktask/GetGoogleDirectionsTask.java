package com.mobile.swollestandroid.noteifi.asynktask;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mobile.swollestandroid.noteifi.interfaces.AsyncGoogleDirectionResponse;
import com.mobile.swollestandroid.noteifi.util.AppController;
import com.mobile.swollestandroid.noteifi.util.GoogleDirectionsResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rudolph on 2016/06/10.
 */
public class GetGoogleDirectionsTask extends AsyncTask<String, Integer, GoogleDirectionsResponseHandler> {

    //private ArrayList<Model> itemArray = new ArrayList<>();
    public AsyncGoogleDirectionResponse delegate = null;
    //private ProgressBar progressBar;

    public GetGoogleDirectionsTask(AsyncGoogleDirectionResponse response)
    {
        super();
        delegate = response;
    }

    //public void setProgressBar(ProgressBar bar) {
    //    this.progressBar = bar;
    //}

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //if (this.progressBar != null) {
         //   progressBar.setProgress(values[0]);
        //}
    }

    @Override
    protected void onPostExecute(GoogleDirectionsResponseHandler model) {
        super.onPostExecute(model);
        delegate.processFinish(model);
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    protected GoogleDirectionsResponseHandler doInBackground(String... params) {
        String url = params[0];
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("NOTEIFILOG", "response = " + response);
                        //GoogleDirectionsResponseHandler googleDirectionsResponseHandler = new GoogleDirectionsResponseHandler();
                        //GetGoogleDirectionsTask.this.onPostExecute(googleDirectionsResponseHandler);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                AppController.getInstance().addToRequestQueue(jsObjRequest);
                return null;
    }
}

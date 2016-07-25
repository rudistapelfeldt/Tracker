package com.mobile.swollestandroid.noteifi.interfaces;


import com.mobile.swollestandroid.noteifi.util.GoogleDirectionsResponseHandler;

import java.util.ArrayList;

/**
 * Created by Rudolph on 2016/06/10.
 */
public interface AsyncGoogleDirectionResponse {

    void processFinish(GoogleDirectionsResponseHandler responseHandler);
}

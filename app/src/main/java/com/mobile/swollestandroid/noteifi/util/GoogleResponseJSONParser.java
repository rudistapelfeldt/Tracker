package com.mobile.swollestandroid.noteifi.util;

import com.google.android.gms.maps.model.LatLng;
import com.mobile.swollestandroid.noteifi.trip.parameters.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rudi on 7/24/2016.
 */
public class GoogleResponseJSONParser {

    private JSONObject jsonObject;


    public GoogleDirectionsResponseHandler parse(JSONObject response){
        GoogleDirectionsResponseHandler handler = new GoogleDirectionsResponseHandler();
        try {
            JSONArray waypoints = response.getJSONArray("geocoded_waypoints");
            for(int i = 0; i < waypoints.length();i++){
                JSONObject wp = waypoints.getJSONObject(i);
                handler.getWaypoints().getPlaceID().add(wp.getString("place_id"));
            }
            JSONArray mRoutes = response.getJSONArray("routes");
            for(int i = 0; i < mRoutes.length();i++){
                JSONObject rs = mRoutes.getJSONObject(i);
                handler.getRoute().getSummary().add(rs.getString("summary"));
                handler.getRoute().getCopyrights().add(rs.getString("copyrights"));
                handler.getRoute().getWarnings().add(rs.getString("warnings"));
                JSONArray lgs = rs.getJSONArray("legs");
                for(int j = 0; j < lgs.length();j++){
                    JSONObject leg = lgs.getJSONObject(j);
                    JSONArray step = leg.getJSONArray("steps");
                    for(int k = 0; k < step.length();k++){
                        JSONObject stepObject = step.getJSONObject(k);
                        Steps steps = new Steps();
                        JSONObject sDistance = stepObject.getJSONObject("distance");
                        steps.setDistance(sDistance.getString("text"));
                        JSONObject sDuration = stepObject.getJSONObject("duration");
                        steps.setDuration(sDuration.getString("text"));
                        JSONObject stLocation = stepObject.getJSONObject("start_location");
                        steps.setStartLocation(new LatLng(Float.parseFloat(stLocation.getString("lat")), Float.parseFloat(stLocation.getString("lng"))));
                        JSONObject endLocation = stepObject.getJSONObject("end_location");
                        steps.setEndLocation(new LatLng(Float.parseFloat(endLocation.getString("lat")), Float.parseFloat(endLocation.getString("lng"))));
                        JSONObject polyline = stepObject.getJSONObject("polyline");
                        steps.setPolyline(polyline.getString("points"));
                        steps.setHtmlInstructions(stepObject.getString("html_instructions"));
                        handler.getLegs().getSteps().add(steps);

                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return handler;
    }
}

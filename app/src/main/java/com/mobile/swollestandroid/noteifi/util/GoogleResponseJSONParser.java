package com.mobile.swollestandroid.noteifi.util;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mobile.swollestandroid.noteifi.trip.parameters.LatLong;
import com.mobile.swollestandroid.noteifi.trip.parameters.Legs;
import com.mobile.swollestandroid.noteifi.trip.parameters.Route;
import com.mobile.swollestandroid.noteifi.trip.parameters.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudi on 7/24/2016.
 */
public class GoogleResponseJSONParser {

    private JSONObject jsonObject;

    public GoogleDirectionsResponseHandler parse(JSONObject response){
        GoogleDirectionsResponseHandler handler = new GoogleDirectionsResponseHandler();
        RouteDetail routeDetail = new RouteDetail();
        try {
            /*JSONArray waypoints = response.getJSONArray("geocoded_waypoints");
            for(int i = 0; i < waypoints.length();i++){
                JSONObject wp = waypoints.getJSONObject(i);
                handler.getWaypoints().getPlaceID().add(wp.getString("place_id"));
            }*/
            JSONArray mRoutes = response.getJSONArray("routes");

            for(int i = 0; i < mRoutes.length();i++){
                handler.getRoute().add(i, new Route());
                routeDetail.setId(i);
                Log.i("JSONPARSERLOG", "Route number " + i);
                JSONObject rs = mRoutes.getJSONObject(i);
                //Get polyline
                JSONObject overviewPolylines = rs.getJSONObject("overview_polyline");
                handler.getRoute().get(i).setEncodedString(overviewPolylines.getString("points"));
                handler.getRoute().get(i).setSummary(rs.getString("summary"));
                routeDetail.setSummary(rs.getString("summary"));
                handler.getRoute().get(i).getCopyrights().add(rs.getString("copyrights"));
                handler.getRoute().get(i).setWarnings(rs.getString("warnings"));
                routeDetail.setWarnings(rs.getString("warnings"));
                JSONArray lgs = rs.getJSONArray("legs");
                for(int j = 0; j < lgs.length();j++){
                    JSONObject leg = lgs.getJSONObject(j);

                    handler.getRoute().get(i).getLegs().add(j, new Legs());
                    handler.getRoute().get(i).getLegs().get(j).setStartAddress(leg.getString("start_address"));
                    handler.getRoute().get(i).getLegs().get(j).setEndAddress(leg.getString("end_address"));
                    if (leg.has("duration_in_traffic")) {
                        //duration in traffic
                        JSONObject lDurationInTraffic = leg.getJSONObject("duration_in_traffic");

                        if (lDurationInTraffic != null) {
                            routeDetail.getlDurationInTraffic().add(j, lDurationInTraffic.getLong("value"));
                        }
                    }
                    //duration
                    JSONObject lDuration = leg.getJSONObject("duration");
                    if (lDuration != null) {
                        routeDetail.getlDuration().add(j, lDuration.getLong("value"));
                    }

                    JSONArray step = leg.getJSONArray("steps");
                    for(int k = 0; k < step.length();k++){
                        JSONObject stepObject = step.getJSONObject(k);
                        handler.getRoute().get(i).getLegs().get(j).getSteps().add(k, new Steps());
                        //Steps steps = new Steps();
                        JSONObject sDistance = stepObject.getJSONObject("distance");
                        handler.getRoute().get(i).getLegs().get(j).getSteps().get(k).setDistance(sDistance.getString("text"));
                        JSONObject sDuration = stepObject.getJSONObject("duration");
                        handler.getRoute().get(i).getLegs().get(j).getSteps().get(k).setDuration(sDuration.getString("text"));
                        JSONObject stLocation = stepObject.getJSONObject("start_location");
                        handler.getRoute().get(i).getLegs().get(j).getSteps().get(k).setStartLocation(new LatLong(Double.valueOf(stLocation.getString("lat")), Double.valueOf(stLocation.getString("lng"))));
                        JSONObject endLocation = stepObject.getJSONObject("end_location");
                        handler.getRoute().get(i).getLegs().get(j).getSteps().get(k).setEndLocation(new LatLong(Double.valueOf(endLocation.getString("lat")), Double.valueOf(endLocation.getString("lng"))));
                        JSONObject polyline = stepObject.getJSONObject("polyline");
                        handler.getRoute().get(i).getLegs().get(j).getSteps().get(k).setPolyline(polyline.getString("points"));
                        handler.getRoute().get(i).getLegs().get(j).getSteps().get(k).setHtmlInstructions(stepObject.getString("html_instructions"));

                    }
                }
                handler.getRoute().get(i).setRouteDetails(routeDetail);
            }
        } catch (JSONException e) {
            Log.e("JSONPARSERLOG", e.getMessage());
            e.printStackTrace();
        }
        return handler;
    }


}
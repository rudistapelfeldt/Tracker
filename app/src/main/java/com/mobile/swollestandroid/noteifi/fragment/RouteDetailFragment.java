package com.mobile.swollestandroid.noteifi.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobile.swollestandroid.noteifi.R;
import com.mobile.swollestandroid.noteifi.activity.PlanTripActivity;
import com.mobile.swollestandroid.noteifi.util.Constants;
import com.mobile.swollestandroid.noteifi.util.RouteDetail;

import java.util.Locale;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RouteDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RouteDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteDetailFragment extends Fragment implements View.OnClickListener{

    SharedPreferences sharedPreferences;
    private int mode;
    EditText etSummary, etWarnings, etDuration, etDurationInTraffic;
    Button btnGotIt;
    RouteDetail routeDetail;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RouteDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RouteDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteDetailFragment newInstance(String param1, String param2) {
        RouteDetailFragment fragment = new RouteDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_route_detail, container, false);
        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREFS, mode);
        mode = Activity.MODE_PRIVATE;
        //Bundle
        Bundle args = getArguments();
        routeDetail = (RouteDetail)args.getSerializable("routeDetail");

        //Edit Text
        etSummary = (EditText)rootView.findViewById(R.id.etSummary);
        etWarnings = (EditText)rootView.findViewById(R.id.etWarnings);
        etDuration = (EditText)rootView.findViewById(R.id.etDuration);
        etDurationInTraffic = (EditText)rootView.findViewById(R.id.etDurationInTraffic);

        //set editText
        if (routeDetail != null) {
            etSummary.setText(routeDetail.getSummary());
            if (!routeDetail.getWarnings().equals("[]")) {
                etWarnings.setText(routeDetail.getWarnings());
            }else{
                etWarnings.setText("");
            }
            if(routeDetail.getlDuration().size() > 0) {
                etDuration.setText(getReadableDuration(routeDetail.getDurationTotal()));
            }
            if (routeDetail.getlDurationInTraffic().size() > 0) {
                etDurationInTraffic.setText(getReadableDuration(routeDetail.getDurationInTrafficTotal()));
            }
        }
        //Button
        btnGotIt = (Button)rootView.findViewById(R.id.btnGotIt);
        btnGotIt.setOnClickListener(this);

        return rootView;
    }

    private String getReadableDuration(long duration){

        long longVal = duration;
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        String readableDuration = hours + " hours " + mins + " minutes " + secs + " seconds";
        return readableDuration;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case R.id.btnGotIt:
                PlanTripActivity planTrip = new PlanTripActivity();
                getActivity().finish();
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                sendTripNotification();
                break;
        }
    }

    private void sendTripNotification(){
        Set<String> numbers = sharedPreferences.getStringSet("Recipients", null);
        if(numbers != null) {
            for (String number : numbers) {
                SmsManager sms = SmsManager.getDefault();
                String strMessage = "Hi. I am leaving " + routeDetail.getOrigin() + ", and traveling to " + routeDetail.getDestination() + ". I should be at "
                        + routeDetail.getDestination() + " in " + etDurationInTraffic.getText() + ".";
                sms.sendTextMessage(number, null, strMessage, null, null);
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

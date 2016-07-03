package com.lavalamp.assessment.tracker_v11;

/**
 * Created by Rudolph on 2016/07/01.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<String> mainDataList = null;
    List<CheckBox> list = new ArrayList<CheckBox>();
    private ViewHolder holder;

    public PlaceAdapter(Context context, List<String> mainDataList) {
        mContext = context;
        this.mainDataList = mainDataList;
        inflater = LayoutInflater.from(mContext);
    }
    static class ViewHolder {
        protected CheckBox check;
    }
    @Override
    public int getCount() {
        return this.mainDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mainDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_row, null);
            holder.check = (CheckBox) view.findViewById(R.id.cbListRow);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.check.setText(mainDataList.get(position));
        holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox bView = (CheckBox)buttonView;
                bView.setChecked(isChecked);
                if (isChecked) {
                    list.add(bView);
                }else{
                    list.remove(bView);
                }
            }
        });
        return view;
    }

    public List<CheckBox> getSelectBoxes(){
        return this.list;
    }
}

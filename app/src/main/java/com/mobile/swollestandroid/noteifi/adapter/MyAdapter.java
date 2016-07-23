package com.mobile.swollestandroid.noteifi.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mobile.swollestandroid.noteifi.util.Model;
import com.lavalamp.assessment.noteifi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rudolph on 2016/07/06.
 */
public class MyAdapter extends ArrayAdapter<Model> {

    private final List<Model> list;
    private List<String> selectedBoxes = new ArrayList<>();
    private final Activity context;

    public MyAdapter(Activity context, List<Model> list) {
        super(context, R.layout.list_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            convertView = inflator.inflate(R.layout.list_row, null);
            viewHolder = new ViewHolder();
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.cbListRow);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    list.get(getPosition).setSelected(buttonView.isChecked());
                    if(isChecked){
                        selectedBoxes.add(list.get(getPosition).getName());
                    }else{
                        if(selectedBoxes.contains(list.get(getPosition).getName())){
                            selectedBoxes.remove(list.get(getPosition).getName());
                        }
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.cbListRow, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkbox.setText(list.get(position).getName());
        viewHolder.checkbox.setTag(position);
        viewHolder.checkbox.setChecked(list.get(position).isSelected());

        return convertView;
    }

    public List<String> getSelectBoxes(){
        return selectedBoxes;
    }


}


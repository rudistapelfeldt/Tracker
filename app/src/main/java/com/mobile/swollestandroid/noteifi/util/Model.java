package com.mobile.swollestandroid.noteifi.util;

/**
 * Created by Rudolph on 2016/07/06.
 */
public class Model {

    private String name;
    private boolean selected;

    public Model(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}


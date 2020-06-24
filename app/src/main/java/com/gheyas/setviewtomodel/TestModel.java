package com.gheyas.setviewtomodel;

import android.icu.text.TimeZoneNames;

import java.lang.reflect.Field;
import java.util.jar.Attributes;

public class TestModel {
    private String NameText;
    private int NameTag;

    public String getNameText() {
        return NameText;
    }

    public void setNameText(String nameText) {
        NameText = nameText;
    }

    public int getNameTag() {
        return NameTag;
    }

    public void setNameTag(int nameTag) {
        NameTag = nameTag;
    }
}

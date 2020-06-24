package com.gheyas.setviewtomodel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Map<String, String> dictionary = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnViewToModel = findViewById(R.id.btn_view_to_model);
        Button btnModelToView = findViewById(R.id.btn_model_to_view);
        TextInputEditText editText = findViewById(R.id.text_xml);
        TagValueModel tagValueModel = new TagValueModel();
        editText.setTag(1);
        tagValueModel.setText("NameText");
        tagValueModel.setTag("NameTag");
        editText.setTag(R.string.view_tag, tagValueModel);

        btnViewToModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestClass testClass = (TestClass) setViewToModel(TestClass.class);
                int a = 1;
            }
        });

        btnModelToView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestClass testClass = new TestClass();
                testClass.setNameText("AliDoran");
                testClass.setNameTag(1);
                setModelToViews(testClass);
            }
        });


    }

    private void getChildView() {
        ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        getChildView(root, dictionary);
    }

    private void getChildView(View v, Map<String, String> entityList) {
        try {
            TagValueModel tagValueModel = (TagValueModel) v.getTag(R.string.view_tag);
            String key;
            String value = "";
            if (v instanceof TextInputEditText) {
                if (!tagValueModel.getText().isEmpty()) {
                    key = tagValueModel.getText();
                    value = ((TextInputEditText) v).getText().toString();
                    entityList.put(key, value);
                }
                if (!tagValueModel.getTag().isEmpty()) {
                    key = tagValueModel.getTag();
                    value = ((TextInputEditText) v).getTag().toString();
                    entityList.put(key, value);
                }
            } else if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    getChildView(child, entityList);
                }
            }
        } catch (Exception e) {

        }
    }

    private void setModelToViewByTag(View v, String fieldName, Object value) {
        try {
            if (v instanceof MaterialTextView || v instanceof TextInputEditText) {
                int viewId = v.getId();
                if (value != null && viewId != -1) {
                    String id = getResources().getResourceEntryName(viewId);
                    String[] keys = id.split("___", -1);
                    for (int i = 0; i < keys.length; i++) {
                        if (keys[i].equals(fieldName)) {
                            if (i == 0 && keys.length > 1)
                                v.setTag(value.toString());
                            else {
                                if (v instanceof MaterialTextView)
                                    ((MaterialTextView) v).setText(value.toString());
                                else
                                    ((TextInputEditText) v).setText(value.toString());
                            }
                        }
                    }
                }

            } else if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    setModelToViewByTag(child, fieldName, value);
                }
            }
        } catch (Exception e) {
            Log.d("SetViewError", e.toString());
        }

    }

    public <Model> void setModelToViews(Model model) {
        ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        try {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                setModelToViewByTag(root, field.getName(), field.get(model));
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

    }

    public <T> Object setViewToModel(Class<T> type) {
        getChildView();
        try {
            T model = type.newInstance();
            Field[] allFields = model.getClass().getDeclaredFields();

            for (Field f : allFields) {
                if (f.getType() == Long.TYPE || f.getType() == Integer.TYPE) {
                    f.setAccessible(true);
                    f.set(model, -1);
                }
            }
            for (Map.Entry<String, String> entry : dictionary.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                for (Field field : allFields) {
                    if (field.getName().equals(key)) {
                        field.setAccessible(true);
                        field.set(model, getValue(field.getType().toString(), value));
                        break;
                    }
                }
            }
            dictionary.clear();
            return model;
        } catch (Exception e) {
            Log.e("convertDictionary", e.getMessage());
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .show();
        }
        return null;
    }

    private Object getValue(String type, String value) {
        try {
            if (type.contains("long") || type.contains("Long")) {
                return (value.isEmpty() ? -1 : Long.parseLong(value.replace(",", "")));
            } else if (type.contains("int")) {
                return Integer.parseInt(value);
            } else if (type.contains("float")) {
                return Long.parseLong(value);
            } else if (type.contains("boolean")) {
                return value.equals("true");
            }
        } catch (Exception e) {
            Log.e("getValue", e.getMessage());
        }
        return value;
    }

}


package com.example.meetinglocation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class AddressItemView extends LinearLayout implements Serializable {
    TextView textView;
    TextView textView2;

    public AddressItemView(Context context) {
        super(context);

        init(context);
    }

    public AddressItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.address_item, this, true);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
    }

    public void setAddress(String address) {
        textView.setText(address);
    }

    public void setName(String name) {
        textView2.setText(name);
    }


}

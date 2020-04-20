package com.example.meetinglocation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class FriendsItemView extends LinearLayout {
    TextView textView;
    TextView textView2;

    public FriendsItemView(Context context) {
        super(context);

        init(context);
    }

    public FriendsItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.friends_item, this, true);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
    }

    public void setName(String name) {
        textView.setText(name);
    }

    public void setAddress(String address) {
        textView2.setText(address);
    }
}

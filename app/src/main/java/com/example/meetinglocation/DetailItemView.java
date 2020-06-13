package com.example.meetinglocation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DetailItemView extends LinearLayout {
    TextView detailName;
    TextView detailTime;

    public DetailItemView(Context context) {
        super(context);

        init(context);
    }

    public DetailItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.detail_item, this, true);

        detailName = (TextView) findViewById(R.id.detailname);
        detailTime = (TextView) findViewById(R.id.detailtime);
    }

    public void setDetailName(String name) {
        detailName.setText(name);
    }

    public void setDetailTime(String time) {
        detailTime.setText(time);
    }
}

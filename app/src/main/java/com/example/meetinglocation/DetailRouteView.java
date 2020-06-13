package com.example.meetinglocation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DetailRouteView extends LinearLayout {
    TextView detailName;
    TextView detailRoute;

    public DetailRouteView(Context context) {
        super(context);

        init(context);
    }

    public DetailRouteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.detail_item, this, true);

        detailName = (TextView) findViewById(R.id.detailname);
        detailRoute = (TextView) findViewById(R.id.detailtime);
    }

    public void setDetailName(String name) {
        detailName.setText(name);
    }

    public void setDetailRoute(String time) {
        detailRoute.setText(time);
    }
}

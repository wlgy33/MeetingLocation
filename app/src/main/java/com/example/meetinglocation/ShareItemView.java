package com.example.meetinglocation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ShareItemView extends LinearLayout implements Serializable {
    TextView shareName;
    ImageView share;

    public ShareItemView(Context context) {
        super(context);

        init(context);
    }

    public ShareItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.share_item, this, true);

        shareName = (TextView) findViewById(R.id.shareName);
    }



    public void setName(String name) {
        shareName.setText(name);
    }


}

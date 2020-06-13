package com.example.meetinglocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DetailInfo extends Activity {
    private static final String TAG = "SHARE";

    ImageView close;
    ListView detailList;
    DetailAdapter detailAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_info);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.5));

        close = findViewById(R.id.close);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(DetailInfo.this);
                finish();
            }
        });

        detailList = findViewById(R.id.detailView);
        detailAdapter = new DetailAdapter();
        detailList.setAdapter(detailAdapter);
        Intent intent = getIntent();
        detailAdapter.items = (ArrayList<AddressItem>) intent.getSerializableExtra("list");
        detailAdapter.notifyDataSetChanged();
    }


    public void linearOnClick(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }
    public static void hideSoftKeyboard(Activity context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getApplicationWindowToken(),0);
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    class DetailAdapter extends BaseAdapter {
        ArrayList<AddressItem> items = new ArrayList<>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DetailItemView  view = null;

            if (convertView == null) {
                view = new DetailItemView(getApplicationContext());


            } else {
                view = (DetailItemView) convertView;
            }

            AddressItem item = items.get(position);
            view.setDetailName(item.getName());
            view.setDetailTime(item.getTime());
            return view;
        }
    }
}

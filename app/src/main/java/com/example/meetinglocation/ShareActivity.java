package com.example.meetinglocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ShareActivity extends Activity {
    private static final String TAG = "SHARE";

    ImageView share;
    ImageView close;
    ListView shareList;
    ShareAdapter shareAdapter;
    String centroid;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_share);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.5));

        close = findViewById(R.id.close);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(ShareActivity.this);
                finish();
            }
        });

        shareList = findViewById(R.id.shareView);
        shareAdapter = new ShareAdapter();
        shareList.setAdapter(shareAdapter);
        Intent intent = getIntent();
        shareAdapter.items = (ArrayList<AddressItem>) intent.getSerializableExtra("list");
        centroid = intent.getStringExtra("centroid");
        if (centroid != (null)) {
            int comma = centroid.indexOf(',');
            String latcen = centroid.substring(0, comma);
            String lngcen = centroid.substring(comma + 2);
            centroid = latcen + "," + lngcen;
        }
        shareAdapter.notifyDataSetChanged();
        shareList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String latlng = shareAdapter.items.get(position).latitude+","+shareAdapter.items.get(position).longitude;
                String url = "https://www.google.com/maps/dir/?api=1&origin="+latlng+"&destination="+centroid+"&travelmode=transit";
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,url);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent,null);
                startActivity(shareIntent);
            }
        });









    }



    public void onClick(View view) {
        int position;
        switch (view.getId()){
            case R.id.shareButton:
                position = (int) view.getTag();
                String latlng = shareAdapter.items.get(position).latitude+","+shareAdapter.items.get(position).longitude;
                String url = "https://www.google.com/maps/dir/?api=1&origin="+latlng+"&destination="+centroid+"&travelmode=transit";
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,url);
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent,null);
                startActivity(shareIntent);
        }
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

    class ShareAdapter extends BaseAdapter {
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
            ShareItemView  view = null;

            if (convertView == null) {
                view = new ShareItemView(getApplicationContext());


            } else {
                view = (ShareItemView) convertView;
            }

            AddressItem item = items.get(position);
            view.setName(item.getName());

            return view;
        }
    }
}

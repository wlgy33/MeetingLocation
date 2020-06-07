package com.example.meetinglocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class PopModify1Tab extends Activity {
    private static final String TAG = "POP";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 9000;
    double friendlat;
    double friendlong;
    EditText friend_address;
    EditText friend_name;
    Button addBook;
    ImageView close;
    FirebaseFirestore db;



    public static void hideSoftKeyboard(Activity context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getApplicationWindowToken(),0);
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop1tab);
        friend_address = findViewById(R.id.friend_address);
        friend_name = findViewById(R.id.friend_name);
        addBook = findViewById(R.id.addBook);
        close = findViewById(R.id.close);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.3));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(PopModify1Tab.this);
                finish();
            }
        });
        Intent intent = getIntent();
        Log.d(TAG,"intent : "+intent);
        final String bName = intent.getExtras().getString("name");
        String bAddress = intent.getExtras().getString("address");
        final int index = intent.getExtras().getInt("index");
        final double lat = intent.getExtras().getDouble("lat");
        final double lng = intent.getExtras().getDouble("lng");
        friendlat = lat;
        friendlong = lng;

        friend_name.setText(bName);
        friend_address.setText(bAddress);

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = friend_name.getText().toString();
                final String address = friend_address.getText().toString();
                final double latitude = friendlat;
                final double longitude = friendlong;
                if(name.equals("")||address.equals("")){
                    Toast.makeText(getApplicationContext(),"입력이 부족합니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent();
                    intent.putExtra("name",name);
                    intent.putExtra("address",address);
                    intent.putExtra("lat",latitude);
                    intent.putExtra("long",longitude);
                    intent.putExtra("index",index);
                    setResult(RESULT_OK,intent);
                    Log.d(TAG, "name, address, lat, lng, index : " + name + ","+address+","+latitude+","+longitude+","+index);
                    finish();
                }


            }
        });
        friend_address.setFocusable(false);


    }

    public void startAutocompleteActivity(View view){
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountries(Arrays.asList("KR"))
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            friend_address.setText(place.getAddress().substring(5)+" "+place.getName());
            Log.d(TAG, "friend_address : "+friend_address.getText().toString());
            friendlat = place.getLatLng().latitude;
            friendlong = place.getLatLng().longitude;
            InputMethodManager imm = (InputMethodManager) PopModify1Tab.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            hideSoftKeyboard(PopModify1Tab.this);
        }
        if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void linearOnClick(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }
}

package com.example.meetinglocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;
import com.google.firebase.firestore.core.Query;

import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;




public class Pop extends Activity {
    private static final String TAG = "POP";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 9000;
    double friendlat;
    double friendlong;
    EditText friend_address;
    EditText friend_name;
    Button addBook;
    ImageView close;
    FirebaseFirestore db;
    String userEmail;
    private FirebaseAuth mAuth;

    public static void hideSoftKeyboard(Activity context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getApplicationWindowToken(),0);
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop);
        friend_address = findViewById(R.id.friend_address);
        friend_name = findViewById(R.id.friend_name);
        addBook = findViewById(R.id.addBook);
        close = findViewById(R.id.close);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser fUser = mAuth.getCurrentUser();
        userEmail = fUser.getEmail();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.3));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(Pop.this);
                finish();
            }
        });
        /**/

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = friend_name.getText().toString();
                final String address = friend_address.getText().toString();
                final LatLng latlng = new LatLng(friendlat,friendlong);

                if (!name.equals("")&&!address.equals("")){
                    DocumentReference checkDoc = db.collection("users").document(userEmail).collection("Friends").document(name);
                    checkDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists())
                                    Toast.makeText(getApplicationContext(),"이미 있는 이름입니다. ",Toast.LENGTH_SHORT).show();
                                else{
                                    DocumentReference documentReference = db.collection("users").document(userEmail)
                                            .collection("Friends").document(name);
                                    Map<String, Object> userFriend = new HashMap<>();
                                    userFriend.put("name", name);
                                    userFriend.put("address", address);
                                    userFriend.put("latlng", latlng);
                                    documentReference.set(userFriend).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                                    finish();
                                }
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"입력이 부족합니다.",Toast.LENGTH_SHORT).show();
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
            InputMethodManager imm = (InputMethodManager) Pop.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            hideSoftKeyboard(Pop.this);
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

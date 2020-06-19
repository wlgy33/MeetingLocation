package com.example.meetinglocation;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.AndroidUtilsLight;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.TextSearchRequest;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, OnInfoWindowClickListener {
    String apiKey; // APIkey values 폴더 strings.xml 에 입력
    public static TabHost host;


    // 탭 1의 위젯 변수
    EditText input_theme;               // 입력받을 테마
    EditText input_address;             // 입력받을 주소
    EditText input_name;                // 입력받을 이름 (주소와 그룹)
    Button add_friend_btn;              // 주소와 이름을 입력받고 리스트에 추가
    AddressAdapter adapter1;            // 리스트 뷰를 위한 어댑터
    TextView initializer;               // 리스트 초기화
    Button cal_centroid;                // 중점 계산 버튼
    String departure;                   // 출발지 위도 경도 저장

    double latitude;
    double longitude;
    CameraUpdate cameraUpdate;
    LatLng latlngcen;
    String themeName;
    String operation ="";
    List<Marker> markersList;
    List<String> names;



    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;  // 주소 입력시 onStartActivity값
    private static final int ADD_FRIEND = 9001;
    private static final int MODIFY_START = 9002;
    private static final int LOADING = 9003;
    private GeoApiContext mGeoApiContext = null;

    // 탭 2의 위젯 변수
    TextView detailed_info;                 // 상세 정보 클릭용
    TextView detailed_path;                 // 상세 경로 클릭용
    TextView share_with;                    // SNS 공유 클릭용
    private GoogleMap map;                  // 구글 맵
    private Marker currentMarker = null;    // 구글 맵의 현재 위치 마커
    boolean firstTime = true;
    boolean firstTimeForCentroid = true;

    // 구글 맵 업데이트를 위한 변수들
    private static final String TAG = "googlemap";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용
    boolean needRequest = false;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    // 앱을 실행하기 위해 필요한 퍼미션 정의
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Location currentLocation;
    LatLng currentPosition;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;
    private String centroid;

    // 퍼미션 요청 스낵바
    private View request;   // 위치 정보 퍼미션 요청을 위한 뷰 (스낵바는 뷰가 있어야 함)

    // 탭 3의 위젯 변수
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    FriendsAdapter adapter2;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userEmail;
    private ArrayList<FriendsItem> mFriends;

    // 뒤로가기 두번 시 앱 종료
    private BackPressCloseHandler backKeyClickHandler;

    private RequestQueue mRequestQueue;
    private StringRequest StringRequest;
    ProgressDialog progressDialog;

    //On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiKey = getString(R.string.google_map_api_key);

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userEmail = mAuth.getCurrentUser().getEmail();
        setContentView(R.layout.activity_main);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);



        // 2번 눌러 뒤로가기
        backKeyClickHandler = new BackPressCloseHandler(this);

        //구글 플레이스 initialize
        Places.initialize(getApplicationContext(), getString(R.string.google_map_api_key));
        final PlacesClient placesClient = Places.createClient(this);

        // 탭 호스트 구성

        host = (TabHost) findViewById(R.id.host);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("tab1");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_edit_location_black_24dp, null));
        spec.setContent(R.id.tab_content1);
        host.addTab(spec);

        spec = host.newTabSpec("tab2");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_directions_black_24dp, null));
        spec.setContent(R.id.tab_content2);
        host.addTab(spec);

        spec = host.newTabSpec("tab3");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_group_black_24dp, null));
        spec.setContent(R.id.tab_content3);
        host.addTab(spec);

        // 탭 1 화면 구현 (목적지, 상대방 정보 입력)
        ListView listView1 = (ListView) findViewById(R.id.listView1);
        input_theme = (EditText) findViewById(R.id.theme);
        add_friend_btn = (Button) findViewById(R.id.add_friend);
        cal_centroid = (Button) findViewById(R.id.centroid_button);

        adapter1 = new AddressAdapter();

        listView1.setAdapter(adapter1);

        // 탭 1의 리스트 아이템 클릭 시 동작 구현
        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);

                getMenuInflater().inflate(R.menu.menu_listview_tab1,popupMenu.getMenu());
                final int index = position;
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.modify:
                                Intent intent = new Intent(MainActivity.this,PopModify1Tab.class);
                                intent.putExtra("name",adapter1.items.get(index).name);
                                intent.putExtra("address",adapter1.items.get(index).address);
                                intent.putExtra("lat",adapter1.items.get(index).latitude);
                                intent.putExtra("lng",adapter1.items.get(index).longitude);
                                intent.putExtra("index",index);
                                startActivityForResult(intent,MODIFY_START);
                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("삭제");
                                builder.setMessage("삭제하시겠습니까?");
                                builder.setCancelable(true);
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        adapter1.items.remove(index);
                                        adapter1.notifyDataSetChanged();

                                    }
                                });
                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.create().show();

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
        progressDialog = new ProgressDialog(MainActivity.this);


        // 탭 1의 만나는 장소 버튼 구현
        cal_centroid.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("MeetMid 중...");


                if (v!=null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
                //지도 마커 초기화
                map.clear();
                themeName = "";
                final String theme = input_theme.getText().toString();
                Log.d(TAG, "theme : " + theme);
                map.clear();
                firstTimeForCentroid = true;
                if (adapter1.items.size() <= 1) {
                    Toast.makeText(getApplicationContext(), "출발지가 부족합니다", Toast.LENGTH_SHORT).show();

                }
                else {
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getCentroid();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    double centroid_latitude;
                                    double centroid_longitude;

                                    int comma = centroid.indexOf(',');
                                    centroid_latitude = Double.parseDouble(centroid.substring(0, comma));
                                    centroid_longitude = Double.parseDouble(centroid.substring(comma + 2));
                                    latlngcen = new LatLng(centroid_latitude,centroid_longitude);

                                    // 출발지 지도에 마커로 표시
                                    for (int i = 0; i < adapter1.items.size(); i++) {

                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions
                                                .position(new LatLng(adapter1.items.get(i).latitude, adapter1.items.get(i).longitude))
                                                .title(adapter1.items.get(i).name)
                                                .snippet(adapter1.items.get(i).address);

                                        markersList.add(map.addMarker(markerOptions));
                                    }

                                    // 처음 중점 지도에 마커로 표시
                                    LatLng result = latlngcen;
                                    Log.d(TAG, "result : "+latlngcen + ", " +result);
                                    Marker Centroid;
                                    if (theme.equals("")){
                                        Centroid = map.addMarker(new MarkerOptions()
                                                .position(result)
                                                .title("중점")
                                                .snippet(result.latitude+","+result.longitude)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                    }
                                    else{
                                        if (!operation.equals("")) {
                                            Centroid = map.addMarker(new MarkerOptions()
                                                    .position(result)
                                                    .title(themeName + " ("+operation+")")
                                                    .snippet("자세한 정보를 보려면 클릭하세요.")
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                        }
                                        else{
                                            Centroid = map.addMarker(new MarkerOptions()
                                                    .position(result)
                                                    .title(themeName)
                                                    .snippet("자세한 정보를 보려면 클릭하세요.")
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                        }
                                    }

                                    //테마 입력/미입력 시 경로 출력
                                    for (int i=0; i< markersList.size(); i++) {
                                        calculateDirections(i,markersList.get(i), Centroid);
                                    }

                                    // 모든 출발지 화면 내에 표시
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (Marker m : markersList) {
                                        builder.include(m.getPosition());
                                        progressDialog.dismiss();
                                    }
                                    int padding = 70;
                                    LatLngBounds bounds = builder.build();
                                    cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                        @Override
                                        public void onMapLoaded() {
                                            if (firstTimeForCentroid) {
                                                map.animateCamera(cameraUpdate);
                                                firstTimeForCentroid = false;
                                            }


                                        }
                                    });

                                    host.setCurrentTab(1);
                                }
                            });
                        }
                    }).start();


                    /*markersList = new ArrayList<Marker>();
                    names = new ArrayList<String>();
                    for (int i = 0; i < adapter1.items.size(); i++) {
                        names.add(adapter1.items.get(i).name);
                    }
                    // URL 만들기
                    String cenurl = "https://us-central1-meetinglocation-492f2.cloudfunctions.net/main?location=";
                    String data = "{";
                    for (int i = 0; i < adapter1.items.size(); i++) {
                        data += adapter1.items.get(i).latlng;
                        if (i != adapter1.items.size() - 1)
                            data += ",";
                    }
                    cenurl = cenurl + data + "}";
                    if (!theme.equals("")){
                        cenurl = cenurl + "&keyword="+"\""+theme+"\"";
                    }
                    cenurl = cenurl + "&apikey=" + "\""+apiKey+"\"";
                    Log.d(TAG, "cenurl = "+cenurl);
                    //HTTP 통신
                    try {
                        centroid = new HttpAsyncTask().execute(cenurl).get();
                        Log.d(TAG, "centroid: " + centroid);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 중점 lATlNG 형태로 변형
                    if (!theme.equals("")){
                        int comma = centroid.indexOf(',');
                        themeName = centroid.substring(1,comma);
                        centroid = centroid.substring(comma+2,centroid.length()-1);
                    }
                    */



                }
            }
            private void getCentroid(){
                markersList = new ArrayList<Marker>();
                names = new ArrayList<String>();
                String theme = input_theme.getText().toString();
                for (int i = 0; i < adapter1.items.size(); i++) {
                    names.add(adapter1.items.get(i).name);
                }

                // URL 만들기
                String cenurl = "https://us-central1-meetinglocation-492f2.cloudfunctions.net/main?location=";
                String data = "{";
                for (int i = 0; i < adapter1.items.size(); i++) {
                    data += adapter1.items.get(i).latlng;
                    if (i != adapter1.items.size() - 1)
                        data += ",";
                }
                cenurl = cenurl + data + "}";

                if (!theme.equals("")){
                    cenurl = cenurl + "&keyword="+"\""+theme+"\"";
                }
                cenurl = cenurl + "&apikey=" + "\""+apiKey+"\"";
                Log.d(TAG, "cenurl = "+cenurl);
                //HTTP 통신
                try {
                    centroid = new HttpAsyncTask().execute(cenurl).get();

                    Log.d(TAG, "centroid: " + centroid);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 중점 lATlNG 형태로 변형
                if (!theme.equals("")){
                    int comma = centroid.indexOf(',');

                    themeName = centroid.substring(1,comma);
                    centroid = centroid.substring(comma+2);
                    int bracket = centroid.indexOf(']');
                    Log.d(TAG, "themeCentroid = "+centroid + "   operation? " + centroid.substring(bracket+1));

                    if (centroid.substring(bracket+1).length()>1){


                        String isOpen = centroid.substring(bracket+3,centroid.length()-1);
                        centroid = centroid.substring(0,bracket+1);
                        if (isOpen.equals("true"))
                            operation = "영업중";
                        else
                            operation = "영업종료";
                    }
                    else{

                        centroid = centroid.substring(0,bracket+1);
                    }
                }
                centroid = centroid.substring(1, centroid.length() - 1);
                Log.d(TAG,"getcentroid(): "+centroid + " operation ? "+operation);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });


        // 테마의 엔터 후 키보드 숨기기
        input_theme.setOnEditorActionListener(new DoneOnEditorActionListener());


        // 탭 1의 친구 추가 버튼 구현
        input_address = (EditText) findViewById(R.id.input_address);
        input_name = (EditText) findViewById(R.id.input_name);
        input_name.setNextFocusDownId(R.id.input_address);

        //출발지 검색기능
        input_address.setFocusable(false);


        // 이름의 엔터 후 키보드 숨기기
        input_name.setOnEditorActionListener(new NextOnEditorActionListener());

        // 주소의 엔터 후 키보드 숨기기
        input_address.setOnEditorActionListener(new DoneOnEditorActionListener());


        //추가 버튼 구현
        add_friend_btn = (Button) findViewById(R.id.add_friend);
        add_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_address.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"출발지를 입력하세요.",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"주소 입력 X");
                }
                else{
                    String address = input_address.getText().toString();
                    String name = input_name.getText().toString();
                    String latlng = departure;
                    double lat = latitude;
                    double lon = longitude;

                    adapter1.addItem(new AddressItem(address, name, latlng, lat, lon));
                    adapter1.notifyDataSetChanged();
                    input_address.setText(null);
                    input_name.setText(null);
                    if (v!=null){
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    }
                    Log.d(TAG,"주소 입력 O, 주소 : "+input_address.getText());
                }
            }
        });

        // 탭 1의 리스트 뷰 초기화 기능 구현
        initializer = (TextView) findViewById(R.id.init);
        initializer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "초기화" 색상 변환
                initializer.setTextColor(Color.parseColor("#1592E6"));

                // 초기화 재확인 AlertDialog 생성 및 동작
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("알림").setMessage("초기화하시겠습니까?");
                // 확인 버튼 눌렀을 때
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "초기화하였습니다", Toast.LENGTH_SHORT).show();
                        adapter1.clear();
                        map.clear();
                        firstTime = true;
                        input_theme.setText(null);
                        input_address.setText(null);
                        input_name.setText(null);
                    }
                });
                // 취소 버튼 눌렀을 때
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "초기화를 취소하였습니다", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                adapter1.notifyDataSetChanged();


            }
        });

        // 탭 2 상단 텍스트 뷰 클릭 기능 구현
        // 주변 상세 정보
        detailed_info = (TextView) findViewById(R.id.info);

        detailed_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailInfo.class);
                intent.putExtra("list", adapter1.items);
                startActivity(intent);
            }
        });

        share_with = (TextView) findViewById(R.id.share);
        share_with.setClickable(true);
        //공유 버튼 구현
        share_with.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                intent.putExtra("list", adapter1.items);
                intent.putExtra("centroid",centroid);
                startActivity(intent);
            }
        });

        // 탭 2 화면 구현 (구글 맵 구현)
        locationRequest = new LocationRequest().setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(
                UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }

        // 탭 3 화면 구현 (목적지, 상대방 정보 입력)
        ListView listView2 = (ListView) findViewById(R.id.listView2);
        adapter2 = new FriendsAdapter();
        listView2.setAdapter(adapter2);

        TextView addFriend = (TextView) findViewById(R.id.add);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Pop.class);
                startActivityForResult(intent,ADD_FRIEND);}
        });
        db.collection("users").document(userEmail).collection("Friends")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            return;
                        }
                        ArrayList<FriendsItem> friendsItems = (ArrayList<FriendsItem>) queryDocumentSnapshots.toObjects(FriendsItem.class);
                        adapter2.items = friendsItems;
                        adapter2.notifyDataSetChanged();
                    }
                });
        listView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,view);

                getMenuInflater().inflate(R.menu.menu_listview,popupMenu.getMenu());
                final int index = position;
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.addtolist:
                                String name = adapter2.items.get(index).name;
                                String address = adapter2.items.get(index).address;
                                com.example.meetinglocation.LatLng mlatlng = adapter2.items.get(index).latlng;
                                String slatlng = "\""+mlatlng.getLatitude()+"\""+":"+"\""+mlatlng.getLongitude()+"\"";
                                adapter1.items.add(new AddressItem(address,name,slatlng,mlatlng.getLatitude(),mlatlng.getLongitude()));
                                adapter1.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "추가되었습니다.",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.modify:
                                Intent intent = new Intent(MainActivity.this,PopModify.class);
                                intent.putExtra("name",adapter2.items.get(index).name);
                                intent.putExtra("address",adapter2.items.get(index).address);
                                startActivity(intent);
                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("삭제");
                                builder.setMessage("삭제하시겠습니까?");
                                builder.setCancelable(true);
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.collection("users").document(userEmail).collection("Friends")
                                                .document(adapter2.items.get(index).name).delete();
                                        Toast.makeText(MainActivity.this, "삭제되었습니다.",Toast.LENGTH_SHORT).show();

                                    }
                                });
                                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.create().show();

                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });
    }

    //지도에 경로 표시
    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(Color.BLACK);
                    polyline.setClickable(true);

                }
            }
        });
    }

    //경로 계산 및 addPolylinesToMap 수행
    private void calculateDirections(final int index, final Marker markerOrigin, Marker markerDestination) {

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                markerDestination.getPosition().latitude,
                markerDestination.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        markerOrigin.getPosition().latitude,
                        markerOrigin.getPosition().longitude
                ));
        directions.mode(TravelMode.TRANSIT);
        directions.language("ko");
        Log.d(TAG, "destination latlng: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {

                String route = "";
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints.toString());
                Log.d(TAG, "onResult: directionstep : "+result.routes[0].legs[0].steps[1].htmlInstructions.toString());
                adapter1.items.get(index).time = (result.routes[0].legs[0].duration).toString();
                for (int i=0; i<result.routes[0].legs[0].steps.length;i++){
                    route = route + result.routes[0].legs[0].steps[i].htmlInstructions +" "+ result.routes[0].legs[0].steps[i].duration + "\n";
                }
                adapter1.items.get(index).route = route;
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {

                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });

    }

    //HTTP 통신
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();


        @Override
        protected void onPreExecute(){

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String strUrl = params[0];

            try {
                Request request = new Request.Builder()
                        .url(strUrl)
                        .build();
                Response response = client.newCall(request).execute();
                return result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);




            if (s != null)
                Log.d(TAG, s);
        }
    }

    // 탭 1 화면의 리스트 뷰 기능 구현
    class AddressAdapter extends BaseAdapter {
        ArrayList<AddressItem> items = new ArrayList<AddressItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(AddressItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void clear() {
            items.clear();
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddressItemView view = null;
            if (convertView == null) {
                view = new AddressItemView(getApplicationContext());
            } else {
                view = (AddressItemView) convertView;
            }

            AddressItem item = items.get(position);
            view.setAddress(item.getAddress());
            view.setName(item.getName());

            return view;
        }
    }

    // 탭 3 화면의 리스트 뷰 구현
    class FriendsAdapter extends BaseAdapter {
        ArrayList<FriendsItem> items = new ArrayList<FriendsItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(FriendsItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void clear() {
            items.clear();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FriendsItemView view = new FriendsItemView(getApplicationContext());


            FriendsItem item = items.get(position);
            view.setName(item.getName());
            view.setAddress(item.getAddress());

            return view;
        }
    }

    // 탭 2 화면의 지도 시작지점(서울)
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Log.d(TAG, "onMapReady : ");

        map = googleMap;
        map.setOnInfoWindowClickListener(MainActivity.this);
        // 지도의 초기위치 : 서울. 런타임 퍼미션 요청 대화상자 및 GPS 활성 요청 대화상자 보이기 전
        setDefaultLocation();


        // 런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는 지 확인
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // 2-1. 퍼미션을 가지고 있다면
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            startLocationUpdates();
        } else { // 2-2. 퍼미션이 없다면 사용자에게 퍼미션 요청
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있다면
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 사용자에게 퍼미션 요청 이유 설명
                Snackbar.make(request, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 3-3. 사용자에게 퍼미션 요청. 요청 결과는 onRequestPermissionResult에 수신
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else { // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우, 바로 퍼미션 요청
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "OnMapClick :");
            }
        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = "현재 위치";
                String markerSnippet = "위도 : " + String.valueOf(location.getLatitude() + "경도 : " + String.valueOf(location.getLongitude()));


                // 현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                currentLocation = location;
            }
        }
    };


    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServicesSetting");
            showDialogForLocationServicesSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "startLocationUpdates : 퍼미션을 가지고 있지 않음");
                return;
            }
        }

        Log.d(TAG, "startLocationUpdates : call fusedLocationClient.requestLocationUpdates");
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        if (checkPermission()) {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {
            Log.d(TAG, "onStart : call fusedLocationClient.requestLocationUpdates");
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (fusedLocationClient != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
/*
    public String getCurrentAddress(LatLng latLng) {
        // GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException ioException) {
            // 네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }*/

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) {
            currentMarker.remove();
        }

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        //currentMarker = map.addMarker(markerOptions);

        // 처음에만 카메라 현재 위치로 이동
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng,15);
        if (firstTime) {
            map.animateCamera(cameraUpdate);
            firstTime = false;
        }
    }

    public void setDefaultLocation() {
        // 초기 마커 위치 : 서울
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);


        if (currentMarker != null) {
            currentMarker.remove();
        }

       /* String markerTitle = "위치정보를 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부를 확인하세요";
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = map.addMarker(markerOptions);*/

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        map.moveCamera(cameraUpdate);
    }

    // 런타임 퍼미션 처리를 위한 메소드
    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    // ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
    public void onRequestPermissionResult(int permissionsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permissionsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean checkResult = true;

            // 모든 퍼미션 허용 여부 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false;
                    break;
                }
            }

            if (checkResult) {
                // 퍼미션을 허용했다면 위치 업데이트 시작
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유 설명 후 앱 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // 사용자가 거부만 선택한 경우, 앱을 재실행하여 허용을 선택하면 앱 사용 가능
                    Snackbar.make(request, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우, 설정(앱 정보)에서 퍼미션을 허용해야
                    // 앱 사용 가능
                    Snackbar.make(request, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    // GPS 활성화를 위한 메소드
    private void showDialogForLocationServicesSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {

            Place place = Autocomplete.getPlaceFromIntent(data);
            input_address.setText(place.getAddress().substring(5)+" "+place.getName());
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            departure = "\"" + latitude + "\"" + ":" + "\"" + longitude + "\"";
            hideSoftKeyboard(MainActivity.this);

        }
        if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        if (requestCode == MODIFY_START){
            if (resultCode == RESULT_OK){

                String mName = data.getExtras().getString("name");
                String mAddress = data.getExtras().getString("address");
                Double mLat = data.getExtras().getDouble("lat");
                Double mLong = data.getExtras().getDouble("long");
                String mLatLng = "\""+mLat+"\""+":"+"\""+mLong+"\"";
                int index = data.getExtras().getInt("index");
                adapter1.items.set(index,new AddressItem(mAddress,mName,mLatLng,mLat,mLong));
                adapter1.notifyDataSetChanged();
            }
        }
        if (requestCode == LOADING){
            if (resultCode == RESULT_OK){
                Log.d(TAG, "success Loading");
                centroid = data.getStringExtra("centroid");
                themeName = data.getStringExtra("themeName");
                Log.d(TAG,"centroid, themeName : "+centroid + themeName);
                double centroid_latitude;
                double centroid_longitude;
                int comma = centroid.indexOf(',');
                centroid_latitude = Double.parseDouble(centroid.substring(0, comma));
                centroid_longitude = Double.parseDouble(centroid.substring(comma + 2));
                latlngcen = new LatLng(centroid_latitude,centroid_longitude);
            }
            else{
                Log.d(TAG,"failed Loading");
            }
        }




        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                // 사용자가 GPS 활성 여부 확인
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되어있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {

        //super.onBackPressed();
        backKeyClickHandler.onBackPressed();
    }
    //완료 누르면 키보드 내리기
    class DoneOnEditorActionListener implements TextView.OnEditorActionListener{
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                return true;
            }
            return false;
        }
    }

    //다음 누르면 키보드 내리기
    class NextOnEditorActionListener implements TextView.OnEditorActionListener{
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT){
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                return true;
            }
            return false;
        }
    }

    //키보드 떠있을때 화면 누르면 키보드 내리기
    public void linearOnClick(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

    //출발지 입력 onClick
    public void startAutocompleteActivity(View view){
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountries(Arrays.asList("KR"))
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }
    private class Loading extends AsyncTask<Void,Void,Void>{
        protected void onPreExecute(){
            super.onPreExecute();
            cal_centroid.setEnabled(false);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    //키보드 숨기기
    public static void hideSoftKeyboard(Activity context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getApplicationWindowToken(),0);
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onInfoWindowClick(Marker marker){
        if (marker.getSnippet().equals("자세한 정보를 보려면 클릭하세요.")){
            Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f?q=%s", latlngcen.latitude, latlngcen.longitude, themeName));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
        Log.d(TAG, "marker : "+marker.getSnippet());

    }
}
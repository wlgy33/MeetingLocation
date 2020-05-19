package com.example.meetinglocation;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    String apiKey = "AIzaSyCOQWzdRUsvgcFfM1BbD1U3B401zsL1_AQ";
    // 탭 1의 위젯 변수
    EditText input_theme;               // 입력받을 테마
    EditText input_address;             // 입력받을 주소
    EditText input_name;                // 입력받을 이름 (주소와 그룹)
    Button add_friend_btn;              // 주소와 이름을 입력받고 리스트에 추가
    AddressAdapter adapter1;            // 리스트 뷰를 위한 어댑터
    TextView initializer;               // 리스트 초기화
    int AUTOCOMPLETE_REQUEST_CODE = 1;  // 주소 입력시 onStartActivity값
    Button cal_centroid;                // 중점 계산 버튼
    String departure;                      // 출발지 위도 경도 저장
    private GeoApiContext mGeoApiContext = null;
    double latitude;
    double longitude;

    // 탭 2의 위젯 변수
    TextView detailed_info;                 // 상세 정보 클릭용
    TextView detailed_path;                 // 상세 경로 클릭용
    TextView share_with;                    // SNS 공유 클릭용
    private GoogleMap map;                  // 구글 맵
    private Marker currentMarker = null;    // 구글 맵의 현재 위치 마커

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

    // 퍼미션 요청 스낵바
    private View request;   // 위치 정보 퍼미션 요청을 위한 뷰 (스낵바는 뷰가 있어야 함)

    // 탭 3의 위젯 변수
    EditText input_name2;   //
    EditText getInput_address2;
    // 뒤로가기 두번 시 앱 종료
    private BackPressCloseHandler backKeyClickHandler;

    //On create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 2번 눌러 뒤로가기
        backKeyClickHandler = new BackPressCloseHandler(this);

        //구글 플레이스 initialize
        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);

        // 탭 호스트 구성
        TabHost host = (TabHost) findViewById(R.id.host);
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
        adapter1.addItem(new AddressItem("서울시 강남구", "김지효","latlng",1232,12312));
        listView1.setAdapter(adapter1);

        // 탭 1의 리스트 아이템 클릭 시 동작 구현
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddressItem item = (AddressItem) adapter1.getItem(position);
                Toast.makeText(getApplicationContext(), "선택 : " + item.getName(), Toast.LENGTH_LONG).show();

            }
        });

        // 탭 1의 테마 버튼 구현
        input_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_theme.setText(input_theme.getText().toString());
            }
        });

        // 탭 1의 만나는 장소 버튼 구현
        cal_centroid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Centroid.class);
                intent.putExtra("locations", adapter1.items);
                startActivity(intent);
                String centroid = intent.getExtras().getString("centroid");
                if (currentMarker != null)
                    currentMarker.remove();

                for (int i=0; i<adapter1.items.size(); i++){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions
                            .position(new LatLng(adapter1.items.get(i).latitude, adapter1.items.get(i).longitude))
                            .title(adapter1.items.get(i).name);
                    map.addMarker(markerOptions);
                }
                


            }
        });

        // editText의 키보드 줄바꿈->완료
        input_theme.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        break;
                }
                return true;
            }
        });

        // 탭 1의 친구 추가 버튼 구현
        input_address = (EditText) findViewById(R.id.input_address);
        input_name = (EditText) findViewById(R.id.input_name);

        //출발지 검색기능
        input_address.setFocusable(false);
        input_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG,Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MainActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        // editText의 키보드 줄바꿈->완료
        input_name.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                        break;
                }
                return true;
            }
        });

        // editText의 키보드 줄바꿈->다음 editText
        input_address.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        break;
                }
                return true;
            }
        });

        //추가 버튼 구현
        add_friend_btn = (Button) findViewById(R.id.add_friend);
        add_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = input_address.getText().toString();
                String name = input_name.getText().toString();
                String latlng = departure;
                double lat = latitude;
                double lon = longitude;

                adapter1.addItem(new AddressItem(address, name, latlng,lat,lon));
                adapter1.notifyDataSetChanged();
                input_address.setText(null);
                input_name.setText(null);
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
        detailed_info = (TextView) findViewById(R.id.info);
        detailed_path = (TextView) findViewById(R.id.path);
        share_with = (TextView) findViewById(R.id.share);

        // 탭 2 화면 구현 (구글 맵 구현)
        locationRequest = new LocationRequest().setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(
                        UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 탭 3 화면 구현 (목적지, 상대방 정보 입력)

        ListView listView2 = (ListView) findViewById(R.id.listView2);

        FriendsAdapter adapter2 = new FriendsAdapter();
        adapter2.addItem(new FriendsItem("김동현", "서울시 노원구 XX아파트"));

        listView2.setAdapter(adapter2);

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

    // 탭 2 화면의 지도 시작지점(서울)
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Log.d(TAG, "onMapReady : ");

        map = googleMap;

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

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도 : " + String.valueOf(location.getLatitude() + "경도 : " + String.valueOf(location.getLongitude()));
                Log.d(TAG, "onLocationResult : " + markerSnippet);

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
    }

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

        currentMarker = map.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        map.moveCamera(cameraUpdate);
    }

    public void setDefaultLocation() {
        // 초기 마커 위치 : 서울
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보를 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부를 확인하세요";

        if (currentMarker != null) {
            currentMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = map.addMarker(markerOptions);

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
        if (requestCode==AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK){

            Place place = Autocomplete.getPlaceFromIntent(data);
            input_address.setText(place.getName());
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            departure = "\""+latitude+"\""+":"+"\""+longitude+"\"";

        }
        else if(resultCode==AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(),
                    Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBackPressed(){

        //super.onBackPressed();
        backKeyClickHandler.onBackPressed();
    }


}

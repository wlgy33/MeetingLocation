package com.example.meetinglocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // 탭 1의 위젯 변수
    EditText input_theme;
    EditText input_address;
    EditText input_name;
    Button add_friend_btn;
    AddressAdapter adapter1;
    TextView initializer;

    // 탭 2의 위젯 변수
    TextView detailed_info;
    TextView detailed_path;
    TextView share_with;
    private GoogleMap map;

    // 탭 3의 위젯 변수
    EditText input_name2;
    EditText getInput_address2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // 탭 1 리스트 뷰 생성
        adapter1 = new AddressAdapter();
        adapter1.addItem(new AddressItem("서울시 강남구", "김지효"));
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

        // 탭 1의 친구 추가 버튼 구현
        input_address = (EditText) findViewById(R.id.input_address);
        input_name = (EditText) findViewById(R.id.input_name);

        add_friend_btn = (Button) findViewById(R.id.add_friend);
        add_friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = input_address.getText().toString();
                String name = input_name.getText().toString();

                adapter1.addItem(new AddressItem(address, name));
                adapter1.notifyDataSetChanged();
            }
        });

        // 탭 1의 리스트 뷰 초기화 기능 구현
        initializer = (TextView) findViewById(R.id.init);
        initializer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter1.clear();

                adapter1.notifyDataSetChanged();
            }
        });

        // 탭 2 상단 텍스트 뷰 클릭 기능 구현
        detailed_info = (TextView) findViewById(R.id.info);
        detailed_path = (TextView) findViewById(R.id.path);
        share_with = (TextView) findViewById(R.id.share);

        // 탭 2 화면 구현 (구글 맵 구현)
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
        map = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
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
}

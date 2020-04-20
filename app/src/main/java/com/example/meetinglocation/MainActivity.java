package com.example.meetinglocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;

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

    private GoogleMap map;

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

        // 첫 번째 탭 화면 구현 (목적지, 상대방 정보 입력)
        ListView listView1 = (ListView) findViewById(R.id.listView1);
        Button button = (Button) findViewById(R.id.button);

        AddressAdapter adapter1 = new AddressAdapter();
        adapter1.addItem(new AddressItem("서울시 강남구", "나"));
        adapter1.addItem(new AddressItem("서울시 용산구", "친구1"));
        adapter1.addItem(new AddressItem("서울시 송파구", "친구2"));
        adapter1.addItem(new AddressItem("서울시 서초구", "친구3"));

        listView1.setAdapter(adapter1);

        // 두 번째 화면 구현 (구글 맵 구현)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 세 번째 탭 화면 구현 (목적지, 상대방 정보 입력)
        ListView listView2 = (ListView) findViewById(R.id.listView2);

        FriendsAdapter adapter2 = new FriendsAdapter();
        adapter2.addItem(new FriendsItem("김동현", "서울시 노원구 XX아파트"));
        adapter2.addItem(new FriendsItem("김지효", "서울시 마포구 XX아파트"));
        adapter2.addItem(new FriendsItem("최진형", "서울시 동대문구 XX아파트"));
        adapter2.addItem(new FriendsItem("배인석", "서울시 송파구 XX아파트"));

        listView2.setAdapter(adapter2);

    }

    // 첫 번째 탭 화면의 리스트 뷰 구현
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddressItemView view = new AddressItemView(getApplicationContext());

            AddressItem item = items.get(position);
            view.setAddress(item.getAddress());
            view.setName(item.getName());

            return view;
        }
    }

    // 두 번째 탭 화면의 지도 시작지점(서울)
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

    // 세 번째 탭 화면의 리스트 뷰 구현
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

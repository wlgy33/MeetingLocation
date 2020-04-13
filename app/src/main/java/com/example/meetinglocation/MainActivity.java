package com.example.meetinglocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);

        AddressAdapter adapter = new AddressAdapter();
        adapter.addItem(new AddressItem("서울시 강남구", "나"));
        adapter.addItem(new AddressItem("서울시 용산구", "친구1"));
        adapter.addItem(new AddressItem("서울시 송파구", "친구2"));
        adapter.addItem(new AddressItem("서울시 서초구", "친구3"));

        listView.setAdapter(adapter);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

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
}

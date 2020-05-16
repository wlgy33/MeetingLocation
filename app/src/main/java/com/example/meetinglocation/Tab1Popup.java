package com.example.meetinglocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class Tab1Popup extends Activity {
    EditText addressEntered1;
    EditText nameEntered1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1_pop_up);

        addressEntered1 = (EditText) findViewById(R.id.address_entered1);
        nameEntered1 = (EditText) findViewById(R.id.name_entered1);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

    }

    // 확인 버튼 클릭
    public void popupClicked(View v) {
        // 데이터 전달하기

        // 팝업 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 팝업 바깥 터치 시 안 닫힘
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // 뒤로 가기 방지
        return;
    }
}

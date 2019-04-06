package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity  {

    private FineInput fineInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void show1(View view) {
        //这里调取软键盘
        if(fineInput==null) {
            fineInput = new FineInput(this);
        }
        fineInput.showInput();
    }
}

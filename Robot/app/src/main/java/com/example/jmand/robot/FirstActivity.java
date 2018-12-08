package com.example.jmand.robot;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        ImageButton setBtn=(ImageButton)findViewById(R.id.setting);
        Button milkBtn=(Button)findViewById(R.id.btn1);
        Button singBtn=(Button)findViewById(R.id.btn2);


        setBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FirstActivity.this,MainActivity.class);
                startActivity(intent);
                 }
        }) ;

        milkBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FirstActivity.this,MilkActivity.class);
                startActivity(intent);
            }
        }) ;

        singBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FirstActivity.this,SongActivity.class);
                startActivity(intent);
            }
        }) ;



    }
}

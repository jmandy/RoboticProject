package com.example.jmand.robot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MusicSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        CharSequence info[] = new CharSequence[] {"내정보", "로그아웃" };


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("제목");

        builder.setItems(info, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                switch(which)

                {

                    case 0:

                        // 내정보

                        Toast.makeText(MusicSelect.this, "내정보", Toast.LENGTH_SHORT).show();

                        break;

                    case 1:

                        // 로그아웃

                        Toast.makeText(MusicSelect.this, "로그아웃", Toast.LENGTH_SHORT).show();

                        break;

                }

                dialog.dismiss();

            }

        });

        builder.show();



    }
}

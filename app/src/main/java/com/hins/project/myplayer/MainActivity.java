package com.hins.project.myplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button_audio;
    private Button button_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_audio = (Button) findViewById(R.id.button_audio);
        button_video = (Button) findViewById(R.id.button_video);

        button_audio.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AudioActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        button_video.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,VideoActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

}

package com.hins.project.myplayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private Button fileButton, fullscreenButton;
    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "VideoActivity";
    private VideoView videoView;
    private Uri fileUri = null;
    private MediaController mc;
    private boolean fullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        fileButton = (Button) findViewById(R.id.fileButton);
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,FILE_SELECT_CODE);
            }
        });

        fullscreenButton = (Button) findViewById(R.id.fullscreenButton);
        fullscreenButton.setVisibility(View.INVISIBLE);
        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fullscreen){//设置RelativeLayout的全屏模式
                    RelativeLayout.LayoutParams layoutParams=
                            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT
                    );
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    videoView.setLayoutParams(layoutParams);

                    fullscreen = true;//改变全屏/窗口的标记
                }else{//设置RelativeLayout的窗口模式
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(320,240);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    videoView.setLayoutParams(lp);
                    fullscreen = false;//改变全屏/窗口的标记
                }
            }
        });

        videoView = (VideoView) findViewById(R.id.videoView);
        mc = new MediaController(this);
        videoView.setMediaController(mc);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText( VideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
                fullscreenButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == FILE_SELECT_CODE) {
            fileUri = data.getData();
            Log.i(TAG, "------->" + fileUri.getPath());
            play(fileUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void play(Uri videoUri){
        if (fileUri!=null) {
            fileButton.setVisibility(View.VISIBLE);
            videoView.setVideoURI(fileUri);

            Toolbar toolbar = new Toolbar(this);
            int statusBarHeight = getStatusBarHeight();
            int toolBar = toolbar.getHeight();
            int videoViewHeight = videoView.getHeight();
            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            mc.setPadding(0, 0, 0, screenHeight - toolBar - videoViewHeight - statusBarHeight);
            fullscreenButton.setVisibility(View.VISIBLE);

            videoView.start();
        }
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}

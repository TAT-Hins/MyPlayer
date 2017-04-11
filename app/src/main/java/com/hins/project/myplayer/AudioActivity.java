package com.hins.project.myplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.*;

public class AudioActivity extends AppCompatActivity {

    private ListView playList;
    private ImageButton prevButton, pauseButton, nextButton;
    private TextView title, author;
    private FloatingActionButton fab;
    private MediaPlayer mp;
    private SimpleAdapter listAdapter;
    private ArrayList<HashMap<String, Object>> mylist;
    private boolean isPaused = true;
    private int currentPosition;
    private File path;
    private SeekBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        scan();
        listAdapter = new SimpleAdapter(this, mylist, R.layout.listitemlayout,
                new String[]{"musicTitle", "music_author"}, new int[]{R.id.musicTitle, R.id.musicAuthor});
        playList = (ListView) findViewById(R.id.playList);
        playList.setAdapter(listAdapter);

        mp = new MediaPlayer();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);

        progressBar = (SeekBar) findViewById(R.id.progressBar);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = progressBar.getProgress();
                if (mp!=null && mp.isPlaying()){
                    mp.seekTo(progress);
                }
            }
        });

        prevButton = (ImageButton) findViewById(R.id.prevButton);
        pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);

        fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scan();
               // getAllFiles(path);
            }
        });
        prevButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (currentPosition > 0)
                    switchMusic(currentPosition-1);
                else switchMusic(mylist.size()-1);
            }
        });
        pauseButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    if (mp!=null){
                        if (!isPaused){
                            mp.pause();
                            ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.play));
                            isPaused = true;
                        }
                        else{
                            mp.start();
                            ((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.pause));
                            isPaused = false;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        nextButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (currentPosition + 1 < mylist.size())
                    switchMusic(currentPosition+1);
                else switchMusic(0);
            }
        });

        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                switchMusic(position);
            }
        });

    }

    public void switchMusic(int position){

        currentPosition = position;
        HashMap<String, Object> itemData = (HashMap<String, Object>) playList.getItemAtPosition(currentPosition);
        String clickedItemUrl = itemData.get("musicFileUrl").toString();
        System.out.println(clickedItemUrl);
        String clickedItemTitle = itemData.get("musicTitle").toString();
        String clickedItemAuthor = itemData.get("music_author").toString();
        String clickedItemAlbum = itemData.get("music_album").toString();
        play(clickedItemTitle, clickedItemAuthor, clickedItemAlbum, clickedItemUrl);
    }

    public void scan(){
    //生成动态集合，用于存储数据
        mylist = new ArrayList<HashMap<String, Object>>();

    //查询媒体数据库
        ContentResolver cR = this.getContentResolver();
        Cursor cursor = cR.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //遍历媒体数据库
        try{
            if(cursor.moveToFirst()){

                while (!cursor.isAfterLast()) {

                    //歌曲名
                    String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                    String author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                    String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    System.out.println(url);
                    //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                    Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                    if(size>1024*800){//如果文件大小大于800K，将该文件信息存入到map集合中
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("musicTitle", tilte);
                        map.put("musicFileUrl", url);
                        map.put("music_author",author);
                        map.put("music_album", album);
                        mylist.add(map);
                    }
                    cursor.moveToNext();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //返回存储数据的集合
    }


    public void play(String musicTitle, String musicAuthor, String musicAlbum, String musicUrl){
        try{
            if (mp!=null)
                mp.reset();
            title.setText(musicTitle);
            author.setText(musicAuthor + " - " + musicAlbum);
            mp.setDataSource(musicUrl);
            mp.prepare();

            progressBar.setMax(mp.getDuration());
            progressBar.setProgress(0);
            mp.start();

            new Thread(){

                @Override
                public void run(){
                    try{
                        isPaused = false;
                        while (!isPaused){
                            int current = mp.getCurrentPosition();
                            progressBar.setProgress(current);
                            sleep(500);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}

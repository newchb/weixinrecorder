package com.item.weixinrecoder;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import adapter.listadapter;
import manager.MediaManager;
import widget.Recode_Button;

public class MainActivity extends AppCompatActivity {

    private Recode_Button mRecorder_btn;
    private ListView mRecorder_lv;
    private ArrayList<Recorder> list = new ArrayList<>();
    private ArrayAdapter mAdapter;
    private View anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        initevent();

    }
    private void initevent() {
        mRecorder_btn.setOnRecorderStateChangeListener(new Recode_Button.OnRecorderStateChangeListener() {
            @Override
            public void updatatoact(String path, float time) {
                Recorder recorder = new Recorder(path, time);
                list.add(recorder);
                mAdapter.notifyDataSetChanged();
            }
        });
        mRecorder_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //以防音频在播放的时候点击其他动画会出现叠音,在这里把音频控制只有1个
                if(anim != null){
                    anim.setBackgroundResource(R.mipmap.adj);
                    anim = null;
                }
                //播放动画
                anim = view.findViewById(R.id.list_item_anim);
                anim.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable drawable = (AnimationDrawable) anim.getBackground();
                drawable.start();
                //播放音频,需要一个文件路径,还有一个播放完毕的回调
                MediaManager.playvoice(list.get(position).filepath,new MediaPlayer.OnCompletionListener(){

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //在这里播放完音频后,停止播放动画
                        anim.setBackgroundResource(R.mipmap.adj);
                    }
                });
            }
        });
    }

    private void initview() {
        mRecorder_btn = (Recode_Button) findViewById(R.id.recoder_btn);
        mRecorder_lv = (ListView) findViewById(R.id.recoder_lv);
        mAdapter = new listadapter(this,list);
        mRecorder_lv.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.onpause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.onresume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.onrelease();
    }

    //定义一个recorder类负责接收音频数据文件
   public class Recorder{
        String filepath;
        float time;

        public Recorder(String filepath, float time) {
            this.filepath = filepath;
            this.time = time;
        }

        public String getFilepath() {
            return filepath;
        }

        public float getTime() {
            return time;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public void setTime(float time) {
            this.time = time;
        }
    }


}

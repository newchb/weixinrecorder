package widget;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.item.weixinrecoder.R;

import manager.MediaRecorderManager;
import manager.dialogmanager;

/**
 * 创建者: hiboy
 * 创建时间: 2016/9/17.
 */
public class Recode_Button extends Button implements View.OnLongClickListener, MediaRecorderManager.preparedcallback {
    //定义btn的3种状态
    private static final  int STATE_NORMAL = 1;
    private static final  int STATE_RECONDING = 2;
    private static final  int STATE_WANT_TOCANCEL = 3;
    private int state = STATE_NORMAL;
    //定义btn在y轴上往上滑出现取消的距离
    private static  final int DISTANCE_Y_WANT_TOCANCEL = 50;
    private boolean isrecoding = false;
    private dialogmanager mDialogmanager;
    private String mDir;
    private MediaRecorderManager mRecorderManager;

    public Recode_Button(Context context) {
        this(context,null);
    }
    public Recode_Button(Context context, AttributeSet attrs) {
        super(context, attrs);

            mDir = Environment.getExternalStorageDirectory()+"/weixinrecoder";

        mDialogmanager = new dialogmanager(getContext());
        mRecorderManager = MediaRecorderManager.getSinstance(mDir);
        mRecorderManager.setpreparedlistenter(this);
        setOnLongClickListener(this);

    }
    @Override
    public boolean onLongClick(View v) {
        //在这里准备完成后触发完成的回调→wellprepared
       mRecorderManager.prepareAudio();
        isready = true;
        return false;
    }
    //定义3个what的状态
    private static  final  int what_prepared = 1;
    private static  final  int what_changevoicelevel = 2;
    private static  final  int what_cancel = 3;
    private static  final  int MAX_LEVEL =7;
    private float mtime;
    private boolean isready = false;
    private static final String TAG = "MainActivity";
    //在这里每次都进行数据的更新并计时
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while(isrecoding) {
                try {
                    Thread.sleep(100);
                    mtime += 0.1f;
                    Log.d(TAG, "time: "+mtime);
                    mHandler.sendEmptyMessage(what_changevoicelevel);

                    isready = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    //定义一个借口让act去更新回调
    public interface OnRecorderStateChangeListener{
        void updatatoact(String path, float time);
    }
    private OnRecorderStateChangeListener listener;
    public void setOnRecorderStateChangeListener(OnRecorderStateChangeListener mlistener){
        this.listener = mlistener;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case what_prepared:
                    //当设备准备好的时候再进行dialog的出现以及录音的开始
                    mDialogmanager.showRecordingDialog();
                    isrecoding = true;
                    //并且开启一个线程，让设备每次都去获取数据，并开始计时
                    new Thread(mRunnable).start();
                    break;
                case what_changevoicelevel:
                    //让dialog去更新音量
                    mDialogmanager.updatevoicelevel(mRecorderManager.getvoicelevel(MAX_LEVEL));
                    break;
                case what_cancel:
                    mDialogmanager.dissmissdialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void wellprepared() {
        //可以在这里用handler来发送消息，通知btn状态进行改变
        mHandler.sendEmptyMessage(what_prepared);

    }
    //把完成的dialogmanager添加到btn页面来
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下的时候确认正在录音,并且在这里拿到一开始的x,y
                //*//*这个状态判断只是为了测试做的
                isrecoding = true;
                changetostate(STATE_RECONDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isrecoding){
                    //在这里判断状态
                    if(wanttocancel(x,y)){
                        changetostate(STATE_WANT_TOCANCEL);
                    }else {
                        changetostate(STATE_RECONDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //在这里还要加多个状态判断，如果音频设备还没准备完毕就送收，则要return掉
                if(!isready){
                    reset();
                    return super.onTouchEvent(event);
                }
                //还有2中情况就是当录音时间过短时的事件
                if(mtime < 0.8f || !isrecoding){
                    mDialogmanager.tooshort();
                    mRecorderManager.cancel();
                    mHandler.sendEmptyMessageDelayed(what_cancel,2000);
                } else if(state == STATE_RECONDING){
                    //release
                    //如果正在录音的话，则要release音频。并且把文件存储到currentpath中
                        mDialogmanager.dissmissdialog();
                        mRecorderManager.release();
                       String currentpath = mRecorderManager.getcurrentpath();
                        //假如有人在监听这个事件，则把数据更新给act
                        if(listener != null){
                            listener.updatatoact(currentpath,mtime);
                        }
                    }else if(state == STATE_WANT_TOCANCEL){
                    //cancel
                    mDialogmanager.dissmissdialog();
                    //让音频文件放弃保存
                    mRecorderManager.cancel();
                }
                reset();
                break;
        }

        return super.onTouchEvent(event);
    }



    private boolean wanttocancel(float x, float y) {
        //在这里判断什么时候要取消录音
        if(x <0 || x>getWidth()){
            return true;
        }
        if(y < - DISTANCE_Y_WANT_TOCANCEL ||y > getHeight() +DISTANCE_Y_WANT_TOCANCEL){
            return  true;
        }
        return false;
    }

    //恢复一些状态
    private void reset() {
        changetostate(STATE_NORMAL);
        isready = false;
        mtime = 0;
        isrecoding = false;
    }

    private void changetostate(int changestate) {

            this.state = changestate;
        switch (changestate){
            case STATE_NORMAL:
                setBackgroundResource(R.drawable.btn_normal);
                setText(R.string.state_normal);
                break;
            case STATE_RECONDING:
                setBackgroundResource(R.drawable.btn_press);
                setText(R.string.state_recoding);
                if(isrecoding){
                    mDialogmanager.recording();
                }
                break;
            case STATE_WANT_TOCANCEL:
                setBackgroundResource(R.drawable.btn_press);
                setText(R.string.want_cancel);
                if(isrecoding){
                    mDialogmanager.wanttocancel();
                }
                break;
        }


    }



}

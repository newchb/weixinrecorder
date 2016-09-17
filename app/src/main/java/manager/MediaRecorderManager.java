package manager;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 创建者: hiboy
 * 创建时间: 2016/9/17.
 * 构建一个单例的mediarecorder的类，提供4个方法：prepareAudio,getvoicelevel
 * release,cancel,并提供一个借口回调告诉btn我prepared完了
 *
 */

public  class MediaRecorderManager {
    private static  MediaRecorderManager sinstance;
    private String mdir;
    private String mCurrentpath;
    private MediaRecorder mMediaRecorder;
    private boolean isprepared = false;

    public  MediaRecorderManager(String dir){
       this.mdir = dir;
    }
    private preparedcallback mlistener;

    public String getcurrentpath() {
        return  mCurrentpath;
    }

    public interface preparedcallback{
        void wellprepared();
    }
    public void setpreparedlistenter(preparedcallback listener){
        this.mlistener = listener;
    }
    //单例化一个mediarecorder，生命周期为activity同级
    public static MediaRecorderManager getSinstance(String dir){
        synchronized (MediaRecorderManager.class) {
            if (sinstance == null) {
                sinstance = new MediaRecorderManager(dir);
            }
        }
        return sinstance;
    }
    //准备开启recorder并提供一个借口，告诉btn我准备完了
    public void prepareAudio(){
        //一开始先准备一个file文件的保存路径
        isprepared = false;
        try {
            File dir = new File(mdir);
            if(!dir.exists()){
                dir.mkdirs();
            }
        String filename = getgeneratefilename();
        File file = new File(dir,filename);

        mCurrentpath = file.getAbsolutePath();
        //开始准备一个mediarecoreder的音频录音类
        mMediaRecorder = new MediaRecorder();
        //设置文件的输出路径
        mMediaRecorder.setOutputFile(mCurrentpath);
        //使用麦克风作为音频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置输出的音频格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //设置输出的音频编码方式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.prepare();
        mMediaRecorder.start();
            //这里告诉btn我准备完了,同时设置个标志位，让类中的其他方法能够响应
        if(mlistener != null){
            mlistener.wellprepared();
        }
            isprepared = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getgeneratefilename() {
        return UUID.randomUUID().toString()+".amr";
    }

    public int getvoicelevel(int maxlevel){
        if(mMediaRecorder != null && isprepared){
            //getmaxamplitude的范围为1~32767
            // 并且是间隔一段时间调用一次，需要放在线程中，第一次调用会返回0
            //把这个值/32768保证 这个值是小于0的，*max会四舍五入返回一个0~6再加以1就确保了再max范围内了
            //max这里设置为7,而且可能会出现设备故障，不能让其直接返回，需要有个异常来包裹
            try {
                int level = maxlevel*mMediaRecorder.getMaxAmplitude()/32768+1;
                return  level;
            } catch (IllegalStateException e) {
            }
        }
        return 1;
    }
    public void release(){
        if(mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    public void cancel(){
        //在释放这个recorder录音设备的基础上，还要把生成的文件路径给delete掉
        release();
        if(mCurrentpath != null) {
            File file = new File(mCurrentpath);
            file.delete();
        }
    }
}

package manager;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 创建者: hiboy
 * 创建时间: 2016/9/17.
 */
public class MediaManager {
    private static MediaPlayer player;
    public static void playvoice(String filepath, MediaPlayer.OnCompletionListener onCompletionListener) {
        //在这里负责播放动画,只要是走mediaplayer的生命周期，并提供3个和activity对应的生命周期方法
        if(player == null){
            player = new MediaPlayer();
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    player.reset();
                    return true;
                }
            });
        }else {
            player.reset();
        }
        try {
            player.setDataSource(filepath);
            //存取为音乐类节点文件
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(onCompletionListener);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static  void onpause(){
        if(player != null && player.isPlaying()){
            player.pause();
        }

    }
    public static  void onresume(){
        if(player != null && player.isPlaying()){
            player.start();
        }

    }
    public static  void onrelease(){
        if(player != null && player.isPlaying()){
            player.release();
        }

    }
}

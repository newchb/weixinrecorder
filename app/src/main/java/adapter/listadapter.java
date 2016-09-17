package adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.item.weixinrecoder.MainActivity;
import com.item.weixinrecoder.R;

import java.util.List;

/**
 * 创建者: hiboy
 * 创建时间: 2016/9/17.
 */
public class listadapter extends ArrayAdapter<MainActivity.Recorder> {
    private Context mContext;
    private List<MainActivity.Recorder> mdata;
    //定义最大和最小的屏幕宽高
    private int minlength;
    private int maxlength;
    public listadapter(Context context, List<MainActivity.Recorder> datas) {
        super(context, -1,datas);
        this.mContext = context;
        this.mdata = datas;
        getscreensize(context);

    }

    private void getscreensize(Context context) {
      WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outmetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outmetrics);
        maxlength = (int) (outmetrics.widthPixels * 0.6f);
        minlength = (int) (outmetrics.widthPixels * 0.15f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(getContext(), R.layout.list_item,null);
            holder = new ViewHolder();
            holder.anim = convertView.findViewById(R.id.list_item_anim);
            holder.length = (FrameLayout) convertView.findViewById(R.id.list_item_lenght);
            holder.time = (TextView) convertView.findViewById(R.id.list_item_time);
            convertView.setTag(holder);
        }else {
           holder = (ViewHolder) convertView.getTag();
        }
            holder.time.setText(Math.round(getItem(position).getTime())+"'");
        ViewGroup.LayoutParams params = holder.length.getLayoutParams();
        params.width = (int) (minlength +(maxlength*getItem(position).getTime()/60));


        return convertView;
    }
    class ViewHolder{
        TextView time;
        View anim;
        FrameLayout length;
    }
}

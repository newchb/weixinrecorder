package manager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.item.weixinrecoder.R;

/**
 * 创建者: hiboy
 * 创建时间: 2016/9/17.
 */
public class dialogmanager {
    private Context mContext;
    private Dialog mdialog;
    private ImageView mIcon;
    private ImageView mVoicelevel;
    private TextView mDialog_text;

    public dialogmanager(Context context) {
        this.mContext = context;
    }

    public void showRecordingDialog() {
        mdialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_recoding, null);
        mdialog.setContentView(view);
        mIcon = (ImageView) mdialog.findViewById(R.id.dialog_icon);
        mVoicelevel = (ImageView) mdialog.findViewById(R.id.dialog_voicelevel);
        mDialog_text = (TextView) mdialog.findViewById(R.id.dialog_text);
        mdialog.show();
    }

    public void recording() {
        if (mdialog != null && mdialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoicelevel.setVisibility(View.VISIBLE);
            mDialog_text.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.mipmap.recorder);
            mVoicelevel.setImageResource(R.drawable.v1);
            mDialog_text.setText(R.string.dialog_normal);
        }
    }

    public void dissmissdialog() {
        if (mdialog != null && mdialog.isShowing()) {
            mdialog.dismiss();
            mdialog = null;
        }
    }

    public void tooshort() {
        if (mdialog != null && mdialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoicelevel.setVisibility(View.GONE);
            mDialog_text.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.mipmap.voice_to_short);
            mDialog_text.setText(R.string.dialog_too_short);
        }
    }

    public void wanttocancel() {
        if (mdialog != null && mdialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoicelevel.setVisibility(View.GONE);
            mDialog_text.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.mipmap.cancel);
            mDialog_text.setText(R.string.dialog_tocancel);
        }
    }

    public void updatevoicelevel(int level) {
        if (mdialog != null && mdialog.isShowing()) {
           /* mIcon.setVisibility(View.VISIBLE);
            mVoicelevel.setVisibility(View.VISIBLE);
            mDialog_text.setVisibility(View.VISIBLE);*/
            //使用这种identifier的形势来动态改变声量图片的声量大小
            int resId = mContext.getResources().getIdentifier("v" + level, "drawable"
                    , mContext.getPackageName());
            mVoicelevel.setImageResource(resId);
//           mVoicelevel.setImageResource(R.mipmap.v1);
            mDialog_text.setText(R.string.dialog_normal);
        }
    }

}

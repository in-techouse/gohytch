package lcwu.fyp.gohytch.director;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jeevandeshmukh.fancybottomsheetdialoglib.FancyBottomSheetDialog;

import lcwu.fyp.gohytch.R;

public class Helpers {
    public boolean isConnected(Context c) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
        return  connected;
    }
    public void showError(Activity a,String title,String message){
        new FancyBottomSheetDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor("#F43636")) //don't use R.color.somecolor
                .setIcon(R.drawable.ic_action_error,true)
                .isCancellable(false)
                .OnNegativeClicked(new FancyBottomSheetDialog.FancyBottomSheetDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .OnPositiveClicked(new FancyBottomSheetDialog.FancyBottomSheetDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .setNegativeBtnText("Cancel")
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground(Color.parseColor("#F43636"))//don't use R.color.somecolor
                .setNegativeBtnBackground(Color.WHITE)//don't use R.color.somecolor
                .build();
    }
}

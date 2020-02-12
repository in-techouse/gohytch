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
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
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

    public void showErrorWithActivityClose(final Activity a,String title,String message){
        new FancyBottomSheetDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor("#F43636")) //don't use R.color.somecolor
                .setIcon(R.drawable.ic_action_error,true)
                .isCancellable(false)
                .OnNegativeClicked(new FancyBottomSheetDialog.FancyBottomSheetDialogListener() {
                    @Override
                    public void OnClick() {
                        a.finish();
                    }
                })
                .OnPositiveClicked(new FancyBottomSheetDialog.FancyBottomSheetDialogListener() {
                    @Override
                    public void OnClick() {
                        a.finish();
                    }
                })
                .setNegativeBtnText("Cancel")
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground(Color.parseColor("#F43636"))//don't use R.color.somecolor
                .setNegativeBtnBackground(Color.WHITE)//don't use R.color.somecolor
                .build();
    }

    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        long factor = (long)Math.pow(10, 3);
        dist = dist * factor;
        double temp = Math.round(dist);
        return (temp/factor);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}

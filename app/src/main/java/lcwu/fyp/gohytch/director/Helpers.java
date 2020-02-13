package lcwu.fyp.gohytch.director;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.jeevandeshmukh.fancybottomsheetdialoglib.FancyBottomSheetDialog;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.activities.BookingDetails;
import lcwu.fyp.gohytch.activities.VendorDashboard;

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
                .setPositiveBtnText("Okay")
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

    public void sendNotification(Activity activity, String text, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "1");
        builder.setTicker(text);
        builder.setAutoCancel(true);
        builder.setChannelId("1");
        builder.setContentInfo(text);
        builder.setContentTitle(text);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.build();
        NotificationManager manager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(10,builder.build());
        }
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

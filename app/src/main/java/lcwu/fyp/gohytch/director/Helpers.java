package lcwu.fyp.gohytch.director;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeevandeshmukh.fancybottomsheetdialoglib.FancyBottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.model.Booking;
import lcwu.fyp.gohytch.model.Notification;
import lcwu.fyp.gohytch.model.User;

public class Helpers {
    private ValueEventListener listener;

    public boolean isConnected(Context c) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }

    public void showError(Activity a, String title, String message) {
        new FancyBottomSheetDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor("#F43636"))
                .setIcon(R.drawable.ic_action_error, true)
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
                .setPositiveBtnBackground(Color.parseColor("#F43636"))
                .setNegativeBtnBackground(Color.WHITE)
                .build();
    }

    public void showSuccess(Activity a, String title, String message) {
        new FancyBottomSheetDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor("#6EA5E2"))
                .setIcon(R.drawable.ic_action_error, true)
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
                .setPositiveBtnText("Okay")
                .setPositiveBtnBackground(Color.parseColor("#6EA5E2"))
                .setNegativeBtnBackground(Color.WHITE)
                .build();
    }

    public void showSuccessNoClose(Activity a, String title, String message) {
        new FancyBottomSheetDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor("#6EA5E2"))
                .setIcon(R.drawable.ic_action_error, true)
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
                .setPositiveBtnBackground(Color.parseColor("#6EA5E2"))
                .setNegativeBtnBackground(Color.WHITE)
                .build();
    }

    public void showErrorWithActivityClose(final Activity a, String title, String message) {
        new FancyBottomSheetDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setBackgroundColor(Color.parseColor("#F43636"))
                .setIcon(R.drawable.ic_action_error, true)
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
                .setPositiveBtnBackground(Color.parseColor("#F43636"))
                .setNegativeBtnBackground(Color.WHITE)
                .build();
    }

    public void sendNotification(Activity activity, String text, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "1");
        builder.setTicker(text);
        builder.setAutoCancel(true);
        builder.setChannelId("1");
        builder.setContentInfo(text);
        builder.setContentTitle(text);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.build();
        NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(10, builder.build());
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
        long factor = (long) Math.pow(10, 3);
        dist = dist * factor;
        double temp = Math.round(dist);
        return (temp / factor);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void cancelRequest(final Booking booking, User provider) {
        if (provider.getType().equals("Driver"))
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Bookings").child(booking.getId()).child("status").setValue("Rejected");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener != null)
                    reference.child("Users").child(booking.getUserId()).removeEventListener(listener);
                if (dataSnapshot.exists()) {
                    User customer = dataSnapshot.getValue(User.class);
                    if (customer != null) {
                        Log.e("RejectBooking", "Customer found: " + customer.getName());
                        sendNotification(booking, customer, provider, "Your booking request has been rejected by " + provider.getName(), "You reject the booking request of " + customer.getName());
                    } else {
                        Log.e("RejectBooking", "Customer is Null");
                    }
                } else {
                    Log.e("RejectBooking", "DataSnapShot is Null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null)
                    reference.child("Users").child(booking.getUserId()).removeEventListener(listener);

                Log.e("RejectBooking", "Data read failed");
            }
        };
        reference.child("Users").child(booking.getUserId()).addValueEventListener(listener);
    }

    public void sendNotification(Booking activeBooking, User customer, User provider, String userText, String providerText) {
        try {
            final Notification notification = new Notification();
            final DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
            String id = notificationReference.push().getKey();
            notification.setId(id);
            notification.setBookingId(activeBooking.getId());
            notification.setUserId(customer.getPhoneNumber());
            notification.setDriverId(provider.getPhoneNumber());
            notification.setRead(false);
            Date d = new Date();
            String date = new SimpleDateFormat("EEE dd, MMM, yyyy hh:mm aa").format(d);
            notification.setDate(date);
            notification.setDriverText(providerText);
            notification.setUserText(userText);
            notificationReference.child(notification.getId()).setValue(notification);
        } catch (Exception e) {
            Log.e("SendNotification", "Exception Occur: " + e.getMessage());
        }
    }

    public void updateUserLocation(User user) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Log.e("location", "Update location called");
        reference.child(user.getPhoneNumber()).child("lat").setValue(user.getLat());
        reference.child(user.getPhoneNumber()).child("lng").setValue(user.getLng());
    }
}

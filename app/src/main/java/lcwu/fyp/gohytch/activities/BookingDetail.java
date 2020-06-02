package lcwu.fyp.gohytch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Booking;
import lcwu.fyp.gohytch.model.Notification;
import lcwu.fyp.gohytch.model.User;

public class BookingDetail extends AppCompatActivity {
    private static final String TAG = "BookingDetail";
    private Booking booking;
    private Notification notification;
    private User user;
    private Helpers helpers;

    private MapView map;
    private GoogleMap googleMap;

    private LinearLayout progress;
    private ScrollView main;
    private Button startRide;
    private CircleImageView userImage;
    private TextView userName, userContact, userEmail, address, bookingDate, bookingStatus, bookingFare, withDriver;
    private RelativeLayout withDriverUpper;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener bookingListener, userListener;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        Intent it = getIntent();
        if (it == null) {
            Log.e(TAG, "Intent is null");
            finish();
            return;
        }

        Bundle bundle = it.getExtras();
        if (bundle == null) {
            Log.e(TAG, "Bundle is null");
            finish();
            return;
        }

        booking = (Booking) bundle.getSerializable("booking");
        notification = (Notification) bundle.getSerializable("notification");

        if (booking == null && notification == null) {
            Log.e(TAG, "Booking and Notification is null");
            finish();
            return;
        }

        progress = findViewById(R.id.progress);
        main = findViewById(R.id.main);
        startRide = findViewById(R.id.startRide);

        map = findViewById(R.id.map);

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userContact = findViewById(R.id.userContact);
        address = findViewById(R.id.address);
        bookingDate = findViewById(R.id.bookingDate);
        bookingStatus = findViewById(R.id.bookingStatus);
        bookingFare = findViewById(R.id.bookingFare);
        withDriverUpper = findViewById(R.id.withDriverUpper);
        withDriver = findViewById(R.id.withDriver);


        Session session = new Session(BookingDetail.this);
        user = session.getSession();
        helpers = new Helpers();

//        if (!user.getType().equals("Renter")) {
//            startRide.setVisibility(View.GONE);
//        }

//        main.setVisibility(View.GONE);
//        startRide.setVisibility(View.GONE);
//        progress.setVisibility(View.VISIBLE);


        map.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(BookingDetail.this);
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gM) {
                    Log.e("Maps", "Call back received");
                    View locationButton = ((View) map.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on right bottom
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    rlp.setMargins(0, 350, 100, 0);
                    googleMap = gM;
                    LatLng defaultPosition = new LatLng(31.5204, 74.3487);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(defaultPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    displayData();
                }
            });
        } catch (Exception e) {
            helpers.showError(BookingDetail.this, "Map Error", "Something Went Wrong!");
        }
    }

    private void displayData() {
        if (booking != null) {
            loadUserDetail();
        } else {

        }
    }

    private void loadUserDetail() {
        if (user.getType().equals("User")) {
            userId = booking.getDriverId();
        } else {
            userId = booking.getUserId();
        }
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userListener != null)
                    databaseReference.child("Users").child(userId).addValueEventListener(userListener);

                if (dataSnapshot.exists()) {
                    User otherUser = dataSnapshot.getValue(User.class);
                    if (otherUser != null) {
                        if (otherUser.getImage() != null && otherUser.getImage().length() > 1) {
                            Glide.with(getApplicationContext()).load(otherUser.getImage()).into(userImage);
                        }
                        userName.setText(otherUser.getName());
                        userContact.setText(otherUser.getPhoneNumber());
                        userEmail.setText(otherUser.getEmail());

                        address.setText(booking.getAddress());
                        bookingDate.setText(booking.getBookingTime());
                        bookingStatus.setText(booking.getStatus());
                        bookingFare.setText(booking.getFare() + " RS");

                        if (booking.getType().equals("Renter")) {
                            if (booking.isWithDriver())
                                withDriver.setText("YES");
                            else
                                withDriver.setText("NO");
                        } else {
                            withDriverUpper.setVisibility(View.GONE);
                        }
                    }
                }
                progress.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (userListener != null)
                    databaseReference.child("Users").child(userId).addValueEventListener(userListener);
                progress.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
            }
        };

        databaseReference.child("Users").child(userId).addValueEventListener(userListener);
    }


    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        if (userListener != null)
            databaseReference.child("Users").child(userId).addValueEventListener(userListener);
//        if (databaseReference != null) {
//            if (bookingListener != null) {
//                databaseReference.removeEventListener(bookingListener);
//            }
//            if (userListener != null) {
//                databaseReference.removeEventListener(userListener);
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
}
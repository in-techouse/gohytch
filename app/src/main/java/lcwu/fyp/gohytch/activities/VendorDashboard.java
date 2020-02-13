package lcwu.fyp.gohytch.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeevandeshmukh.fancybottomsheetdialoglib.FancyBottomSheetDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Booking;
import lcwu.fyp.gohytch.model.Notification;
import lcwu.fyp.gohytch.model.User;

public class VendorDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private ValueEventListener bookingsValueEventListener, userValueEventListener, bookingValueEventListener;
    private MapView map;
    private Helpers helpers;
    private User user;
    private GoogleMap googleMap;
    private DrawerLayout drawer;
    private Session session;
    private FusedLocationProviderClient locationProviderClient;
    private TextView locationAddress;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressBar sheetProgress;
    private RelativeLayout mainSheet;
    private BottomSheetBehavior sheetBehavior;
    private Booking activeBooking;
    private User activeCustomer;
    private CircleImageView customerImage;
    private TextView customerName, customerContact, customerEmail, bookingDate, bookingAddress, bookingFare;
    private Button cancelBooking , completeBooking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);
        helpers = new Helpers();
        session = new Session(VendorDashboard.this);
        user = session.getSession();
        locationAddress = findViewById(R.id.locationAddress);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout layoutBottomSheet = findViewById(R.id.vendor_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetProgress = findViewById(R.id.vendor_sheetProgress);
        mainSheet = findViewById(R.id.vendor_mainSheet);


        customerImage = findViewById(R.id.vendor_Image);
        customerName = findViewById(R.id.vendorName);
        customerContact = findViewById(R.id.providerCategory);
        bookingDate = findViewById(R.id.vendor_bookingDate);
        bookingAddress = findViewById(R.id.vendor_bookingAddress);
        customerEmail = findViewById(R.id.customerEmail);
        Button completeBooking = findViewById(R.id.completeBooking);
        Button cancelBooking = findViewById(R.id.cancelBooking);
        bookingFare = findViewById(R.id.bookingFare);

        completeBooking.setOnClickListener(this);
        cancelBooking.setOnClickListener(this);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(VendorDashboard.this);
        TextView profile_Phone = header.findViewById(R.id.profile_Phone);
        TextView profile_Name = header.findViewById(R.id.profile_Name);
        TextView profile_Email = header.findViewById(R.id.profile_Email);
        CircleImageView profile_Image = header.findViewById(R.id.profile_image);
        TextView type = header.findViewById(R.id.profile_type);

        profile_Name.setText(user.getName());
        profile_Email.setText(user.getEmail());
        profile_Phone.setText(user.getPhoneNumber());
        type.setText(user.getType());

        if(user.getImage() != null && user.getImage().length() > 5){
            Glide.with(getApplicationContext()).load(user.getImage()).into(profile_Image);
        }
        map = findViewById(R.id.map);
        map.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(VendorDashboard.this);
            Log.e("Maps", "Try Block");
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gM) {
                    Log.e("Maps", "Call back received");
                    googleMap = gM;
                    LatLng defaultPosition = new LatLng(31.5204, 74.3487);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(defaultPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    enableLocation();
                }
            });
        } catch (Exception e) {
            Log.e("Maps", "Catch Block");
            helpers.showError(VendorDashboard.this, "Error", "Something went wrong.Try again");
        }
    }

    private boolean hasPermissions(Context c, String... permission){
        for(String p : permission){
            if(ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public void enableLocation() {
        boolean flag = hasPermissions(VendorDashboard.this, PERMISSIONS);
        if(!flag){
            ActivityCompat.requestPermissions(VendorDashboard.this, PERMISSIONS, 1);
        }
        else{
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    FusedLocationProviderClient current = LocationServices.getFusedLocationProviderClient(VendorDashboard.this);
                    current.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            getDeviceLocation();
                        }
                    });
                    return false;
                }
            });
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            getDeviceLocation();
            listenToBookings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            enableLocation();
        }
    }

    private void getDeviceLocation() {
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
                helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + ex.getMessage());
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + ex.getMessage());
            }
            if (!gps_enabled && !network_enabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(VendorDashboard.this);
                dialog.setMessage("Oppss.Your Location Service is off.\n Please turn on your Location and Tr again Later");
                dialog.setPositiveButton("Let me On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
                return;
            }
            locationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            googleMap.clear();
                            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(me).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 11));
                            Geocoder geocoder = new Geocoder(VendorDashboard.this);
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(me.latitude, me.longitude, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String strAddress = "";
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        strAddress = strAddress + "" + address.getAddressLine(i);
                                    }
                                    locationAddress.setText(strAddress);
                                }
                            } catch (Exception e) {
                                helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                            }
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                }
            });
        }catch (Exception e){
            helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case (R.id.nav_Booking): {
                Intent it=new Intent(VendorDashboard.this, BookingActivity.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_Notification): {
                Intent it=new Intent(VendorDashboard.this, NotificationActivity.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_profile): {
                Intent it = new Intent(VendorDashboard.this , EditUserProfile.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_Logout): {
                auth.signOut();
                session.destroySession();
                Intent it=new Intent(VendorDashboard.this,LoginActivity.class);
                startActivity(it);
                finish();

                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        map.onDestroy();
        if(bookingsValueEventListener != null){
            bookingReference.removeEventListener(bookingsValueEventListener);
        }
        if(bookingValueEventListener != null){
            bookingReference.removeEventListener(bookingValueEventListener);
        }
        if(userValueEventListener != null){
            userReference.removeEventListener(userValueEventListener);
        }
    }

    private void listenToBookings() {
        Log.e("VendorDashboard" , "in func");
        bookingsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    Log.e("VendorDashboard", "Bookings Listener DataSnapshot: " + data.toString());
                    final Booking booking = data.getValue(Booking.class);
                    if(booking != null && booking.getDriverId() != null ){
                        Log.e("VendorDashboard", "Booking Status: " + booking.getStatus());
                        if(booking.getStatus().equals("In Progress")){
                            bookingReference.removeEventListener(bookingsValueEventListener);
                            Log.e("VendorDashboard" , "Booking status in Progress found");
                            activeBooking = booking;
                            sheetBehavior.setHideable(false);
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            loadCustomerDetails();
                        }
                        else if (booking.getDriverId().length() < 1 && booking.getStatus().equals("New") && booking.getType().equals(user.getType())) {
                            showBookingDialog(booking);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("VendorDashboard" , "in onCancelled "+databaseError.toString());
            }
        };
        bookingReference.addValueEventListener(bookingsValueEventListener);
    }


    private void loadCustomerDetails(){
        Log.e("VendorDashboard", "Call Received to Load Customer");
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userReference.removeEventListener(userValueEventListener);
                if(activeCustomer == null) {
                    Log.e("VendorDashboard", "User Value Received, on Data Changed: " + dataSnapshot.toString());
                    activeCustomer = dataSnapshot.getValue(User.class);
                    if (activeCustomer != null) {
                        if (activeCustomer.getImage() != null && activeCustomer.getImage().length() > 1) {
                            Log.e("VendorDashboard", "Active Customer Image Found");
                            Glide.with(getApplicationContext()).load(activeCustomer.getImage()).into(customerImage);
                        }
                        Log.e("VendorDashboard", "Active Customer Detail set, Id: " + activeCustomer.getId());
                        Log.e("VendorDashboard", "Active Customer Detail set, Name: " + activeCustomer.getName() + " Contact: " + activeCustomer.getPhoneNumber());
                        Log.e("VendorDashboard", "Active Customer Detail set, Image: " + activeCustomer.getImage() + " Email: " + activeCustomer.getEmail());
                        customerName.setText(activeCustomer.getName());
                        customerContact.setText(activeCustomer.getPhoneNumber());
                        customerEmail.setText(activeCustomer.getEmail());
                        bookingDate.setText(activeBooking.getBookingTime());
                        bookingAddress.setText(activeBooking.getAddress());
                        bookingFare.setText(activeBooking.getFare() + " RS.");
                    }
                }
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                incrementInFare();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userReference.removeEventListener(userValueEventListener);
                Log.e("VendorDashboard", "User Value Received, on Cancelled: " + databaseError.getMessage());
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }
        };
        userReference.child(activeBooking.getUserId()).addValueEventListener(userValueEventListener);
        listenToActiveBookingChange();
    }

    private void listenToActiveBookingChange(){
        bookingValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("VendorDashboard", "Booking Listener: " + dataSnapshot.toString());
                Booking booking = dataSnapshot.getValue(Booking.class);
                if(booking != null && activeBooking != null){
                    Log.e("VendorDashboard", "Booking Listener, Fare: " + booking.getFare() + " Status: " + booking.getStatus());
                    switch (booking.getStatus()){
                        case "Cancelled":{
                            Log.e("VendorDashboard", "Booking Cancelled");
                            activeBooking = booking;
                            showCancelledNotification();
                            break;
                        }
                        case "Completed": {
                            Log.e("VendorDashboard", "Booking Completed");
                            activeBooking = booking;
                            showCompletedNotification();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        bookingReference.child(activeBooking.getId()).addValueEventListener(bookingValueEventListener);
    }

    private void incrementInFare(){
        Log.e("VendorDashboard", "Increment in Fare called");
        new CountDownTimer(12000, 12000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                if(activeBooking != null && activeBooking.getStatus().equals("In Progress")){
                    String fare = bookingFare.getText().toString();
                    String[] fareArray = fare.split(" ");
                    int totalFare = Integer.parseInt(fareArray[0]);
                    totalFare = totalFare + 17;
                    FirebaseDatabase.getInstance().getReference().child("Bookings").child(activeBooking.getId()).child("fare").setValue(totalFare);
                    bookingFare.setText(totalFare + " RS.");
                    incrementInFare();
                }
            }
        }.start();
    }

    private void showCompletedNotification(){
        helpers.sendNotification(VendorDashboard.this, "Booking Completed", "Your booking has been Completed.");
        helpers.showError(VendorDashboard.this, "Booking Completed", "Your booking with " + activeCustomer.getName() +" has been Completed.");
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        activeBooking = null;
    }


    private void showCancelledNotification(){
        bookingReference.removeEventListener(bookingValueEventListener);
        helpers.sendNotification(VendorDashboard.this, "Booking Cancelled", "Your booking has been cancelled.");
        helpers.showError(VendorDashboard.this, "Booking Cancelled", "Your booking with " + activeCustomer.getName() +" has been cancelled.");
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        activeBooking = null;
        listenToBookings();
    }

    private void showBookingDialog(final Booking booking){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(VendorDashboard.this, "1");
        builder.setTicker("New Booking");
        builder.setAutoCancel(true);
        builder.setChannelId("1");
        builder.setContentInfo("New Booking Found.");
        builder.setContentTitle("New Booking Found.");
        builder.setContentText("We have a new booking for you. It's time to get some revenue.");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        builder.build();
        Intent notificationIntent = new Intent(VendorDashboard.this, BookingDetails.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Booking", booking);
        notificationIntent.putExtras(bundle);
        PendingIntent conPendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(conPendingIntent);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(10,builder.build());
        }

        new FancyBottomSheetDialog.Builder(VendorDashboard.this)
                .setTitle("New Booking Found")
                .setMessage("We have a new booking for you. It's time to get some revenue.")
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
                        Intent it = new Intent(VendorDashboard.this, BookingDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Booking", booking);
                        it.putExtras(bundle);
                        startActivity(it);
                    }
                })
                .setNegativeBtnText("Reject")
                .setPositiveBtnText("DETAILS")
                .setPositiveBtnBackground(Color.parseColor("#F43636"))//don't use R.color.somecolor
                .setNegativeBtnBackground(Color.WHITE)//don't use R.color.somecolor
                .build();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.completeBooking:{
                onBookingCompleted();
                break;
            }
            case R.id.cancelBooking:{
                onBookingCancelled();
                break;
            }
        }
    }

    private void onBookingCompleted(){
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        activeBooking.setStatus("Completed");
        final Notification notification = new Notification();
        final DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String id = notificationReference.push().getKey();
        notification.setId(id);
        notification.setBookingId(activeBooking.getId());
        notification.setUserId(activeCustomer.getPhoneNumber());
        notification.setDriverId(user.getPhoneNumber());
        notification.setRead(false);
        Date d = new Date();
        String date = new SimpleDateFormat("EEE dd, MMM, yyyy HH:mm").format(d);
        notification.setDate(date);
        notification.setDriverText("You completed the booking of " + activeCustomer.getName());
        notification.setUserText("Your booking has been completed by " + user.getName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookings");
        reference.child(activeBooking.getId()).setValue(activeBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notificationReference.child(notification.getId()).setValue(notification);
                sheetBehavior.setHideable(true);
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                helpers.showError(VendorDashboard.this, "ERROR", "Something went wrong.");
            }
        });
    }


    private void onBookingCancelled(){
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        activeBooking.setStatus("Cancelled");
        final Notification notification = new Notification();
        final DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String id = notificationReference.push().getKey();
        notification.setId(id);
        notification.setBookingId(activeBooking.getId());
        notification.setUserId(activeCustomer.getPhoneNumber());
        notification.setDriverId(user.getPhoneNumber());
        notification.setRead(false);
        Date d = new Date();
        String date = new SimpleDateFormat("EEE dd, MMM, yyyy HH:mm").format(d);
        notification.setDate(date);
        notification.setDriverText("You cancelled the booking of " + activeCustomer.getName());
        notification.setUserText("Your booking has been cancelled by " + user.getName());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookings");
        reference.child(activeBooking.getId()).setValue(activeBooking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notificationReference.child(notification.getId()).setValue(notification);
                sheetBehavior.setHideable(true);
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
                helpers.showError(VendorDashboard.this, "ERROR", "Something went wrong.");
            }
        });
    }

    @Override
    protected void onPause () {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onResume () {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onLowMemory () {
        super.onLowMemory();
        map.onLowMemory();
    }
}


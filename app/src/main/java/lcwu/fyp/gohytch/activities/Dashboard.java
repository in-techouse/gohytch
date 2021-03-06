package lcwu.fyp.gohytch.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.dialog.ReviewDialog;
import lcwu.fyp.gohytch.dialog.UserDialog;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Booking;
import lcwu.fyp.gohytch.model.User;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private MapView map;
    private Helpers helpers;
    private User user;
    private GoogleMap googleMap;
    private DrawerLayout drawer;
    private Session session;
    private FusedLocationProviderClient locationProviderClient;
    private Marker marker;
    private TextView locationAddress;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference vendorReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference driverReference = FirebaseDatabase.getInstance().getReference().child("Users");
    private List<User> renters = new ArrayList<>();
    private DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private Spinner selectType;
    private ValueEventListener bookingListener, bookingsListener, vendorsListener, driverListener;
    private Button confirm;
    private LinearLayout searching;
    private ProgressBar sheetProgress;
    private RelativeLayout mainSheet;
    private Booking activeBooking;
    private User activeDriver;
    private CountDownTimer timer;
    private BottomSheetBehavior sheetBehavior;
    private TextView driverName, driverBookingDate, driverBookingAddress, driverContact, driverEmail;
    private CircleImageView driverImage;
    private Button cancelBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        helpers = new Helpers();
        session = new Session(Dashboard.this);
        user = session.getSession();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(Dashboard.this);

        LinearLayout layoutBottomSheet = findViewById(R.id.customer_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        sheetProgress = findViewById(R.id.customer_sheetProgress);
        mainSheet = findViewById(R.id.customer_mainSheet);


        TextView profile_Phone = header.findViewById(R.id.profile_Phone);
        TextView profile_Name = header.findViewById(R.id.profile_Name);
        TextView profile_Email = header.findViewById(R.id.profile_Email);
        CircleImageView profile_Image = header.findViewById(R.id.profile_image);
        locationAddress = findViewById(R.id.locationAddress);
        selectType = findViewById(R.id.selecttype);

        selectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    Intent it = new Intent(Dashboard.this, SelectRenter.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("users", (Serializable) renters);
                    it.putExtras(bundle);
                    startActivity(it);
                    selectType.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        searching = findViewById(R.id.searching);

        driverName = findViewById(R.id.driver_name);
        driverImage = findViewById(R.id.driverImage);
        driverBookingDate = findViewById(R.id.driver_bookingDate);
        driverBookingAddress = findViewById(R.id.driver_bookingAddress);
        driverContact = findViewById(R.id.driverContact);
        driverEmail = findViewById(R.id.driverEmail);
        cancelBooking = findViewById(R.id.cancelDriverBooking);
        cancelBooking.setOnClickListener(this);


        profile_Name.setText(user.getName());
        profile_Email.setText(user.getEmail());
        profile_Phone.setText(user.getPhoneNumber());
        if (user.getImage() != null && user.getImage().length() > 5) {
            Glide.with(Dashboard.this).load(user.getImage()).into(profile_Image);
        }
        map = findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(Dashboard.this);
            Log.e("Maps", "Try Block");
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gM) {
                    Log.e("Maps", "Call back received");
                    View locationButton = ((View) map.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on right bottom
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    rlp.setMargins(0, 350, 50, 0);

                    googleMap = gM;
                    LatLng defaultPosition = new LatLng(31.5204, 74.3487);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(defaultPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    enableLocation();
                }
            });
        } catch (Exception e) {
            Log.e("Maps", "Catch Block");
            helpers.showError(Dashboard.this, "Error", "Something went wrong.Try again");
        }
        if (user.getType() == null || user.getType().equals("None")) {
            UserDialog userDialog = new UserDialog(Dashboard.this, Dashboard.this);
            userDialog.setCanceledOnTouchOutside(false);
            userDialog.setCancelable(false);
            userDialog.show();
        }
    }

    private boolean hasPermissions(Context c, String... permission) {
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void enableLocation() {
        boolean flag = hasPermissions(Dashboard.this, PERMISSIONS);
        if (!flag) {
            ActivityCompat.requestPermissions(Dashboard.this, PERMISSIONS, 1);
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    FusedLocationProviderClient current = LocationServices.getFusedLocationProviderClient(Dashboard.this);
                    current.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    getDeviceLocation();
                                }
                            });
                    return false;
                }
            });
            getDeviceLocation();
            loadVendors();
            listenToBookingChanges();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            enableLocation();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.confirm: {
                if (!helpers.isConnected(Dashboard.this)) {
                    helpers.showError(Dashboard.this, "Connection Error", "Check your Internet Connection");
                    return;
                }
                if (selectType.getSelectedItemPosition() == 0) {
                    helpers.showError(Dashboard.this, "Service Type", "Choose a Type First");
                    return;
                }
                if (selectType.getSelectedItemPosition() == 2) {
                    Intent it = new Intent(Dashboard.this, SelectRenter.class);
                    startActivity(it);
                    return;
                }
                searching.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.GONE);
                selectType.setVisibility(View.GONE);
                String key = bookingReference.push().getKey();
                activeBooking = new Booking();
                activeBooking.setId(key);
                activeBooking.setUserId(user.getPhoneNumber());
                Date d = new Date();
                String date = new SimpleDateFormat("EEE dd, MMM, yyyy hh:mm aa").format(d);
                activeBooking.setBookingTime(date);
                activeBooking.setStartTime("");
                activeBooking.setEndTime("");
                activeBooking.setLat(marker.getPosition().latitude);
                activeBooking.setLng(marker.getPosition().longitude);
                activeBooking.setStatus("New");
                activeBooking.setDriverId("");
                activeBooking.setAddress(locationAddress.getText().toString());
                activeBooking.setType(selectType.getSelectedItem().toString());
                activeBooking.setFare(0);
                activeBooking.setWithDriver(false);
                cancelBooking.setVisibility(View.VISIBLE);
                bookingReference.child(activeBooking.getId()).setValue(activeBooking)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                timer = new CountDownTimer(30000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        Log.e("Dashboard", "OnTick");
                                    }

                                    @Override
                                    public void onFinish() {
                                        Log.e("Dashboard", "onFinish");
                                        if (activeBooking.getStatus().equals("New")) {
                                            activeBooking.setStatus("Rejected");
                                            bookingReference.child(activeBooking.getId()).setValue(activeBooking);
                                            searching.setVisibility(View.GONE);
                                            confirm.setVisibility(View.VISIBLE);
                                            selectType.setVisibility(View.VISIBLE);
                                            helpers.showError(Dashboard.this, "Booking Error", "No Driver/Renter Available Please Try Again Later!");
                                        } else if (activeBooking.getStatus().equals("In Progress")) {
                                            searching.setVisibility(View.GONE);
                                            confirm.setVisibility(View.VISIBLE);
                                            selectType.setVisibility(View.VISIBLE);
                                            // Show Bottom Sheet
                                            mainSheet.setVisibility(View.VISIBLE);
                                            showBottomSheet();
                                        }
                                    }
                                };
                                timer.start();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                searching.setVisibility(View.GONE);
                                confirm.setVisibility(View.VISIBLE);
                                selectType.setVisibility(View.VISIBLE);
                                helpers.showError(Dashboard.this, "Booking Error", "Something Went Wrong");
                            }
                        });

                break;
            }
            case R.id.cancelDriverBooking: {
                Log.e("booking", "Click Captured");
                activeBooking.setStatus("Cancelled");
                bookingReference.child(activeBooking.getId()).setValue(activeBooking);
                Booking temp = activeBooking;
                helpers.sendNotification(temp, user, activeDriver, "You cancelled your booking with " + activeDriver.getName(), "Your booking has been cancelled by " + user.getName());
                break;
            }
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
                helpers.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + ex.getMessage());
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                helpers.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + ex.getMessage());
            }
            if (!gps_enabled && !network_enabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Dashboard.this);
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
            locationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                Location location = task.getResult();
                                if (location != null) {
                                    if (marker != null)
                                        marker.remove();
                                    LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                                    marker = googleMap.addMarker(new MarkerOptions().position(me).title("You are here").
                                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 11));
                                    Geocoder geocoder = new Geocoder(Dashboard.this);
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(me.latitude, me.longitude, 1);
                                        if (addresses != null && addresses.size() > 0) {
                                            Address address = addresses.get(0);
                                            String strAddress = "";
                                            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                                strAddress = strAddress + address.getAddressLine(i) + " ";
                                            }
                                            locationAddress.setText(strAddress);
                                            user.setLat(me.latitude);
                                            user.setLng(me.longitude);
                                            session.setSession(user);
                                            helpers.updateUserLocation(user);
                                        }
                                    } catch (Exception e) {
                                        helpers.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helpers.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            helpers.showError(Dashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case (R.id.nav_Booking): {
                Intent it = new Intent(Dashboard.this, BookingActivity.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_Notification): {
                Intent it = new Intent(Dashboard.this, NotificationActivity.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_profile): {
                Intent it = new Intent(Dashboard.this, EditUserProfile.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_Logout): {
                auth.signOut();
                session.destroySession();
                Intent it = new Intent(Dashboard.this, LoginActivity.class);
                startActivity(it);
                finish();
                break;
            }

        }
        drawer.closeDrawer(GravityCompat.START);
        return false;

    }

    private void loadVendors() {
        vendorsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Dashboard", "Vendors Listener Captured");
                renters.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    User u = d.getValue(User.class);
                    if (u != null && (u.getType().equals("Renter") || u.getType().equals("Driver"))) {
                        LatLng user_location = new LatLng(u.getLat(), u.getLng());
                        MarkerOptions markerOptions = new MarkerOptions().position(user_location).title(u.getType());
                        switch (u.getType()) {
                            case "Renter":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                                break;
                            case "Driver":
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.driver));
                                break;
                        }
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.showInfoWindow();
                        marker.setTag(u);
                        if (u.getType().equals("Renter") && u.getRenter() != null && !u.getRenter().isBooked()) {
                            renters.add(u);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        vendorReference.addValueEventListener(vendorsListener);
    }

    private void listenToBookingChanges() {
        bookingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Dashboard", "Bookings Listener");
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Log.e("Dashboard", "DataSnapShot Each Child: " + d.toString());
                    Booking booking = d.getValue(Booking.class);
                    if (booking != null) {
                        Log.e("Dashboard", "Booking Status: " + booking.getStatus());
                        if (booking.getStatus().equals("In Progress") || booking.getStatus().equals("Started")) {
                            Log.e("Dashboard", "Booking Status In Progress Found");
                            activeBooking = booking;
                            if (bookingsListener != null)
                                bookingReference.orderByChild("userId").equalTo(user.getPhoneNumber()).removeEventListener(bookingsListener);
                            if (bookingsListener != null)
                                bookingReference.removeEventListener(bookingsListener);
                            if (vendorsListener != null) {
                                vendorReference.removeEventListener(vendorsListener);
                            }
                            if (booking.getStatus().equals("Started")) {
                                cancelBooking.setVisibility(View.GONE);
                            }
                            Log.e("Dashboard", "Bookings Listener Removed");
                            googleMap.clear();
                            getDeviceLocation();
                            onInProgress();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        bookingReference.orderByChild("userId").equalTo(user.getPhoneNumber()).addValueEventListener(bookingsListener);
    }

    private void onInProgress() {
        if (timer != null)
            timer.cancel();

        showBottomSheet();

        searching.setVisibility(View.GONE);
        confirm.setVisibility(View.VISIBLE);
        selectType.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        sheetProgress.setVisibility(View.VISIBLE);

        bookingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Dashboard", "Booking Listener");
                Booking booking = dataSnapshot.getValue(Booking.class);
                Log.e("Dashboard", "Active Booking Updated");
                if (booking != null && activeBooking != null) {
                    Log.e("Dashboard", "Active Booking Status: " + booking.getStatus());
                    Log.e("Dashboard", "Active Booking Fare: " + booking.getFare());
                    switch (booking.getStatus()) {
                        case "Cancelled": {
                            onBookingCancelled();
                            break;
                        }
                        case "Completed": {
                            onBookingCompleted();
                            break;
                        }
                        case "Started": {
                            cancelBooking.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        bookingReference.child(activeBooking.getId()).addValueEventListener(bookingListener);

        driverListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Dashboard", "Driver Listener Captured");
                if (activeDriver == null) {
                    driverReference.removeEventListener(driverListener);
                    activeDriver = dataSnapshot.getValue(User.class);
                    if (activeDriver != null) {
                        // Fil bottom sheet here
                        driverName.setText(activeDriver.getName());
                        driverBookingDate.setText(activeBooking.getBookingTime());
                        driverBookingAddress.setText(activeBooking.getAddress());
                        driverContact.setText(activeDriver.getPhoneNumber());
                        driverEmail.setText(activeDriver.getEmail());
                        Log.e("Dashboard", "Driver Image: " + activeDriver.getImage());
                        if (activeDriver.getImage() != null && activeDriver.getImage().length() > 1) {
                            Glide.with(getApplicationContext()).load(activeDriver.getImage()).into(driverImage);
                        } else {
                            driverImage.setImageResource(R.drawable.userprofile);
                        }
                    }
                }
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        driverReference.child(activeBooking.getDriverId()).addValueEventListener(driverListener);
    }

    private void showBottomSheet() {
        sheetBehavior.setHideable(false);
        sheetBehavior.setSkipCollapsed(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void onBookingCancelled() {
        bookingReference.child(activeBooking.getId()).removeEventListener(bookingListener);
        removeListeners();
        helpers.showError(Dashboard.this, "ERROR", "Your booking has been cancelled.");
        helpers.sendNotification(Dashboard.this, "BOOKING", "Your booking has been cancelled.");
    }

    private void onBookingCompleted() {
        Log.e("Dashboard", "Active Driver: " + activeDriver.toString());
        Log.e("Dashboard", "Active Driver Id: " + activeDriver.getId());
        Log.e("Dashboard", "Active Driver Name: " + activeDriver.getName());
        String vendorId = activeDriver.getId();
        String bookingId = activeBooking.getId();
        bookingReference.child(activeBooking.getId()).removeEventListener(bookingListener);
        removeListeners();
        helpers.showSuccessNoClose(Dashboard.this, "Your Booking has been marked as completed.", "");
        helpers.sendNotification(Dashboard.this, "BOOKING", "Your Booking has been marked as completed.");

        ReviewDialog reviewDialog = new ReviewDialog(Dashboard.this, user.getId(), vendorId, bookingId);
        reviewDialog.setCancelable(false);
        reviewDialog.setCanceledOnTouchOutside(false);
        reviewDialog.show();
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
        removeAllDatabaseListeners();
    }

    private void removeAllDatabaseListeners() {
        if (bookingListener != null) {
            bookingReference.removeEventListener(bookingListener);
        }
        if (bookingsListener != null) {
            bookingReference.removeEventListener(bookingsListener);
        }
        if (vendorsListener != null) {
            vendorReference.removeEventListener(vendorsListener);
        }
        if (driverListener != null) {
            driverReference.removeEventListener(driverListener);
        }
    }

    private void removeListeners() {
        removeAllDatabaseListeners();
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        loadVendors();
        listenToBookingChanges();
        activeBooking = null;
    }
}


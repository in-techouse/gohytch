package lcwu.fyp.gohytch.activities;

import android.Manifest;
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
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.jeevandeshmukh.fancybottomsheetdialoglib.FancyBottomSheetDialog;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Booking;
import lcwu.fyp.gohytch.model.User;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

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
    private Button cancelBooking, completeBooking;
    private boolean isInProgress = false;
    private TextView profile_rating;
    private MaterialRatingBar ratingBar;
    private ValueEventListener vendorValueEventListener;

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
        completeBooking = findViewById(R.id.completeBooking);
        cancelBooking = findViewById(R.id.cancelBooking);
        bookingFare = findViewById(R.id.bookingFare);

        completeBooking.setOnClickListener(this);
        cancelBooking.setOnClickListener(this);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.nav_edit);
        if (user.getType().equals("Driver")) {
            item.setTitle("Edit Driver");
        } else {
            item.setTitle("Edit Renter");
        }
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
        profile_rating = header.findViewById(R.id.profile_rating);
        ratingBar = header.findViewById(R.id.ratingBar);

        profile_Name.setText(user.getName());
        profile_Email.setText(user.getEmail());
        profile_Phone.setText(user.getPhoneNumber());
        type.setText(user.getType());

        if (user.getType().equals("Renter")) {
            profile_rating.setText(String.format("%.2f", user.getRenter().getRating()));
            ratingBar.setRating((float) user.getRenter().getRating());
        } else if (user.getType().equals("Driver")) {
            profile_rating.setText(String.format("%.2f", user.getDriver().getRating()));
            ratingBar.setRating((float) user.getDriver().getRating());
        }

        if (user.getImage() != null && user.getImage().length() > 5) {
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
                    loadVendorDetail();
                }
            });
        } catch (Exception e) {
            Log.e("Maps", "Catch Block");
            helpers.showError(VendorDashboard.this, "Error", "Something went wrong.Try again");
        }
    }

    private void loadVendorDetail() {
        Log.e("VendorDashboard", "Load Vendor Called");
        vendorValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("VendorDashboard", "Load Vendor On Success");
                if (vendorValueEventListener != null)
                    userReference.child(user.getId()).removeEventListener(vendorValueEventListener);
                if (dataSnapshot.exists()) {
                    Log.e("VendorDashboard", "Load Vendor SnapShot Exists");
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        Log.e("VendorDashboard", "Load Vendor User is not null");
                        session.setSession(user);
                        if (user.getType().equals("Renter")) {
                            Log.e("VendorDashboard", "Load Vendor Renter rating set");
                            profile_rating.setText(String.format("%.2f", user.getRenter().getRating()));
                            ratingBar.setRating((float) user.getRenter().getRating());
                        } else if (user.getType().equals("Driver")) {
                            Log.e("VendorDashboard", "Load Vendor Driver rating set");
                            profile_rating.setText(String.format("%.2f", user.getDriver().getRating()));
                            ratingBar.setRating((float) user.getDriver().getRating());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("VendorDashboard", "Load Vendor On Failed");
                if (vendorValueEventListener != null)
                    userReference.child(user.getId()).removeEventListener(vendorValueEventListener);
            }
        };

        userReference.child(user.getId()).addValueEventListener(vendorValueEventListener);
    }

    public void enableLocation() {
        boolean flag = hasPermissions(VendorDashboard.this, PERMISSIONS);
        if (!flag) {
            ActivityCompat.requestPermissions(VendorDashboard.this, PERMISSIONS, 1);
        } else {
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

    private boolean hasPermissions(Context c, String... permission) {
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
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
            locationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
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
                                                strAddress = strAddress + address.getAddressLine(i) + " ";
                                            }
                                            locationAddress.setText(strAddress);
                                            user.setLat(me.latitude);
                                            user.setLng(me.longitude);
                                            session.setSession(user);
                                            helpers.updateUserLocation(user);
                                        }
                                    } catch (Exception e) {
                                        helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                                    }
                                }
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            helpers.showError(VendorDashboard.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
        }
    }

    private void listenToBookings() {
        bookingsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    final Booking booking = data.getValue(Booking.class);
                    if (booking != null && booking.getDriverId() != null) {
                        Log.e("VendorDashboard", "Booking Status: " + booking.getStatus());
                        if (user.getType().equals("Renter")) {
                            if (booking.getStatus().equals("In Progress") || booking.getStatus().equals("Started")) {
                                if (bookingsValueEventListener != null)
                                    bookingReference.orderByChild("type").equalTo(user.getType()).removeEventListener(bookingsValueEventListener);
                                if (bookingsValueEventListener != null)
                                    bookingReference.removeEventListener(bookingsValueEventListener);
                                Log.e("VendorDashboard", "Booking status in Progress found");
                                activeBooking = booking;
                                if (booking.getStatus().equals("Started")) {
                                    isInProgress = true;
                                    completeBooking.setText("MARK COMPLETE");
                                    cancelBooking.setVisibility(View.GONE);
                                } else {
                                    isInProgress = false;
                                    completeBooking.setText("START RIDE");
                                }
                                sheetBehavior.setHideable(false);
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                loadCustomerDetails();
                            } else if (booking.getStatus().equals("New") && booking.getDriverId().equals(user.getId())) {
                                showBookingDialog(booking);
                            }
                        } else {
                            if (booking.getStatus().equals("In Progress") || booking.getStatus().equals("Started")) {
                                if (bookingsValueEventListener != null)
                                    bookingReference.orderByChild("type").equalTo(user.getType()).removeEventListener(bookingsValueEventListener);
                                if (bookingsValueEventListener != null)
                                    bookingReference.removeEventListener(bookingsValueEventListener);
                                Log.e("VendorDashboard", "Booking status in Progress found");
                                activeBooking = booking;
                                if (booking.getStatus().equals("Started")) {
                                    isInProgress = true;
                                    completeBooking.setText("MARK COMPLETE");
                                    cancelBooking.setVisibility(View.GONE);
                                    incrementInFare();
                                } else {
                                    isInProgress = false;
                                    completeBooking.setText("START RIDE");
                                }
                                sheetBehavior.setHideable(false);
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                loadCustomerDetails();
                            } else if (booking.getDriverId().length() < 1 && booking.getStatus().equals("New")) {
                                showBookingDialog(booking);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("VendorDashboard", "in onCancelled " + databaseError.toString());
            }
        };

        bookingReference.orderByChild("type").equalTo(user.getType()).addValueEventListener(bookingsValueEventListener);
    }

    private void showBookingDialog(final Booking booking) {
        helpers.sendNotification(VendorDashboard.this, "New Booking", "We have a new booking for you. It's time to get some revenue.");
        new FancyBottomSheetDialog.Builder(VendorDashboard.this)
                .setTitle("New Booking Found")
                .setMessage("We have a new booking for you. It's time to get some revenue.")
                .setBackgroundColor(Color.parseColor("#6EA5E2"))
                .setIcon(R.drawable.ic_action_error, true)
                .isCancellable(false)
                .OnNegativeClicked(new FancyBottomSheetDialog.FancyBottomSheetDialogListener() {
                    @Override
                    public void OnClick() {
                        helpers.cancelRequest(booking, user);
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
                .setPositiveBtnBackground(Color.parseColor("#6EA5E2"))
                .setNegativeBtnBackground(Color.parseColor("#F43636"))
                .build();
    }

    private void loadCustomerDetails() {
        Log.e("VendorDashboard", "Call Received to Load Customer");
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userValueEventListener != null)
                    userReference.child(activeBooking.getUserId()).removeEventListener(userValueEventListener);
                if (userValueEventListener != null)
                    userReference.removeEventListener(userValueEventListener);
                if (activeCustomer == null) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("VendorDashboard", "User Value Received, on Cancelled: " + databaseError.getMessage());
                sheetProgress.setVisibility(View.GONE);
                mainSheet.setVisibility(View.VISIBLE);
            }
        };
        userReference.child(activeBooking.getUserId()).addValueEventListener(userValueEventListener);
        listenToActiveBookingChange();
    }

    private void listenToActiveBookingChange() {
        bookingValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("VendorDashboard", "Booking Listener: " + dataSnapshot.toString());
                Booking booking = dataSnapshot.getValue(Booking.class);
                if (booking != null && activeBooking != null) {
                    Log.e("VendorDashboard", "Booking Listener, Fare: " + booking.getFare() + " Status: " + booking.getStatus());
                    switch (booking.getStatus()) {
                        case "Cancelled": {
                            Log.e("VendorDashboard", "Booking Cancelled");
                            activeBooking = booking;
                            if (bookingValueEventListener != null)
                                bookingReference.child(activeBooking.getId()).removeEventListener(bookingValueEventListener);
                            if (bookingValueEventListener != null)
                                bookingReference.removeEventListener(bookingValueEventListener);
                            showCancelledNotification();
                            break;
                        }
                        case "Completed": {
                            Log.e("VendorDashboard", "Booking Completed");
                            activeBooking = booking;
                            if (bookingValueEventListener != null)
                                bookingReference.child(activeBooking.getId()).removeEventListener(bookingValueEventListener);
                            if (bookingValueEventListener != null)
                                bookingReference.removeEventListener(bookingValueEventListener);
                            showCompletedNotification();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        bookingReference.child(activeBooking.getId()).addValueEventListener(bookingValueEventListener);
    }

    private void showCompletedNotification() {
        helpers.sendNotification(VendorDashboard.this, "Booking Completed", "Your booking has been Completed.");
        helpers.showSuccessNoClose(VendorDashboard.this, "Booking Completed", "Your booking with " + activeCustomer.getName() + " has been Completed.");
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        activeBooking = null;
        listenToBookings();
    }

    private void showCancelledNotification() {
        helpers.sendNotification(VendorDashboard.this, "Booking Cancelled", "Your booking has been cancelled.");
        helpers.showError(VendorDashboard.this, "Booking Cancelled", "Your booking with " + activeCustomer.getName() + " has been cancelled.");
        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        activeBooking = null;
        listenToBookings();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.completeBooking: {
                if (isInProgress) {
                    onBookingCompleted();
                } else {
                    isInProgress = true;
                    completeBooking.setText("MARK COMPLETE");
                    cancelBooking.setVisibility(View.GONE);
                    incrementInFare();
                    bookingReference.child(activeBooking.getId()).child("status").setValue("Started");
                    activeBooking.setStatus("Started");
                }
                break;
            }
            case R.id.cancelBooking: {
                onBookingCancelled();
                break;
            }
        }
    }

    private void incrementInFare() {
        Log.e("VendorDashboard", "Increment in Fare called");
        new CountDownTimer(12000, 12000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (activeBooking != null && activeBooking.getStatus().equals("Started")) {
                    String fare = bookingFare.getText().toString();
                    String[] fareArray = fare.split(" ");
                    int totalFare = Integer.parseInt(fareArray[0]);
                    Random rand = new Random();
                    int n = rand.nextInt(10);
                    n = n + 1;
                    totalFare = totalFare + n;
                    bookingReference.child(activeBooking.getId()).child("fare").setValue(totalFare);
                    activeBooking.setFare(totalFare);
                    bookingFare.setText(totalFare + " RS.");
                    incrementInFare();
                }
            }
        }.start();
    }

    private void onBookingCompleted() {
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        activeBooking.setStatus("Completed");
        final Booking temp = activeBooking;
        bookingReference.child(activeBooking.getId()).setValue(activeBooking)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cancelBooking.setVisibility(View.VISIBLE);
                        sheetBehavior.setHideable(true);
                        sheetProgress.setVisibility(View.GONE);
                        mainSheet.setVisibility(View.VISIBLE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        helpers.sendNotification(temp, activeCustomer, user, "Your booking has been completed by " + user.getName(), "You completed the booking of " + activeCustomer.getName());
                        helpers.showSuccessNoClose(VendorDashboard.this, "RIDE COMPLETE", "Total fare to be collected from " + activeCustomer.getName() + " is " + temp.getFare() + " RS.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sheetProgress.setVisibility(View.GONE);
                        mainSheet.setVisibility(View.VISIBLE);
                        helpers.showError(VendorDashboard.this, "ERROR", "Something went wrong.");
                    }
                });
    }

    private void onBookingCancelled() {
        sheetProgress.setVisibility(View.VISIBLE);
        mainSheet.setVisibility(View.GONE);
        activeBooking.setStatus("Cancelled");
        final Booking temp = activeBooking;
        bookingReference.child(activeBooking.getId()).setValue(activeBooking)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        helpers.sendNotification(temp, activeCustomer, user, "Your booking has been cancelled by " + user.getName(), "You cancelled the booking of " + activeCustomer.getName());
                        sheetBehavior.setHideable(true);
                        sheetProgress.setVisibility(View.GONE);
                        mainSheet.setVisibility(View.VISIBLE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sheetProgress.setVisibility(View.GONE);
                        mainSheet.setVisibility(View.VISIBLE);
                        helpers.showError(VendorDashboard.this, "ERROR", "Something went wrong.");
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case (R.id.nav_Booking): {
                Intent it = new Intent(VendorDashboard.this, BookingActivity.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_Notification): {
                Intent it = new Intent(VendorDashboard.this, NotificationActivity.class);
                startActivity(it);
                break;
            }
            case R.id.nav_reviews: {
                Intent it = new Intent(VendorDashboard.this, ReviewsActivity.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_profile): {
                Intent it = new Intent(VendorDashboard.this, EditUserProfile.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_edit): {
                Intent it;
                if (user.getType().equals("Driver")) {
                    it = new Intent(VendorDashboard.this, EditDriverActivity.class);
                } else {
                    it = new Intent(VendorDashboard.this, EditRenterActivity.class);
                }
                startActivity(it);
                break;
            }
            case (R.id.nav_Logout): {
                auth.signOut();
                session.destroySession();
                Intent it = new Intent(VendorDashboard.this, LoginActivity.class);
                startActivity(it);
                finish();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        if (bookingsValueEventListener != null) {
            bookingReference.removeEventListener(bookingsValueEventListener);
        }
        if (bookingValueEventListener != null) {
            bookingReference.removeEventListener(bookingValueEventListener);
        }
        if (userValueEventListener != null) {
            userReference.removeEventListener(userValueEventListener);
        }
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
}


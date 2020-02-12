package lcwu.fyp.gohytch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class BookingDetails extends AppCompatActivity implements View.OnClickListener {

    private Booking booking;
    private TextView UserName, user_address, travel, YourAddress, fare;
    private MapView map;
    private GoogleMap googleMap;
    private FusedLocationProviderClient locationProviderClient;
    private Helpers helpers;
    private Session session;
    private User user, customer;
    private CircleImageView userImage;
    private Button reject , accept;
    private LinearLayout progress, main;
    private DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private ValueEventListener listener;
    private boolean isListening = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        Intent it = getIntent();
        if(it == null)
        {
            Log.e("BookingDetail", "Intent is NULL");
            finish();
            return;
        }
        Bundle b = it.getExtras();
        if (b == null)
        {
            Log.e("BookingDetail", "Extra is NULL");
            finish();
            return;
        }

        booking = (Booking) b.getSerializable("Booking");
        if (booking == null)
        {
            Log.e("BookingDetail", "Booking is NULL");
            finish();
            return;
        }

        progress = findViewById(R.id.progress);
        main = findViewById(R.id.main);


        UserName = findViewById(R.id.userName);
        userImage = findViewById(R.id.userImage);
        user_address = findViewById(R.id.address);
        map = findViewById(R.id.map);
        travel = findViewById(R.id.Travel);
        YourAddress = findViewById(R.id.your_address);

        session = new Session(BookingDetails.this);
        user = session.getSession();
        helpers = new Helpers();
        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);
        fare = findViewById(R.id.fare);
        accept.setOnClickListener(this);
        reject.setOnClickListener(this);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(BookingDetails.this);
        map.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(BookingDetails.this);
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
                    LatLng defaultPosition = new LatLng(31.5204,74.3487) ;
                    CameraPosition cameraPosition =new CameraPosition.Builder().target(defaultPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    enableLocation();
                }
            });
        }
        catch (Exception e){
            helpers.showError(BookingDetails.this, "Map Error" , "Something Went Wrong!" );
        }

    }

    private void loadUserData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(booking.getUserId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    customer = dataSnapshot.getValue(User.class);
                    if (customer!=null){
                        main.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        UserName.setText(customer.getName());
                        if(customer.getImage() != null && user.getImage().length() > 0){
                            Glide.with(BookingDetails.this).load(customer.getImage()).into(userImage);
                        }
                    }
                    else{
                        UserName.setText("");
                        main.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    }
                }
                else{
                    UserName.setText("");
                    main.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                main.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });
    }
    private boolean askForPermission(){
        if (ActivityCompat.checkSelfPermission(BookingDetails.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(BookingDetails.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(BookingDetails.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return false;
        }
        return true;
    }

    public void enableLocation(){
        if(askForPermission()){
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    FusedLocationProviderClient current = LocationServices.getFusedLocationProviderClient(BookingDetails.this);
                    current.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>(){
                        public void onSuccess(Location location){
                            getDeviceLocation();
                        }
                    });
                    return true;
                }
            });
            getDeviceLocation();
        }
    }

    private void getDeviceLocation(){
        Log.e("Location", "Call received to get device location");
        try {
            LocationManager lm =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;
            try {
                gps_enabled =lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            catch (Exception ex){
                helpers.showError(BookingDetails.this, "Error" , "Something Went Wrong");
            }
            try {
                network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }catch (Exception ex){
                helpers.showError(BookingDetails.this, "Error" , "Something Went Wrong");

            }
            if (!gps_enabled&& !network_enabled){
                AlertDialog.Builder dialog = new AlertDialog.Builder(BookingDetails.this);
                dialog.setMessage("Oppsss.Your Location Service is off.\n Please turn on your Location and Try again Later");
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
                            googleMap.addMarker(new MarkerOptions().position(me).title("You're Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            LatLng customerlocation = new LatLng(booking.getLat(), booking.getLng());
                            googleMap.addMarker(new MarkerOptions().position(customerlocation).title("Customer Is Here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 11));
                            Geocoder geocoder = new Geocoder(BookingDetails.this);
                            List<Address> addresses = null;
                            try {
                                // Get Provider Current Address
                                addresses = geocoder.getFromLocation(me.latitude, me.longitude, 1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    String strAddress = "";
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        strAddress = strAddress + " " + address.getAddressLine(i);
                                    }
                                    YourAddress.setText(strAddress);
                                }
                                loadUserData();
                                user_address.setText(booking.getAddress());
                                fare.setText(booking.getFare()+"+ Rs");

                                double distance = helpers.distance(me.latitude,me.longitude, customerlocation.latitude,customerlocation.longitude);
                                travel.setText(distance+" KM" );

                            } catch (Exception exception) {
                                helpers.showError(BookingDetails.this  , "Error" , "Something Went Wrong");
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    helpers.showError(BookingDetails.this  , "Error" , "Something Went Wrong");
                }
            });
        }
        catch (Exception e){
            helpers.showError(BookingDetails.this  , "Error" , "Something Went Wrong");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==10){
            if (grantResults.length> 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                enableLocation();
            }
        }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.accept : {
                main.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);

                listener = bookingReference.child(booking.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookingReference.removeEventListener(listener);
                        Booking tempBooking = dataSnapshot.getValue(Booking.class);
                        if(!isListening){
                            return;
                        }
                        isListening = false;
                        if(tempBooking != null){
                            if((tempBooking.getDriverId() == null || tempBooking.getDriverId().length() < 1) && tempBooking.getStatus().equals("New")){
                                Log.e("BookingDetail", "Temp Booking Found with empty driver and New Status");
                                acceptBooking(tempBooking);
                            }
                            else{
                                main.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);
                                helpers.showErrorWithActivityClose(BookingDetails.this, "SORRY", "The booking has been accepted by another driver.");
                            }
                        }
                        else{
                            main.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                            helpers.showError(BookingDetails.this, "ERROR", "Something went wrong. Please try again later");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        bookingReference.removeEventListener(listener);
                        main.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        helpers.showError(BookingDetails.this, "ERROR", "Something went wrong. Please try again later");
                    }
                });
                break;
            }
            case R.id.reject : {
                finish();
                break;
            }
        }
    }

    private void acceptBooking(final Booking tempBooking){
        tempBooking.setStatus("In Progress");
        tempBooking.setDriverId(user.getPhoneNumber());
        bookingReference.child(tempBooking.getId()).setValue(tempBooking)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("booking" , "inOnSuccess");
                        sendNotification(tempBooking);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("booking" , "Failed");
                        main.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                        helpers.showError(BookingDetails.this, "ERROR", "Something went wrong. Please try again later");
                    }
                });
    }

    private void sendNotification(Booking b){
        Notification notification=new Notification();
        DatabaseReference notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String id= notificationReference.push().getKey();
        notification.setId(id);
        notification.setBookingId(b.getId());
        notification.setUserId(customer.getPhoneNumber());
        notification.setDriverId(user.getPhoneNumber());
        notification.setRead(false);
        Date d = new Date();
        String date = new SimpleDateFormat("EEE DD, MMM, yyyy HH:mm").format(d);
        notification.setDate(date);
        notification.setNotification("Your booking has been accepted by " + user.getName());
        notificationReference.child(notification.getId()).setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                main.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                main.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                finish();
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

    @Override
    protected void onDestroy () {
        super.onDestroy();
        map.onDestroy();

    }
}

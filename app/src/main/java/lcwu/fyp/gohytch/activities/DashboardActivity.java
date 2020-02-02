package lcwu.fyp.gohytch.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

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

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.dialog.UserDialog;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class DashboardActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private MapView map;
    private Helpers helpers;
    private User user;
    private GoogleMap googleMap;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Session session;
    private FusedLocationProviderClient locationProviderClient;
    private Marker marker;
    private Icon icon;
    private CircleImageView Profile_Image;
    private TextView profile_Email;
    private TextView profile_Name;
    private TextView profile_Phone;
    private TextView locationAddress;
    private FirebaseAuth auth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        helpers = new Helpers();
        session = new Session(DashboardActivity.this);
        user = session.getSession();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(DashboardActivity.this);
        profile_Phone = header.findViewById(R.id.profile_Phone);
        profile_Name = header.findViewById(R.id.profile_Name);
        profile_Email = header.findViewById(R.id.profile_Email);
        Profile_Image = header.findViewById(R.id.profile_image);
        locationAddress = findViewById(R.id.locationAddress);



        profile_Name.setText(user.getName());
        profile_Email.setText(user.getEmail());
        profile_Phone.setText(user.getPhoneNumber());
        map = findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(DashboardActivity.this);
            Log.e("Maps", "Try Block");
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gM) {
                    Log.e("Maps", "Call back received");
                    googleMap = gM;
                    LatLng defaultPosition = new LatLng(31.5204, 74.3487);
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(defaultPosition).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    enableLocatin();
                }
            });
        } catch (Exception e) {
            Log.e("Maps", "Catch Block");
            helpers.showError(DashboardActivity.this, "Error", "Something went wrong.Try again");
        }
        if (user.getType() == null || user.getType().length() < 1) {
            UserDialog userDialog = new UserDialog(DashboardActivity.this);
            userDialog.setCanceledOnTouchOutside(false);
            userDialog.setCancelable(false);
            userDialog.show();
        }

    }


    private boolean askForPermission() {
        if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);


            return false;
        }
        return true;
    }

    public void enableLocatin() {
        if (askForPermission()) {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    FusedLocationProviderClient current = LocationServices.getFusedLocationProviderClient(DashboardActivity.this);
                    current.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            getDeviceLocation();
                        }
                    });
                    return false;
                }
            });
            getDeviceLocation();
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
                helpers.showError(DashboardActivity.this, "ERROR!", "Something went wrong.\nPlease try again later. " + ex.getMessage());
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
                helpers.showError(DashboardActivity.this, "ERROR!", "Something went wrong.\nPlease try again later. " + ex.getMessage());
            }
            if (!gps_enabled && !network_enabled) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DashboardActivity.this);
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
                            marker = googleMap.addMarker(new MarkerOptions().position(me).title("You are here").
                                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 11));
                            Geocoder geocoder = new Geocoder(DashboardActivity.this);
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
                                helpers.showError(DashboardActivity.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    helpers.showError(DashboardActivity.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
                }
            });
        }catch (Exception e){
            helpers.showError(DashboardActivity.this, "ERROR!", "Something went wrong.\nPlease try again later. " + e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        switch (id) {
            case (R.id.nav_Booking): {
                Intent it=new Intent(DashboardActivity.this, CreateDriver.class);
               // Intent it=new Intent(DashboardActivity.this, BookingActivity.class);
                startActivity(it);
                break;
            }
            case (R.id.nav_Notification): {
            //      Intent it=new Intent(DashboardActivity.this,NotificationActivity.class);
             Intent it = new Intent(DashboardActivity.this , VendorDashboard.class);
                startActivity(it);

                break;
            }
            case (R.id.nav_Logout): {
                auth.signOut();
                session.destroySession();
                Intent it=new Intent(DashboardActivity.this,LoginActivity.class);
                startActivity(it);
                finish();

                break;
            }

        }
        drawer.closeDrawer(GravityCompat.START);
        return false;

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


package lcwu.fyp.gohytch.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Renter;
import lcwu.fyp.gohytch.model.User;

public class CreateRenter extends AppCompatActivity implements View.OnClickListener {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private Button btnSave;
    private ProgressBar saveProgress;
    private Helpers helpers;
    private User user;
    private Session session;
    private SliderAdapter adapter;
    private List<Uri> carImages;
    private Renter renter;
    private EditText licenseNumber, carModel, carRegistrationNumber, sittingCapacity, perHourRent;
    private Spinner carCompany;
    private String strLicenseNumber, strCarCompany, strCarModel, strCarRegistrationNumber, strSittingCapacity, strPerHourRent = "";
    private SliderView sliderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);


        FloatingActionButton nav_gallery = findViewById(R.id.nav_gallery);
        nav_gallery.setOnClickListener(this);
        carImages = new ArrayList<>();

        sliderView = findViewById(R.id.imageSlider);
        licenseNumber = findViewById(R.id.licenseNumber);
        carCompany = findViewById(R.id.carCompany);
        carModel = findViewById(R.id.carModel);
        carRegistrationNumber = findViewById(R.id.carRegistrationNumber);
        sittingCapacity = findViewById(R.id.sittingCapacity);
        btnSave = findViewById(R.id.btnSave);
        perHourRent = findViewById(R.id.perHourRent);
        saveProgress = findViewById(R.id.saveProgress);

        btnSave.setOnClickListener(this);

        adapter = new SliderAdapter(CreateRenter.this);

        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();
        session = new Session(CreateRenter.this);
        user = session.getSession();
        renter = new Renter();
        helpers = new Helpers();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(CreateRenter.this, Dashboard.class);
        startActivity(it);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSave: {
                Log.e("Renter", "Button Clicked");
                boolean isConn = helpers.isConnected(CreateRenter.this);
                if (!isConn) {
                    helpers.showError(CreateRenter.this, "ERROR", "No Internet Connection.Please check your Internet Connection");
                    return;
                }
                boolean flag = isValid();
                if (flag) {
                    saveProgress.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    renter.setLicenseNumber(strLicenseNumber);
                    renter.setCarCompany(strCarCompany);
                    renter.setCarModel(strCarModel);
                    renter.setCarRegistrationNumber(strCarRegistrationNumber);
                    renter.setSittingCapacity(strSittingCapacity);
                    renter.setPerHourRate(Integer.parseInt(strPerHourRent));
                    uploadImage(0);
                }
                break;

            }
            case R.id.nav_gallery: {
                boolean flag = hasPermissions(CreateRenter.this, PERMISSIONS);
                if (!flag) {
                    ActivityCompat.requestPermissions(CreateRenter.this, PERMISSIONS, 1);
                } else {
                    openGallery();
                }
                break;
            }
        }
    }

    private void uploadImage(final int count) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Renters").child(user.getPhoneNumber());
        Calendar calendar = Calendar.getInstance();
        storageReference.child(calendar.getTimeInMillis() + "").putFile(carImages.get(count))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.e("Renter", "in OnSuccess: " + uri.toString());
                                renter.getImages().add(uri.toString());
                                if (renter.getImages().size() == carImages.size()) {
                                    Log.e("Renter", "Car Image Size: " + carImages.size());
                                    Log.e("Renter", "Renter Car Image Size: " + renter.getImages().size());
                                    saveToDatabase();
                                } else {
                                    uploadImage(count + 1);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Renter", "DownloadUrl: " + e.getMessage());
                                btnSave.setVisibility(View.VISIBLE);
                                saveProgress.setVisibility(View.GONE);
                                helpers.showError(CreateRenter.this, "ERROR!", "Something went wrong.\nPlease check your connection");
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Renter", "Upload Image Url:" + e.getMessage());
                        btnSave.setVisibility(View.VISIBLE);
                        saveProgress.setVisibility(View.GONE);
                        helpers.showError(CreateRenter.this, "ERROR!", "Something went wrong.\nPlease check your connection");

                    }
                });
    }

    private void saveToDatabase() {
        Log.e("Renter", "Call received in save to database");
        user.setType("Renter");
        user.setRenter(renter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getPhoneNumber());
        databaseReference.setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        btnSave.setVisibility(View.VISIBLE);
                        saveProgress.setVisibility(View.GONE);
                        session.setSession(user);
                        Intent intent = new Intent(CreateRenter.this, VendorDashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        ActivityCompat.finishAffinity(CreateRenter.this);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Renter", "Save in database failed");
                        btnSave.setVisibility(View.VISIBLE);
                        saveProgress.setVisibility(View.GONE);
                        helpers.showError(CreateRenter.this, "ERROR!", "Something went wrong.\nPlease check your connection");
                    }

                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        ImagePicker.create(CreateRenter.this)
                .toolbarImageTitle("Tap to select")
                .multi()
                .limit(2)
                .showCamera(true)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            carImages.clear();
            sliderView.setSliderAdapter(null);
            List<Image> images = ImagePicker.getImages(data);
            List<Uri> uriList = new ArrayList<>();
            for (Image img : images) {
                Uri uri = Uri.fromFile(new File(img.getPath()));
                uriList.add(uri);
            }
            carImages = uriList;
            sliderView.setSliderAdapter(adapter);
            sliderView.setIndicatorAnimation(IndicatorAnimations.WORM);
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
            sliderView.setIndicatorSelectedColor(Color.WHITE);
            sliderView.setIndicatorUnselectedColor(Color.GRAY);
            sliderView.setScrollTimeInSec(4);
            sliderView.startAutoCycle();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean hasPermissions(Context c, String... permission) {
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private boolean isValid() {
        boolean flag = true;
        int count = carImages.size();
        strLicenseNumber = licenseNumber.getText().toString();
        strCarCompany = carCompany.getSelectedItem().toString();
        strCarModel = carModel.getText().toString();
        strCarRegistrationNumber = carRegistrationNumber.getText().toString();
        strSittingCapacity = sittingCapacity.getText().toString();
        strPerHourRent = perHourRent.getText().toString();

        Log.e("Renter", "LicnseNumber:" + strLicenseNumber);
        String error = "";
        if (count == 0) {
            error = error + "*Select at least one car image first.\n";
            flag = false;
        }
        if (strLicenseNumber.length() < 4) {
            licenseNumber.setError("Enter a valid License Number");
            flag = false;
        } else {
            licenseNumber.setError(null);
        }
        if (carCompany.getSelectedItemPosition() == 0) {
            flag = false;
            error = error + "*Select car company first.\n";
        }

        if (strCarModel.length() < 4) {
            carModel.setError("Enter valid Car Model");
            flag = false;
        } else {
            carModel.setError(null);
        }

        if (strCarRegistrationNumber.length() < 5) {
            carRegistrationNumber.setError("Enter valid car Registration Number");
            flag = false;
        } else {
            carRegistrationNumber.setError(null);
        }
        if (strSittingCapacity.length() < 1) {
            sittingCapacity.setError("Enter valid sitting capacity");
            flag = false;
        } else {
            sittingCapacity.setError(null);
        }

        if (strPerHourRent.length() < 1) {
            perHourRent.setError("Enter valid car per hour rent");
            flag = false;
        } else {
            perHourRent.setError(null);
        }

        if (error.length() > 0) {
            helpers.showError(CreateRenter.this, "ERROR", error);
        }
        return flag;
    }

    class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        SliderAdapter(Context context) {
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
            return new SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
            Glide.with(viewHolder.itemView)
                    .load(carImages.get(position))
                    .into(viewHolder.imageViewBackground);
        }

        @Override
        public int getCount() {
            return carImages.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
            View itemView;
            ImageView imageViewBackground;

            SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
                this.itemView = itemView;
            }
        }
    }
}

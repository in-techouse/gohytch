package lcwu.fyp.gohytch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Renter;
import lcwu.fyp.gohytch.model.User;

public class CreateRenter extends AppCompatActivity  implements View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate, BSImagePicker.OnMultiImageSelectedListener {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    Button btnSave;
//    EditText edtLicenseNumber, edtCar_Model, edtRegistrationNumber, edtsittingCapacity, edtCompany;
//    String strLicenseNumber,strCar_Model,strRegistrationNumber,strsittingCapacity,strCompany;
    ProgressBar saveProgress;
//    TextView chooseImage, ChooseCarImage;
    Helpers helpers;
    private User user;
    private Session session;
    private SliderAdapter adapter;
    private List<Uri> carImages;
    private Renter renter;
    private EditText licenseNumber, carModel, carRegistrationNumber, sittingCapacity;
    private Spinner carCompany;
    private String strLicenseNumber, strCarCompany, strCarModel, strCarRegistrationNumber, strSittingCapacity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renter);

        FloatingActionButton nav_gallery = findViewById(R.id.nav_gallery);
        nav_gallery.setOnClickListener(this);
        carImages = new ArrayList<>();

        SliderView sliderView = findViewById(R.id.imageSlider);
        licenseNumber = findViewById(R.id.licenseNumber);
        carCompany = findViewById(R.id.carCompany);
        carModel = findViewById(R.id.carModel);
        carRegistrationNumber = findViewById(R.id.carRegistrationNumber);
        sittingCapacity = findViewById(R.id.sittingCapacity);
        btnSave = findViewById(R.id.btnSave);
        saveProgress = findViewById(R.id.saveProgress);

        btnSave.setOnClickListener(this);

        adapter = new SliderAdapter(CreateRenter.this);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        session = new Session(CreateRenter.this);
        user = session.getSession();
        renter = new Renter();
        helpers = new Helpers();
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
                if (flag){
                    saveProgress.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    renter.setLicenseNumber(strLicenseNumber);
                    renter.setCarCompany(strCarCompany);
                    renter.setCarModel(strCarModel);
                    renter.setCarRegistrationNumber(strCarRegistrationNumber);
                    renter.setSittingCapacity(strSittingCapacity);
                    uploadImage(0);
                }
                break;

            }
            case R.id.nav_gallery: {
                boolean flag = hasPermissions(CreateRenter.this, PERMISSIONS);
                if(!flag){
                    ActivityCompat.requestPermissions(CreateRenter.this, PERMISSIONS, 1);
                }
                else{
                    openGallery();
                }
                break;
            }
        }
    }

    private void uploadImage(final int count){
        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Renters").child(user.getPhoneNumber());
        Calendar calendar=Calendar.getInstance();
        storageReference.child(calendar.getTimeInMillis()+"").putFile(carImages.get(count)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("Renter","in OnSuccess: " + uri.toString());
                        renter.getImages().add(uri.toString());
                        if(renter.getImages().size() == carImages.size()){
                            Log.e("Renter", "Car Image Size: " + carImages.size());
                            Log.e("Renter", "Renter Car Image Size: " + renter.getImages().size());
                            saveToDatabase();
                        }
                        else{
                            uploadImage(count+1);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Renter","DownloadUrl: " + e.getMessage());
                        btnSave.setVisibility(View.VISIBLE);
                        saveProgress.setVisibility(View.GONE);
                        helpers.showError(CreateRenter.this,"ERROR!","Something went wrong.\nPlease check your connection");
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Renter", "Upload Image Url:" + e.getMessage());
                btnSave.setVisibility(View.VISIBLE);
                saveProgress.setVisibility(View.GONE);
                helpers.showError(CreateRenter.this, "ERROR!", "Something went wrong.\nPlease check your connection");

            }
        });
    }
    private void saveToDatabase(){
        Log.e("Renter","Call received in save to database");
        user.setType("Renter");
        user.setRenter(renter);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getPhoneNumber());
        databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                btnSave.setVisibility(View.VISIBLE);
                saveProgress.setVisibility(View.GONE);
                session.setSession(user);
                Intent intent = new Intent(CreateRenter.this, VendorDashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Renter","Save in database failed");
                btnSave.setVisibility(View.VISIBLE);
                saveProgress.setVisibility(View.GONE);
                helpers.showError(CreateRenter.this,"ERROR!","Something went wrong.\nPlease check your connection");
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
        BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("lcwu.fyp.gohytch.fileprovider")
                .isMultiSelect() //Set this if you want to use multi selection mode.
                .setMinimumMultiSelectCount(1) //Default: 1.
                .setMaximumMultiSelectCount(2) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                .setMultiSelectTextColor(R.color.primary_text) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                .disableOverSelectionMessage() //You can also decide not to show this over select message.
                .build();
        multiSelectionPicker.show(getSupportFragmentManager(), "picker");
    }


    private boolean hasPermissions(Context c, String... permission){
        for(String p : permission){
            if(ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED){
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
        Log.e("Renter","LicnseNumber:" + strLicenseNumber);
        String error = "";
        if(count == 0){
            error = error + "*Select at least one car image first.";
            flag = false;
        }
        if (strLicenseNumber.length()<4){
            licenseNumber.setError("Enter a valid License Number");
            flag = false;
        }else{
            licenseNumber.setError(null);
        }
        if(carCompany.getSelectedItemPosition() == 0){
            flag = false;
            error = error + "*Select car company first.";
        }
        
        if (strCarModel.length()<4){
            carModel.setError("Enter a valid Car Model");
            flag=false;
        }else{
            carModel.setError(null);
        }
        
        if (strCarRegistrationNumber.length() < 5){
            carRegistrationNumber.setError("Enter a valid car Registration Number");
            flag=false;
        }else{
            carRegistrationNumber.setError(null);
        }
        if (strSittingCapacity.length() < 1){
            sittingCapacity.setError("Enter a valid sittingCapacity");
            flag=false;
        }else{
            sittingCapacity.setError(null);
        }
        if(error.length() > 0){
            helpers.showError(CreateRenter.this, "ERROR", error);
        }
        return flag;
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        Log.e("CreateRenter", "URI: " + uri);
    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
//        Glide.with(CreateRenter.this).load(imageUri).into(UserImage);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        carImages = uriList;
        adapter.notifyDataSetChanged();

    }

    public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
        private Context context;

        public SliderAdapter(Context context) {
            this.context = context;
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
            //slider view count could be dynamic siz
            return carImages.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
            View itemView;
            ImageView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
                this.itemView = itemView;
            }
        }
    }
}

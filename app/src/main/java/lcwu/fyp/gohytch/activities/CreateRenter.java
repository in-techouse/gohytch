package lcwu.fyp.gohytch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Car;
import lcwu.fyp.gohytch.model.Renter;
import lcwu.fyp.gohytch.model.User;

public class CreateRenter extends AppCompatActivity  implements View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate, BSImagePicker.OnMultiImageSelectedListener {
    Button btnSave;
    EditText edtLicenseNumber, edtCar_Model, edtRegistrationNumber, edtsittingCapacity, edtCompany;
    String strLicenseNumber,strCar_Model,strRegistrationNumber,strsittingCapacity,strCompany;
    ProgressBar SaveProgress;
    TextView chooseImage, ChooseCarImage;
    Helpers helpers;
    private User user;
    private Session session;
    private CircleImageView UserImage;
    private SliderAdapter adapter;
    private List<Uri> CarImages;
    private Renter renter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renter);
        CarImages=new ArrayList<>();

        btnSave = findViewById(R.id.btnSave);
        edtLicenseNumber = findViewById(R.id.LicenseNumber);
        edtCar_Model = findViewById(R.id.Car_Model);
        edtRegistrationNumber = findViewById(R.id.RegistrationNumber);
        edtsittingCapacity = findViewById(R.id.sittingCapacity);
        edtCompany = findViewById(R.id.Company);
        chooseImage = findViewById(R.id.ChooseImage);
        ChooseCarImage = findViewById(R.id.ChooseCarImage);
        UserImage = findViewById(R.id.UserImage);
        chooseImage.setOnClickListener(this);
        ChooseCarImage.setOnClickListener(this);


        SaveProgress = findViewById(R.id.SaveProgress);

        btnSave.setOnClickListener(this);
        helpers = new Helpers();
        SliderView sliderView = findViewById(R.id.imageSlider);

         adapter = new SliderAdapter(CreateRenter.this);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        session=new Session(CreateRenter.this);
        user=session.getSession();
        renter = new Renter();
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
                    SaveProgress.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    UploadImage(CarImages.get(0));

                }
                break;

            }
            case R.id.ChooseImage:{
                if (askForPermission()){
                    OpenGallery();
                }
                break;
            }
            case R.id.ChooseCarImage:{
                if (askForPermission()){
                    OpenGalleryForCar();
                }
                break;
            }
        }
    }
    private void UploadImage(Uri imagePath ){
        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Renters").child(user.getPhoneNumber());
        Calendar calendar=Calendar.getInstance();
        storageReference.child(calendar.getTimeInMillis()+"").putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("Profile","in OnSuccess"+uri.toString());
                        renter.setImage1(uri.toString());
                        saveToDatabase();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Profile","DownloadUrl:"+e.getMessage());
                        btnSave.setVisibility(View.GONE);
                        SaveProgress.setVisibility(View.VISIBLE);
                        helpers.showError(CreateRenter.this,"ERROR!","Something went wrong.\nPlease check your connection");
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Profile", "Upload Image Url:" + e.getMessage());
                btnSave.setVisibility(View.GONE);
                SaveProgress.setVisibility(View.VISIBLE);
                helpers.showError(CreateRenter.this, "ERROR!", "Something went wrong.\nPlease check your connection");

            }
        });
    }
private void saveToDatabase(){
        btnSave.setVisibility(View.GONE);
        SaveProgress.setVisibility(View.VISIBLE);
        renter.setLicenseNumber(strLicenseNumber);
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Renters").child(user.getPhoneNumber());
    databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            btnSave.setVisibility(View.GONE);
            SaveProgress.setVisibility(View.VISIBLE);
            helpers.showError(CreateRenter.this,"ERROR!","Something went wrong.\nPlease check your connection");
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.e("Profile","DownloadUrl:"+e.getMessage());
            btnSave.setVisibility(View.GONE);
            SaveProgress.setVisibility(View.VISIBLE);
            helpers.showError(CreateRenter.this,"ERROR!","Something went wrong.\nPlease check your connection");
        }

    });
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==10){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            OpenGallery();
            }
        }
    }
    private void OpenGallery(){
        BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("lcwu.fyp.gohytch.fileprovider")
                .setMaximumDisplayingImages(24) //Default: Integer.MAX_VALUE. Don't worry about performance :)
                .setSpanCount(3) //Default: 3. This is the number of columns
                .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
                .hideCameraTile() //Default: show. Set this if you don't want user to take photo.
                .hideGalleryTile() //Default: show. Set this if you don't want to further let user select from a gallery app. In such case, I suggest you to set maximum displaying images to Integer.MAX_VALUE.
                .setTag("A request ID") //Default: null. Set this if you need to identify which picker is calling back your fragment / activity.
                .build();
        singleSelectionPicker.show(getSupportFragmentManager(), "picker");
    }

    private void OpenGalleryForCar(){
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


    private boolean askForPermission() {
            if (ActivityCompat.checkSelfPermission(CreateRenter.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(CreateRenter.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateRenter.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                  return false;
            }
            return true;
        }

        private boolean isValid() {
        boolean flag=true;
        strLicenseNumber=edtLicenseNumber.getText().toString();
        strCar_Model=edtCar_Model.getText().toString();
        strRegistrationNumber=edtRegistrationNumber.getText().toString();
        strsittingCapacity=edtsittingCapacity.getText().toString();
        strCompany=edtCompany.getText().toString();
        Log.e("Renter","LicnseNumber:" + strLicenseNumber);
        if (strLicenseNumber.length()<4){
            edtLicenseNumber.setError("Enter a valid License Number");
            flag=false;
        }else{
            edtLicenseNumber.setError(null);
        }


        if (strCar_Model.length()<4){
            edtCar_Model.setError("Enter a valid Car Model");
            flag=false;
        }else{
            edtCar_Model.setError(null);
        }


        if (strRegistrationNumber.length()<5){
            edtRegistrationNumber.setError("Enter a valid RegistrationNumber");
            flag=false;
        }else{
            edtRegistrationNumber.setError(null);
        }
        if (strsittingCapacity.length()<4){
            edtsittingCapacity.setError("Enter a valid sittingCapacity");
            flag=false;
        }else{
            edtsittingCapacity.setError(null);
        }
        if (strCompany.length()<5){
            edtCompany.setError("Enter a valid Company Name");
            flag=false;
        }else{
            edtCompany.setError(null);
        }

        return flag;
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        Log.e("CreateRenter", "URI: " + uri);
    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(CreateRenter.this).load(imageUri).into(UserImage);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        CarImages=uriList;
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
                    .load(CarImages.get(position))
                    .into(viewHolder.imageViewBackground);
        }

        @Override
        public int getCount() {
            //slider view count could be dynamic siz
            return CarImages.size();
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

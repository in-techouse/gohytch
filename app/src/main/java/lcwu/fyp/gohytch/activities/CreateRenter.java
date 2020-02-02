package lcwu.fyp.gohytch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class CreateRenter extends AppCompatActivity  implements View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {
    Button btnSave;
    EditText edtLicenseNumber, edtCar_Model, edtRegistrationNumber, edtsittingCapacity, edtCompany;
    String strLicenseNumber,strCar_Model,strRegistrationNumber,strsittingCapacity,strCompany;
    ProgressBar SaveProgress;
    TextView chooseImage;
    Helpers helpers;
    private CircleImageView UserImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renter);

        btnSave = findViewById(R.id.btnSave);
        edtLicenseNumber = findViewById(R.id.LicenseNumber);
        edtCar_Model = findViewById(R.id.Car_Model);
        edtRegistrationNumber = findViewById(R.id.RegistrationNumber);
        edtsittingCapacity = findViewById(R.id.sittingCapacity);
        edtCompany = findViewById(R.id.Company);
        chooseImage = findViewById(R.id.ChooseImage);
        UserImage = findViewById(R.id.UserImage);
        chooseImage.setOnClickListener(this);



        SaveProgress = findViewById(R.id.SaveProgress);

        btnSave.setOnClickListener(this);
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
                    SaveProgress.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    final User u=new User();
                    final Session session=new Session(CreateRenter.this);
                }
                break;

            }
            case R.id.ChooseImage:{
                if (askForPermission()){
                    OpenGallery();

                }
                break;
            }

            }


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
}

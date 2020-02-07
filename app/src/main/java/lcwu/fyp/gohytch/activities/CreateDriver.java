package lcwu.fyp.gohytch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
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
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class CreateDriver extends AppCompatActivity implements View.OnClickListener,BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {
    Button btnSave;
    EditText edtLicenseNumber, edtExpertise;
    TextView ChooseImage;
    String strLicenseNumber;
    ProgressBar SaveProgress;
    Helpers helpers;
    private CircleImageView UserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_driver);


        btnSave = findViewById(R.id.btnSave);
        edtLicenseNumber = findViewById(R.id.LicenseNumber);
        edtExpertise = findViewById(R.id.Expertise);
        SaveProgress = findViewById(R.id.SaveProgress);
        btnSave.setOnClickListener(this);
        helpers = new Helpers();

        ChooseImage = findViewById(R.id.ChooseImage);
        UserImage = findViewById(R.id.UserImage);

        ChooseImage.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSave: {
                Log.e("Driver", "Button Clicked");
                boolean isConn = helpers.isConnected(CreateDriver.this);
                if (!isConn) {
                    helpers.showError(CreateDriver.this, "Error", "No Internet Connection.Please Check Your Connection and try again later");
                    return;
                }
                boolean flag = isValid();
                if (flag) {
                    btnSave.setVisibility(View.GONE);
                    final User u = new User();
                    final Session session = new Session(CreateDriver.this);
                }
                break;

            }
            case R.id.ChooseImage: {
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
        if (ActivityCompat.checkSelfPermission(CreateDriver.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateDriver.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateDriver.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
                    return false;
        }
        return true;
    }

    private boolean isValid() {
        boolean flag=true;
        strLicenseNumber=edtLicenseNumber.getText().toString();
        Log.e("Renter","LicnseNumber:" + strLicenseNumber);
        if (strLicenseNumber.length()<4){
            edtLicenseNumber.setError("Enter a valid License Number");
            flag=false;
        }else{
            edtLicenseNumber.setError(null);
        }
        return flag;
    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(CreateDriver.this).load(imageUri).into(UserImage);
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        Log.e("CreateDriver","URI:"+uri);
    }
}

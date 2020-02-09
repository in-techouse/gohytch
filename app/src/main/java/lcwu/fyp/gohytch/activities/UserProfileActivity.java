package lcwu.fyp.gohytch.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.util.Calendar;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    Button btnSave;
    EditText edtName,edtEmail;
    String strPhonenumber,strName,strEmail;
    ProgressBar SaveProgress;
    Helpers helpers;
    ImageView image;
    boolean isImage = false;
    Uri imageUri;
    User user;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent it = getIntent();
        if(it == null){
            finish();
            return;
        }
        strPhonenumber = it.getStringExtra("Phone");
        if (strPhonenumber == null)
        {
            finish();
            return;
        }

        helpers = new Helpers();
        session = new Session(UserProfileActivity.this);
        user = new User();

        FloatingActionButton fab = findViewById(R.id.nav_gallery);
        fab.setOnClickListener(this);
        btnSave=findViewById(R.id.btnSave);
        edtName=findViewById(R.id.Name);
        edtEmail=findViewById(R.id.Email);
        SaveProgress=findViewById(R.id.SaveProgress);
        btnSave.forceLayout();

        btnSave.setOnClickListener(this);

        image = findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        AppBarLayout app_bar = findViewById(R.id.app_bar);
        app_bar.setExpanded(true, true);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.btnSave: {
                Log.e("User", "Button Clicked");

                boolean isConn = helpers.isConnected(UserProfileActivity.this);
                if (!isConn) {
                    helpers.showError(UserProfileActivity.this, "ERROR!", "No Internet Connection. Please check your Internet Connection.");
                    return;
                }
                boolean flag=isValid();
                if(flag){
                    SaveProgress.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    user.setId(strPhonenumber);
                    user.setEmail(strEmail);
                    user.setName(strName);
                    user.setPhoneNumber(strPhonenumber);
                    user.setType("None");
                    Log.e("Login", "Id: " + user.getId());
                    Log.e("Login", "Email: " + user.getEmail());
                    Log.e("Login", "Name: " + user.getName());
                    Log.e("Login", "Phone Number: " + user.getPhoneNumber());
                    if(isImage){
                        uploadImage();
                    }
                    else{
                        user.setImage("");
                        saveUser();
                    }

                }
                break;
            }
            case R.id.nav_gallery:{
                boolean flag = hasPermissions(UserProfileActivity.this, PERMISSIONS);
                if(!flag){
                    ActivityCompat.requestPermissions(UserProfileActivity.this, PERMISSIONS, 1);
                }
                else{
                    openGallery();
                }
                break;
            }
        }
    }

    private void uploadImage(){
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(user.getId());
//        Uri selectedMediaUri = Uri.parse(imagePath.toString());
        Calendar calendar = Calendar.getInstance();
        storageReference.child(calendar.getTimeInMillis()+"").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        user.setImage(uri.toString());
                                        saveUser();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Login", "Upload Image Url: " + e.getMessage());
                                        SaveProgress.setVisibility(View.GONE);
                                        btnSave.setVisibility(View.VISIBLE);
                                        helpers.showError(UserProfileActivity.this, "ERROR!", "Something went wrong.\n Please try again later.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Login", "Upload Image Url: " + e.getMessage());
                        SaveProgress.setVisibility(View.GONE);
                        btnSave.setVisibility(View.VISIBLE);
                        helpers.showError(UserProfileActivity.this, "ERROR!", "Something went wrong.\n Please try again later.");   
                    }
                });
    }

    private void saveUser(){
        Log.e("Login", "Id: " + user.getId());
        Log.e("Login", "Email: " + user.getEmail());
        Log.e("Login", "Name: " + user.getName());
        Log.e("Login", "Phone Number: " + user.getPhoneNumber());
        Log.e("Login", "Image: " + user.getImage());
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(user.getId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SaveProgress.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                session.setSession(user);
                Intent it=new Intent(UserProfileActivity.this,DashboardActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SaveProgress.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                helpers.showError(UserProfileActivity.this, "ERROR", "Something went wrong.\nPlease try again later.");
            }
        });
    }


    private boolean hasPermissions(Context c, String... permission){
        for(String p : permission){
            if(ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public void openGallery(){
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

    private boolean isValid() {
        boolean flag = true;
        strName=edtName.getText().toString();
        strEmail = edtEmail.getText().toString();
        Log.e("User", "Email: " + strEmail);
        if (strName.length() < 3) {
            edtName.setError("Enter a valid name");
            flag=false;
        } else {
            edtName.setError(null);
        }

        if (strEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            Log.e("User", "If Email: " + strEmail);
            edtEmail.setError("Enter a valid email");
            flag=false;
        } else {
            Log.e("User", "Else Email: " + strEmail);
            edtEmail.setError(null);

        }
        return flag;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            openGallery();
        }
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        isImage = true;
        imageUri = uri;
        Glide.with(UserProfileActivity.this).load(imageUri).into(image);

    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {

    }
}
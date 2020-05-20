package lcwu.fyp.gohytch.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.asksira.bsimagepicker.BSImagePicker;
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
import android.view.MenuItem;
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

public class EditUserProfile extends AppCompatActivity implements View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {

    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private EditText profileName, profileEmail, profilePhoneNumber;
    private Button updateBtn;
    private String strPhonenumber, strName, strEmail;
    private ProgressBar SaveProgress;
    private Helpers helpers;
    private ImageView image;
    private boolean isImage = false;
    private Uri imageUri;
    private User user;
    private Session session;
    private DatabaseReference updateReference = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.profile_nav_gallery);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhoneNumber = findViewById(R.id.profilePhoneNumber);
        updateBtn = findViewById(R.id.btnUpdate);
        session = new Session(EditUserProfile.this);
        helpers = new Helpers();
        user = session.getSession();
        fab.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        SaveProgress = findViewById(R.id.profileSaveProgress);

        image = findViewById(R.id.profileImage);
        if (user.getImage() != null && user.getImage().length() > 0) {
            Glide.with(EditUserProfile.this).load(user.getImage()).into(image);
        } else {
            image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        }
        AppBarLayout app_bar = findViewById(R.id.app_bar);
        app_bar.setExpanded(true, true);

        profileName.setText(user.getName());
        profilePhoneNumber.setText(user.getPhoneNumber());
        profileEmail.setText(user.getEmail());
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnUpdate: {
                Log.e("User", "Button Clicked");
                boolean isConn = helpers.isConnected(EditUserProfile.this);
                if (!isConn) {
                    helpers.showError(EditUserProfile.this, "ERROR!", "No Internet Connection. Please check your Internet Connection.");
                    return;
                }
                boolean flag = isValid();
                if (flag) {
                    SaveProgress.setVisibility(View.VISIBLE);
                    updateBtn.setVisibility(View.GONE);
                    user.setEmail(strEmail);
                    user.setName(strName);
                    Log.e("Login", "Id: " + user.getId());
                    Log.e("Login", "Email: " + user.getEmail());
                    Log.e("Login", "Name: " + user.getName());
                    Log.e("Login", "Phone Number: " + user.getPhoneNumber());
                    if (isImage) {
                        uploadImage();
                    } else {
                        user.setImage("");
                        saveUser();
                    }

                }
                break;
            }
            case R.id.profile_nav_gallery: {
                boolean flag = hasPermissions(EditUserProfile.this, PERMISSIONS);
                if (!flag) {
                    ActivityCompat.requestPermissions(EditUserProfile.this, PERMISSIONS, 1);
                } else {
                    openGallery();
                }
                break;
            }
        }

    }

    private void uploadImage() {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Users").child(user.getId());
        Calendar calendar = Calendar.getInstance();
        storageReference.child(calendar.getTimeInMillis() + "").putFile(imageUri)
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
                                        updateBtn.setVisibility(View.VISIBLE);
                                        helpers.showError(EditUserProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Login", "Upload Image Url: " + e.getMessage());
                        SaveProgress.setVisibility(View.GONE);
                        updateBtn.setVisibility(View.VISIBLE);
                        helpers.showError(EditUserProfile.this, "ERROR!", "Something went wrong.\n Please try again later.");
                    }
                });
    }

    private void saveUser() {
        Log.e("Login", "Id: " + user.getId());
        Log.e("Login", "Email: " + user.getEmail());
        Log.e("Login", "Name: " + user.getName());
        Log.e("Login", "Phone Number: " + user.getPhoneNumber());
        Log.e("Login", "Image: " + user.getImage());
        user.setLat(0);
        user.setLng(0);
        updateReference.child(user.getId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SaveProgress.setVisibility(View.GONE);
                updateBtn.setVisibility(View.VISIBLE);
                session.setSession(user);
                if (user.getType().equals("User") || user.getType().equals("None")) {
                    Intent it = new Intent(EditUserProfile.this, Dashboard.class);
                    startActivity(it);
                    finish();
                } else {
                    Intent it = new Intent(EditUserProfile.this, VendorDashboard.class);
                    startActivity(it);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SaveProgress.setVisibility(View.GONE);
                updateBtn.setVisibility(View.VISIBLE);
                helpers.showError(EditUserProfile.this, "ERROR", "Something went wrong.\nPlease try again later.");
            }
        });
    }

    private boolean hasPermissions(Context c, String... permission) {
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void openGallery() {
        BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("lcwu.fyp.gohytch.fileprovider").build();
        singleSelectionPicker.show(getSupportFragmentManager(), "picker");
    }


    private boolean isValid() {
        boolean flag = true;
        strName = profileName.getText().toString();
        strEmail = profileEmail.getText().toString();
        Log.e("User", "Email: " + strEmail);
        if (strName.length() < 3) {
            profileName.setError("Enter a valid name");
            flag = false;
        } else {
            profileName.setError(null);
        }

        if (strEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            Log.e("User", "If Email: " + strEmail);
            profileEmail.setError("Enter a valid email");
            flag = false;
        } else {
            Log.e("User", "Else Email: " + strEmail);
            profileEmail.setError(null);
        }
        return flag;
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        isImage = true;
        imageUri = uri;
        Glide.with(EditUserProfile.this).load(imageUri).into(image);
    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        isImage = true;
        this.imageUri = imageUri;
        Glide.with(EditUserProfile.this).load(imageUri).into(image);
    }
}

package lcwu.fyp.gohytch.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private Button btnSave;
    private EditText edtName, edtEmail;
    private String strPhonenumber, strName, strEmail;
    private ProgressBar SaveProgress;
    private Helpers helpers;
    private ImageView image;
    private boolean isImage = false;
    private Uri imageUri;
    private User user;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent it = getIntent();
        if (it == null) {
            finish();
            return;
        }
        strPhonenumber = it.getStringExtra("Phone");
        if (strPhonenumber == null) {
            finish();
            return;
        }

        helpers = new Helpers();
        session = new Session(UserProfileActivity.this);
        user = new User();

        FloatingActionButton fab = findViewById(R.id.nav_gallery);
        fab.setOnClickListener(this);
        btnSave = findViewById(R.id.btnSave);
        edtName = findViewById(R.id.Name);
        edtEmail = findViewById(R.id.Email);
        SaveProgress = findViewById(R.id.SaveProgress);
        btnSave.forceLayout();

        btnSave.setOnClickListener(this);

        image = findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        AppBarLayout app_bar = findViewById(R.id.app_bar);
        app_bar.setExpanded(true, true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSave: {
                Log.e("User", "Button Clicked");

                boolean isConn = helpers.isConnected(UserProfileActivity.this);
                if (!isConn) {
                    helpers.showError(UserProfileActivity.this, "ERROR!", "No Internet Connection. Please check your Internet Connection.");
                    return;
                }
                boolean flag = isValid();
                if (flag) {
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
                    if (isImage) {
                        uploadImage();
                    } else {
                        user.setImage("");
                        saveUser();
                    }

                }
                break;
            }
            case R.id.nav_gallery: {
                boolean flag = hasPermissions(UserProfileActivity.this, PERMISSIONS);
                if (!flag) {
                    ActivityCompat.requestPermissions(UserProfileActivity.this, PERMISSIONS, 1);
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

    private void saveUser() {
        Log.e("Login", "Id: " + user.getId());
        Log.e("Login", "Email: " + user.getEmail());
        Log.e("Login", "Name: " + user.getName());
        Log.e("Login", "Phone Number: " + user.getPhoneNumber());
        Log.e("Login", "Image: " + user.getImage());
        user.setLat(0);
        user.setLng(0);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(user.getId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SaveProgress.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                session.setSession(user);
                Intent it = new Intent(UserProfileActivity.this, Dashboard.class);
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


    private boolean hasPermissions(Context c, String... permission) {
        for (String p : permission) {
            if (ActivityCompat.checkSelfPermission(c, p) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void openGallery() {
        ImagePicker.create(UserProfileActivity.this)
                .toolbarImageTitle("Tap to select")
                .single()
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image img = ImagePicker.getFirstImageOrNull(data);
            Log.e("User", "Image: " + img.getName());
            Log.e("User", "Image: " + img.getPath());
            isImage = true;
            imageUri = Uri.fromFile(new File(img.getPath()));
            Glide.with(UserProfileActivity.this).load(imageUri).into(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isValid() {
        boolean flag = true;
        strName = edtName.getText().toString();
        strEmail = edtEmail.getText().toString();
        Log.e("User", "Email: " + strEmail);
        if (strName.length() < 3) {
            edtName.setError("Enter a valid name");
            flag = false;
        } else {
            edtName.setError(null);
        }

        if (strEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            Log.e("User", "If Email: " + strEmail);
            edtEmail.setError("Enter a valid email");
            flag = false;
        } else {
            Log.e("User", "Else Email: " + strEmail);
            edtEmail.setError(null);
        }
        return flag;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            openGallery();
        }
    }
}
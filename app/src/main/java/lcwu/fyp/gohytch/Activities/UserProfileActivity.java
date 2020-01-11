package lcwu.fyp.gohytch.Activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.OnOtpCompletionListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnSave;
    EditText edtphonenumber,edtName,edtEmail;
    String strPhonenumber,strName,strEmail;
    ProgressBar SaveProgress;
    String verificationId;
    Helpers helpers;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent it=getIntent();
        if(it==null){
            finish();
            return;
        }
        strPhonenumber=it.getStringExtra("Phone");
        if (strPhonenumber==null)
        {
            finish();
            return;
        }
        btnSave=findViewById(R.id.btnSave);
        edtphonenumber=findViewById(R.id.PhoneNumber);
        edtName=findViewById(R.id.Name);
        edtEmail=findViewById(R.id.Email);
        SaveProgress=findViewById(R.id.SaveProgress);

        btnSave.setOnClickListener(this);
        helpers=new Helpers();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        image = findViewById(R.id.image);
        image.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        edtphonenumber.setText(strPhonenumber);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.btnSave: {
                boolean isConn = helpers.isConnected(UserProfileActivity.this);
                if (!isConn) {

                    helpers.showError(UserProfileActivity.this, "ERROR!", "No Internet Connection. Please check your Internet Connection.");
                    return;
                }
                boolean flag=isValid();
                if(!flag){

                    SaveProgress.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    final User u=new User();
                    final Session session=new Session(UserProfileActivity.this);

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                    u.setId(strPhonenumber);
                    u.setEmail(strEmail);
                    u.setName(strName);
                    u.setPhoneNumber(strPhonenumber);

                    reference.child("Users").child(u.getId()).setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            SaveProgress.setVisibility(View.GONE);
                            btnSave.setVisibility(View.VISIBLE);
                            session.setSession(u);
                            Intent it=new Intent(UserProfileActivity.this,DashboardActivity.class);
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
            }
        }

    }



    private boolean isValid() {
        boolean flag = true;
        strName=edtName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPhonenumber = edtphonenumber.getText().toString();

        if (strName.length() < 3) {
            edtName.setError("Enter a valid name");
            flag=false;
        } else {
            edtName.setError(null);
        }

        if (strPhonenumber.length() != 11) {
            edtphonenumber.setError("Enter a valid number");
            flag=false;

        } else {
            edtphonenumber.setError(null);
        }
        if (strEmail.length() < 6 || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            edtEmail.setError("Enter a valid email");
            flag=false;
        } else {
            edtEmail.setError(null);

        }
        return flag;
    }

}
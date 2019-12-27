package lcwu.fyp.gohytch.Activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        btnSave=findViewById(R.id.btnSave);
        edtphonenumber=findViewById(R.id.PhoneNumber);
        edtName=findViewById(R.id.Name);
        edtEmail=findViewById(R.id.Email);
        SaveProgress=findViewById(R.id.SaveProgress);

        btnSave.setOnClickListener(this);
        helpers=new Helpers();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
                    User u=new User();
                    Session session=new Session(UserProfileActivity.this);

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

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
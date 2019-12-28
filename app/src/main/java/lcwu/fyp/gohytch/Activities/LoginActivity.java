package lcwu.fyp.gohytch.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jeevandeshmukh.fancybottomsheetdialoglib.FancyBottomSheetDialog;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import java.util.concurrent.TimeUnit;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin;
    EditText edtphonenumber;
    String strphonenumber;
    ProgressBar LoginProgress;
    String verificationId;
    Helpers helpers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtphonenumber = findViewById(R.id.PhoneNumber);
        LoginProgress=findViewById(R.id.LoginProgress);


        btnLogin.setOnClickListener(this);

        helpers = new Helpers();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLogin: {

                boolean isConn= helpers.isConnected(LoginActivity.this);
                if (!isConn){
                helpers.showError(LoginActivity.this,"ERROR!","No Internet Connection. Please check your Internet Connection.");
                    return;
                }

                strphonenumber = edtphonenumber.getText().toString();
                boolean flag=isValid();
                if (flag){
                    //firebase
                    LoginProgress.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);

                    PhoneAuthProvider auth=PhoneAuthProvider.getInstance();
                    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
                    callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            LoginProgress.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                            verificationId=s;
                            OTPDialog dialog=new OTPDialog(LoginActivity.this);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                            FirebaseAuth auth= FirebaseAuth.getInstance();
                            auth.signInWithCredential(credential)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                           // LoginProgress.setVisibility(View.GONE);
                                            //btnLogin.setVisibility(View.VISIBLE);
                                            //Intent it=new Intent(LoginActivity.this,DashboardActivity.class);
                                            //startActivity(it);
                                            //finish();
                                            call_to_database();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    LoginProgress.setVisibility(View.GONE);
                                    btnLogin.setVisibility(View.VISIBLE);
                                    helpers.showError(LoginActivity.this,"ERROR!",e.getMessage());

                                }
                            });
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            LoginProgress.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                            helpers.showError(LoginActivity.this,"ERROR!",e.getMessage());

                        }
                    };
                    auth.verifyPhoneNumber(strphonenumber,120, TimeUnit.SECONDS,this, callbacks);

                }
                break;
            }

        }

    }
    private boolean isValid(){
        boolean flag=true;
        if (strphonenumber.length() != 13) {
            edtphonenumber.setError("Enter a valid number");
            flag=false;
        } else {
            edtphonenumber.setError(null);
        }
        return flag;
    }

    private void call_to_database(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    //data is valid
                    User u = dataSnapshot.getValue(User.class);
                    Session session = new Session(LoginActivity.this);
                    session.setSession(u);
                    //start dashboard activity
                }
                else {
                    Intent it=new Intent(LoginActivity.this,UserProfileActivity.class);
                    it.putExtra("Phone", strphonenumber);
                    startActivity(it);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                helpers.showError(LoginActivity.this, "EEROR", "Something went wrong.\n Please try again later");
            }
        });  //data read
    }


    class OTPDialog extends Dialog{

        OtpView otp_view;
        TextView timer,resendOtp,error;
        ProgressBar OtpProgress;

        public OTPDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.otpdialog);

            otp_view = findViewById(R.id.otp_view);
            timer = findViewById(R.id.timer);
            resendOtp = findViewById(R.id.ResendOtp);
            OtpProgress = findViewById(R.id.OtpProgress);
            error=findViewById(R.id.error);
            resendOtp.setEnabled(false);

            otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
                @Override
                public void onOtpCompleted(String otp) {
                    OtpProgress.setVisibility(View.VISIBLE);
                    Log.e("otp", otp);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    FirebaseAuth auth= FirebaseAuth.getInstance();
                    auth.signInWithCredential(credential)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                        call_to_database();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            OtpProgress.setVisibility(View.GONE);
                            error.setText(e.getMessage());
                            helpers.showError(LoginActivity.this, "ERROR", e.getMessage());
                        }
                    });
                }
            });

            startTimer();
        }

        private void startTimer(){
            new CountDownTimer(120000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    millisUntilFinished=millisUntilFinished/1000;
                    long second=millisUntilFinished % 60;
                    long minutes=(millisUntilFinished / 60)% 60;
                    String time="";
                    if (second>9){
                        time="0" + minutes + ":" + second;
                    }
                    else {
                        time = "0" + minutes + ":" + "0" + second;
                    }
                    timer.setText(time);
                }

                @Override
                public void onFinish() {
                    timer.setText("--:--");
                    resendOtp.setEnabled(true);
                }
            }.start();
        }

    }





}





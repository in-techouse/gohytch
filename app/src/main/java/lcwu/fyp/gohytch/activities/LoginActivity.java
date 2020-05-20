package lcwu.fyp.gohytch.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin;
    private EditText edtphonenumber;
    private String strphonenumber;
    private ProgressBar LoginProgress;
    private String verificationId;
    private Helpers helpers;
    private OTPDialog dialog;
    private PhoneAuthProvider auth = PhoneAuthProvider.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtphonenumber = findViewById(R.id.PhoneNumber);
        LoginProgress = findViewById(R.id.LoginProgress);


        btnLogin.setOnClickListener(this);

        helpers = new Helpers();
        dialog = new OTPDialog(LoginActivity.this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLogin: {

                boolean isConn = helpers.isConnected(LoginActivity.this);
                if (!isConn) {
                    helpers.showError(LoginActivity.this, "ERROR!", "No Internet Connection. Please check your Internet Connection.");
                    return;
                }

                strphonenumber = edtphonenumber.getText().toString();
                boolean flag = isValid();
                if (flag) {
                    //firebase
                    LoginProgress.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);

                    callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            LoginProgress.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                            verificationId = s;
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            if (dialog.isShowing()) {
                                dialog.OtpProgress.setVisibility(View.GONE);
                                dialog.resendOtp.setVisibility(View.VISIBLE);
                                dialog.timer.setVisibility(View.VISIBLE);
                                dialog.resendOtp.setEnabled(false);
                                dialog.startTimer();
                            } else {
                                dialog.show();
                            }
                        }

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            auth.signInWithCredential(credential)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            call_to_database();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Login", "On Failure 109");
                                    LoginProgress.setVisibility(View.GONE);
                                    btnLogin.setVisibility(View.VISIBLE);
                                    helpers.showError(LoginActivity.this, "ERROR!", e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            if (dialog.isShowing()) {
                                dialog.timer.setText("--:--");
                                dialog.resendOtp.setEnabled(true);
                                dialog.OtpProgress.setVisibility(View.GONE);
                                dialog.error.setText(e.getMessage());
                                helpers.showError(LoginActivity.this, "ERROR", e.getMessage());
                            }
                            LoginProgress.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                            helpers.showError(LoginActivity.this, "ERROR!", e.getMessage());

                        }
                    };
                    auth.verifyPhoneNumber(strphonenumber, 120, TimeUnit.SECONDS, this, callbacks);
                }
                break;
            }

        }

    }

    private boolean isValid() {
        boolean flag = true;
        if (strphonenumber.length() != 13) {
            edtphonenumber.setError("Enter a valid number");
            flag = false;
        } else {
            edtphonenumber.setError(null);
        }
        return flag;
    }

    private void call_to_database() {
        if (dialog.isShowing()) {
            dialog.countDownTimer.cancel();
            dialog.error.setVisibility(View.GONE);
            dialog.resendOtp.setVisibility(View.GONE);
            dialog.timer.setVisibility(View.GONE);
        }
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(strphonenumber);
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.removeEventListener(listener);
                if (dataSnapshot.getValue() != null) {
                    dialog.dismiss();
                    //data is valid
                    User u = dataSnapshot.getValue(User.class);
                    if (u != null) {
                        Session session = new Session(LoginActivity.this);
                        session.setSession(u);
                        //start dashboard activity
                        if (u.getType().equals("User") || u.getType().equals("None")) {
                            Intent it = new Intent(LoginActivity.this, Dashboard.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);
                        } else {
                            Intent it = new Intent(LoginActivity.this, VendorDashboard.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(it);
                        }
                        finish();
                    } else {
                        Intent it = new Intent(LoginActivity.this, UserProfileActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        it.putExtra("Phone", strphonenumber);
                        startActivity(it);
                        finish();
                    }
                } else {
                    Intent it = new Intent(LoginActivity.this, UserProfileActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    it.putExtra("Phone", strphonenumber);
                    startActivity(it);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                reference.removeEventListener(listener);
                if (dialog.isShowing()) {
                    dialog.countDownTimer.cancel();
                    dialog.error.setVisibility(View.VISIBLE);
                    dialog.resendOtp.setVisibility(View.VISIBLE);
                    dialog.timer.setVisibility(View.VISIBLE);
                }
                helpers.showError(LoginActivity.this, "ERROR", "Something went wrong.\n Please try again later");
            }
        });  //data read
    }


    class OTPDialog extends Dialog {

        OtpView otp_view;
        TextView timer, resendOtp, error;
        ProgressBar OtpProgress;
        CountDownTimer countDownTimer;

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
            error = findViewById(R.id.error);
            resendOtp.setEnabled(false);

            otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
                @Override
                public void onOtpCompleted(String otp) {
                    OtpProgress.setVisibility(View.VISIBLE);
                    Log.e("otp", otp);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithCredential(credential)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    call_to_database();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Login", "On Failure 229");
                            countDownTimer.cancel();
                            timer.setText("--:--");
                            resendOtp.setEnabled(true);
                            OtpProgress.setVisibility(View.GONE);
                            error.setText(e.getMessage());
                            helpers.showError(LoginActivity.this, "ERROR", e.getMessage());
                        }
                    });
                }
            });

            resendOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Login", "Resend Clicked");
                    dialog.error.setText("");
                    OtpProgress.setVisibility(View.VISIBLE);
                    resendOtp.setVisibility(View.GONE);
                    timer.setVisibility(View.GONE);
                    auth.verifyPhoneNumber(strphonenumber, 120, TimeUnit.SECONDS, LoginActivity.this, callbacks);

                }
            });

            startTimer();
        }

        private void startTimer() {
            countDownTimer = new CountDownTimer(120000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    millisUntilFinished = millisUntilFinished / 1000;
                    long second = millisUntilFinished % 60;
                    long minutes = (millisUntilFinished / 60) % 60;
                    String time = "";
                    if (second > 9) {
                        time = "0" + minutes + ":" + second;
                    } else {
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





package lcwu.fyp.gohytch;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.jeevandeshmukh.fancybottomsheetdialoglib.FancyBottomSheetDialog;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin;
    EditText edtphonenumber;
    String strphonenumber;
    ProgressBar LoginProgress;
    String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtphonenumber = findViewById(R.id.PhoneNumber);
        LoginProgress=findViewById(R.id.LoginProgress);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLogin: {

                boolean isConn= isConnected();
                if (!isConn){

                    new FancyBottomSheetDialog.Builder(this)
                            .setTitle("Alert bottom sheet dialog")
                            .setMessage("This is where we show the information.This is a message.This is where we show message explain or showing the information.")
                            .setBackgroundColor(Color.parseColor("#3F51B5")) //don't use R.color.somecolor
                            .setIcon(R.drawable.ic_action_error,true)
                            .isCancellable(false)
                            .OnNegativeClicked(new FancyBottomSheetDialog.FancyBottomSheetDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            })
                            .OnPositiveClicked(new FancyBottomSheetDialog.FancyBottomSheetDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            })
                            .setNegativeBtnText("Cancel")
                            .setPositiveBtnText("Ok")
                            .setPositiveBtnBackground(Color.parseColor("#3F51B5"))//don't use R.color.somecolor
                            .setNegativeBtnBackground(Color.WHITE)//don't use R.color.somecolor
                            .build();
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
                            verificationId=s;
                            btnLogin.setVisibility(View.VISIBLE);
                            OTPDialog dialog=new OTPDialog(LoginActivity.this);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }

                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {

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
            edtphonenumber.setError("Enter a valid name");
            flag=false;
        } else {
            edtphonenumber.setError(null);
        }
        return flag;
    }
    // Check Internet Connection
    private boolean isConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
        return  connected;
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

                        OtpProgress.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        OtpProgress.setVisibility(View.GONE);
                        error.setText(e.getMessage());

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





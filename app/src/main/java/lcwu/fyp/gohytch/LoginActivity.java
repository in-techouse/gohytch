package lcwu.fyp.gohytch;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin;
    EditText edtphonenumber;
    String strphonenumber;
    ProgressBar LoginProgress;
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
class OTPDialog extends Dialog{

    public OTPDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.otpdialog);
    }
}





}





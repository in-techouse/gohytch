package lcwu.fyp.gohytch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnRegister;
    EditText edtphonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnRegister = findViewById(R.id.buttonRegister);
        edtphonenumber = findViewById(R.id.PhoneNumber);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.buttonRegister: {
                String strphonenumber = edtphonenumber.getText().toString();

                if (strphonenumber.length() != 11) {
                    edtphonenumber.setError("Enter a valid name");
                } else {
                    edtphonenumber.setError(null);
                    Intent it = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(it);
                }

                break;
            }

        }

    }
}





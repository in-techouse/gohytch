package lcwu.fyp.gohytch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import lcwu.fyp.gohytch.R;

public class CreateDriver extends AppCompatActivity implements View.OnClickListener {
    Button btnSave;
    EditText edtLicenseNumber,edtPastExperience,edtExpertise;
    ProgressBar SaveProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_driver);

        btnSave=findViewById(R.id.btnSave);
        edtLicenseNumber=findViewById(R.id.LicenseNumber);
        edtPastExperience=findViewById(R.id.PastExperience);
        edtExpertise=findViewById(R.id.Expertise);
        SaveProgress=findViewById(R.id.SaveProgress);
        btnSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}

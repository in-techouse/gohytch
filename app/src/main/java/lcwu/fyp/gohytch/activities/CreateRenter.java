package lcwu.fyp.gohytch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import lcwu.fyp.gohytch.R;

public class CreateRenter extends AppCompatActivity  implements View.OnClickListener{
    Button btnSave;
    EditText edtLicenseNumber,edtCar_Model,edtRegistrationNumber,edtsittingCapacity,edtCompany;
    ProgressBar SaveProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renter);

        btnSave=findViewById(R.id.btnSave);
        edtLicenseNumber=findViewById(R.id.LicenseNumber);
        edtCar_Model=findViewById(R.id.Car_Model);
        edtRegistrationNumber=findViewById(R.id.RegistrationNumber);
        edtsittingCapacity=findViewById(R.id.sittingCapacity);
        edtCompany=findViewById(R.id.Company);

        SaveProgress=findViewById(R.id.SaveProgress);

        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){

        }

    }
}

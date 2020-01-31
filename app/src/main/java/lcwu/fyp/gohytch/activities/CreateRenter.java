package lcwu.fyp.gohytch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class CreateRenter extends AppCompatActivity  implements View.OnClickListener {
    Button btnSave;
    EditText edtLicenseNumber, edtCar_Model, edtRegistrationNumber, edtsittingCapacity, edtCompany;
    String strLicenseNumber,strCar_Model,strRegistrationNumber,strsittingCapacity,strCompany;
    ProgressBar SaveProgress;
    Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renter);

        btnSave = findViewById(R.id.btnSave);
        edtLicenseNumber = findViewById(R.id.LicenseNumber);
        edtCar_Model = findViewById(R.id.Car_Model);
        edtRegistrationNumber = findViewById(R.id.RegistrationNumber);
        edtsittingCapacity = findViewById(R.id.sittingCapacity);
        edtCompany = findViewById(R.id.Company);

        SaveProgress = findViewById(R.id.SaveProgress);

        btnSave.setOnClickListener(this);
        helpers = new Helpers();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnSave: {
                Log.e("Renter", "Button Clicked");
                boolean isConn = helpers.isConnected(CreateRenter.this);
                if (!isConn) {
                    helpers.showError(CreateRenter.this, "ERROR", "No Internet Connection.Please check your Internet Connection");
                    return;

                }
                boolean flag = isValid();
                if (flag){
                    SaveProgress.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    final User u=new User();
                    final Session session=new Session(CreateRenter.this);
                }


            }

        }

    }

    private boolean isValid() {
        boolean flag=true;
        strLicenseNumber=edtLicenseNumber.getText().toString();
        strCar_Model=edtCar_Model.getText().toString();
        strRegistrationNumber=edtRegistrationNumber.getText().toString();
        strsittingCapacity=edtsittingCapacity.getText().toString();
        strCompany=edtCompany.getText().toString();
        Log.e("Renter","LicnseNumber:" + strLicenseNumber);
        if (strLicenseNumber.length()<4){
            edtLicenseNumber.setError("Enter a valid License Number");
            flag=false;
        }else{
            edtLicenseNumber.setError(null);
        }


        if (strCar_Model.length()<4){
            edtCar_Model.setError("Enter a valid Car Model");
            flag=false;
        }else{
            edtCar_Model.setError(null);
        }


        if (strRegistrationNumber.length()<5){
            edtRegistrationNumber.setError("Enter a valid RegistrationNumber");
            flag=false;
        }else{
            edtRegistrationNumber.setError(null);
        }
        if (strsittingCapacity.length()<4){
            edtsittingCapacity.setError("Enter a valid sittingCapacity");
            flag=false;
        }else{
            edtsittingCapacity.setError(null);
        }
        if (strCompany.length()<5){
            edtCompany.setError("Enter a valid Company Name");
            flag=false;
        }else{
            edtCompany.setError(null);
        }


        return flag;
    }

}

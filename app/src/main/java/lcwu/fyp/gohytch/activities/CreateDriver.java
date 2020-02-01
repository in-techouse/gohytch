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

public class CreateDriver extends AppCompatActivity implements View.OnClickListener {
    Button btnSave;
    EditText edtLicenseNumber, edtExpertise;
    String strLicenseNumber;
    ProgressBar SaveProgress;
    Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_driver);

        btnSave=findViewById(R.id.btnSave);
        edtLicenseNumber=findViewById(R.id.LicenseNumber);
        edtExpertise=findViewById(R.id.Expertise);
        SaveProgress=findViewById(R.id.SaveProgress);
        btnSave.setOnClickListener(this);
        helpers=new Helpers();
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btnSave:{
                Log.e("Driver","Button Clicked");
                boolean isConn=helpers.isConnected(CreateDriver.this);
                if(!isConn){
                    helpers.showError(CreateDriver.this,"Error","No Internet Connection.Please Check Your Connection and try again later");
                    return;
                }
                boolean flag=isValid();
                if (flag){
                    btnSave.setVisibility(View.GONE);
                    final User u= new User();
                    final Session session=new Session(CreateDriver.this);
                }
            }
        }

    }

    private boolean isValid() {
        boolean flag=true;
        strLicenseNumber=edtLicenseNumber.getText().toString();
        Log.e("Renter","LicnseNumber:" + strLicenseNumber);
        if (strLicenseNumber.length()<4){
            edtLicenseNumber.setError("Enter a valid License Number");
            flag=false;
        }else{
            edtLicenseNumber.setError(null);
        }
        return flag;
    }
}

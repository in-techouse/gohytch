package lcwu.fyp.gohytch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinner;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Driver;
import lcwu.fyp.gohytch.model.User;

public class CreateDriver extends AppCompatActivity implements View.OnClickListener{
    List<String> list;
    LinkedHashMap<String, Boolean> listArray = new LinkedHashMap<>();
    EditText licenseNumber;
    Spinner experience;
    MultiSpinner expertise;
    String strLicenseNumber, strExperience = "";
    List<String> listExpertise = new ArrayList<>();
    Button btnSave;
    ProgressBar saveProgress;
    private Session session;
    private User user;
    Helpers helpers;
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_driver);


        btnSave = findViewById(R.id.btnSave);
        licenseNumber = findViewById(R.id.licenseNumber);
        experience = findViewById(R.id.experience);
        expertise = findViewById(R.id.expertise);
        saveProgress = findViewById(R.id.saveProgress);

        btnSave.setOnClickListener(this);

        helpers = new Helpers();
        driver = new Driver();

        session = new Session(CreateDriver.this);
        user = session.getSession();

        list = Arrays.asList(getResources().getStringArray(R.array.expertise));

        for (int i = 0; i < list.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list.get(i));
            h.setSelected(false);
            listArray.put(list.get(i), false);
        }
        expertise.setItems(listArray, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                Log.e("CreateDriver", "Selected List: " + selected.length);
                for (int i = 0; i < selected.length; i++){
                    Log.e("CreateDriver", "Index: " + i + " Selected: " + selected[i]);
                    if(selected[i]) {
                        Log.i("TAG", i + " : "+ list.get(i));
                        listExpertise.add(list.get(i));
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSave: {
                Log.e("Driver", "Button Clicked");
                boolean isConn = helpers.isConnected(CreateDriver.this);
                if (!isConn) {
                    helpers.showError(CreateDriver.this, "Error", "No Internet Connection.Please Check Your Connection and try again later");
                    return;
                }
                boolean flag = isValid();
                if (flag) {
                    btnSave.setVisibility(View.GONE);
                    saveProgress.setVisibility(View.VISIBLE);
                    driver.setLicenseNumber(strLicenseNumber);
                    driver.setExpertise(listExpertise);
                    driver.setPastExperience(strExperience);
                    driver.setRating(0);
                    user.setDriver(driver);
                    user.setType("Driver");

                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getPhoneNumber());
                    databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid){
                            btnSave.setVisibility(View.VISIBLE);
                            saveProgress.setVisibility(View.GONE);
                            session.setSession(user);
                            Log.e("CreateDriver", "Driver Saved Successfully");
                            Intent intent = new Intent(CreateDriver.this, VendorDashboard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btnSave.setVisibility(View.VISIBLE);
                            saveProgress.setVisibility(View.GONE);
                            helpers.showError(CreateDriver.this,"Error!","Something went wrong.Please check your connection");
                        }
                    });
                }
                break;
            }
        }
    }

    private boolean isValid() {
        boolean flag = true;
        String error = "";
        strLicenseNumber = licenseNumber.getText().toString();
        strExperience = experience.getSelectedItem().toString();
        Log.e("CreateDriver","LicenseNumber:" + strLicenseNumber);
        if (strLicenseNumber.length() < 4){
            licenseNumber.setError("Enter a valid License Number");
            flag=false;
        }else{
            licenseNumber.setError(null);
        }
        if(experience.getSelectedItemPosition() == 0){
            error = "*Select your experience.\n";
            flag=false;
        }
        if(listExpertise.size() == 0){
            error = error + "*Select your expertise.\n";
            flag=false;
        }
        if(error.length() > 0){
            helpers.showError(CreateDriver.this, "ERROR", error);
        }
        return flag;
    }
}

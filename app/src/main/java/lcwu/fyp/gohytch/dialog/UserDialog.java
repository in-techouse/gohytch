package lcwu.fyp.gohytch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Driver;
import lcwu.fyp.gohytch.model.User;

public class UserDialog extends Dialog implements View.OnClickListener{
    Button btnUser,btnRenter,btnDriver;
    private Session session;
    private User user;
    private Context context;
    public UserDialog(@NonNull Context c) {
        super(c);
        context = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_dialog);
        btnUser=findViewById(R.id.btnUser);
        btnDriver=findViewById(R.id.btnDriver);
        btnRenter=findViewById(R.id.btnRenter);

        btnUser.setOnClickListener(this);
        btnDriver.setOnClickListener(this);
        btnRenter.setOnClickListener(this);
        session= new Session(context);
        user = session.getSession();
        Log.e("UserDialog", "Id: " + user.getId());
        Log.e("UserDialog", "Email: " + user.getEmail());
        Log.e("UserDialog", "Name: " + user.getName());
        Log.e("UserDialog", "Phone Number: " + user.getPhoneNumber());
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.btnUser:{
                user.setType("User");
                updateToDatabase();
                dismiss();
                break;
            }
            case R.id.btnDriver:{
                user.setType("Driver");
                updateToDatabase();
                dismiss();
                break;
            }
            case R.id.btnRenter:{
                user.setType("Renter");
                updateToDatabase();
                dismiss();
                break;
            }
        }
    }

    private void updateToDatabase(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getId()).setValue(user);
        session.setSession(user);
    }
}

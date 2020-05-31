package lcwu.fyp.gohytch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.activities.CreateDriver;
import lcwu.fyp.gohytch.activities.CreateRenter;
import lcwu.fyp.gohytch.activities.Dashboard;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class UserDialog extends Dialog implements View.OnClickListener {
    private Session session;
    private User user;
    private Context context;
    private Dashboard dashboard;

    public UserDialog(@NonNull Context c, Dashboard d) {
        super(c);
        context = c;
        dashboard = d;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_dialog);
        Button btnUser = findViewById(R.id.btnUser);
        Button btnDriver = findViewById(R.id.btnDriver);
        Button btnRenter = findViewById(R.id.btnRenter);

        btnUser.setOnClickListener(this);
        btnDriver.setOnClickListener(this);
        btnRenter.setOnClickListener(this);
        session = new Session(context);
        user = session.getSession();
        Log.e("UserDialog", "Id: " + user.getId());
        Log.e("UserDialog", "Email: " + user.getEmail());
        Log.e("UserDialog", "Name: " + user.getName());
        Log.e("UserDialog", "Phone Number: " + user.getPhoneNumber());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnUser: {
                user.setType("User");
                updateToDatabase();
                dismiss();
                break;
            }
            case R.id.btnDriver: {
                user.setType("Driver");
                Intent it = new Intent(context, CreateDriver.class);
                context.startActivity(it);
                dashboard.finish();
                break;
            }
            case R.id.btnRenter: {
                user.setType("Renter");
                Intent it = new Intent(context, CreateRenter.class);
                context.startActivity(it);
                dashboard.finish();
                break;
            }
        }
    }

    private void updateToDatabase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(user.getId()).setValue(user);
        session.setSession(user);
    }
}

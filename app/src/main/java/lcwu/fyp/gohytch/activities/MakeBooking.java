package lcwu.fyp.gohytch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class MakeBooking extends AppCompatActivity {
    private Session session;
    private User user, renter;
    private Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_booking);

        Intent it = getIntent();
        if (it == null) {
            Log.e("MakeBooking", "Intent is Null");
            return;
        }
        Bundle bundle = it.getExtras();
        if (bundle == null) {
            Log.e("MakeBooking", "Bundle is Null");
            return;
        }

        renter = (User) bundle.getSerializable("renter");
        if (renter == null) {
            Log.e("MakeBooking", "Intent is Null");
            return;
        }

        session = new Session(MakeBooking.this);
        user = session.getSession();
        helpers = new Helpers();


    }
}

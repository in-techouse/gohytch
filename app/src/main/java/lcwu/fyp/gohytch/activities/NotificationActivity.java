package lcwu.fyp.gohytch.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.adapters.NotificationAdapter;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Notification;
import lcwu.fyp.gohytch.model.User;

public class NotificationActivity extends AppCompatActivity {
    private LinearLayout loading;
    private TextView noRecord;
    private RecyclerView notification;
    private User user;
    private Helpers helpers;
    private List<Notification> data;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications");
    private String type;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        loading = findViewById(R.id.loading);
        noRecord = findViewById(R.id.noRecord);
        notification = findViewById(R.id.notifications);
        Session session = new Session(NotificationActivity.this);
        helpers = new Helpers();
        user = session.getSession();
        data = new ArrayList<>();
        if (user.getType().equals("None") || user.getType().equals("User")) {
            type = "userId";
        } else {
            type = "driverId";
        }
        notification.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        adapter = new NotificationAdapter(user.getType(), getApplicationContext());
        notification.setAdapter(adapter);
        loadNotifications();

    }

    private void loadNotifications() {
        if (!helpers.isConnected(NotificationActivity.this)) {
            helpers.showError(NotificationActivity.this, "ERROR!", "No Internet Connection.Please check your Internet Connection");
            return;

        }
        loading.setVisibility(View.VISIBLE);
        noRecord.setVisibility(View.GONE);
        notification.setVisibility(View.GONE);
        reference.orderByChild(type).equalTo(user.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Notification notification = d.getValue(Notification.class);
                    if (notification != null) {
                        data.add(notification);
                    }
                }
                if (data.size() > 0) {
                    Collections.reverse(data);
                    adapter.setData(data);
                    notification.setVisibility(View.VISIBLE);
                    noRecord.setVisibility(View.GONE);
                } else {
                    notification.setVisibility(View.GONE);
                    noRecord.setVisibility(View.VISIBLE);
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noRecord.setVisibility(View.VISIBLE);
                notification.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }
}

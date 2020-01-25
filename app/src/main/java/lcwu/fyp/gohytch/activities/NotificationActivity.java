package lcwu.fyp.gohytch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import lcwu.fyp.gohytch.R;

public class NotificationActivity extends AppCompatActivity {
 private LinearLayout loading;
 private TextView  noRecord;
 private RecyclerView notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        loading=findViewById(R.id.loading);
        noRecord=findViewById(R.id.noRecord);
        notification=findViewById(R.id.notification);
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

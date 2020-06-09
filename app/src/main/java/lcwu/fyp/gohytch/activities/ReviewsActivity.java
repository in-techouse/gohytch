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
import lcwu.fyp.gohytch.adapters.ReviewAdapter;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Review;
import lcwu.fyp.gohytch.model.User;

public class ReviewsActivity extends AppCompatActivity {
    private LinearLayout loading;
    private TextView noRecord;
    private RecyclerView reviews;
    private User user;
    private Helpers helpers;
    private List<Review> data;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Reviews");
    private String type;
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        loading = findViewById(R.id.loading);
        noRecord = findViewById(R.id.noRecord);
        reviews = findViewById(R.id.reviews);

        Session session = new Session(ReviewsActivity.this);
        helpers = new Helpers();
        user = session.getSession();
        data = new ArrayList<>();

        reviews.setLayoutManager(new LinearLayoutManager(ReviewsActivity.this));
        adapter = new ReviewAdapter(getApplicationContext());
        reviews.setAdapter(adapter);
        loadReviews();
    }

    private void loadReviews() {
        if (!helpers.isConnected(ReviewsActivity.this)) {
            helpers.showError(ReviewsActivity.this, "ERROR!", "No Internet Connection.Please check your Internet Connection");
            loading.setVisibility(View.GONE);
            noRecord.setVisibility(View.VISIBLE);
            reviews.setVisibility(View.GONE);
            return;
        }

        loading.setVisibility(View.VISIBLE);
        noRecord.setVisibility(View.GONE);
        reviews.setVisibility(View.GONE);

        reference.orderByChild("vendorId").equalTo(user.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Review review = d.getValue(Review.class);
                    if (review != null) {
                        data.add(review);
                    }
                }
                if (data.size() > 0) {
                    Collections.reverse(data);
                    //Resume from this
                    adapter.setData(data);
                    reviews.setVisibility(View.VISIBLE);
                    noRecord.setVisibility(View.GONE);
                } else {
                    reviews.setVisibility(View.GONE);
                    noRecord.setVisibility(View.VISIBLE);
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                noRecord.setVisibility(View.VISIBLE);
                reviews.setVisibility(View.GONE);
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
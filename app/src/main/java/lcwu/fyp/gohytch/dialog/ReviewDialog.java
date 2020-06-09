package lcwu.fyp.gohytch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.model.Review;
import lcwu.fyp.gohytch.model.User;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewDialog extends Dialog implements View.OnClickListener {
    private MaterialRatingBar rating;
    private EditText review;
    private float finalRating, averageRating;
    private String strReview, userId, vendorId, bookingid;
    private LinearLayout progress, main;
    private Button postReview, close;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener listener, reviewsListener;

    public ReviewDialog(@NonNull Context context, String uId, String vId, String bId) {
        super(context);
        userId = uId;
        vendorId = vId;
        bookingid = bId;
        averageRating = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.review_dailog);

        finalRating = -1;
        postReview = findViewById(R.id.postReview);
        close = findViewById(R.id.close);
        postReview.setOnClickListener(this);
        close.setOnClickListener(this);

        rating = findViewById(R.id.rating);
        review = findViewById(R.id.review);

        main = findViewById(R.id.main);
        progress = findViewById(R.id.progress);

        rating.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                finalRating = rating;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.postReview: {
                strReview = review.getText().toString();
                Log.e("ReviewDialog", "Review: " + strReview);
                Log.e("ReviewDialog", "Rating: " + finalRating);
                if (finalRating == -1) {
                    return;
                }
                progress.setVisibility(View.VISIBLE);
                main.setVisibility(View.GONE);
                postReview.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                Review review = new Review();
                String dateTime = new SimpleDateFormat("EEE, dd, MMM yyyy hh:mm a").format(new Date());
                review.setDateTime(dateTime);
                review.setRating(finalRating);
                review.setReview(strReview);
                review.setUserId(userId);
                review.setVendorId(vendorId);
                review.setBookingId(bookingid);
                String rId = reference.child("Reviews").push().getKey();
                review.setId(rId);
                reference.child("Reviews").child(review.getId()).setValue(review)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                new CalculateRating().execute();
                                dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dismiss();
                            }
                        });
                break;
            }
            case R.id.close: {
                dismiss();
                break;
            }
        }
    }

    class CalculateRating extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            reviewsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (reviewsListener != null)
                        reference.child("Reviews").orderByChild("vendorId").equalTo(vendorId).removeEventListener(reviewsListener);
                    if (dataSnapshot.exists()) {
                        int count = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Review r = data.getValue(Review.class);
                            if (r != null) {
                                averageRating = (float) (averageRating + r.getRating());
                                count++;
                            }
                        }

                        Log.e("ReviewDialog", "Total Rating: " + averageRating);
                        Log.e("ReviewDialog", "Total Reviews: " + count);
                        averageRating = averageRating / count;
                        Log.e("ReviewDialog", "Average Rating: " + averageRating);
                        averageRating = round(averageRating, 2);
                        Log.e("ReviewDialog", "After Format Average Rating: " + averageRating);
                        getVendor(averageRating);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (reviewsListener != null)
                        reference.child("Reviews").orderByChild("vendorId").equalTo(vendorId).removeEventListener(reviewsListener);
                }
            };

            reference.child("Reviews").orderByChild("vendorId").equalTo(vendorId).addValueEventListener(reviewsListener);
            return null;
        }
    }

    private void getVendor(final float averageRating) {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (listener != null)
                    reference.child("Users").child(vendorId).removeEventListener(listener);
                if (dataSnapshot.exists()) {
                    User vendor = dataSnapshot.getValue(User.class);
                    if (vendor != null) {
                        Log.e("ReviewDialog", "Vendor Name: " + vendor.getName());
                        if (vendor.getType().equals("Renter")) {
                            Log.e("ReviewDialog", "Vendor Rating: " + vendor.getRenter().getRating());
                            vendor.getRenter().setRating(averageRating);
                        } else if (vendor.getType().equals("Driver")) {
                            Log.e("ReviewDialog", "Vendor Rating: " + vendor.getDriver().getRating());
                            vendor.getDriver().setRating(averageRating);
                        }
                        reference.child("Users").child(vendorId).setValue(vendor);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null)
                    reference.child("Users").child(vendorId).removeEventListener(listener);

            }
        };

        reference.child("Users").child(vendorId).addValueEventListener(listener);
    }

    public float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}

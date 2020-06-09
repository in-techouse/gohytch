package lcwu.fyp.gohytch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.model.Review;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    private List<Review> data;
    private Context context;

    public ReviewAdapter(Context c) {
        data = new ArrayList<>();
        context = c;
    }

    public void setData(List<Review> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        final Review review = data.get(position);
        holder.ratingBar.setRating((float) review.getRating());
        holder.review.setText(review.getReview());
        holder.dateTime.setText(review.getDateTime() == null ? "" : review.getDateTime());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        MaterialRatingBar ratingBar;
        TextView review, dateTime;

        ReviewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            review = itemView.findViewById(R.id.review);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }
}

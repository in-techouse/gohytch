package lcwu.fyp.gohytch.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.activities.BookingDetail;
import lcwu.fyp.gohytch.model.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingHolder> {
    private List<Booking> data;
    private Context context;

    public BookingAdapter(Context c) {
        data = new ArrayList<>();
        context = c;
    }

    public void setData(List<Booking> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHolder holder, int position) {
        final Booking b = data.get(position);
        holder.date.setText(b.getBookingTime());
        holder.booking.setText(b.getAddress());
        holder.fare.setText(b.getFare() + " RS.");
        holder.status.setText(b.getStatus());
        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, BookingDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("booking", b);
                it.putExtras(bundle);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BookingHolder extends RecyclerView.ViewHolder {
        TextView booking, date, status, fare;
        CardView mainCard;

        BookingHolder(@NonNull View itemView) {
            super(itemView);
            booking = itemView.findViewById(R.id.booking);
            date = itemView.findViewById(R.id.bookingDate);
            status = itemView.findViewById(R.id.bookingStatus);
            fare = itemView.findViewById(R.id.bookingFare);
            mainCard = itemView.findViewById(R.id.mainCard);
        }
    }
}

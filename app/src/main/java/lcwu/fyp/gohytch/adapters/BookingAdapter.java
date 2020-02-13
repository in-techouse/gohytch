package lcwu.fyp.gohytch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.model.Booking;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingHolder>{
    private List<Booking> data;
    private String type;
    public BookingAdapter(String t)
    {
        data=new ArrayList<>();
        type = t;

    }

    public void setData(List<Booking> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking,parent,false);
                return new BookingHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull BookingHolder holder, int position) {
    final Booking b = data.get(position);
    holder.date.setText(b.getBookingTime());
    holder.booking.setText(b.getAddress());
    holder.fare.setText(b.getFare()+"");
//    holder.fare.setText(b.getFare());
    holder.status.setText(b.getStatus());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BookingHolder extends RecyclerView.ViewHolder {

        TextView booking , date , status , fare;

        public BookingHolder(@NonNull View itemView) {

            super(itemView);
            booking = itemView.findViewById(R.id.booking);
            date = itemView.findViewById(R.id.bookingDate);
            status = itemView.findViewById(R.id.bookingStatus);
            fare = itemView.findViewById(R.id.bookingFare);
//            booking = itemView.findViewById(R.id)
        }
    }
}

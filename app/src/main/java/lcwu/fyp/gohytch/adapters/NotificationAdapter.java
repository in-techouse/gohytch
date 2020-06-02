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
import lcwu.fyp.gohytch.model.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private List<Notification> data;
    private String type;
    private Context context;

    public NotificationAdapter(String t, Context c) {
        data = new ArrayList<>();
        type = t;
        context = c;
    }

    public void setData(List<Notification> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        final Notification n = data.get(position);
        holder.date.setText(n.getDate());
        if (type.equals("None") || type.equals("User")) {
            holder.notification.setText(n.getUserText());
        } else {
            holder.notification.setText(n.getDriverText());
        }
        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, BookingDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("notification", n);
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

    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView notification, date;
        CardView mainCard;

        NotificationHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            date = itemView.findViewById(R.id.date);
            mainCard = itemView.findViewById(R.id.mainCard);
        }
    }
}

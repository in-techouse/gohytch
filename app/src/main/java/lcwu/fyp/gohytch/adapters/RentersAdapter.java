package lcwu.fyp.gohytch.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.activities.SelectRenter;
import lcwu.fyp.gohytch.model.Renter;
import lcwu.fyp.gohytch.model.User;

public class RentersAdapter extends RecyclerView.Adapter<RentersAdapter.RenterHolder> {

    private List<User> renters;
    private Context context;
    private SelectRenter selectRenter;

    public RentersAdapter(List<User> renters, Context c, SelectRenter sr) {
        this.renters = renters;
        context = c;
        selectRenter = sr;
    }

    @NonNull
    @Override
    public RenterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_renter, parent, false);
        return new RenterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RenterHolder holder, final int position) {
        final User user = renters.get(position);
        final Renter renter = user.getRenter();
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SelectCar", "Item clicked on position: " + position);
            }
        });
        if (renter != null) {
            if (renter.getImages() != null && renter.getImages().size() > 0) {
                Glide.with(context).load(renter.getImages().get(0)).into(holder.image);
            } else {
                holder.image.setVisibility(View.GONE);
            }
            holder.carCompany.setText(renter.getCarCompany());
            holder.carModel.setText(renter.getCarModel());
            holder.sittingCapacity.setText("Sitting Capacity: " + renter.getSittingCapacity());
            holder.perHourRent.setText(renter.getPerHourRate() + " RS. per hour");
            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectRenter.showBottomSheet(user);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return renters.size();
    }

    class RenterHolder extends RecyclerView.ViewHolder {
        CardView main;
        ImageView image;
        TextView carCompany, carModel, sittingCapacity, perHourRent;

        RenterHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main);
            image = itemView.findViewById(R.id.image);
            carCompany = itemView.findViewById(R.id.carCompany);
            carModel = itemView.findViewById(R.id.carModel);
            sittingCapacity = itemView.findViewById(R.id.sittingCapacity);
            perHourRent = itemView.findViewById(R.id.perHourRent);
        }
    }
}

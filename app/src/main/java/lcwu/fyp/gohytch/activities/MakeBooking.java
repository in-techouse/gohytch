package lcwu.fyp.gohytch.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class MakeBooking extends AppCompatActivity implements View.OnClickListener {
    private Session session;
    private User user, renter;
    private Helpers helpers;
    private SliderView renterSlider;
    private Button confirm;
    private ProgressBar progress;

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


        renterSlider = findViewById(R.id.renterSlider);
        confirm = findViewById(R.id.confirm);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        confirm.setOnClickListener(this);

        SliderAdapter sliderAdapter = new SliderAdapter(getApplicationContext());
        renterSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        renterSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        renterSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        renterSlider.setIndicatorSelectedColor(Color.WHITE);
        renterSlider.setIndicatorUnselectedColor(Color.GRAY);
        renterSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
        renterSlider.startAutoCycle();
        renterSlider.setSliderAdapter(sliderAdapter);
        sliderAdapter.setImages(renter.getRenter().getImages());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.confirm: {
                break;
            }
        }
    }


    public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
        private List<String> images;

        SliderAdapter(Context context) {
            images = new ArrayList<>();
        }

        @Override
        public SliderAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, parent, false);
            return new SliderAdapter.SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapter.SliderAdapterVH viewHolder, int position) {

            Glide.with(viewHolder.itemView)
                    .load(images.get(position))
                    .into(viewHolder.imageViewBackground);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        void setImages(List<String> images) {
            this.images.clear();
            this.images = images;
            notifyDataSetChanged();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
            View itemView;
            ImageView imageViewBackground;

            SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
                this.itemView = itemView;
            }
        }
    }
}

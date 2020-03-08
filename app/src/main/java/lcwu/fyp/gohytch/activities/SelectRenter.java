package lcwu.fyp.gohytch.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.adapters.RentersAdapter;
import lcwu.fyp.gohytch.model.Renter;
import lcwu.fyp.gohytch.model.User;

public class SelectRenter extends AppCompatActivity implements View.OnClickListener {
    private List<User> renters;
    private RecyclerView list;
    private RentersAdapter adapter;
    private BottomSheetBehavior sheetBehavior;
    private Button closeSheet, book;
    private CircleImageView image;
    private TextView name, phone, email, company, model, sittingCapacity, perHourRent;
    private SliderView renterSlider;
    private SliderAdapter sliderAdapter;
    private LinearLayout buttonLayout;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_renter);
        Intent it = getIntent();
        if (it == null) {
            finish();
            return;
        }
        Bundle bundle = it.getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        renters = (List<User>) bundle.getSerializable("users");
        if (renters == null) {
            finish();
            return;
        }

        Log.e("SelectCar", "Renters List size: " + renters.size());
        list = findViewById(R.id.list);

        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new RentersAdapter(renters, getApplicationContext(), SelectRenter.this);

        list.setAdapter(adapter);

        LinearLayout layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setHideable(true);
        sheetBehavior.setPeekHeight(0);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        closeSheet = findViewById(R.id.closeSheet);
        book = findViewById(R.id.book);
        closeSheet.setOnClickListener(this);
        book.setOnClickListener(this);

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        renterSlider = findViewById(R.id.renterSlider);
        company = findViewById(R.id.company);
        model = findViewById(R.id.model);
        sittingCapacity = findViewById(R.id.sittingCapacity);
        perHourRent = findViewById(R.id.perHourRent);

        buttonLayout = findViewById(R.id.buttonLayout);
        progress = findViewById(R.id.progress);
    }

    public void showBottomSheet(User user) {
        if (user != null) {
            sheetBehavior.setHideable(false);
            sheetBehavior.setPeekHeight(1000);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (user.getRenter() != null) {
                renterSlider.setSliderAdapter(null);
                sliderAdapter = new SliderAdapter(getApplicationContext());
                renterSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                renterSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                renterSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                renterSlider.setIndicatorSelectedColor(Color.WHITE);
                renterSlider.setIndicatorUnselectedColor(Color.GRAY);
                renterSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
                renterSlider.startAutoCycle();
                renterSlider.setSliderAdapter(sliderAdapter);
                Renter renter = user.getRenter();
                sliderAdapter.setImages(renter.getImages());
                company.setText(renter.getCarCompany());
                model.setText(renter.getCarModel());
                sittingCapacity.setText(renter.getSittingCapacity());
                perHourRent.setText(renter.getPerHourRate() + " RS.");
            }
            if (user.getImage() != null && user.getImage().length() > 0) {
                Glide.with(getApplicationContext()).load(user.getImage()).into(image);
            }
            name.setText(user.getName());
            phone.setText(user.getPhoneNumber());
            email.setText(user.getEmail());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.closeSheet: {
                sheetBehavior.setHideable(true);
                sheetBehavior.setPeekHeight(0);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            }
            case R.id.book: {
                buttonLayout.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setHideable(true);
            sheetBehavior.setPeekHeight(0);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else
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

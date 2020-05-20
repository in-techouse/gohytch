package lcwu.fyp.gohytch.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
    private List<User> renters, tempList;
    private RentersAdapter adapter;
    private BottomSheetBehavior sheetBehavior;
    private CircleImageView image;
    private TextView name, phone, email, company, model, sittingCapacity, perHourRent;
    private SliderView renterSlider;
    private Spinner carCompany, carCapacity, rating, carRent;
    private String strCarCompany, strCarCapacity, strRating, strCarRent;
    private boolean isFirst = false;
    private User currentRenter;

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
        tempList = new ArrayList<>();
        tempList.addAll(renters);
        Log.e("SelectCar", "Renters List size: " + renters.size() + " Temp List Size: " + tempList.size());
        RecyclerView list = findViewById(R.id.list);
        carCompany = findViewById(R.id.carCompany);
        carCapacity = findViewById(R.id.carCapacity);
        rating = findViewById(R.id.rating);
        carRent = findViewById(R.id.carRent);

        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new RentersAdapter(renters, getApplicationContext(), SelectRenter.this);

        list.setAdapter(adapter);

        RelativeLayout layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setHideable(true);
        sheetBehavior.setPeekHeight(0);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Button closeSheet = findViewById(R.id.closeSheet);
        Button book = findViewById(R.id.book);
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

        carCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) {
                    strCarCompany = carCompany.getSelectedItem().toString();
                    Log.e("SelectRenter", "Car Company: " + strCarCompany);
                    filterResults();
                } else {
                    Log.e("SelectRenter", "No");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carCapacity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) {
                    strCarCapacity = carCapacity.getSelectedItem().toString();
                    Log.e("SelectRenter", "Car Capacity: " + strCarCapacity);
                    filterResults();
                } else {
                    Log.e("SelectRenter", "No");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) {
                    strRating = rating.getSelectedItem().toString();
                    Log.e("SelectRenter", "Car Rating: " + strRating);
                    filterResults();
                } else {
                    Log.e("SelectRenter", "No");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carRent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) {
                    strCarRent = carRent.getSelectedItem().toString();
                    Log.e("SelectRenter", "Car Rent: " + strCarRent);
                    filterResults();
                } else {
                    Log.e("SelectRenter", "No");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        strCarCompany = strCarCapacity = strRating = strCarRent = "Any";
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isFirst = true;
            }
        }.start();
    }

    private void filterResults() {
        renters.clear();
        if (strCarCompany.equals("Any")) {
            renters.addAll(tempList);
        } else {
            for (User u : tempList) {
                Log.e("SelectRenter", "Car Company: " + strCarCompany + " Current Car Company: " + u.getRenter().getCarCompany());
                if (u.getRenter().getCarCompany().equalsIgnoreCase(strCarCompany))
                    renters.add(u);
            }
        }
        List<User> searchList = new ArrayList<>(renters);
        renters.clear();
        Log.e("SelectRenter", "Car Company Filter Completed, Size is: " + searchList.size());

        if (strCarCapacity.equals("Any")) {
            renters.addAll(searchList);
        } else if (strCarCapacity.equals("6+")) {
            for (User u : searchList) {
                int capacity = Integer.parseInt(u.getRenter().getSittingCapacity());
                if (capacity > 6) {
                    renters.add(u);
                }
            }
        } else {
            for (User u : searchList) {
                Log.e("SelectRenter", "Car Capacity: " + strCarCapacity + " Current Car Capacity: " + u.getRenter().getSittingCapacity());
                if (u.getRenter().getSittingCapacity().equalsIgnoreCase(strCarCapacity))
                    renters.add(u);
            }
        }

        Log.e("SelectRenter", "Car Sitting Capacity Filter Completed, Size is: " + renters.size());

        searchList.clear();

        if (strRating.equals("Any")) {
            searchList.addAll(renters);
        } else if (strRating.equals("4+")) {
            for (User u : renters) {
                double rating = u.getRenter().getRating();
                double currentSelected = 4;
                if (rating > currentSelected) {
                    searchList.add(u);
                }
            }
        } else {
            for (User u : renters) {
                double rating = u.getRenter().getRating();
                double currentSelected = Integer.parseInt(strRating);
                if (rating == currentSelected) {
                    searchList.add(u);
                }
            }
        }

        Log.e("SelectRenter", "Car Rating Filter Completed, Size is: " + searchList.size());

        renters.clear();

        if (strCarRent.equals("Any")) {
            renters.addAll(searchList);
        } else {

            String arr = strCarRent.substring(0, strCarRent.indexOf('+'));
            Log.e("SelectRenter", "Rent Plus Index: " + strCarRent.indexOf('+') + ", Sub String is: " + arr);
            int rent = Integer.parseInt(arr);
            for (User u : searchList) {
                if (u.getRenter().getPerHourRate() > rent)
                    renters.add(u);
            }
        }
        Log.e("SelectRenter", "Car Rent Filter Completed, Size is: " + renters.size());
        adapter.setRenters(renters);
    }

    public void showBottomSheet(User user) {
        if (user != null) {
            sheetBehavior.setHideable(false);
            sheetBehavior.setPeekHeight(1400);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (user.getRenter() != null) {
                renterSlider.setSliderAdapter(null);
                SliderAdapter sliderAdapter = new SliderAdapter(getApplicationContext());
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
            currentRenter = user;
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
                if (currentRenter != null) {
                    Intent it = new Intent(SelectRenter.this, MakeBooking.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("renter", currentRenter);
                    it.putExtras(bundle);
                    startActivity(it);
                }
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

package lcwu.fyp.gohytch.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.Booking;
import lcwu.fyp.gohytch.model.User;

public class MakeBooking extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MakeBooking";
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bookings");
    private TextView company, model, sittingCapacity, perHourRent, location, date, time, endTime, endDate, estimatedFare;
    private Session session;
    private User user, renter;
    private Helpers helpers;
    private SliderView renterSlider;
    private Button confirm;
    private ProgressBar progress;
    private RelativeLayout selectLocation, selectDate, selectTime, selectEndTime, selectEndDate;
    private Booking booking;
    private String strLocation, strDate, strTime, strEndTime, strEndDate;
    private CheckBox withDriver;

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
        booking = new Booking();
        booking.setType("Renter");

        renterSlider = findViewById(R.id.renterSlider);
        confirm = findViewById(R.id.confirm);
        selectLocation = findViewById(R.id.selectLocation);
        selectDate = findViewById(R.id.selectDate);
        selectTime = findViewById(R.id.selectTime);
        selectEndTime = findViewById(R.id.selectEndTime);
        selectEndDate = findViewById(R.id.selectEndDate);
        withDriver = findViewById(R.id.withDriver);

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        confirm.setOnClickListener(this);
        selectLocation.setOnClickListener(this);
        selectDate.setOnClickListener(this);
        selectTime.setOnClickListener(this);
        selectEndTime.setOnClickListener(this);
        selectEndDate.setOnClickListener(this);

        company = findViewById(R.id.company);
        model = findViewById(R.id.model);
        sittingCapacity = findViewById(R.id.sittingCapacity);
        perHourRent = findViewById(R.id.perHourRent);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        endTime = findViewById(R.id.endTime);
        endDate = findViewById(R.id.endDate);
        estimatedFare = findViewById(R.id.estimatedFare);

        company.setText(renter.getRenter().getCarCompany());
        model.setText(renter.getRenter().getCarModel());
        sittingCapacity.setText(renter.getRenter().getSittingCapacity() + " Persons");
        perHourRent.setText(renter.getRenter().getPerHourRate() + " RS.");

        SliderAdapter sliderAdapter = new SliderAdapter();
        renterSlider.setIndicatorAnimation(IndicatorAnimations.WORM);
        renterSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        renterSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        renterSlider.setIndicatorSelectedColor(Color.WHITE);
        renterSlider.setIndicatorUnselectedColor(Color.GRAY);
        renterSlider.setScrollTimeInSec(4);
        renterSlider.startAutoCycle();
        renterSlider.setSliderAdapter(sliderAdapter);
        sliderAdapter.setImages(renter.getRenter().getImages());

        strLocation = strDate = strTime = strEndTime = "";
    }

    private boolean isValid() {
        boolean flag = true;

        strLocation = location.getText().toString();
        strDate = date.getText().toString();
        strEndDate = endDate.getText().toString();
        strTime = time.getText().toString();
        strEndTime = endTime.getText().toString();

        String error = "";
        if (strLocation.length() < 1 || strLocation.equals("Select Pickup Location")) {
            error = error + "*Select your pickup location.\n";
            flag = false;
        }

        if (strDate.equals("Select Pickup Date")) {
            error = error + "*Select your pickup date.\n";
            flag = false;
        }

        if (strEndDate.equals("Select End Date")) {
            error = error + "*Select your end date.\n";
            flag = false;
        }

        if (strTime.equals("Select Pickup Time")) {
            error = error + "*Select your pickup time.\n";
            flag = false;
        }

        if (strEndTime.equals("Select End Time")) {
            error = error + "*Select your end time.\n";
            flag = false;
        }

        if (flag) {
            try {
                String strFullDateTime = strDate + " " + strTime;
                String strFullEndDateTime = strEndDate + " " + strEndTime;
                Log.e(TAG, "Date: " + strFullDateTime);
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd, MMM-yyyy hh:mm aa");
                Date date = format.parse(strFullDateTime);
                Date current = new Date();
                if (date != null) {
                    Log.e(TAG, "Selected Date: " + date.toString());
                    Log.e(TAG, "Current Date: " + current.toString());
                    if (date.after(current)) {
                        Log.e(TAG, "Current is after Selected Date");
                    } else {
                        error = error + "*You can't select a time from the past. Please select the correct date and time for the booking.\n";
                        flag = false;
                    }
                } else {
                    error = error + "*You can't select a time from the past. Please select the correct date and time for the booking.\n";
                    flag = false;
                }

                Date endDate = format.parse(strFullEndDateTime);

                if (endDate != null) {
                    Log.e(TAG, "Selected End Date: " + endDate.toString());
                    Log.e(TAG, "Current Date: " + current.toString());
                    if (endDate.after(current)) {
                        Log.e(TAG, "Current is after Selected End Date");
                    } else {
                        error = error + "*You can't select a time from the past. Please select the correct end date and time for the booking.\n";
                        flag = false;
                    }
                } else {
                    error = error + "*You can't select a time from the past. Please select the correct end date and time for the booking.\n";
                    flag = false;
                }

                if (date != null && endDate != null) {
                    Log.e(TAG, "Selected End Date: " + endDate.toString());
                    Log.e(TAG, "Start Date: " + date.toString());
                    if (endDate.after(date)) {
                        Log.e(TAG, "Date is after Selected End Date");
                    } else {
                        error = error + "*Please select the correct end date and time for the booking.\n";
                        flag = false;
                    }
                } else {
                    error = error + "*Please select the correct end date and time for the booking.\n";
                    flag = false;
                }

                if (flag) {
                    double diff = endDate.getTime() - date.getTime();
                    double seconds = diff / 1000;
                    double minutes = seconds / 60;
                    double hours = minutes / 60;
                    Log.e(TAG, "Time difference in hours is: " + hours);
                    double fare = renter.getRenter().getPerHourRate() * hours;
                    estimatedFare.setText(fare + " RS.");
                }

            } catch (Exception e) {
                Log.e(TAG, "Date parsing exception: " + e.getMessage());
                error = error + "*You can't select a time from the past. Please select the correct date and time for the booking.\n";
                flag = false;
            }
        }

        if (error.length() > 0) {
            helpers.showError(MakeBooking.this, "ERROR", error);
        }

        return flag;
    }

    private void calculateFare() {
        boolean flag = true;

        strDate = date.getText().toString();
        strEndDate = endDate.getText().toString();
        strTime = time.getText().toString();
        strEndTime = endTime.getText().toString();
        if (strDate.equals("Select Pickup Date")) {
            flag = false;
        }

        if (strEndDate.equals("Select End Date")) {
            flag = false;
        }

        if (strTime.equals("Select Pickup Time")) {
            flag = false;
        }

        if (strEndTime.equals("Select End Time")) {
            flag = false;
        }

        if (flag) {
            try {
                String strFullDateTime = strDate + " " + strTime;
                String strFullEndDateTime = strEndDate + " " + strEndTime;
                Log.e(TAG, "Date: " + strFullDateTime);
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd, MMM-yyyy hh:mm aa");
                Date date = format.parse(strFullDateTime);
                Date current = new Date();
                if (date != null) {
                    Log.e(TAG, "Selected Date: " + date.toString());
                    Log.e(TAG, "Current Date: " + current.toString());
                    if (date.after(current)) {
                        Log.e(TAG, "Current is after Selected Date");
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }

                Date endDate = format.parse(strFullEndDateTime);

                if (endDate != null) {
                    Log.e(TAG, "Selected End Date: " + endDate.toString());
                    Log.e(TAG, "Current Date: " + current.toString());
                    if (endDate.after(current)) {
                        Log.e(TAG, "Current is after Selected End Date");
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }

                if (date != null && endDate != null) {
                    Log.e(TAG, "Selected End Date: " + endDate.toString());
                    Log.e(TAG, "Start Date: " + date.toString());
                    if (endDate.after(date)) {
                        Log.e(TAG, "Date is after Selected End Date");
                    } else {
                        flag = false;
                    }
                }

                if (flag) {
                    double diff = endDate.getTime() - date.getTime();
                    double seconds = diff / 1000;
                    double minutes = seconds / 60;
                    double hours = minutes / 60;
                    Log.e(TAG, "Time difference in hours is: " + hours);
                    double fare = renter.getRenter().getPerHourRate() * hours;
                    estimatedFare.setText(fare + " RS.");
                }

            } catch (Exception e) {
                Log.e(TAG, "Date parsing exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.confirm: {
                if (isValid()) {
                    progress.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    String bId = reference.push().getKey();
                    booking.setId(bId);
                    booking.setBookingTime(strDate + " " + strTime);
                    booking.setStartTime(strDate + " " + strTime);
                    booking.setEndTime(strEndDate + " " + strEndTime);
                    booking.setUserId(user.getId());
                    booking.setDriverId(renter.getId());
                    booking.setStatus("New");
                    booking.setFare(0);
                    booking.setWithDriver(withDriver.isChecked());
                    reference.child(booking.getId()).setValue(booking)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    helpers.sendNotification(booking, user, renter, "You submit a request to " + renter.getName() + ". You will be notified as soon as the renter respond to your request.", "You got a new booking request from " + user.getName());
                                    progress.setVisibility(View.GONE);
                                    confirm.setVisibility(View.VISIBLE);
                                    helpers.showSuccess(MakeBooking.this, "BOOKING REQUEST!", "Your request has been sent to the rider. You will be notified as soon as the renter accepted your request.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progress.setVisibility(View.GONE);
                                    confirm.setVisibility(View.VISIBLE);
                                    helpers.showError(MakeBooking.this, "ERROR!", "Something went wrong.\nPlease try again later.");
                                }
                            });
                }
                break;
            }
            case R.id.selectDate: {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(MakeBooking.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                try {
                                    month = month + 1;
                                    Log.e(TAG, "onDateSet: mm/dd/yy: " + month + "/" + dayOfMonth + "/" + year);
                                    String strDate = month + "/" + dayOfMonth + "/" + year;
                                    date.setText(strDate);
                                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                                    Date d = format.parse(strDate);
                                    strDate = new SimpleDateFormat("EEE, dd, MMM-yyyy").format(d);
                                    date.setText(strDate);
                                    calculateFare();
                                } catch (Exception e) {
                                    Log.e(TAG, "Date parsing Exception: " + e.getMessage());
                                }
                            }
                        }, year, month, day);
                dialog.show();
                break;
            }
            case R.id.selectEndDate: {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(MakeBooking.this,
                        android.R.style.Theme_DeviceDefault_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                try {
                                    month = month + 1;
                                    Log.e(TAG, "onDateSet: mm/dd/yy: " + month + "/" + dayOfMonth + "/" + year);
                                    String strDate = month + "/" + dayOfMonth + "/" + year;
                                    endDate.setText(strDate);
                                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                                    Date d = format.parse(strDate);
                                    strDate = new SimpleDateFormat("EEE, dd, MMM-yyyy").format(d);
                                    endDate.setText(strDate);
                                    calculateFare();
                                } catch (Exception e) {
                                    Log.e(TAG, "Date parsing Exception: " + e.getMessage());
                                }
                            }
                        }, year, month, day);
                dialog.show();
                break;
            }
            case R.id.selectTime: {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(MakeBooking.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            Log.e(TAG, "onTimeSet: hh:mm: " + hourOfDay + ":" + minute);
                            String strTime = hourOfDay + ":" + minute;
                            time.setText(strTime);
                            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                            Date d = formatter.parse(strTime);
                            Log.e(TAG, "Time Parsed: " + d.toString());
                            strTime = new SimpleDateFormat("hh:mm aa").format(d);
                            Log.e(TAG, "Time Formatted: " + strTime);
                            time.setText(strTime);
                            calculateFare();
                        } catch (Exception e) {
                            Log.e(TAG, "Time parsing exception: " + e.getMessage());
                        }
                    }
                }, hour, minute, false);
                dialog.show();
                break;
            }
            case R.id.selectEndTime: {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(MakeBooking.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            Log.e(TAG, "onTimeSet: hh:mm: " + hourOfDay + ":" + minute);
                            String strTime = hourOfDay + ":" + minute;
                            endTime.setText(strTime);
                            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                            Date d = formatter.parse(strTime);
                            Log.e(TAG, "Time Parsed: " + d.toString());
                            strTime = new SimpleDateFormat("hh:mm aa").format(d);
                            Log.e(TAG, "Time Formatted: " + strTime);
                            endTime.setText(strTime);
                            calculateFare();
                        } catch (Exception e) {
                            Log.e(TAG, "Time parsing exception: " + e.getMessage());
                        }
                    }
                }, hour, minute, false);
                dialog.show();
                break;
            }
            case R.id.selectLocation: {
                Intent it = new Intent(MakeBooking.this, SelectLocation.class);
                startActivityForResult(it, 30);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 30 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Booking b = (Booking) bundle.getSerializable("result");
                    if (b != null) {
                        Log.e(TAG, "Location Received: " + b.getAddress());
                        booking.setLat(b.getLat());
                        booking.setLng(b.getLng());
                        booking.setAddress(b.getAddress());
                        location.setText(booking.getAddress());
                    }
                }
            }
        }
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


    class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
        private List<String> images;

        SliderAdapter() {
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

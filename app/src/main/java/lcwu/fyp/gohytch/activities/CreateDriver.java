package lcwu.fyp.gohytch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class CreateDriver extends AppCompatActivity implements View.OnClickListener {
    Button btnSave;
    EditText edtLicenseNumber, edtExpertise;
    String strLicenseNumber;
    ProgressBar SaveProgress;
    Helpers helpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_driver);


        btnSave = findViewById(R.id.btnSave);
        edtLicenseNumber = findViewById(R.id.LicenseNumber);
        edtExpertise = findViewById(R.id.Expertise);
        SaveProgress = findViewById(R.id.SaveProgress);
        btnSave.setOnClickListener(this);
        helpers = new Helpers();


        SliderView sliderView = findViewById(R.id.imageSlider);

        SliderAdapter adapter = new SliderAdapter(CreateDriver.this);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btnSave:{
                Log.e("Driver","Button Clicked");
                boolean isConn=helpers.isConnected(CreateDriver.this);
                if(!isConn){
                    helpers.showError(CreateDriver.this,"Error","No Internet Connection.Please Check Your Connection and try again later");
                    return;
                }
                boolean flag=isValid();
                if (flag){
                    btnSave.setVisibility(View.GONE);
                    final User u= new User();
                    final Session session=new Session(CreateDriver.this);
                }
            }
        }

    }

    private boolean isValid() {
        boolean flag=true;
        strLicenseNumber=edtLicenseNumber.getText().toString();
        Log.e("Renter","LicnseNumber:" + strLicenseNumber);
        if (strLicenseNumber.length()<4){
            edtLicenseNumber.setError("Enter a valid License Number");
            flag=false;
        }else{
            edtLicenseNumber.setError(null);
        }
        return flag;
    }
    public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        private Context context;

        public SliderAdapter(Context context) {
            this.context = context;
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
            return new SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

            switch (position) {
                case 0:
                    Glide.with(viewHolder.itemView)
                            .load("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                            .into(viewHolder.imageViewBackground);
                    break;
                case 1:
                    Glide.with(viewHolder.itemView)
                            .load("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260")
                            .into(viewHolder.imageViewBackground);
                    break;
                case 2:
                    Glide.with(viewHolder.itemView)
                            .load("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                            .into(viewHolder.imageViewBackground);
                    break;
                default:
                    Glide.with(viewHolder.itemView)
                            .load("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                            .into(viewHolder.imageViewBackground);
                    break;

            }

        }

        @Override
        public int getCount() {
            //slider view count could be dynamic size
            return 4;
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
            View itemView;
            ImageView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
                this.itemView = itemView;
            }
        }
    }
}

package lcwu.fyp.gohytch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.rbddevs.splashy.Splashy;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setSplashy();
    }
   void setSplashy() {
       new Splashy(this)         // For JAVA : new Splashy(this)
                .setLogo(R.drawable.logo)
                .setTitle("Splashy")
                .setTitleColor("#FFFFFF")
                .setSubTitle("Splash screen made easy")
                .setProgressColor(R.color.white)
              //  .setBackgroundResource("#000000")
                .setFullScreen(true)
                .setTime(5000)
               .showProgress(true)
               .setAnimation(Splashy.Animation.SLIDE_IN_TOP_BOTTOM, 1000)
                .show();
    }

}

package lcwu.fyp.gohytch.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.rbddevs.splashy.Splashy;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Session;


public class SplashActivity extends AppCompatActivity{

    Splashy splashy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Splashy.OnComplete() {
            @Override
            public void onComplete() {
                Log.e("Splashy", "OnComplete 1");
                Session session=new Session(SplashActivity.this);
                if(session.getSession()==null){
                    Intent it=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(it);
                    finish();
                }
                else{
                    Intent it=new Intent(SplashActivity.this,DashboardActivity.class);
                    startActivity(it);
                    finish();
                }


            }
        }.onComplete();
        setSplashy();
    }

   void setSplashy() {
       new Splashy(this)         // For JAVA : new Splashy(this)
                .setLogo(R.drawable.logo)
                .setTitle("GOHYTCH")
                .setTitleColor("#0000FF")
                .setSubTitle("")
                .setProgressColor(R.color.white)
                .setFullScreen(true)
                .setTime(2000)
               .showProgress(true)
               .setAnimation(Splashy.Animation.SLIDE_IN_TOP_BOTTOM, 1000)
               .show();
    }
}

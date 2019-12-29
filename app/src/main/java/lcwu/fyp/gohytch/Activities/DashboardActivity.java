package lcwu.fyp.gohytch.Activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.director.Helpers;
import lcwu.fyp.gohytch.director.Session;
import lcwu.fyp.gohytch.model.User;

public class DashboardActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private Helpers helpers;
    private User user;
    private Session session;

    private CircleImageView Profile_Image;
    private TextView profile_Email;
    private TextView profile_Name;
    private TextView profile_Phone;



    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        helpers=new Helpers();
        session=new Session(DashboardActivity.this);
        user=session.getSession();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header= navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        profile_Phone=header.findViewById(R.id.profile_Phone);
        profile_Name=header.findViewById(R.id.profile_Name);
        profile_Email=header.findViewById(R.id.profile_Email);
        Profile_Image=header.findViewById(R.id.profile_image);

        profile_Name.setText(user.getName());
        profile_Email.setText(user.getEmail());
        profile_Phone.setText(user.getPhoneNumber());
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        switch (id){
            case(R.id.nav_Home):{
                break;
            }
            case (R.id.nav_Booking):{
                break;
            }
            case (R.id.nav_Notification):{
                break;
            }
            case(R.id.nav_Logout):{
                break;
            }

        }
        drawer.closeDrawer(GravityCompat.START);
        return false;

    }
}

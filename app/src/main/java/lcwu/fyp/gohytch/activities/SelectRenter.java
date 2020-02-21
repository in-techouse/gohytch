package lcwu.fyp.gohytch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.List;

import lcwu.fyp.gohytch.R;
import lcwu.fyp.gohytch.adapters.RentersAdapter;
import lcwu.fyp.gohytch.model.User;

public class SelectRenter extends AppCompatActivity {
    private List<User> renters;
    private RecyclerView list;
    private RentersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_renter);
        Intent it = getIntent();
        if( it == null){
            finish();
            return;
        }
        Bundle bundle = it.getExtras();
        if(bundle == null){
            finish();
            return;
        }

        renters = (List<User>) bundle.getSerializable("users");
        if(renters == null){
            finish();
            return;
        }

        Log.e("SelectCar", "Renters List size: " + renters.size());
        list = findViewById(R.id.list);

        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new RentersAdapter(renters, getApplicationContext());

        list.setAdapter(adapter);

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
}

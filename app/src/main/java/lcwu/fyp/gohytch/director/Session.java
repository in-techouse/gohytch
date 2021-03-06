package lcwu.fyp.gohytch.director;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import lcwu.fyp.gohytch.model.User;

public class Session {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public Session(Context c){
        context = c;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void setSession(User user){
        Log.e("UserSession", "Id: " + user.getId());
        Log.e("UserSession", "Email: " + user.getEmail());
        Log.e("UserSession", "Name: " + user.getName());
        Log.e("UserSession", "Phone Number: " + user.getPhoneNumber());
        Gson gson = new Gson();
        String value = gson.toJson(user);
        editor.putString("user", value);
        editor.commit();
    }

    public User getSession(){
        String str = preferences.getString("user", "*");
        if(str.equals("*"))
            return null;
        Gson gson = new Gson();
        User u = gson.fromJson(str, User.class);
        return  u;
    }

    public void destroySession(){
        editor.remove("user");
        editor.apply();
    }




}

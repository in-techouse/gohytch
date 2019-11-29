package lcwu.fyp.gohytch.director;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helpers {
    public boolean isConnected(Context c) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
        return  connected;
    }
}

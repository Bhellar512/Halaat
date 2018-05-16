package helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import helpers.UIHelper;


/**
 * Created by khan_muhammad on 3/27/2017.
 */

public class InternetHelper {

    public static boolean CheckInternetConectivityandShowToast(Context activity) {

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null) {
            return true;
        } else {
            // text.setText("Look your not online");

            UIHelper.showLongToastInCenter(activity, "Internet is not connected");
            return false;
        }


    }

}

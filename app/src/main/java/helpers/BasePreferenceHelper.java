package helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class BasePreferenceHelper extends PreferenceHelper  {

    private Context context;

    protected static final String KEY_LOGIN_STATUS = "islogin";

    private static final String FILENAME = "preferences";

    protected static final String Firebase_TOKEN = "Firebasetoken";

    protected static final String contacts = "contacts";


    protected static final String UserId = "UserId";


    public BasePreferenceHelper(Context c) {
        this.context = c;
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(FILENAME, Activity.MODE_PRIVATE);
    }

    public void setLoginStatus( boolean isLogin ) {
        putBooleanPreference( context, FILENAME, KEY_LOGIN_STATUS, isLogin );
    }

    public boolean isLogin() {
        return getBooleanPreference(context, FILENAME, KEY_LOGIN_STATUS);
    }


    public String getFirebase_TOKEN() {
        return getStringPreference(context, FILENAME, Firebase_TOKEN);
    }

    public void setFirebase_TOKEN(String _token) {
        putStringPreference(context, FILENAME, Firebase_TOKEN, _token);
    }

    public String getSavedContacts() {
        return getStringPreference(context, FILENAME, contacts);
    }

    public void setSavedContacts(String contact) {
        putStringPreference(context, FILENAME, contacts, contact);
    }

    public String getUserId() {
        return getStringPreference(context, FILENAME, UserId);
    }

    public void setUserId(String userId) {
        putStringPreference(context, FILENAME, UserId, userId);
    }


}

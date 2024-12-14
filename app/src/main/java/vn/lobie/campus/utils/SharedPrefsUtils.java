package vn.lobie.campus.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtils {
    private static final String PREF_NAME = "CampusExpensePrefs";
    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_PASSWORD = "saved_password";
    
    private final SharedPreferences prefs;
    
    public SharedPrefsUtils(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveCredentials(String email, String password) {
        prefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .apply();
    }
    
    public void clearCredentials() {
        prefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .apply();
    }
    
    public String getSavedEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }
    
    public String getSavedPassword() {
        return prefs.getString(KEY_PASSWORD, "");
    }
}

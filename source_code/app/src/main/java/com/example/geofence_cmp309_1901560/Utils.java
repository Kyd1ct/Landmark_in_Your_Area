package com.example.geofence_cmp309_1901560;

import android.content.Context;
import android.content.pm.PackageManager;

public class Utils {

    // Debug tag
    public static final String TAG = "Geofence_Debug";

    // Method checking all permissions
    public static boolean checkAllPermissions(Context c, String[] permissions){
        Boolean result = false;
        for (String p:permissions) {
            result &= (c.checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }
}

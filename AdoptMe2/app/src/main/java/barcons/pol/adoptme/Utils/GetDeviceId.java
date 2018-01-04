package barcons.pol.adoptme.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Marta on 04/01/2018.
 */

public class GetDeviceId {
    private Context mContext;

    public GetDeviceId(Context mContext) {
        this.mContext = mContext;
    }


    /*Anirà al first time activity tmb*/
    public boolean CheckReadPermission(){
        //Asking permissions to obtain the device id
        return ActivityCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }


    public String GetId(TelephonyManager telephonyManager) {

        if (ActivityCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            if (telephonyManager == null) {
                throw new AssertionError();}
            else {
                String deviceId = telephonyManager.getDeviceId();
                Log.e("mcoll", "value:"+deviceId);
                return deviceId;
            }
        }else return null;

    }
}

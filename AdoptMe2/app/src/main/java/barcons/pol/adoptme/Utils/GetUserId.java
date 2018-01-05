package barcons.pol.adoptme.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import barcons.pol.adoptme.CreaActivity;
import barcons.pol.adoptme.R;

/**
 * Created by Marta on 05/01/2018.
 */

public class GetUserId {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);

    private String mDeviceid;
    private Context mContext;
    private View mView;
    private CheckBox mSavecheck;
    private String mAdkey;


    public GetUserId(Context mContext,String mDeviceid,CheckBox mSavecheck,String mAdkey) {
        this.mContext=mContext;
        this.mDeviceid = mDeviceid;
        this.mSavecheck = mSavecheck;
        this.mAdkey = mAdkey;
    }
    public GetUserId(Context mContext,String mDeviceid,View mView) {
        this.mContext=mContext;
        this.mDeviceid = mDeviceid;
        this.mView=mView;
    }

    public void GetUser(final int requestcode) {

        final String TAG = "GettingUserId";

        Query query = UsersRef.orderByChild("uid").equalTo(mDeviceid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String id = null;

                    Log.e("mcoll", "User exists");
                    for (DataSnapshot uid : dataSnapshot.getChildren()) {
                        id = uid.getKey();
                    }
                    switch (requestcode) {
                        case 0:
                            createAd(mView, id);
                            break;
                        case 1 :
                            saveAd(mSavecheck,id,mAdkey);
                            break;
                    }

                    Log.e("mcoll", "value:" + id);

                } else {
                    if(requestcode==0) {
                        Log.e(TAG, "User not found");
                        Snackbar.make(mView, R.string.usrnotfound, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //S'hauria de canviar el main a coordinatorlayout i passar-ho per referència per tenir un Snackbar (mirar creaActivity).
                    }else Toast.makeText(mContext,R.string.usrnotfound,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database Error");
            }
        });
    }

    private void createAd(View view, String user) { //anar al layout i assignar aquest metode a un botó per a iniciar la infoactivity
        Intent intent = new Intent(mContext, CreaActivity.class);
        intent.putExtra("user", user);
        mContext.startActivity(intent);
    }
    private void saveAd(CheckBox savechk, String user, final String adkey){
        final DatabaseReference savedRef = UsersRef.child(user).child("saved");
        if (savechk.isChecked()){
            HashMap<String, Object> AdSaved= new HashMap<>();
            AdSaved.put(adkey, true);
            savedRef.updateChildren(AdSaved);
        } else {
            savedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(adkey)){
                        savedRef.child(adkey).removeValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("GetUserId/SaveAd","DatabaseError");
                }
            });
        }
    }


}

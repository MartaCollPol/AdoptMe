package barcons.pol.adoptme.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import barcons.pol.adoptme.CreaActivity;
import barcons.pol.adoptme.InfoActivity;
import barcons.pol.adoptme.Objectes.Ad;
import barcons.pol.adoptme.R;

/**
 * Created by Marta on 05/01/2018.
 */

public class GetUserId {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);
    private DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);

    private FirebaseRecyclerAdapter<Ad, ImgViewHolder> mAdapter;

    private String mDeviceid;
    private Context mContext;
    private View mView;
    private CheckBox mSavecheck;
    private String mAdkey;
    private RecyclerView rcvListImg;
    private HashMap<String, Object> mSaved;
    private Button mDelete;
    private Button mEdit;
    private String mUserToCompare;

    private GetUserId(Button mDelete, Button mEdit, String mUserToCompare,String mDeviceid,String mAdkey,Context mContext){
        this.mDelete = mDelete;
        this.mEdit = mEdit;
        this.mUserToCompare= mUserToCompare;
        this.mDeviceid=mDeviceid;
        this.mAdkey = mAdkey;
        this.mContext=mContext;
    }
    private GetUserId(CheckBox mSavecheck, HashMap<String, Object> mSaved, String mDeviceid){
        this.mSavecheck=mSavecheck;
        this.mSaved=mSaved;
        this.mDeviceid=mDeviceid;
    }


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
    public GetUserId(Context mContext, String mDeviceid, RecyclerView rcvListImg){
        this.mContext=mContext;
        this.mDeviceid=mDeviceid;
        this.rcvListImg=rcvListImg;
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
                        case 1:
                            saveAd(mSavecheck, id, mAdkey);
                            break;
                        case 2:
                            assert id != null;
                            ShowAds(AdsRef.orderByChild("saved/" + id).equalTo(true));
                            break;
                        case 3:
                            ShowAds(AdsRef.orderByChild("user").equalTo(id));
                            break;
                        case 4:
                            NeedsACheck(id,mSavecheck,mSaved);
                            break;
                        case 5:
                            EditDelete(id,mUserToCompare,mDelete,mEdit,mAdkey);
                    }

                } else {
                    if(requestcode==0) {
                        Log.e(TAG, "User not found");
                        Snackbar.make(mView, R.string.usrnotfound, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //TODO: S'hauria de canviar el main a coordinatorlayout i passar-ho per referència per tenir un Snackbar (mirar creaActivity).
                    }else Log.e("GetUserId","User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database Error");
            }
        });
    }

    private void EditDelete(String user, final String userToCompare, Button btn_delete, Button btn_edit, final String adkey){
        if(!user.equals(userToCompare)){
            btn_edit.setVisibility(View.INVISIBLE);
            btn_delete.setVisibility(View.INVISIBLE);
        }else{
            btn_edit.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorageReference StorageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference fileRef = StorageRef.child(adkey);
                    fileRef.delete();
                    AdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(adkey)){
                                AdsRef.child(adkey).removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("GetUserId/Delete","DatabaseError");
                        }
                    });
                }
            });

        }
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreaActivity.class);
                intent.putExtra("user", userToCompare);
                intent.putExtra("ad",adkey);
                mContext.startActivity(intent);
            }
        });
    }

    private void NeedsACheck(final String user,final CheckBox check,HashMap<String, Object> savedAds) {
        if(savedAds!=null){
            for ( String key : savedAds.keySet() ) {
                if (key.equals(user)){
                    check.setChecked(true);
                }else check.setChecked(false);
            }
        }else check.setChecked(false);
    }

    private void createAd(View view, String user) { //anar al layout i assignar aquest metode a un botó per a iniciar la infoactivity
        String ad = "Void";
        Intent intent = new Intent(mContext, CreaActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("ad",ad);
        mContext.startActivity(intent);
    }
    private void saveAd(CheckBox savechk, final String user, final String adkey){
        final DatabaseReference savedRef = AdsRef.child(adkey).child("saved");
        if (savechk.isChecked()){
            savedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(user)){
                        HashMap<String, Object> AdSaved= new HashMap<>();
                        AdSaved.put(user, true);
                        savedRef.updateChildren(AdSaved);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("GetUserId/SaveAd","DatabaseError");
                }
            });
        } else {
            savedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user)){
                        savedRef.child(user).removeValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("GetUserId/SaveAd","DatabaseError");
                }
            });
        }
    }
    public void ShowAds(Query query){
        mAdapter.cleanup();
        ClearedFirst(query);
    }

    public void ClearedFirst(Query query){
        mAdapter = new FirebaseRecyclerAdapter<Ad, ImgViewHolder>(
                Ad.class, R.layout.anunci, ImgViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final ImgViewHolder viewHolder, Ad model, final int position) {
                GetUserId NeedsCheck = new GetUserId(viewHolder.saveCheck,model.saved,mDeviceid);
                NeedsCheck.GetUser(4);

                GetUserId EditAndDelete = new GetUserId(viewHolder.btn_delete,viewHolder.btn_edit,model.user,mDeviceid,getRef(position).getKey(), mContext);
                EditAndDelete.GetUser(5);
                viewHolder.dateView.setText(model.data);
                viewHolder.nameView.setText(model.sexe);
                Glide.with(mContext)
                        .load(model.url)
                        .centerCrop()
                        .error(R.drawable.common_google_signin_btn_icon_dark)
                        .into(viewHolder.imageView);

                viewHolder.saveCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetUserId SaveAd = new GetUserId(mContext,mDeviceid,viewHolder.saveCheck,getRef(position).getKey());
                        SaveAd.GetUser(1);
                        notifyDataSetChanged();
                    }
                });
                viewHolder.btn_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showinfo(v,getRef(position).getKey());
                    }
                });
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(false);
        rcvListImg.setHasFixedSize(false);
        rcvListImg.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, layoutManager.getOrientation());
        Drawable Divider = ContextCompat.getDrawable(mContext,R.drawable.divider_sample);

        itemDecoration.setDrawable(Divider);
        rcvListImg.addItemDecoration(itemDecoration);

        rcvListImg.setAdapter(mAdapter);

    }

    private void showinfo(View view,String adname){
        Intent intent = new Intent(mContext, InfoActivity.class);
        intent.putExtra("ad",adname);
        mContext.startActivity(intent);
    }


}

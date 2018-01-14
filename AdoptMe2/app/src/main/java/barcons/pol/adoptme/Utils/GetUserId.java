package barcons.pol.adoptme.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
                        case 1 :
                            saveAd(mSavecheck,id,mAdkey);
                            break;
                        case 2:
                            assert id != null;
                            ShowAds(AdsRef.orderByChild("saved/"+id).equalTo(true));
                            break;
                        case 3:
                            ShowAds(AdsRef.orderByChild("user").equalTo(id));
                            break;
                    }

                    Log.e("mcoll", "value:" + id);

                } else {
                    if(requestcode==0) {
                        Log.e(TAG, "User not found");
                        Snackbar.make(mView, R.string.usrnotfound, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //TODO: S'hauria de canviar el main a coordinatorlayout i passar-ho per referència per tenir un Snackbar (mirar creaActivity).
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
    private void saveAd(CheckBox savechk, final String user, final String adkey){
        final DatabaseReference savedRef = AdsRef.child(adkey).child("saved");
        if (savechk.isChecked()){
            HashMap<String, Object> AdSaved= new HashMap<>();
            AdSaved.put(user, true);
            savedRef.updateChildren(AdSaved);
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

    //constructor : device, mContext
    public void ShowAds(Query query){
        mAdapter = new FirebaseRecyclerAdapter<Ad, ImgViewHolder>(
                Ad.class, R.layout.anunci, ImgViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final ImgViewHolder viewHolder, Ad model, final int position) {
                viewHolder.nameView.setText(model.sexe);
                Picasso.with(mContext)
                        .load(model.url)
                        .error(R.drawable.common_google_signin_btn_icon_dark)
                        .into(viewHolder.imageView);

                viewHolder.saveCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetUserId SaveAd = new GetUserId(mContext,mDeviceid,viewHolder.saveCheck,getRef(position).getKey());
                        SaveAd.GetUser(1);
                    }
                });
                viewHolder.btn_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showinfo(v,getRef(position).getKey());
                    }
                });
                //TODO: Afegir comprovar Current user -> delete i edit setVisibility(), activar les funcions editar i borrar
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setReverseLayout(false);
        rcvListImg.setHasFixedSize(false);
        rcvListImg.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, layoutManager.getOrientation());

        //TODO: Afegir un divisor "gris" com el de la app Reddit entre els anuncis, i un marge al final : https://www.bignerdranch.com/blog/a-view-divided-adding-dividers-to-your-recyclerview-with-itemdecoration/

        rcvListImg.addItemDecoration(itemDecoration);
        rcvListImg.setAdapter(mAdapter);



    }




    private void showinfo(View view,String adname){
        Intent intent = new Intent(mContext, InfoActivity.class);
        intent.putExtra("ad",adname);
        mContext.startActivity(intent);
    }


}

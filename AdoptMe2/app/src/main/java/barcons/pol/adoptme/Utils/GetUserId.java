package barcons.pol.adoptme.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
    DatabaseReference GeoRef = database.getReference("geofire");
    GeoFire geoFire = new GeoFire(GeoRef);

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
    private int mDistance;
    private String mCodeLoc;
    private int mEdatMin;
    private int mEdatMax;



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
    public GetUserId(Context mContext,String mDeviceid,RecyclerView rcvListImg,int mDistance,String mCodeLoc,int mEdatMin, int mEdatMax){
        this.mDistance=mDistance;
        this.mContext=mContext;
        this.mDeviceid=mDeviceid;
        this.rcvListImg=rcvListImg;
        this.mCodeLoc=mCodeLoc;
        this.mEdatMin=mEdatMin;
        this.mEdatMax=mEdatMax;
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
                            break;
                        case 6:
                            FilterLocation(mDistance,mContext,id,mCodeLoc,mEdatMin,mEdatMax);
                    }

                } else {
                    if(requestcode==0) {
                        Log.e(TAG, "User not found");
                        Snackbar.make(mView, R.string.usrnotfound, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
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
        if(user.equals(userToCompare)) {
            btn_edit.setVisibility(View.VISIBLE);
            btn_delete.setVisibility(View.VISIBLE);

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorageReference StorageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference fileRef = StorageRef.child(adkey);
                    fileRef.delete();
                    DatabaseReference GeoRef = database.getReference("geofire");
                    GeoFire geoFire = new GeoFire(GeoRef);
                    geoFire.removeLocation(adkey); //borrem la localització
                    AdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(adkey)) {
                                AdsRef.child(adkey).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("GetUserId/Delete", "DatabaseError");
                        }
                    });
                }
            });

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CreaActivity.class);
                    intent.putExtra("user", userToCompare);
                    intent.putExtra("ad", adkey);
                    mContext.startActivity(intent);
                }
            });
        }
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

    private void showinfo(View view,String adname){
        Intent intent = new Intent(mContext, InfoActivity.class);
        intent.putExtra("ad",adname);
        mContext.startActivity(intent);
    }

    private void DistanceToTextView(String adloc, final TextView text){
        GPSTracker mGPS = new GPSTracker(mContext);
        final GeoLocation crntLocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        final float[] distance= new float[5];
        geoFire.getLocation(adloc, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    Location.distanceBetween (crntLocation.latitude,
                            crntLocation.longitude,
                            location.latitude,
                            location.longitude,
                            distance);
                    float disInKm = Math.round(distance[0]/1000);
                    String sDist = String.valueOf(disInKm)+" Km";
                    text.setText(sDist);
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });
    }

    public void ClearedFirst(Query query){
        mAdapter = new FirebaseRecyclerAdapter<Ad, ImgViewHolder>(
                Ad.class, R.layout.anunci, ImgViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final ImgViewHolder viewHolder, Ad model, final int position) {
                viewHolder.btn_delete.setVisibility(View.INVISIBLE);
                viewHolder.btn_edit.setVisibility(View.INVISIBLE);
                GetUserId EditAndDelete = new GetUserId(viewHolder.btn_delete,viewHolder.btn_edit,model.user,mDeviceid,getRef(position).getKey(), mContext);
                EditAndDelete.GetUser(5);
                viewHolder.dateView.setText(model.data);

                GetUserId NeedsCheck = new GetUserId(viewHolder.saveCheck,model.saved,mDeviceid);
                NeedsCheck.GetUser(4);

                NeedsADelete(model.data,getRef(position).getKey());

                DistanceToTextView(getRef(position).getKey(),viewHolder.nameView);
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

    private void FilterLocation(int distance, Context ctx, final String user, final String codeloc, final int edatmin, final int edatmax){
        final DistanceQueries GetAdsToQuery=new DistanceQueries(distance,ctx,user);
        UsersRef.child(user).child("AdsToQuery").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String Sedatmin;
                String Sedatmax;
                if(mEdatMin>=10){
                    Sedatmin="d"+String.valueOf(edatmin);
                }else Sedatmin=String.valueOf(edatmin);
                if(mEdatMax>=10){
                    Sedatmax="d"+String.valueOf(edatmax);
                }else Sedatmax=String.valueOf(edatmax);

                switch (codeloc) {
                    case "1":
                        GetAdsToQuery.DistanceQuery();
                        break;
                    case "2":
                        GetAdsToQuery.DistanceQueryMF("female");
                        break;
                    case "3":
                        GetAdsToQuery.DistanceQueryMF("male");
                        break;
                    case "4":
                        GetAdsToQuery.DistanceQueryUNK();
                        break;
                    case "5":
                        GetAdsToQuery.DistanceQueryKN(edatmin,edatmax);
                        break;
                    case "6":
                        GetAdsToQuery.DistanceQueryMFUNK("F");
                        break;
                    case "7":
                        GetAdsToQuery.DistanceQueryMFKN(Sedatmin,Sedatmax,"F");
                    case "8":
                        GetAdsToQuery.DistanceQueryMFUNK("M");
                        break;
                    case "9":
                        GetAdsToQuery.DistanceQueryMFKN(Sedatmin,Sedatmax,"M");
                        break;
                }

                ClearedFirstLoc(user);
            }
        });

    }

    private void ClearedFirstLoc(final String user){
        FirebaseRecyclerAdapter<Boolean,ImgViewHolder> AdaptLoc;
        AdaptLoc = new FirebaseRecyclerAdapter<Boolean, ImgViewHolder>(
                Boolean.class, R.layout.anunci, ImgViewHolder.class,UsersRef.child(user).child("AdsToQuery")) {
            @Override
            protected void populateViewHolder(final ImgViewHolder viewHolder, Boolean model, final int position) {
                viewHolder.btn_delete.setVisibility(View.INVISIBLE);
                viewHolder.btn_edit.setVisibility(View.INVISIBLE);
                String key = getRef(position).getKey();
                AdsRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Ad locmodel= dataSnapshot.getValue(Ad.class);

                        EditDelete(user,locmodel.user,viewHolder.btn_delete,viewHolder.btn_edit,getRef(position).getKey());
                        NeedsACheck(user,viewHolder.saveCheck,locmodel.saved);
                        NeedsADelete(locmodel.data,getRef(position).getKey());

                        viewHolder.dateView.setText(locmodel.data);
                        DistanceToTextView(getRef(position).getKey(),viewHolder.nameView);
                        Glide.with(mContext)
                                .load(locmodel.url)
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("AdaptLoc","DatabaseError");
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

        rcvListImg.setAdapter(AdaptLoc);

    }
    //Eliminem anuncis antics
    private void NeedsADelete(String data, final String adkey){
        Calendar current = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
        String currentdata = df.format(current.getTime());
        Date currDate = null;
        try {
            currDate = df.parse(currentdata);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date strDate = null;
        try {
            strDate = df.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar toCompare = Calendar.getInstance();
        toCompare.setTime(strDate);
        current.setTime(currDate);

        int monthsBetween = 0;
        int dateDiff = toCompare.get(Calendar.DAY_OF_MONTH)-current.get(Calendar.DAY_OF_MONTH);

        if(dateDiff<0) {
            int borrow = toCompare.getActualMaximum(Calendar.DAY_OF_MONTH);
            dateDiff = (toCompare.get(Calendar.DAY_OF_MONTH)+borrow)-current.get(Calendar.DAY_OF_MONTH);
            monthsBetween--;

            if(dateDiff>0) {
                monthsBetween++;
            }
        }
        else {
            monthsBetween++;
        }
        monthsBetween += toCompare.get(Calendar.MONTH)-current.get(Calendar.MONTH);
        monthsBetween  += (toCompare.get(Calendar.YEAR)-current.get(Calendar.YEAR))*12;

        if(monthsBetween>=6){
            StorageReference StorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fileRef = StorageRef.child(adkey);
            fileRef.delete(); //borrem la imatge de l'storage
            DatabaseReference GeoRef = database.getReference("geofire");
            GeoFire geoFire = new GeoFire(GeoRef);
            geoFire.removeLocation(adkey); //borrem la localització
            //borrem l'anunci del DB
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

    }
}

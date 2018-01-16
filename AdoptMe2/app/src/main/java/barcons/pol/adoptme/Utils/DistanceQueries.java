package barcons.pol.adoptme.Utils;

import android.content.Context;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Marta on 16/01/2018.
 */

public class DistanceQueries {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);
    DatabaseReference GeoRef = database.getReference("geofire");
    private DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);

    private int mDistance;
    private Context mContext;
    private String mUser;

    public DistanceQueries(int mDistance,Context mContext, String mUser){
        this.mContext=mContext;
        this.mDistance=mDistance;
        this.mUser=mUser;
    }

    public void DistanceQuery(){
        DatabaseReference GeoRef = database.getReference("geofire");
        GeoFire geoFire = new GeoFire(GeoRef);
        GPSTracker mGPS = new GPSTracker(mContext);
        final GeoLocation crntLocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        GeoQuery geoquery = geoFire.queryAtLocation(new GeoLocation(crntLocation.latitude, crntLocation.longitude), mDistance);
        geoquery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                DatabaseReference AddQueryToUser = UsersRef.child(mUser).child("AdsToQuery");
                HashMap<String, Object> AdSaved= new HashMap<>();
                AdSaved.put(dataSnapshot.getKey(), true);
                AddQueryToUser.updateChildren(AdSaved);
            }
            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                UsersRef.child(mUser).child("AdsToQuery").child(dataSnapshot.getKey()).removeValue();
            }
            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }
            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }
            @Override
            public void onGeoQueryReady() {

            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e("DistanceQueries","DatabaseError");
            }
        });
    }

    public void DistanceQueryMF(String sexe){
        DatabaseReference GeoRef = database.getReference("geofire");
        final GeoFire geoFire = new GeoFire(GeoRef);
        GPSTracker mGPS = new GPSTracker(mContext);
        final GeoLocation crntLocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        Query query= AdsRef.orderByChild("sexe").equalTo(sexe);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot sexsnapshot) {
                for (final DataSnapshot sex:sexsnapshot.getChildren()){
                    GeoQuery geoquery = geoFire.queryAtLocation(new GeoLocation(crntLocation.latitude, crntLocation.longitude), mDistance);
                    geoquery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @Override
                        public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                            DatabaseReference AddQueryToUser = UsersRef.child(mUser).child("AdsToQuery");
                            if(dataSnapshot.getKey().equals(sex.getKey())){
                            HashMap<String, Object> AdSaved= new HashMap<>();
                            AdSaved.put(dataSnapshot.getKey(), true);
                            AddQueryToUser.updateChildren(AdSaved);
                            }
                        }
                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {
                            UsersRef.child(mUser).child("AdsToQuery").child(dataSnapshot.getKey()).removeValue();
                        }
                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onGeoQueryReady() {

                        }
                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("DistanceQueries","DatabaseError");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DistanceQueries","DatabaseError");
            }
        });

    }

    public void DistanceQueryUNK(){
        DatabaseReference GeoRef = database.getReference("geofire");
        final GeoFire geoFire = new GeoFire(GeoRef);
        GPSTracker mGPS = new GPSTracker(mContext);
        final GeoLocation crntLocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        Query query= AdsRef.orderByChild("edat/unknown").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot sexsnapshot) {
                for (final DataSnapshot sex:sexsnapshot.getChildren()){
                    GeoQuery geoquery = geoFire.queryAtLocation(new GeoLocation(crntLocation.latitude, crntLocation.longitude), mDistance);
                    geoquery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @Override
                        public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                            DatabaseReference AddQueryToUser = UsersRef.child(mUser).child("AdsToQuery");
                            if(dataSnapshot.getKey().equals(sex.getKey())){
                                HashMap<String, Object> AdSaved= new HashMap<>();
                                AdSaved.put(dataSnapshot.getKey(), true);
                                AddQueryToUser.updateChildren(AdSaved);
                            }
                        }
                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {
                            UsersRef.child(mUser).child("AdsToQuery").child(dataSnapshot.getKey()).removeValue();
                        }
                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onGeoQueryReady() {

                        }
                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("DistanceQueries","DatabaseError");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DistanceQueries","DatabaseError");
            }
        });

    }

    public void DistanceQueryKN(int mEdatMin, int mEdatMax){
        DatabaseReference GeoRef = database.getReference("geofire");
        final GeoFire geoFire = new GeoFire(GeoRef);
        GPSTracker mGPS = new GPSTracker(mContext);
        final GeoLocation crntLocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        Query query= AdsRef.orderByChild("edat/known").startAt(mEdatMin).endAt(mEdatMax);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot sexsnapshot) {
                for (final DataSnapshot sex:sexsnapshot.getChildren()){
                    GeoQuery geoquery = geoFire.queryAtLocation(new GeoLocation(crntLocation.latitude, crntLocation.longitude), mDistance);
                    geoquery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @Override
                        public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                            DatabaseReference AddQueryToUser = UsersRef.child(mUser).child("AdsToQuery");
                            if(dataSnapshot.getKey().equals(sex.getKey())){
                                HashMap<String, Object> AdSaved= new HashMap<>();
                                AdSaved.put(dataSnapshot.getKey(), true);
                                AddQueryToUser.updateChildren(AdSaved);
                            }
                        }
                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {
                            UsersRef.child(mUser).child("AdsToQuery").child(dataSnapshot.getKey()).removeValue();
                        }
                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onGeoQueryReady() {

                        }
                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("DistanceQueries","DatabaseError");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DistanceQueries","DatabaseError");
            }
        });

    }
    public void DistanceQueryMFUNK(String sexe){
        DatabaseReference GeoRef = database.getReference("geofire");
        final GeoFire geoFire = new GeoFire(GeoRef);
        GPSTracker mGPS = new GPSTracker(mContext);
        final GeoLocation crntLocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        Query query= AdsRef.orderByChild("query").equalTo(sexe+"_-1");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot sexsnapshot) {
                for (final DataSnapshot sex:sexsnapshot.getChildren()){
                    GeoQuery geoquery = geoFire.queryAtLocation(new GeoLocation(crntLocation.latitude, crntLocation.longitude), mDistance);
                    geoquery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @Override
                        public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                            DatabaseReference AddQueryToUser = UsersRef.child(mUser).child("AdsToQuery");
                            if(dataSnapshot.getKey().equals(sex.getKey())){
                                HashMap<String, Object> AdSaved= new HashMap<>();
                                AdSaved.put(dataSnapshot.getKey(), true);
                                AddQueryToUser.updateChildren(AdSaved);
                            }
                        }
                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {
                            UsersRef.child(mUser).child("AdsToQuery").child(dataSnapshot.getKey()).removeValue();
                        }
                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onGeoQueryReady() {

                        }
                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("DistanceQueries","DatabaseError");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DistanceQueries","DatabaseError");
            }
        });
    }

    public void DistanceQueryMFKN(String edatmin, String edatmax, String sexe){
        DatabaseReference GeoRef = database.getReference("geofire");
        final GeoFire geoFire = new GeoFire(GeoRef);
        GPSTracker mGPS = new GPSTracker(mContext);
        final GeoLocation crntLocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        Query query= AdsRef.orderByChild("query").startAt(sexe+"_" + edatmin).endAt(sexe+"_" + edatmax);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot sexsnapshot) {
                for (final DataSnapshot sex:sexsnapshot.getChildren()){
                    GeoQuery geoquery = geoFire.queryAtLocation(new GeoLocation(crntLocation.latitude, crntLocation.longitude), mDistance);
                    geoquery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @Override
                        public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                            DatabaseReference AddQueryToUser = UsersRef.child(mUser).child("AdsToQuery");
                            if(dataSnapshot.getKey().equals(sex.getKey())){
                                HashMap<String, Object> AdSaved= new HashMap<>();
                                AdSaved.put(dataSnapshot.getKey(), true);
                                AddQueryToUser.updateChildren(AdSaved);
                            }
                        }
                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {
                            UsersRef.child(mUser).child("AdsToQuery").child(dataSnapshot.getKey()).removeValue();
                        }
                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

                        }
                        @Override
                        public void onGeoQueryReady() {

                        }
                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.e("DistanceQueries","DatabaseError");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DistanceQueries","DatabaseError");
            }
        });

    }




}

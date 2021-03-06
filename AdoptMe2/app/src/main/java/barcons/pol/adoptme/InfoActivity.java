package barcons.pol.adoptme;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import barcons.pol.adoptme.Objectes.Ad;
import barcons.pol.adoptme.Objectes.User;
import barcons.pol.adoptme.Utils.FirebaseReferences;
import barcons.pol.adoptme.Utils.GPSTracker;

/**
 * Created by Marta on 08/12/2017.
 *
 */

public class InfoActivity extends AppCompatActivity {

    //Referències a la base de dades del Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);
    DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);

    // /Objectes per facilitar la lectura del contingut de la base de dades
    Ad anunci;
    User usuari;

    ImageView showimg;


    //Menú de la barra de dalt de InfoActivity
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        //Per que aparegui el botó de BACK a la barra de dalt
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView text_desc = (TextView)findViewById(R.id.i_text_descripcio);
        final TextView text_edat = (TextView)findViewById(R.id.i_text_edat);
        final TextView text_nom = (TextView)findViewById(R.id.i_text_nom);
        final TextView text_email = (TextView)findViewById(R.id.i_text_email);
        final TextView text_telf = (TextView)findViewById(R.id.i_text_telefon);
        final TextView text_distancia =(TextView)findViewById(R.id.i_text_distancia);

        final CheckBox desconegut = (CheckBox)findViewById(R.id.i_check_desconegut);
        final CheckBox famella = (CheckBox)findViewById(R.id.i_check_female);
        final CheckBox mascle = (CheckBox)findViewById(R.id.i_check_male);
        showimg = (ImageView)findViewById(R.id.i_show_image);


        Intent intent = getIntent();
        final String ad= intent.getStringExtra("ad"); // id de l'anunci

        //llegim un únic cop el contingut de la base de dades de l'anunci corresponent a l'id proporcionat.
        AdsRef.child(ad).addListenerForSingleValueEvent(new ValueEventListener() {
            final String TAG = "InfoDataSnapshot";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                anunci = dataSnapshot.getValue(Ad.class); //capturem tots els camps de l'anunci

                UsersRef.child(anunci.user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        usuari = dataSnapshot.getValue(User.class); //capturem l'usuari que ha creat l'anunci

                        //omplim els texts views corresponents "falten les comprovacions de si mascle
                        // esta checked famella no i el mateix amb l'edat.
                        text_desc.setText(anunci.desc);
                        desconegut.setChecked(anunci.edat.unknown);
                        famella.setChecked(anunci.sexe.equals("female"));
                        mascle.setChecked(anunci.sexe.equals("male"));
                        text_nom.setText(usuari.name);
                        text_email.setText(usuari.email);
                        text_telf.setText(String.valueOf(usuari.phone));
                        DistanceToTextView(ad,text_distancia);

                        String auxedat = String.valueOf(anunci.edat.known);
                        if (auxedat.equals("-1")) {
                            text_edat.setText("-");
                        }else{text_edat.setText(auxedat);}

                        //Carreguem la imatge
                        Glide.with(InfoActivity.this)
                                .load(anunci.url)
                                .centerCrop()
                                .error(R.drawable.common_google_signin_btn_icon_dark)
                                .into(showimg);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i(TAG, "Error when capturing the user value");
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "Error when capturing the ad value");
            }

        });
    }

    private void DistanceToTextView(String adloc, final TextView text){
        GPSTracker mGPS = new GPSTracker(InfoActivity.this);
        DatabaseReference GeoRef = database.getReference("geofire");
        GeoFire geoFire = new GeoFire(GeoRef);
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

}




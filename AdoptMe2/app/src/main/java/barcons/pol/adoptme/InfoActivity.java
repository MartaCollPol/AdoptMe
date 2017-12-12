package barcons.pol.adoptme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import barcons.pol.adoptme.Objectes.Ad;
import barcons.pol.adoptme.Utils.FirebaseReferences;
import barcons.pol.adoptme.Objectes.User;

/**
 * Created by Marta on 08/12/2017.
 *
 *  Hola que tal...
 */

 //TODO: botó per aceptar, botó per tornar enrere
 //TODO: mostra imagtge, i mostra distància
public class InfoActivity extends AppCompatActivity {

    //Referències a la base de dades del Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);
    DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);
    //Objectes per facilitar la lectura del contingut de la base de dades
    Ad anunci;
    User usuari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        final TextView text_desc = (TextView)findViewById(R.id.i_text_descripcio);
        final TextView text_edat = (TextView)findViewById(R.id.i_text_edat);
        final TextView text_nom = (TextView)findViewById(R.id.i_text_nom);
        final TextView text_email = (TextView)findViewById(R.id.i_text_email);
        final TextView text_telf = (TextView)findViewById(R.id.i_text_telefon);

        final CheckBox desconegut = (CheckBox)findViewById(R.id.i_check_desconegut);
        final CheckBox famella = (CheckBox)findViewById(R.id.i_check_female);
        final CheckBox mascle = (CheckBox)findViewById(R.id.i_check_male);

        Intent intent = getIntent();
        String ad= intent.getStringExtra("ad"); // id de l'anunci
        //Obtenir els valors d'un anunci dins d'un object Ad per mostrar-ho.

        //llegim un únic cop el contingut de la base de dades de l'anunci corresponent a l'id proporcionat.
        AdsRef.child(ad).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                anunci = dataSnapshot.getValue(Ad.class); //capturem tots els camps de l'anunci
                UsersRef.child(anunci.user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuari = dataSnapshot.getValue(User.class); //capturem l'usuari que ha creat l'anunci
                        //omplim els texts views corresponents "falten les comprovacions de si mascle esta checked famella no i el mateix amb l'edat.
                        text_desc.setText(anunci.desc);
                        text_edat.setText(String.valueOf(anunci.edat.known));
                        desconegut.setChecked(anunci.edat.unknown);
                        famella.setChecked(anunci.sexe.equals("female"));
                        mascle.setChecked(anunci.sexe.equals("mascle"));
                        text_nom.setText(usuari.name);
                        text_email.setText(usuari.email);
                        text_telf.setText(String.valueOf(usuari.phone));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("mcoll", "Error when capturing the user value");
                    }

                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("mcoll","Error when capturing the ad value");
            }
        });



    }
}

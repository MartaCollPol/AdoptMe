package barcons.pol.adoptme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import barcons.pol.adoptme.Objectes.Ad;
import barcons.pol.adoptme.Utils.FirebaseReferences;
import barcons.pol.adoptme.Objectes.User;
import barcons.pol.adoptme.Objectes.edat;

//TODO: intent de càmera (http://gpmess.com/blog/2013/10/02/como-cargar-fotos-en-una-aplicacion-android-desde-camara-galeria-y-otras-aplicaciones/)
//TODO: guardar url a la base de dades i mostrar la imatge
public class CreaActivity extends AppCompatActivity {

    //Referències a la base de dades del Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);
    DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);
    DatabaseReference UserRef;
    DatabaseReference CreatedRef;

    //Objectes per facilitar la lectura del contingut de la base de dades
    Ad anunci=new Ad(); //invoquem el constructor per defecte
    User usuari=new User();

    EditText text_desc;
    EditText text_edat;
    EditText text_nom;
    EditText text_email;
    EditText text_telf;

    CheckBox desconegut;
    CheckBox female;
    CheckBox male;

    String us;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea);

        text_desc = (EditText) findViewById(R.id.c_text_descripcio);
        text_edat = (EditText) findViewById(R.id.c_text_edat);
        text_nom = (EditText)findViewById(R.id.c_text_nom);
        text_email = (EditText) findViewById(R.id.c_text_email);
        text_telf = (EditText)findViewById(R.id.c_text_telefon);

        desconegut = (CheckBox)findViewById(R.id.c_check_desconegut);
        female = (CheckBox)findViewById(R.id.c_check_female);
        male = (CheckBox)findViewById(R.id.c_check_male);



        Intent intent = getIntent();
        final String us= intent.getStringExtra("user"); // id de l'usuari que crea l'anunci

        UserRef = UsersRef.child(us);
        CreatedRef = UserRef.child("created");

        //preomplim els camps d'informació de contacte si aquests es troben disponibles a la base de dadesz
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuari = dataSnapshot.getValue(User.class);
                text_nom.setText(usuari.name);
                if (usuari.email!=null){
                    text_email.setText(usuari.email);
                }
                if (usuari.phone!=null){
                    text_telf.setText(String.valueOf(usuari.phone));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("mcoll", "Error when capturing the user value");

            }
        });

        //Fem que només un dels checkbox estigui activat al mateix temps
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenchecked(male,female);

            }

        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenchecked(female,male);
            }
        });
        //Si el checkbox de edat desconeguda està activat, buidem l'editText d'edat i si volem ediatar l'edat el desactivem
        desconegut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (desconegut.isChecked()){
                    text_edat.setText("");
                }else desconegut.setChecked(false);
            }
        });

        text_edat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(desconegut.isChecked()){
                    desconegut.setChecked(false);
                }
            }
        });





    }

    private void whenchecked(CheckBox a, CheckBox b) {
        if (a.isChecked()) {
            b.setChecked(false);
        }
    }

    //Menú de la barra de dalt de CreaActivity, on hi posarem el botó de OK
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_crea, menu);
        return true;
    }

    //Opcions que hi haura a la barra de dalt (de moment només el botó OK)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_OK:
                startActivity(new Intent(this, MainActivity.class));

                anunci.user=us;
                if (female.isChecked()){
                    anunci.sexe="female";
                }else anunci.sexe="mascle";
                //Per a numeros '-1' serà l'equivalent al valor 'null'
                String edat =text_edat.getText().toString();
                if (edat.equals("")){
                    edat = "-1";
                }
                anunci.edat = new edat(Integer.parseInt(edat),desconegut.isChecked());
                anunci.desc=text_desc.getText().toString();

                //en cas que l'usuari editi algun camp s'actualitza a la base de dades
                usuari.name= text_nom.getText().toString();
                usuari.email=text_email.getText().toString();
                usuari.phone=Long.parseLong(text_telf.getText().toString());


                DatabaseReference newRef = AdsRef.push(); //creem una referència a la randomkey generada amb el push
                newRef.setValue(anunci);
                String adkey = newRef.getKey();
                //fem un update de l'usuari en cas de que hagin canviat valors en algun dels camps de l'usuari
                HashMap<String,Object> Updateuser = new HashMap<>();
                Updateuser.put("name",usuari.name);
                Updateuser.put("email",usuari.email);
                Updateuser.put("phone",usuari.phone);
                UserRef.updateChildren(Updateuser);

                //afegim l'anunci creat al camp "created" de l'usuari
                HashMap<String, Object> Adcreated = new HashMap<>();
                Adcreated.put(adkey, true);
                CreatedRef.updateChildren(Adcreated);

                return true;



        } return super.onOptionsItemSelected(item);
    }


}

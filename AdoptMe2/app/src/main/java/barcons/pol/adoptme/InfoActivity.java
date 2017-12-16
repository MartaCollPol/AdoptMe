package barcons.pol.adoptme;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import barcons.pol.adoptme.Objectes.Ad;
import barcons.pol.adoptme.Objectes.User;
import barcons.pol.adoptme.Utils.FirebaseReferences;

/**
 * Created by Marta on 08/12/2017.
 *
 */

 //TODO: botó per aceptar, botó per tornar enrere
 //TODO: mostra imagtge, i mostra distància
public class InfoActivity extends AppCompatActivity {

    //Referències a la base de dades del Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);
    DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);
    StorageReference ImgRef;

    // /Objectes per facilitar la lectura del contingut de la base de dades
    Ad anunci;
    User usuari;

    ImageView showimg;

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
        showimg = (ImageView)findViewById(R.id.i_show_image);

        StorageReference StorageRef = FirebaseStorage.getInstance().getReference();




        Intent intent = getIntent();
        String ad= intent.getStringExtra("ad"); // id de l'anunci
        //Obtenir els valors d'un anunci dins d'un object Ad per mostrar-ho.
        ImgRef = StorageRef.child(ad +".jpg");

        //Col·loquem la imatge guardada al Firebase storage al imageView
        ImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new URLtoBitmap().execute(uri);
            }
        });


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
                Log.i("mcoll", "Error when capturing the ad value");
            }


        });



    }

    //hi ha la classe per fer-ho amb el bitmap comentada a sota, passa el mateix en ambdos casos
    private class URLtoBitmap extends AsyncTask<Uri, Void, String> {
        private static final String TAG = "URLtoBitmap";
        @Override
        protected String doInBackground(Uri... params) {

            //peta aqui:  android.os.NetworkOnMainThreadException
            // at android.os.StrictMode$AndroidBlockGuardPolicy.onNetwork(StrictMode.java:1303)
            String result = params.toString();
            return result;
            //Bitmap result = BitmapFactory.decodeStream(downloadurl.openConnection().getInputStream());


        }

        @Override
        protected void onPostExecute(String result){
            if(result!=null){
               //showimg.setImageBitmap(result);
               Picasso.with(InfoActivity.this).load(result).fit().centerCrop().into(showimg);
            }
            else Log.i(TAG,"Could not set the image");
        }
    }

    /*
      private class URLtoBitmap extends AsyncTask<Uri, Void, Bitmap> {
          private static final String TAG = "URLtoBitmap";

          @Override
          protected Bitmap doInBackground(Uri... params) {
              try {
                  URL downloadurl = new URL(params.toString());
                  return BitmapFactory.decodeStream(downloadurl.openConnection().getInputStream());

              } catch (Exception e) {
                  Log.i(TAG, "Could not get the bitmap");
                  return null;
              }

          }

          @Override
          protected void onPostExecute(Bitmap result) {
              if (result != null) {
                  showimg.setImageBitmap(result);

              } else Log.i(TAG, "Could not set the image");
          }
      } */
    }




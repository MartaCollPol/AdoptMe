package barcons.pol.adoptme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import barcons.pol.adoptme.Objectes.Ad;
import barcons.pol.adoptme.Objectes.User;
import barcons.pol.adoptme.Objectes.edat;
import barcons.pol.adoptme.Utils.FirebaseReferences;
import barcons.pol.adoptme.Utils.GPSTracker;


//TODO: Fer que sigui obligatori omplir els camps
// /TODO: guardar url a la base de dades i mostrar la imatge
public class CreaActivity extends AppCompatActivity {

    //Referències a la base de dades del Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);
    DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);
    DatabaseReference AdExistsRef;
    DatabaseReference newRef;
    StorageReference StorageRef = FirebaseStorage.getInstance().getReference();


    DatabaseReference UserRef;
    DatabaseReference CreatedRef;

    //Objectes per facilitar la lectura del contingut de la base de dades
    CoordinatorLayout coordinatorLayout;
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

    ImageView imageView;

    String us;
    String ad;
    static String adkey;
    Uri uri;
    private boolean ImgClickedFlag = false;
    private boolean ImgExistFlag = false;

    //Menú de la barra de dalt de CreaActivity, on hi posarem el botó de OK
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_crea, menu);
        return true;
    }

    //Opcions que hi haura a la barra de dalt (de moment només el botó OK)n
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;

            case R.id.action_OK:
                boolean flag = false;
                if(!ImgExistFlag){
                    flag = true;
                    Snackbar.make(coordinatorLayout, R.string.missingimg, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }if(text_desc.getText().toString().trim().equals("...") || text_desc.getText().toString().trim().equals("...")) {
                    flag = true;
                    text_desc.requestFocus();
                    text_desc.setError(getString(R.string.omple));
                }if((desconegut.isChecked())||!(text_edat.getText().toString().trim().equals(""))) {
                desconegut.setError(null);
                }else{
                    flag = true;
                    desconegut.requestFocus();
                    desconegut.setError("");
                }if(text_nom.getText().toString().trim().equals("")){
                    flag = true;
                    text_nom.requestFocus();
                    text_nom.setError(getString(R.string.omple));
                }if(text_email.getText().toString().trim().equals("")){
                    flag = true;
                    text_email.requestFocus();
                    text_email.setError(getString(R.string.omple));
                }if(text_telf.getText().toString().trim().equals("")){
                    flag = true;
                    text_telf.requestFocus();
                    text_telf.setError(getString(R.string.omple));
            }if((female.isChecked())||(male.isChecked())) {
                female.setError(null);
            }else{
                flag = true;
                female.setError("");
            }
            if(flag == false){
                GPSTracker mGPS = new GPSTracker(this);
                if(mGPS.canGetLocation ){
                    CreaAnunci(); //GuardaLoc esta a dins de CreaAnunci();
                    finish();
                }else {
                    buildAlertMessageNoGps();
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imageView.setClickable(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea);

        //Perquè aparegui el botó de BACK a la barra de dalt
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout =(CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        text_desc = (EditText) findViewById(R.id.c_text_descripcio);
        text_edat = (EditText) findViewById(R.id.c_text_edat);
        text_nom = (EditText)findViewById(R.id.c_text_nom);
        text_email = (EditText) findViewById(R.id.c_text_email);
        text_telf = (EditText)findViewById(R.id.c_text_telefon);

        desconegut = (CheckBox)findViewById(R.id.c_check_desconegut);
        female = (CheckBox)findViewById(R.id.c_check_female);
        male = (CheckBox)findViewById(R.id.c_check_male);
        imageView = (ImageView)findViewById(R.id.c_edit_image);

        Intent intent = getIntent();
        us= intent.getStringExtra("user"); // id de l'usuari que crea l'anunci
        ad=intent.getStringExtra("ad");
        Log.e("mcollAd","Ad value: "+ad);

        //Referències
        UserRef = UsersRef.child(us);
        CreatedRef = UserRef.child("created");

        //damenem permisos de la càmera i d'escritura al dispositiu
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            imageView.setClickable(false);
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else imageView.setClickable(true);

        //Preomplim els camps de l'anunci en cas que estiguem editant
        if (!ad.equals("Void")) {
            AdExistsRef=AdsRef.child(ad);
            AdExistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Ad anunciExist = dataSnapshot.getValue(Ad.class);
                    Glide.with(CreaActivity.this)
                            .load(anunciExist.url)
                            .centerCrop()
                            .error(R.drawable.common_google_signin_btn_icon_dark)
                            .into(imageView);
                    text_desc.setText(anunciExist.desc);
                    desconegut.setChecked(anunciExist.edat.unknown);
                    female.setChecked(anunciExist.sexe.equals("female"));
                    male.setChecked(anunciExist.sexe.equals("male"));
                    String auxedat = String.valueOf(anunciExist.edat.known);
                    if (auxedat.equals("-1")) {
                        text_edat.setText("");
                    }else{text_edat.setText(auxedat);}

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("CreaActivity/Edit", "Database Error");
                }
            });

            ImgExistFlag=true;
        }
        //preomplim els camps d'informació de contacte si aquests es troben disponibles a la base de dadesz
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String TAG = "CreaDataSnapshot";
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
                Log.i(TAG, "Error when capturing the user value");
            }
        });

        //Al fer click al imageview obrim la càmara
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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


    //Creem l'anunci
    private void CreaAnunci(){

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy",Locale.FRENCH);
        String data = df.format(c.getTime());

        anunci.user = us;
        anunci.data = data;
        if (female.isChecked()) {
            anunci.sexe = "female";
        } else anunci.sexe = "male";
        //Per a numeros '-1' serà l'equivalent al valor 'null'
        String edat = text_edat.getText().toString();
        if (edat.equals("")) {
            edat = "-1";
        }
        anunci.edat = new edat(Integer.parseInt(edat), desconegut.isChecked());
        anunci.desc = text_desc.getText().toString();

        //en cas que l'usuari editi algun camp s'actualitza a la base de dades
        usuari.name = text_nom.getText().toString();
        usuari.email = text_email.getText().toString();
        usuari.phone = Long.parseLong(text_telf.getText().toString());

        final String TAG = "FirebaseStorage";

        if(ad.equals(null)) {
            newRef = AdsRef.push(); //creem una referència a la randomkey generada amb el push
            adkey = newRef.getKey();
            //Afegim la imatge fotografiada al Firebase Storage i li assignem el nom de l'anunci

            StorageReference fileRef = StorageRef.child(adkey);

            fileRef.putFile(uri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i(TAG, "Image upload to Cloud Storage failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressLint("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    anunci.url = taskSnapshot.getDownloadUrl().toString();
                    Log.i(TAG, "Image uploaded successfully" + anunci.url);
                    newRef.setValue(anunci);//Afegim l'anunci
                }
            });
            GuardaLoc(adkey);
        }else{ //Editem un anunci existent
            final HashMap<String, Object> UpdateAd = new HashMap<>();
            UpdateAd.put("sexe",anunci.sexe);
            UpdateAd.put("desc",anunci.desc);
            UpdateAd.put("edat/known",anunci.edat.known);
            UpdateAd.put("edat/unknown",anunci.edat.unknown);
            UpdateAd.put("data",anunci.data);

            if(ImgClickedFlag){
                StorageReference fileRef = StorageRef.child(ad);
                fileRef.delete();//Borrem l'anterior foto
                fileRef.putFile(uri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i(TAG, "Image upload to Cloud Storage failed");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressLint("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        anunci.url = taskSnapshot.getDownloadUrl().toString();
                        Log.i(TAG, "Image uploaded successfully" + anunci.url);
                        UpdateAd.put("url",anunci.url);//Afegim l'anunci
                        AdExistsRef.updateChildren(UpdateAd);
                        Log.e("mcolldins","valorflag :"+ImgExistFlag);
                    }
                });

            }else {
                Log.e("mcollfora","valorflag :"+ImgExistFlag);
                AdExistsRef.updateChildren(UpdateAd);
                GuardaLoc(ad);
            }


        }

        //fem un update de l'usuari en cas de que hagin canviat valors en algun dels camps de l'usuari
        HashMap<String, Object> Updateuser = new HashMap<>();
        Updateuser.put("name", usuari.name);
        Updateuser.put("email", usuari.email);
        Updateuser.put("phone", usuari.phone);
        UserRef.updateChildren(Updateuser);

        finish();
    }

    private void whenchecked(CheckBox a, CheckBox b) {
        if (a.isChecked()) {
            b.setChecked(false);
        }
    }

    //Geofire
    private void GuardaLoc(String adloc){
        GPSTracker mGPS = new GPSTracker(this);
        DatabaseReference GeoRef = database.getReference("geofire");
        GeoFire geoFire = new GeoFire(GeoRef); //ref -> FirebaseReference que apunta a geofire.
        GeoLocation currentlocation = new GeoLocation(mGPS.getLatitude(),mGPS.getLongitude());
        geoFire.setLocation(adloc,currentlocation);
    }


  protected void buildAlertMessageNoGps() {

      final AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Please Turn ON your GPS Connection")
              .setCancelable(false)
              .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                  public void onClick(final DialogInterface dialog, final int id) {
                      startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                  }
              })
              .setNegativeButton("No", new DialogInterface.OnClickListener() {
                  public void onClick(final DialogInterface dialog, final int id) {
                      dialog.cancel();
                  }
              });
      final AlertDialog alert = builder.create();
      alert.show();
  }


    //INTENT DE CÀMERA
    static final int REQUEST_TAKE_PHOTO = 110;
    private void dispatchTakePictureIntent() {
        ImgClickedFlag = true;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                uri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            imageView.setImageURI(uri);
            ImgExistFlag=true;
        }
    }

    //GUARDAR EL FITXER AL DISPOSITIU

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRENCH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try{
            image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );}catch (IOException e){
            Log.i("CreateImageFile","Failed on creating Temp File");
        }

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    /*
       afegir
    //Afegeix la imatge a la galeria del dispositiu - no furula perq per ara les imatges es guarden al directori
    //Retornat per getExternalFilesDir el qual el mediaScan no el llegeix ja que és privat per l'app
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
    }
    */

}

package barcons.pol.adoptme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import barcons.pol.adoptme.Utils.FirebaseReferences;
import barcons.pol.adoptme.Utils.GetDeviceId;
import barcons.pol.adoptme.Utils.GetUserId;
import barcons.pol.adoptme.Utils.ListAdapter;

//import per obtenir l'id unic del dispositiu

public class MainActivity extends AppCompatActivity {


    //Referències
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);

    Intent starterintent;
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;
    boolean flag_is_write_permission_set = false;
    private static final int PERMISSION_READ_STATE = 112;
    private boolean flag_is_read_permission_set=false;

    ArrayList<String> imgid = new ArrayList<>();
    ArrayList<String> dist = new ArrayList<>();

    private String deviceId;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filtra:
                startActivity(new Intent(this, FiltraActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                flag_is_read_permission_set=true;
            }else finish();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.System.canWrite(this)) {
            flag_is_write_permission_set = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Demanem permisos per poder evitar que al rotar el dispositiu la app roti també.
        starterintent = getIntent();
        if (!Settings.System.canWrite(this)) {
            flag_is_write_permission_set = false;
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
        } else {
            flag_is_write_permission_set = true;
        }

        PermissionGranted(flag_is_write_permission_set);

    }

    private void PermissionGranted(boolean permission) {
        if (permission) {
            setContentView(R.layout.activity_main);
            //Bloqueigem la rotació del dispositiu
            setAutoOrientationEnabled(this, false);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            signInAnonymously();

            //Obtenim l'id del dispositiu per saber quin és el Current User
            GetDeviceId uid = new GetDeviceId(this);
            if(!uid.CheckReadPermission()) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[] { android.Manifest.permission.READ_PHONE_STATE }, PERMISSION_READ_STATE);
            } else flag_is_read_permission_set=true;

            if(flag_is_read_permission_set){
                TelephonyManager telephonyManager;
                telephonyManager = (TelephonyManager) getSystemService(Context.
                        TELEPHONY_SERVICE);
                deviceId = uid.GetId(telephonyManager);
                if(deviceId==null){
                    Log.e("GetDeviceId/Main","Couldn't get the DeviceId");
                }
            }

            final ListView list = (ListView) findViewById(R.id.list);
            //TODO: canviar el mètode de visualitzar els anuncis per aquest, ja que és més eficient:
            // http://javasampleapproach.com/android/firebase-storage-get-list-files-display-image-firebase-ui-database-android
            AdsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> ads = dataSnapshot.getChildren();
                    for (DataSnapshot ad : ads) {
                        imgid.add(ad.getKey());
                        dist.add("gos");
                        //on posa gos afagirem les distàncies amb ad.child(distancia).getValue();
                    }
                    ListAdapter adapter = new ListAdapter(
                            MainActivity.this,
                            imgid,
                            dist,
                            deviceId
                    );
                    list.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("Adlist", "Failed on retrieving the adlist");
                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: comentar la linea GetUser(view) i activar la de writeNewUser anar al final de la mainactivity i descomentar el mètode, iniciar la app i fer click al boto de creaactivity UN SOL cop per crear el vostre usuari
                    //TODO: un cop fet, tornar a deixar la linea writenewuser comentada i descomentar la de getuser.
                    //writeNewUser("Tester", deviceId);
                    //Obtenim l'usuari i iniciem la CreaActivity
                    GetUserId CreaAd = new GetUserId(MainActivity.this,deviceId,view);
                    CreaAd.GetUser(0);
                }
            });

        } else finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)) {
            Log.d("WriteSettings", "CODE_WRITE_SETTINGS_PERMISSION success");
            finish();
            startActivity(starterintent);
        }
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled) {
        Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    //ho utilitzem per a poder fer servir el FirebaseStorage. Tots els usuaris seran anonims.
    private void signInAnonymously() {
        final String TAG = "AnonymousAuth";
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }

    /*
    //A first time activity per crear un nou user. Juntament amb l'obtenció del device id.
    private void writeNewUser(String name, String uid) {
        User user = new User(name,uid);
        UsersRef.push().setValue(user);
    }*/

}


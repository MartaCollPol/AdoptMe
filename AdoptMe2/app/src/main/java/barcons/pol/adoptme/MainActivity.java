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
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import barcons.pol.adoptme.Utils.FirebaseReferences;
import barcons.pol.adoptme.Utils.GetDeviceId;
import barcons.pol.adoptme.Utils.GetUserId;

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
    private static final int CODE_FILTRAACTIVITY = 110;

    private RecyclerView rcvListImg;
    private String deviceId;
    String code;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filtra:
                startActivityForResult(new Intent(this, FiltraActivity.class),CODE_FILTRAACTIVITY);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Read permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                flag_is_read_permission_set=true;
            }else {
                Toast.makeText(this, R.string.writenotallowed, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(this)) flag_is_write_permission_set = true;
        }else flag_is_write_permission_set=true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Afegir que al tornar enrere d'una activitat estigui seleccionat el mateix menu search/save/home que estaba al marxar
        //Demanem permisos per poder evitar que al rotar el dispositiu la app roti també,
        // per a APIs inferiors a la 23 aquest permis s'activa permenentment en la instalació
        starterintent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(this)){
            flag_is_write_permission_set = false;
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            } else {
            flag_is_write_permission_set = true;
            }
        }else { flag_is_write_permission_set = true;}

        PermissionGranted(flag_is_write_permission_set);

    }

    private void PermissionGranted(boolean permission) {
        if (permission) {
            setContentView(R.layout.activity_main);
            //Bloqueigem la rotació del dispositiu
            setAutoOrientationEnabled(this);
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
                    finish();
                }
            }
            //asynck task, es podria fer sartActivity for result i continuar el programa en obtenir result==ok. Evitaria bugs al carregar la llista d'ads.
            Query query = UsersRef.orderByChild("uid").equalTo(deviceId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Intent FirstTime=new Intent(MainActivity.this,FirstTimeActivity.class);
                        FirstTime.putExtra("uid",deviceId);
                        startActivity(FirstTime);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirstTimeQuery","DatabaseError");
                }
            });

            rcvListImg = (RecyclerView) findViewById(R.id.recyclerview);

            final GetUserId DisplayAds = new GetUserId(MainActivity.this,deviceId,rcvListImg);
            DisplayAds.ClearedFirst(AdsRef.limitToLast(100));
            //Accedir als botons de la Bottom Bar Navigation

            BottomNavigationView bottomNavigationV =(BottomNavigationView) findViewById(R.id.bottom_navigation);
            bottomNavigationV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.action_search:
                            DisplayAds.ShowAds(AdsRef.limitToLast(100));
                            return true;

                        case R.id.action_save:
                            DisplayAds.GetUser(2);
                            return true;

                        case R.id.action_home:
                            DisplayAds.GetUser(3);
                            return true;

                    }
                    return false;
                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Obtenim l'usuari i iniciem la CreaActivity.
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
        if(requestCode==CODE_FILTRAACTIVITY){
            if(resultCode==RESULT_OK){
                code = data.getStringExtra("Codi");
                if(code.equals(0)){
                    //TODO: Filtratge.
                }
            }
        }
    }


    public static void setAutoOrientationEnabled(Context context) {
        Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0);
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

}


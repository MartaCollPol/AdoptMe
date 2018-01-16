package barcons.pol.adoptme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import barcons.pol.adoptme.Utils.Queries;

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
    private static final int CODE_FILTRAACTIVITY = 110;
    public static final int MY_PERMISSIONS_REQUEST = 99;
    public static final int MY_LOCATION_PERMISSION = 88;

    private RecyclerView rcvListImg;
    private String deviceId;
    private boolean notsearch=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.action_filtra);
        register.setVisible(!notsearch);
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


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(this)){
                flag_is_write_permission_set = true;
            }
            else flag_is_write_permission_set=false;

        }else flag_is_write_permission_set=true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            //Demanem permisos per poder evitar que al rotar el dispositiu la app roti també,
            // per a APIs inferiors a la 23 aquest permis s'activa permenentment en la instalació
        starterintent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                flag_is_write_permission_set = false;
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
                finish();
            } else {
                flag_is_write_permission_set = true;
            }
        } else {
            flag_is_write_permission_set = true;
        }
        WPermissionGranted(flag_is_write_permission_set);

    }

    private void WPermissionGranted(boolean permission_write) {

        if (permission_write) {
            checkPermissions();
        }
    }
    private void LRPermissionsGranted(){
        //Bloqueigem la rotació del dispositiu
        setAutoOrientationEnabled(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        signInAnonymously();
        //Obtenim l'id del dispositiu per saber quin és el Current User
        GetDeviceId uid = new GetDeviceId(this);
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);
        deviceId = uid.GetId(telephonyManager);
        if(deviceId==null){
            Log.e("GetDeviceId/Main","Couldn't get the DeviceId");
            finish();
        }else {
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
        }

        rcvListImg = (RecyclerView) findViewById(R.id.recyclerview);

        final GetUserId DisplayAds = new GetUserId(MainActivity.this, deviceId, rcvListImg);
        DisplayAds.ClearedFirst(AdsRef.limitToLast(100));
        //Accedir als botons de la Bottom Bar Navigation

        BottomNavigationView bottomNavigationV = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_search:
                        DisplayAds.ShowAds(AdsRef.limitToLast(100));
                        notsearch = false;
                        invalidateOptionsMenu();
                        return true;

                    case R.id.action_save:
                        DisplayAds.GetUser(2);
                        notsearch = true;
                        invalidateOptionsMenu();
                        return true;

                    case R.id.action_home:
                        DisplayAds.GetUser(3);
                        notsearch = true;
                        invalidateOptionsMenu();
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
                GetUserId CreaAd = new GetUserId(MainActivity.this, deviceId, view);
                CreaAd.GetUser(0);
            }
        });
    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST);
        } else if(ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
        }else if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
        }else{
            LRPermissionsGranted();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)) {
                Log.d("WriteSettings", "CODE_WRITE_SETTINGS_PERMISSION success");
                //startActivity(starterintent);
                finish();
            }
        }
        if(requestCode==CODE_FILTRAACTIVITY){
            if(resultCode==RESULT_OK){
                String codi =data.getStringExtra("Codi");
                String SedatMin = data.getStringExtra("EdatMin");
                String SedatMax = data.getStringExtra("EdatMax");
                String codiloc=data.getStringExtra("CodeLoc");
                String Sdistance=data.getStringExtra("Km");

                Log.e("mcoll","Valors: "+codiloc+" "+Sdistance+" "+SedatMin+" "+SedatMax);
                int edatMin = -1;
                int edatMax = -1;
                if(SedatMin!=null){
                    edatMin = Integer.parseInt(SedatMin);
                    edatMax = Integer.parseInt(SedatMax);
                }

                if(codiloc!= null){
                    GetUserId DisplayAdsLoc = new GetUserId(MainActivity.this,deviceId,rcvListImg,
                            Integer.parseInt(Sdistance),codiloc,edatMin,edatMax);
                    DisplayAdsLoc.GetUser(6);

                }else{
                    GetUserId DisplayAds = new GetUserId(MainActivity.this,deviceId,rcvListImg);
                    Queries Filtra = new Queries(codi,edatMin,edatMax);

                    Query query = Filtra.ResultQuery();
                    DisplayAds.ClearedFirst(query);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    LRPermissionsGranted();
                }
                break;
            case MY_LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    LRPermissionsGranted();
                }
            case MY_PERMISSIONS_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                    LRPermissionsGranted();
                }else{
                    Toast.makeText(this,R.string.locationnotallowed,Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
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


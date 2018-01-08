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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import barcons.pol.adoptme.Objectes.Ad;
import barcons.pol.adoptme.Objectes.User;
import barcons.pol.adoptme.Utils.FirebaseReferences;
import barcons.pol.adoptme.Utils.GetDeviceId;
import barcons.pol.adoptme.Utils.GetUserId;
import barcons.pol.adoptme.Utils.ImgViewHolder;

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

    private RecyclerView rcvListImg;
    private String deviceId;
    private FirebaseRecyclerAdapter<Ad, ImgViewHolder> mAdapter;



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
                }
            }

            rcvListImg = (RecyclerView) findViewById(R.id.recyclerview);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setReverseLayout(false);
            rcvListImg.setHasFixedSize(false);
            rcvListImg.setLayoutManager(layoutManager);

            //TODO: Afegir un divisor "gris" com el de la app Reddit entre els anuncis, i un marge al final : https://www.bignerdranch.com/blog/a-view-divided-adding-dividers-to-your-recyclerview-with-itemdecoration/

            final Query query =AdsRef.limitToLast(10);

            mAdapter = new FirebaseRecyclerAdapter<Ad, ImgViewHolder>(
                    Ad.class, R.layout.anunci, ImgViewHolder.class, query) {
                @Override
                protected void populateViewHolder(final ImgViewHolder viewHolder, Ad model, final int position) {
                    viewHolder.nameView.setText(model.sexe);
                    Picasso.with(MainActivity.this)
                            .load(model.url)
                            .error(R.drawable.common_google_signin_btn_icon_dark)
                            .into(viewHolder.imageView);

                    viewHolder.saveCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetUserId SaveAd = new GetUserId(MainActivity.this,deviceId,viewHolder.saveCheck,getRef(position).getKey());
                            SaveAd.GetUser(1);
                        }
                    });
                    viewHolder.btn_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showinfo(v,getRef(position).getKey());
                        }
                    });
                   //TODO: Afegir comprovar Current user -> delete i edit setVisibility(), activar les funcions editar i borrar
                }
            };

            rcvListImg.setAdapter(mAdapter);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: comentar la linea GetUser(view) i activar la de writeNewUser anar al final de la mainactivity i descomentar el mètode, iniciar la app i fer click al boto de creaactivity UN SOL cop per crear el vostre usuari
                    //TODO: un cop fet, tornar a deixar la linea writenewuser comentada i descomentar la de getuser.
                    //writeNewUser("Tester API 22", deviceId);
                    //Obtenim l'usuari i iniciem la CreaActivity
                    GetUserId CreaAd = new GetUserId(MainActivity.this,deviceId,view);
                    CreaAd.GetUser(0);
                }
            });

        } else finish();
    }

    private void showinfo(View view,String adname){
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("ad",adname);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
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


    //A first time activity per crear un nou user. Juntament amb l'obtenció del device id.
    private void writeNewUser(String name, String uid) {
        User user = new User(name,uid);
        UsersRef.push().setValue(user);
    }

}


package barcons.pol.adoptme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import barcons.pol.adoptme.Utils.ListAdapter;

//import per obtenir l'id unic del dispositiu

public class MainActivity extends AppCompatActivity {

    //Referència a les autentificacions del firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //Referència al database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);

    private ArrayList<String> itemList;
    private ListAdapter adapter;
    ArrayList<String> imgid = new ArrayList<>();
    ArrayList<String> dist = new ArrayList<>();

    private ListView list;
    Intent starterintent;
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;
    boolean flag_is_permission_set = false;



    @Override
    public boolean onCreateOptionsMenu (Menu menu){
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
    protected void onResume(){
        super.onResume();
        if (Settings.System.canWrite(this)) {
            flag_is_permission_set=true;
        }
        PermisionGaranted(flag_is_permission_set);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Demanem permisos per poder evitar que al rotar el dispositiu la app roti també.
        starterintent = getIntent();
        if (!Settings.System.canWrite(this)) {
            flag_is_permission_set = false;
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
        } else {
            flag_is_permission_set = true;
        }

        PermisionGaranted(flag_is_permission_set);

    }

    private void PermisionGaranted(boolean permision) {
        if (permision){
            setContentView(R.layout.activity_main);
            //Bloqueigem la rotació del dispositiu
            setAutoOrientationEnabled(this, false);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            signInAnonymously();

            list = (ListView) findViewById(R.id.list);

            AdsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> ads = dataSnapshot.getChildren();
                    for (DataSnapshot ad : ads) {
                        imgid.add(ad.getKey());
                        dist.add("gos"); // aqui afagirem les distàncies amb ad.child(distancia).getValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("Adlist", "Failed on retrieving the adlist");
                }
            });

            adapter = new ListAdapter(
                    this,
                    imgid,
                    dist
            );
            list.setAdapter(adapter);


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    createAd(view);
                }
            });
        }
    }

    public void createAd(View view){ //anar al layout i assignar aquest metode a un botó per a iniciar la infoactivity
        Intent intent = new Intent(this, CreaActivity.class);
        String userid= "1"; //id de l'anunci "query de key de l'anunci clicat"
        intent.putExtra("user",userid);
        startActivity(intent);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)){
            Log.d("WriteSettings", "CODE_WRITE_SETTINGS_PERMISSION success");
            finish();
            startActivity(starterintent);
        }
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled)
    {
        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    //ho utilitzem per a poder fer servir el FirebaseStorage. Tots els usuaris seran anonims. https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/AnonymousAuthActivity.java#L71-L77
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


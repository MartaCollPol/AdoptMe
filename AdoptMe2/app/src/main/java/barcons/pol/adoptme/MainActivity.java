package barcons.pol.adoptme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.StorageReference;

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
    //DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);

    private ArrayList<String> itemList;
    private ListAdapter adapter;
    StorageReference ImgRef;
    ImageView showimg;
    ArrayList<String> imgid = new ArrayList<>();
    ArrayList<String> dist = new ArrayList<>();

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btn_info = (Button)findViewById(R.id.btn_info);
        list = (ListView) findViewById(R.id.list);

        //problema aqui ?
        AdsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> ads= dataSnapshot.getChildren();
                for (DataSnapshot ad: ads) {
                    imgid.add(ad.getKey());
                    dist.add("gos"); // aqui afagirem les distàncies amb ad.child(distancia).getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Log.i("Adlist","Failed on retrieving the adlist");
            }
        });



        adapter = new ListAdapter(
                this,
                imgid,
                dist

        );
        list.setAdapter(adapter);

        signInAnonymously();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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




    public void createAd(View view){ //anar al layout i assignar aquest metode a un botó per a iniciar la infoactivity
        Intent intent = new Intent(this, CreaActivity.class);
        String userid= "1"; //id de l'anunci "query de key de l'anunci clicat"
        intent.putExtra("user",userid);
        startActivity(intent);

    }


}


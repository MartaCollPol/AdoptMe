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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//import per obtenir l'id unic del dispositiu

public class MainActivity extends AppCompatActivity {

    //Referència a les autentificacions del firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btn_info = (Button)findViewById(R.id.btn_info);

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

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showinfo(v);
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


    public void showinfo(View view){ //anar al layout i assignar aquest metode a un botó per a iniciar la infoactivity
        Intent intent = new Intent(this, InfoActivity.class);
        String adid= "1"; //id de l'anunci "query de key de l'anunci clicat"
        intent.putExtra("ad",adid);
        startActivity(intent);

    }

    public void createAd(View view){ //anar al layout i assignar aquest metode a un botó per a iniciar la infoactivity
        Intent intent = new Intent(this, CreaActivity.class);
        String userid= "1"; //id de l'anunci "query de key de l'anunci clicat"
        intent.putExtra("user",userid);
        startActivity(intent);

    }


}


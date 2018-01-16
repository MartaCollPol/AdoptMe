package barcons.pol.adoptme;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import barcons.pol.adoptme.Objectes.User;
import barcons.pol.adoptme.Utils.FirebaseReferences;


public class FirstTimeActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        Button next = (Button) findViewById(R.id.btn_next);
        final EditText nom = (EditText) findViewById(R.id.first_nom);
        final EditText cognom = (EditText) findViewById(R.id.first_cognom);

        //Obtenim el Id del dispositiu des de la MainActivity.
        Intent data = getIntent();
        final String uid = data.getStringExtra("uid");

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameToSave = nom.getText().toString()+" "+ cognom.getText().toString();
                User user = new User(nameToSave,uid);
                UsersRef.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }
        });

    }


}

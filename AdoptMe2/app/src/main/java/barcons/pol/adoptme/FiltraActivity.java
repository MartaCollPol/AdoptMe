package barcons.pol.adoptme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class FiltraActivity extends AppCompatActivity {

    CheckBox Localització;
    CheckBox Sexe;
    CheckBox Edat;
    CheckBox mascle;
    CheckBox femella;
    CheckBox desc;

    //Menú de la barra de dalt de Filtractivity
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_filtra, menu);
        return true;
    }

    //Opcions que hi haura a la barra de dalt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;




        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtra);

        //Per que aparegui el botó de BACK a la barra de dalt
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Localització = (CheckBox)findViewById(R.id.filtra_loc);
        Edat = (CheckBox)findViewById(R.id.filtra_edat);
        Sexe = (CheckBox)findViewById(R.id.filtra_sexe);
        mascle = (CheckBox)findViewById(R.id.sexe_mascle);
        femella = (CheckBox)findViewById(R.id.sexe_femella);
        desc = (CheckBox)findViewById(R.id.edat_desconegut);

        desc.setEnabled(false);
        mascle.setEnabled(false);
        femella.setEnabled(false);


        //Fem que només un dels checkbox estigui activat al mateix temps
        mascle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenchecked(mascle,femella);

            }

        });
        femella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenchecked(femella,mascle);
            }
        });

        Edat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenchecked2(Edat,desc);
            }
        });

        Sexe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenchecked3(Sexe,mascle,femella);
            }
        });


    }

    private void whenchecked(CheckBox a, CheckBox b) {
        if (a.isChecked()) {
            b.setChecked(false);
        }

    }


    private void whenchecked2(CheckBox a, CheckBox b) {

        if (a.isChecked()) {
            b.setEnabled(true);
        }

        if (!(a.isChecked())){
            b.setChecked(false);
            b.setEnabled(false);
        }
    }


    private void whenchecked3(CheckBox a, CheckBox b, CheckBox c) {

        if (a.isChecked()) {
            b.setEnabled(true);
            c.setEnabled(true);
        }

        if (!(a.isChecked())){
            b.setChecked(false);
            b.setEnabled(false);
            c.setChecked(false);
            c.setEnabled(false);
        }
    }


}

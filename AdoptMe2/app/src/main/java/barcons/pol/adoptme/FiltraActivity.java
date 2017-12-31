package barcons.pol.adoptme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import org.florescu.android.rangeseekbar.RangeSeekBar;


public class FiltraActivity extends AppCompatActivity {

    CheckBox loc;
    CheckBox Sexe;
    CheckBox Edat;
    CheckBox mascle;
    CheckBox femella;
    CheckBox desc;
    RangeSeekBar loc_bar;
    RangeSeekBar edat_bar;

    //Menú de la barra de dalt de Filtractivity
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_filtra, menu);
        return true;
    }

    //Opcions que hi haura a la barra de dalt de FiltraActivity
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

        // Setup the new range seek bar
        RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<>(this);
        // Set the range
        rangeSeekBar.setRangeValues(15, 90);
        rangeSeekBar.setSelectedMinValue(20);
        rangeSeekBar.setSelectedMaxValue(88);

        // Seek bar for which we will set text color in code
        loc_bar = (RangeSeekBar) findViewById(R.id.loc_bar);
        edat_bar = (RangeSeekBar) findViewById(R.id.edat_bar);
        loc_bar.setTextAboveThumbsColorResource(android.R.color.holo_blue_dark);
        edat_bar.setTextAboveThumbsColorResource(android.R.color.holo_blue_dark);

        //Per que aparegui el botó de BACK a la barra de dalt
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loc = (CheckBox)findViewById(R.id.filtra_loc);
        Edat = (CheckBox)findViewById(R.id.filtra_edat);
        Sexe = (CheckBox)findViewById(R.id.filtra_sexe);
        mascle = (CheckBox)findViewById(R.id.sexe_mascle);
        femella = (CheckBox)findViewById(R.id.sexe_femella);
        desc = (CheckBox)findViewById(R.id.edat_desconegut);

        desc.setEnabled(false);
        mascle.setEnabled(false);
        femella.setEnabled(false);
        loc_bar.setEnabled(false);
        edat_bar.setEnabled(false);


        //Fem que només un dels checkbox estigui activat al mateix temps, i condicionem alguns
        // Checkbox a que d'altres estiguin clicats prèviament
        // (funcions whenchecked, whenchecked2 i whenchecked3)

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
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whenchecked4(loc);
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
            edat_bar.setEnabled(true);

        }

        if (!(a.isChecked())){
            b.setChecked(false);
            b.setEnabled(false);
            edat_bar.setEnabled(false);
            edat_bar.resetSelectedValues();

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

    private void whenchecked4(CheckBox a) {

        if (a.isChecked()) {
            loc_bar.setEnabled(true);

        }

        if (!(a.isChecked())){
            loc_bar.setEnabled(false);
            loc_bar.resetSelectedValues();


        }
    }


}

package barcons.pol.adoptme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import barcons.pol.adoptme.Utils.FirebaseReferences;


public class FiltraActivity extends AppCompatActivity {

    CheckBox loc;
    CheckBox Sexe;
    CheckBox Edat;
    CheckBox mascle;
    CheckBox femella;
    CheckBox desc;
    RangeSeekBar<Integer> edat_bar;
    private SeekBar loc_bar;
    private TextView valor_km;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);

    int km = 10;
    int edatMin;
    int edatMax;

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

            case R.id.action_OK:
                String code=null;
                Intent data = new Intent();
                if(mascle.isChecked()){
                    code="0"; //TODO: pensar millor, querys complexes
                }
                else if(femella.isChecked()){
                    code="1";
                    //query=QueryMF("sexe","female");
                }
                else if(Edat.isChecked()){
                    if(desc.isChecked()){
                        code="2";//query=AdsRef.orderByChild("edat/unknown").equalTo(true);
                    }
                    if(edat_bar.getAbsoluteMaxValue()!=edatMax || edat_bar.getAbsoluteMinValue()!=edatMin){
                        code="3";
                        data.putExtra("EdatMin",edatMin);
                        data.putExtra("EdatMax",edatMax);
                    }
                }
                else if(loc.isChecked()){
                    code="4";
                    data.putExtra("Km",km);
                }

                data.putExtra("Codi", code);

                setResult(RESULT_OK, data);
                finish();
                return true;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtra);

        loc_bar = (SeekBar) findViewById(R.id.loc_bar);
        valor_km = (TextView) findViewById(R.id.valor_km);

        loc = (CheckBox)findViewById(R.id.filtra_loc);
        Edat = (CheckBox)findViewById(R.id.filtra_edat);
        Sexe = (CheckBox)findViewById(R.id.filtra_sexe);
        mascle = (CheckBox)findViewById(R.id.sexe_mascle);
        femella = (CheckBox)findViewById(R.id.sexe_femella);
        desc = (CheckBox)findViewById(R.id.edat_desconegut);
        edat_bar = (RangeSeekBar) findViewById(R.id.edat_bar);

        //Per que aparegui el botó de BACK a la barra de dalt
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the range
        RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<>(this);
        // Set the range
        rangeSeekBar.setRangeValues(15, 90);
        rangeSeekBar.setSelectedMinValue(20);
        rangeSeekBar.setSelectedMaxValue(88);

        edat_bar.setTextAboveThumbsColorResource(android.R.color.holo_blue_dark);


        edat_bar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>(){
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,Integer minValue, Integer maxValue) {
                edatMin=minValue;
                edatMax=maxValue;
            }

        });

        loc_bar.setProgress(10); //Per defecte
        valor_km.setText("10 km");

        loc_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                valor_km.setText(progress + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                km = seekBar.getProgress();
            }
        });

        //inicialitzem checks
        desc.setEnabled(false);
        mascle.setEnabled(false);
        femella.setEnabled(false);
        loc_bar.setEnabled(false);
        edat_bar.setEnabled(false);
        //Listeners
        mascle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whencheckedMF(mascle,femella);
            }

        });
        femella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whencheckedMF(femella,mascle);
            }
        });
        Edat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Edat.isChecked()) {
                    desc.setEnabled(true);
                    FiltraActivity.this.edat_bar.setEnabled(true);

                }
                if (!(Edat.isChecked())){
                    desc.setChecked(false);
                    desc.setEnabled(false);
                    FiltraActivity.this.edat_bar.setEnabled(false);
                    FiltraActivity.this.edat_bar.resetSelectedValues();
                }
            }
        });
        Sexe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Sexe.isChecked()) {
                    mascle.setEnabled(true);
                    femella.setEnabled(true);
                }
                if (!(Sexe.isChecked())){
                    mascle.setChecked(false);
                    mascle.setEnabled(false);
                    femella.setChecked(false);
                    femella.setEnabled(false);
                }
            }
        });
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loc.isChecked()) {
                    loc_bar.setEnabled(true);

                }
                if (!(loc.isChecked())){
                    loc_bar.setEnabled(false);
                    loc_bar.setProgress(10);

                }
            }
        });

    }
    //Fem que només un dels checkbox estigui activat al mateix temps
    private void whencheckedMF(CheckBox a, CheckBox b) {
        if (a.isChecked()) {
            b.setChecked(false);
        }
    }

}

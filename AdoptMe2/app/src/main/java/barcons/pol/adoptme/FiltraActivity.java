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

import org.florescu.android.rangeseekbar.RangeSeekBar;


public class FiltraActivity extends AppCompatActivity {

    CheckBox Loc;
    CheckBox Sexe;
    CheckBox Edat;
    CheckBox mascle;
    CheckBox femella;
    CheckBox desc; //desconegut
    RangeSeekBar<Integer> edat_bar;
    private SeekBar loc_bar;
    private TextView valor_km;


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

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;

            case R.id.action_OK:
                String code="17"; //Res seleccionat

                Intent data = new Intent(this,MainActivity.class);
                if(femella.isChecked()&&!Loc.isChecked() && !Edat.isChecked()) code="0";
                else if(mascle.isChecked()&&!Loc.isChecked() && !Edat.isChecked())code="1";
                else if(Edat.isChecked()&&!Sexe.isChecked() && !Loc.isChecked()){
                    if(desc.isChecked()) code="2";
                    else{
                        code="3";
                        data.putExtra("EdatMin",String.valueOf(edatMin));
                        data.putExtra("EdatMax",String.valueOf(edatMax));
                    }
                }
                else if(Loc.isChecked()&&!Sexe.isChecked() && !Edat.isChecked()){
                    code="4";
                    data.putExtra("Km",km);
                }
                else if(femella.isChecked()&& Edat.isChecked()&&!Loc.isChecked()){
                    if(desc.isChecked()) code="5";
                    else {
                        code="6";
                        data.putExtra("EdatMin",String.valueOf(edatMin));
                        data.putExtra("EdatMax",String.valueOf(edatMax));
                    }
                }
                else if(mascle.isChecked()&& Edat.isChecked()&& !Loc.isChecked()){
                    if(desc.isChecked()) code="7";
                    else{
                        code="8";
                        data.putExtra("EdatMin",String.valueOf(edatMin));
                        data.putExtra("EdatMax",String.valueOf(edatMax));
                    }
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

        Loc = (CheckBox)findViewById(R.id.filtra_loc);
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
                desc.setChecked(false);
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
                    edat_bar.setEnabled(true);
                }
                if (!(Edat.isChecked())){
                    desc.setChecked(false);
                    desc.setEnabled(false);
                    edat_bar.setEnabled(false);
                    edat_bar.resetSelectedValues();
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
        Loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Loc.isChecked()) {
                    loc_bar.setEnabled(true);

                }
                if (!(Loc.isChecked())){
                    loc_bar.setEnabled(false);
                    loc_bar.setProgress(10);

                }
            }
        });
        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(desc.isChecked()){
                    edat_bar.resetSelectedValues();
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

package barcons.pol.adoptme.Utils;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by Marta on 15/01/2018.
 */

public class Queries {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference AdsRef = database.getReference(FirebaseReferences.adsRef);

    private String mCodi;
    private int mEdatMin;
    private int mEdatMax;

    public Queries(String mCodi, int mEdatMin, int mEdatMax){
        this.mCodi=mCodi;
        this.mEdatMin=mEdatMin;
        this.mEdatMax=mEdatMax;
    }
    //TODO: codi 4 localització. 9 famella i location ,10 mascle i location ,11 famella edat_kn i location,12 mascle edat_kn i location,13 famella edat_ukn i location
    //14 mascle edat_ukn i location,15 edat_kn i location,16 edat_unk i location
    public Query ResultQuery() {
        Log.e("mcoll","Query: "+mCodi);
        String edatmin;
        String edatmax;
        if(mEdatMin>=10){
            edatmin="d"+String.valueOf(mEdatMin);
        }else edatmin=String.valueOf(mEdatMin);
        if(mEdatMax>=10){
            edatmax="d"+String.valueOf(mEdatMax);
        }else edatmax=String.valueOf(mEdatMax);

        switch (mCodi) {
            case "0":
                return AdsRef.orderByChild("sexe").equalTo("female");
            case "1":
                return AdsRef.orderByChild("sexe").equalTo("male");
            case "2":
                return AdsRef.orderByChild("edat/unknown").equalTo(true);
            case "3":
                return AdsRef.orderByChild("edat/known").startAt(mEdatMin).endAt(mEdatMax);
            case "5":
                return AdsRef.orderByChild("query").equalTo("F_-1");
            case "6":
                return AdsRef.orderByChild("query").startAt("F_" + edatmin).endAt("F_" + edatmax);
            case "7":
                return AdsRef.orderByChild("query").equalTo("M_-1");
            case "8":
                Log.e("mcoll","Edat min"+mEdatMin);
                Log.e("mcoll","Edat max"+mEdatMax);
                return AdsRef.orderByChild("query").startAt("M_" + edatmin).endAt("M_" + edatmax);

            default:
                return AdsRef.limitToLast(100);

        }

    }
}

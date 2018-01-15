package barcons.pol.adoptme.Objectes;

import java.util.HashMap;

/**
 * Created by Marta on 07/12/2017.
 */

public class Ad {

    public String user;
    public String sexe;
    public edat edat;
    public String desc;
    public String url;
    public HashMap<String, Object> saved;
    public String data;
    public String query;




    public Ad() {
        // Default constructor required for calls to DataSnapshot.getValue(Ad.class)
    }

    public Ad(String userid,int edatknown,boolean edatunknown, String sexe,String descripcio,String url,String data,String query) {
        this.user = userid;
        this.desc = descripcio;
        this.edat = new edat(edatknown,edatunknown);
        this.sexe = sexe;
        this.url = url;
        this.data = data;
        this.query = query;

    }

}
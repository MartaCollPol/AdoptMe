package barcons.pol.adoptme.Objectes;

/**
 * Created by Marta on 07/12/2017.
 */

public class Ad {

    public String user;
    public String sexe;
    public edat edat;
    public String desc;
    public String url;



    public Ad() {
        // Default constructor required for calls to DataSnapshot.getValue(Ad.class)
    }

    public Ad(String userid,int edatknown,boolean edatunknown, String sexe,String descripcio,String url) {
        this.user = userid;
        this.desc = descripcio;
        this.edat = new edat(edatknown,edatunknown);
        this.sexe = sexe;
        this.url = url;

    }

}
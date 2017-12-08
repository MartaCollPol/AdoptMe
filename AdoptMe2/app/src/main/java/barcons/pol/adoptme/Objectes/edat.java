package barcons.pol.adoptme.Objectes;

/**
 * Created by Marta on 08/12/2017.
 */

public class edat {

    public int known;
    public boolean unknown;

    public edat() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public edat(int known, boolean unknown){
        this.known = known;
        this.unknown = unknown;
    }
}

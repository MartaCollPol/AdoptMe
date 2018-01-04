package barcons.pol.adoptme.Objectes;

/**
 * Created by Marta on 07/12/2017.
 */

public class User {

    public String name;
    public String uid; //id del dispositiu
    public String email;
    public Long phone;



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String uid) {
        this.name = username;
        this.uid = uid;
        this.email = null;
        this.phone = null;

    }

}
package barcons.pol.adoptme.Objectes;

/**
 * Created by Marta on 07/12/2017.
 */

public class User {

    public String name;
    public String id; //id del dispositiu
    public String email;
    public Long phone;



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String id) {
        this.name = username;
        this.id = id;
        this.email = null;
        this.phone = null;

    }

}
/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private String id;
    private String userType;

    public User(String username, String id, String userType) {
        this.username = username;
        this.id = id;
        this.userType = userType;
    }
    public String getId() {
        return id;
    }
    public String getUserType(){ return userType; }
}

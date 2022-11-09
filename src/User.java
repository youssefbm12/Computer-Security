public class User {
    private String id;
    private String password;
    private String key;

    public User(String id, String password, String key){
        this.id=id;
        this.password=password;
        this.key=key;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getKey() {
        return key;
    }
}

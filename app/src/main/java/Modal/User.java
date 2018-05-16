package Modal;

/**
 * Created by Dell on 12/4/2017.
 */

public class User {

    String name;
    String email;
    String userid;
    String pass;
    String mobile;
    String city;

    public User() {
    }

    public User(String name, String email, String userId, String pass, String mobile, String city) {
        this.name = name;
        this.email = email;
        this.userid = userId;
        this.pass = pass;
        this.mobile = mobile;
        this.city = city;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

}

package vn.lobie.campus.utils;

public class DbStatic {
    public String email;
    public String name;
    public String UserId;
    public String password;

    public DbStatic(String email, String name, String UserId, String password) {
        this.email = email;
        this.name = name;
        this.UserId = UserId;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    
    public String getName() {
        return name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
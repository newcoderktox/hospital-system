// src/main/java/models/User.java
package models;

public abstract class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;

    // Constructor
    public User(int id, String username, String password, String name, String surname, String phoneNumber, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    // Setters (password ve username dışındakiler değiştirilebilir olsun)
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Abstract metodlar: Her kullanıcı türü için farklı davranışlar sergileyecek metodlar
    public abstract String getUserType(); // Kullanıcı türünü dönecek (Doktor, Hasta)

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", userType='" + getUserType() + '\'' +
                '}';
    }
}
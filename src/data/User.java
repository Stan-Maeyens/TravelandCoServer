/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author Stan
 */
public class User {
    String email, passwd, name;

    public User(String email, String passwd, String name) {
        this.email = email;
        this.passwd = passwd;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getName() {
        return name;
    }
    
    
}

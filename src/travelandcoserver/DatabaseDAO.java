/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelandcoserver;

import data.Travel;
import data.User;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stan
 */
public class DatabaseDAO {

    private Connection conn;
    private Properties props;

    public DatabaseDAO() {
        props = getProperties();
        try {     
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/travelsandco", "travelsandco", "travelsandco");

            removeUser("qqsmdfoi");
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean addUser(String email, String passwd, String name) {
        boolean ret = false;
        try {
            PreparedStatement stm = conn.prepareStatement(props.getProperty("CREATE_USER"));
            try {
                stm.setString(1, email);
                stm.setString(2, passwd);
                stm.setString(3, name);
                stm.executeUpdate();
                ret = true;
            }
            finally
            {
                stm.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public boolean removeUser(String email){
        boolean ret = false;
        try {
            PreparedStatement stm = conn.prepareStatement(props.getProperty("DELETE_USER"));
            try {
                stm.setString(1, email);
                stm.executeUpdate();
                ret = true;
            }
            finally
            {
                stm.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public User getUser(String email){
        User ret = null;
        try {
            PreparedStatement stm = conn.prepareStatement(props.getProperty("GET_USER"));
            try {
                stm.setString(1, email);
                ResultSet rs = stm.executeQuery();
                if(rs.next()){
                    ret = new User(rs.getString("EMAIL"), rs.getString("PASSWD"), rs.getString("NAME"));
                }
            }
            finally
            {
                stm.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public boolean addTravel(String name){
        boolean ret = false;
        try {
            PreparedStatement stm = conn.prepareStatement(props.getProperty("CREATE_TRAVEL"));
            try {
                stm.setString(1, name);
                stm.executeUpdate();
                ret = true;
            }
            finally
            {
                stm.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public boolean removeTravel(int id){
        boolean ret = false;
        try {
            PreparedStatement stm = conn.prepareStatement(props.getProperty("DELETE_TRAVEL"));
            try {
                stm.setInt(1, id);
                stm.executeUpdate();
                ret = true;
            }
            finally
            {
                stm.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public List<Travel> getTravels(String email){
        List<Travel> ret = new ArrayList<>();
        try {
            PreparedStatement stm = conn.prepareStatement(props.getProperty("GET_TRAVELS"));
            try {
                stm.setString(1, email);
                ResultSet rs = stm.executeQuery();
                if(rs.next()){
                    Travel t = new Travel(rs.getInt("TRAVELID"), rs.getString("NAME"));
                    ret.add(t);
                }
            }
            finally
            {
                stm.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    public List<User> getUsersInTravel(int id){
        List<User> ret = new ArrayList<>();
        try {
            PreparedStatement stm = conn.prepareStatement(props.getProperty("GET_USERS_IN_TRAVEL"));
            try {
                stm.setInt(1, id);
                ResultSet rs = stm.executeQuery();
                while(rs.next()){
                    User u = new User(rs.getString("EMAIL"), rs.getString("PASSWD"), rs.getString("NAME"));
                    ret.add(u);
                }
            }
            finally
            {
                stm.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private Properties getProperties() {
        Properties props = new Properties();
        try {
            FileInputStream in = new FileInputStream("src/resources/sql.properties");
            props.load(in);
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(DatabaseDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
}

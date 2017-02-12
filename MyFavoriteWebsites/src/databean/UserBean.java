package databean;
/**
 * 08-672 Assignment 3.
 * @author Jiayi Xie
 * @id jiayix
 * 11/25/2016
 */

public class UserBean {
	private int userId;
    private String emailAddress;
    private String password;
    private String firstName;
    private String lastName;
    
    public int getUserId() {
        return userId;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEmailAddress(String email) {
        this.emailAddress = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
}

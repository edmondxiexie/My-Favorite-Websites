package formbean;
/**
 * 08-672 Assignment 3.
 * @author Jiayi Xie
 * @id jiayix
 * 11/25/2016
 */

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class RegisterForm  {
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String password;
    private String button;
	
    public RegisterForm(HttpServletRequest request) {
        emailAddress = request.getParameter("emailAddress");
    	firstName = request.getParameter("firstName");
    	lastName = request.getParameter("lastName");
    	password = request.getParameter("password");
    	button = request.getParameter("button");
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getButton() {
        return button;
    }

    public boolean isPresent() { 
        return button != null; 
    }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<String>();

        if (emailAddress == null || emailAddress.length() == 0) 
            errors.add("Email address is required");
        
        if (firstName == null || firstName.length() == 0) 
        	errors.add("First name is required");
        
        if (lastName == null || lastName.length() == 0) 
        	errors.add("Last name is required");
        
        if (password == null || password.length() == 0) 
        	errors.add("Password is required");
        
        if (button == null) 
        	errors.add("Button is required");

        if (errors.size() > 0) return errors;

        if (!button.equals("Submit")) 
        	errors.add("Invalid button");
		
        return errors;
    }
}


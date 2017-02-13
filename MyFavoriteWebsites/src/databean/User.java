package databean;
import org.genericdao.PrimaryKey;

@PrimaryKey("userName")
public class User {
    private int userId;
    private String emailAddress;
    private String password;
    private String firstName;
    private String lastName;
    
    public int getUserId()                              { return userId;                    }
    public String getEmailAddress()                     { return emailAddress;              }
    public String getPassword()                         { return password;                  }
    public String getFirstName()                        { return firstName;                 }
    public String getLastName()                         { return lastName;                  }
    
    public void setUserId(int userId)                   { this.userId = userId;             }
    public void setEmailAddress(String emailAddress)    { this.emailAddress = emailAddress; }
    public void setPassword(String password)            { this.password = password;         }
    public void setFirstName(String firstName)          { this.firstName = firstName;       }
    public void setLastName(String lastName)            { this.lastName = lastName;         }
}

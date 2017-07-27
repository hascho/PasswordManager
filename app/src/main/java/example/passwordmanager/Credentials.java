package example.passwordmanager;

import java.io.Serializable;

/**
 * Created by hassanchowdhury on 09/05/2017.
 */

public class Credentials implements Serializable
{
    private String website  = "";
    private String username = "";
    private String password = "";

    public Credentials()
    {
        // required empty contructor for firebase
    }

    public Credentials(String website,
                       String username,
                       String password)
    {
        this.website  = website;
        this.username = username;
        this.password = password;
    }

    // getters
    String getWebsite()     { return website;   }
    String getUsername()    { return username;  }
    String getPassword()    { return password;  }

    @Override
    public String toString() {
        return "Credentials{" +
                "website='" + website + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

package example.passwordmanager;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hassanchowdhury on 04/02/2017.
 */

public class ListEntry implements Serializable
{
    private int image_id;
    private String name;
    private String description;
    private String key;
    private String date;
    private Credentials credentials;
    private String secureNote;

//    public enum Category
//    {
//        LOGIN_CREDENTIALS, SECURE_NOTE
//    }
    //private static Category category;
    @Exclude
    public static final int LOGIN_CREDENTIALS = 0;
    public static final int SECURE_NOTE = 1;
    @Exclude
    public int getCategory() { return credentials != null ? LOGIN_CREDENTIALS : SECURE_NOTE; }

    public ListEntry()
    {
        // required empty constructor for firebase
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("image_id", image_id);
        result.put("name", name);
        result.put("description", description);
        result.put("key", key);
        result.put("date", date);
        result.put("credentials", credentials);
        result.put("secureNote", secureNote);

        return result;
    }

    // getters
    public int getImage_id()            { return image_id;      }
    public String getName()             { return name;          }
    public String getDescription()      { return description;   }
    public String getKey()              { return key;           }
    public String getDate()             { return date;          }
    public Credentials getCredentials() { return credentials;   }
    public String getSecureNote()       { return secureNote;    }

    @Exclude
    public String getOptionalParams()
    {
        return credentials != null ? getCredentials().toString() : "'" + secureNote + "'";
    }

    @Override
    public String toString() {
        return "ListEntry{" +
                "image_id=" + image_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", key='" + key + '\'' +
                ", date='" + date + '\'' +
                ", credentials=" + credentials +
                ", secureNote='" + secureNote + '\'' +
                '}';
    }

    private ListEntry(ListEntryBuilder builder)
    {
        this.image_id       = builder.image_id;
        this.name           = builder.name;
        this.description    = builder.description;
        this.key            = builder.key;
        this.date           = builder.date;
        this.credentials    = builder.credentials;
        this.secureNote     = builder.secureNote;
    }

    public static class ListEntryBuilder
    {
        // required parameters
        private int image_id;
        private String name;
        private String description;
        private String key;
        private String date;

        // optional parameters
        private Credentials credentials;
        private String secureNote;

        public ListEntryBuilder(int image_id,
                                String name,
                                String description,
                                String key,
                                String date)
        {
            this.image_id = image_id;
            this.name = name;
            this.description = description;
            this.key = key;
            this.date = date;
        }

        public ListEntryBuilder setCredentials(Credentials credentials)
        {
            this.credentials = credentials;
            return this;
        }

        public ListEntryBuilder setSecureNote(String sec_note)
        {
            this.secureNote = sec_note;
            return this;
        }


        public ListEntry build()
        {
            return new ListEntry(this);
        }
    }
}

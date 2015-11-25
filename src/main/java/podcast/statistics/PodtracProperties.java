
package podcast.statistics;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrewlloyd
 */
public class PodtracProperties {

    private static final Logger LOGGER = Logger.getLogger(PodtracProperties.class.getName());
    private final String m_filepath = System.getProperty("user.dir") + File.separator + "src/main/java/resources/podtrac.properties";
    private final String m_user;
    private final String m_pass;
    private final String m_dropboxAppKey;
    private final String m_dropboxAppSecret;
    private final String m_dropboxAppToken;
    
    public PodtracProperties() {
        Properties prop = new Properties();
        
        try {
            File file = new File(m_filepath);
            prop.load(new FileReader(file));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        m_user = prop.getProperty("user");
        m_pass = prop.getProperty("pass");
        m_dropboxAppKey = prop.getProperty("dropbox.app.key");
        m_dropboxAppSecret = prop.getProperty("dropbox.app.secret");
        m_dropboxAppToken = prop.getProperty("dropbox.app.token");
    }
    
    public String getUser() {
        return m_user;
    }
    
    public String getPass() {
        return m_pass;
    }

    public String getDropboxAppKey() {
        return m_dropboxAppKey;
    }

    public String getDropboxAppSecret() {
        return m_dropboxAppSecret;
    }

    public String getDropboxAppToken() {
        return m_dropboxAppToken;
    }
    
    
    
    public static void main(String[] args) {
        new PodtracProperties();
    }
    
}

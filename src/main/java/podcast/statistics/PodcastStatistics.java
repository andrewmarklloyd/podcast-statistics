package podcast.statistics;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrewlloyd
 */
public class PodcastStatistics {

    private static final Logger LOGGER = Logger.getLogger(PodcastStatistics.class.getName());

    private static final String dir = System.getProperty("user.dir") + File.separator + "stats.txt";

    private final PodtracProperties m_properties;

    private final PodtracInterface m_podtracInterface;

    public PodcastStatistics() {
        m_properties = new PodtracProperties();
        m_podtracInterface = new PodtracInterface(m_properties);
    }

    public void publishStats() {
        int total = m_podtracInterface.getNinetyDayDownloads();
//        int total = 1;

        if (total != -1) {
            //initialize dropbox api
            DbxRequestConfig config = new DbxRequestConfig(
                    "Podcast Statistics Publisher", Locale.getDefault().toString());
            DbxClient client = new DbxClient(config, m_properties.getDropboxAppToken());
            try {
                //download remote copy to local
                String dir = System.getProperty("user.dir") + File.separator + "stats.txt";
                FileOutputStream outputStream = new FileOutputStream(dir);
                DbxEntry.File downloadedFile = client.getFile("/Podcast/stats.txt", null, outputStream);

                //open local copy, update file
                java.io.File file = new File(dir);
                FileWriter writer = new FileWriter(file, true);
                LocalDateTime dateTime = LocalDateTime.now();
                writer.write(dateTime.getDayOfWeek().toString() + ", "+ dateTime.toString() + " ~ " + total + "\n");
                writer.close();
                
                //push changes to remote copy
                FileInputStream inputStream = new FileInputStream(file);
                DbxEntry.File uploadedFile = client.uploadFile("/Podcast/stats.txt",
                        DbxWriteMode.update(null), file.length(), inputStream);
                System.out.println("Uploaded: " + uploadedFile.path);
                
                //delete local copy
                file.delete();

            } catch (DbxException | IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        new PodcastStatistics().publishStats();

    }

}

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;

import org.junit.Test;

public class WebCrawlerTest {

	@Test
	public void testProgram() throws IOException
	{
		WebCrawler testwc = new WebCrawler(5);
		try {
			testwc.crawl("https://moodle.bbk.ac.uk/pluginfile.php/370036/mod_resource/content/3/web%20crawler.pdf");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

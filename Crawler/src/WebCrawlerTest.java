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
			testwc.crawl("http://www.reddit.com/");
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

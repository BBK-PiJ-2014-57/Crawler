import java.io.*;
import java.net.*;
import java.sql.SQLException;

/**
 * Class to implement the CourseWork objective. Provides a override-able method of search()
 * Searches a given webpage for links, storing them in an SQLite Database.
 * @author lewispalmer
 *
 */
public class WebCrawler {

	private int defaultDepth = 5;
	private URL startingURL;
	private UrlDatabase urlDB;
	private int depth = 0;
	private String HTMLLinkTag = "href=\"";
	
	/**
	 * Ctor to allow the programmer to override the depth of the Crawler
	 * @param newDepth the new depth.
	 */
	public WebCrawler(int newDepth)
	{
		this.defaultDepth = newDepth;
	}
	
	/**
	 * Starts the Web crawling with the given URL, creates the SQLite Database and 
	 * loads the first URL as an InputStream
	 * @param URL the starting URL
	 * @throws SQLException If Database is not able to be created due to incorrect permissions
	 * @throws ClassNotFoundException in case the SQLite JDBC JAR cannot be found
	 * @throws MalformedURLExpection exception thrown if given URL is invalid
	 */
	public void crawl(String URL) throws MalformedURLException, ClassNotFoundException, SQLException
	{
		this.startingURL = new URL(URL);
		this.urlDB = new UrlDatabase();
		crawlPage(this.startingURL);
	}
	
	/**
	 * Method to create Input Stream from given URL
	 * @param url URL for which the HTML InputStream is returned.
	 * @return the HTML InputStream
	 */
	private InputStream urltoInputStream(URL url)
	{
		InputStream iStream = null;
		URLConnection urlConn = null;
		try {
			urlConn = url.openConnection();
		} catch (IOException e) {
			return iStream;
		}
		try {
			return urlConn.getInputStream();
		} catch (IOException e) {
			return iStream;
		}
	}
	
	private void crawlPage(URL pageURL)
	{
		InputStream page = urltoInputStream(pageURL);
	}
}

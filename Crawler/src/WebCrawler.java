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

	/**
	 * Pre-set override-able crawling depth.
	 */
	private int defaultDepth = 5;
	
	/**
	 * User Inputted URL to start off the crawler.
	 */
	private URL startingURL;
	
	/**
	 * Current page searching URL.
	 */
	private URL currURL;
	
	/**
	 * SQLite DB to store the links we find.
	 */
	private UrlDatabase urlDB;
	
	private int depth = 0;
	/**
	 * HTML Tag we are searching for in the InputStream
	 */
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
	 * @throws IOException if there are problems with the InputReader
	 * @throws MalformedURLExpection exception thrown if given URL is invalid
	 */
	public void crawl(String URL) throws ClassNotFoundException, SQLException, IOException
	{
		this.startingURL = new URL(URL);
		this.urlDB = new UrlDatabase();
		this.currURL = this.startingURL;
		crawlPage(this.startingURL);
		boolean urlsLeft = true;
		while(depth < this.defaultDepth && urlsLeft)
		{
			while(this.currURL != null && urlsLeft)
			{
				String newURL = this.urlDB.returnTopURL(depth);
				if(newURL != null)
				{
					this.currURL = new URL(newURL);
					crawlPage(this.currURL);
				}
				else
				{
					if(depth == 1)
						urlsLeft = false;
					--depth;
				}
			}
		}
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
	
	/**
	 * Function which crawls the page, searches for links and adds them to the DB.
	 * @param pageURL the web-page to be searched.
	 * @throws ClassNotFoundException if SQLite JAR cannot be found.
	 * @throws IOException if there are problems with InputReader
	 */
	private void crawlPage(URL pageURL) throws ClassNotFoundException, IOException
	{
		int idx = 0;
		boolean search4Href = true;
		HTMLread reader = new HTMLread();
		try{
			this.urlDB.insertEntryIntoDB(depth, pageURL.toString(), true);
		}
		catch(SQLException ex)
		{
			System.out.println("Unable to insert " + pageURL.toString() + " into Database");
		}
		InputStream page = urltoInputStream(pageURL);
		if(page != null)
		{
			++depth;
			try
			{
				while(page.available() > 0)
				{
					while(reader.skipSpace(page, '<') == Character.MIN_VALUE)
					{
						if(reader.readUntil(page, 'a', '>'))
						{
							while(reader.readUntil(page, 'h',  '>'))
							{
								idx = 1;
								search4Href = true;
								while(idx < 6 && search4Href)
								{
									if(reader.skipSpace(page, HTMLLinkTag.charAt(idx)) != Character.MIN_VALUE)
									{
										search4Href = false;
									}
									++idx;
								}
								if(search4Href)
								{
									String URLplusQuot = reader.readString(page, '\"', '#');
									if(URLplusQuot != null)
									{
										try {
											this.urlDB.insertEntryIntoDB(depth, URLplusQuot.substring(0, URLplusQuot.length() - 2), true);
										} catch (SQLException e) {
											System.out.println("Unable to add: " + URLplusQuot.substring(0, URLplusQuot.length() - 2) + " to the DB");
										}
									}
								}
							}
						}
					}
				}
			}
			catch(IOException ex)
			{
				System.out.println("Done searching page: " + pageURL.toString());
			}
			finally
			{
				if(search())
				{
					try {
						this.urlDB.insertEntryIntoDB(depth, pageURL.toString(), false);
					} catch (SQLException e) {
						System.out.println("Unable to add: " + pageURL.toString() + " to the DB");
					}
				}
			}
		}
		page.close();
	}
	
	/**
	 * Function for the next programmer to define which links to store in the results table.
	 * There is a private variable currURL which can be utilised.
	 * @return true for storing in the resultsTable.
	 */
	private boolean search()
	{
		return true;
	}
}

import java.io.File;
import java.sql.*;
/**
 * Class to deal with adding URLs to the relevant Databases.
 * @author lewispalmer
 *
 */
public class UrlDatabase {

	private Connection dB;
	String currentDir = System.getProperty("user.dir");
	private final String tempTableName = "tempURLs";
	private final String resultsTableName = "resultURLs";
	private final String idTableCol = "ID";
	private final String priorityTableCol = "Priority";
	private final String urlTableCol = "URL";
	
	/**
	 * Ctor creates the DB. Assuming it doesn't exist. Lets the programmer handle the Exceptions, since 
	 * the Database is essential for this program.
	 * @throws ClassNotFoundException if the SQLite JDBC is not in the Classpath.
	 * @throws SQLException if Database and therefore Tables already exist.
	 */
	public UrlDatabase() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.sqlite.JDBC");
	    dB = DriverManager.getConnection("jdbc:sqlite:" + currentDir
	    		+ File.separator + "urlDB.db");
	    createTables();
	}
	
	/**
	 * Re-opens connection to the Database, throws Exception upwards.
	 * @throws SQLException 
	 * @throws ClassNotFoundException if the SQLite JDBC is not in the Classpath.
	 */
	private void openConnection() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");
	    dB = DriverManager.getConnection("jdbc:sqlite:" + currentDir
	    		+ File.separator + "urlDB.db");
	}
	
	/**
	 * Closes connection before other processing.
	 * @throws SQLException
	 */
	private void closeConnection() throws SQLException
	{
		dB.close();
	}
	
	/**
	 * Creates the needed SQLite Tables to store URLS (one temporary, one for results).
	 * @throws SQLException
	 * @throws ClassNotFoundException if the SQLite JDBC is not in the Classpath.
	 */
	private void createTables() throws SQLException, ClassNotFoundException
	{
		openConnection();
		Statement query;
		String tempQuery = createCreateQuery(tempTableName);
		query = dB.createStatement();
		query.executeUpdate(tempQuery);
		String resultsQuery = createCreateQuery(resultsTableName);
		query.executeUpdate(resultsQuery);
		query.close();
		closeConnection();
	}
	
	/**
	 * Builds SQL Query for Creating a give Table
	 * @param tableName the given Table name
	 * @return the SQLite Query
	 */
	private String createCreateQuery(String tableName)
	{
		String toReturn = "CREATE TABLE ";
		toReturn += tableName;
		toReturn += "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " Priority INTEGER, "
				+ "URL TEXT);";
		return toReturn;
	}
	
	/**
	 * Builds SQL query for Inserting new URLs.
	 * @param priority integer for the depth of the crawl
	 * @param url the URL to store
	 * @param tableName the table to receive it.
	 * @return the SQLite query
	 */
	private String createInsertQuery(int priority, String url, String tableName)
	{
		String query = "INSERT INTO " + tableName + " (" + priorityTableCol + ", "
				+ urlTableCol + ") VALUES(" + Integer.toString(priority) + ", " +
				"'" + url + "'" + ");";
		return query;
	}
	
	/**
	 * Builds SQL query and inserts the given URL into the given Table, sends the throwables upwards.
	 * @param priority integer for the depth of the crawl
	 * @param url the URL to store
	 * @param tempDB boolean as to whether it inserts into the Temporary table or not.
	 * @throws SQLException if these is an issue with the Connection to the table or the SQL Query
	 * @throws ClassNotFoundException if the SQLite JDBC is not in the Classpath.
	 */
	public void insertEntryIntoDB(int priority, String url, boolean tempDB) throws SQLException, ClassNotFoundException
	{
		boolean add = true;
		if(tempDB)
		{
			//Given the way the WebCrawler works. Only need to check for the temp table.
			add = checkNewLink(url);
		}
		if(add)
		{
			openConnection();
			String sqlQuery = createInsertQuery(priority, url, (tempDB) ? tempTableName : resultsTableName);
			Statement query;
			query = dB.createStatement();
			query.executeUpdate(sqlQuery);
			query.close();
			closeConnection();
		}
	}
	
	/**
	 * Returns the next URL to dive into.
	 * @param priority the current depth of the program
	 * @return the next URL to process
	 * @throws ClassNotFoundException if the SQLite JDBC is not in the Classpath.
	 * @throws SQLException if there is an issue connecting and retrieving from the Database.
	 */
	public String returnTopURL(int priority) throws ClassNotFoundException, SQLException
	{
		openConnection();
		Statement query;
		int id = 0;
		query = dB.createStatement();
		String sql = "SELECT * FROM " + tempTableName + " ORDER BY ID;";
		ResultSet queryresult = query.executeQuery(sql);
		String url = null;
		int respriority = -1;
		boolean processed = false;
		while(queryresult.next() && !processed)
		{
			url = queryresult.getString(urlTableCol);
			id = queryresult.getInt(idTableCol);
			respriority = queryresult.getInt(priorityTableCol);
			if(respriority == priority)
			{
				String idsql = "UPDATE " + tempTableName + " set " + priorityTableCol + " = 0" + 
						" WHERE " + idTableCol + " = " + Integer.toString(id) + ";";
				query.executeUpdate(idsql);
				processed = true;
			}
		}
		queryresult.close();
		query.close();
		closeConnection();
		return url;
	}
	
	/**
	 * Ensures we have distinct values in the Database.
	 * @param url the URL to check
	 * @return true if this is a new Link
	 * @throws ClassNotFoundException if the SQLite JDBC is not in the Classpath.
	 * @throws SQLException if there is an issue connecting to the Database.
	 */
	private boolean checkNewLink(String url) throws ClassNotFoundException, SQLException
	{
		openConnection();
		Statement query;
		query = dB.createStatement();
		String sql = "SELECT * FROM " + tempTableName + " ORDER BY ID;";
		ResultSet queryresult = query.executeQuery(sql);
		String resurl = "";
		while(queryresult.next())
		{
			resurl = queryresult.getString(urlTableCol);
			if(url.equals(resurl))
				return false;
		}
		closeConnection();
		return true;
	}
}

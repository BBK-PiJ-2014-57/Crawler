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
	
	public UrlDatabase() throws ClassNotFoundException, SQLException
	{
		Class.forName("org.sqlite.JDBC");
	    dB = DriverManager.getConnection("jdbc:sqlite:" + currentDir
	    		+ File.separator + "urlDB.db");
	    createTables();
	}
	
	private void openConnection() throws SQLException, ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");
	    dB = DriverManager.getConnection("jdbc:sqlite:" + currentDir
	    		+ File.separator + "urlDB.db");
	}
	
	private void closeConnection() throws SQLException
	{
		dB.close();
	}
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
	
	private String createCreateQuery(String tableName)
	{
		String toReturn = "CREATE TABLE ";
		toReturn += tableName;
		toReturn += "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " Priority INTEGER, "
				+ "URL TEXT);";
		return toReturn;
	}
	
	private String createInsertQuery(int priority, String url, String tableName)
	{
		String query = "INSERT INTO " + tableName + " (" + priorityTableCol + ", "
				+ urlTableCol + ") VALUES(" + Integer.toString(priority) + ", " +
				"'" + url + "'" + ");";
		return query;
	}
	
	public void insertEntryIntoDB(int priority, String url, boolean tempDB) throws SQLException, ClassNotFoundException
	{
		boolean add = true;
		if(tempDB)
		{
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

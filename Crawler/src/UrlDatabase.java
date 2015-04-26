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
	    dB = DriverManager.getConnection("jdbc:sqlite:currentDir"
	    		+ File.separator + "urlDB.db");
	    createTables();
	}
	
	private void createTables() throws SQLException
	{
		Statement query;
		String tempQuery = createCreateQuery(tempTableName);
		query = dB.createStatement();
		query.executeUpdate(tempQuery);
		String resultsQuery = createCreateQuery(resultsTableName);
		query.executeUpdate(resultsQuery);
	}
	
	private String createCreateQuery(String tableName)
	{
		String toReturn = "CREATE TABLE ";
		toReturn += tableName;
		toReturn += " ID INT PRIMARY KEY AUTOINCREMENT, "
				+ " PRIORITY ID, "
				+ "URL TEXT)";
		return toReturn;
	}
}

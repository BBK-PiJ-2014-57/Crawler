import java.io.*;

/**
 * Class created to read the InputStream based on a configuration set in the CourseWork
 * @author lewispalmer
 *
 */
public class HTMLread {

	/**
	 * Simplifies the readString method to return true if it reaches ch1 and false if it reaches ch2
	 * @param is is InputStream of the URL's HTML content.
	 * @param ch1 the search Character which triggers a return of true
	 * @param ch2 the search Character which triggers a return of false
	 * @return true if InputStream reaches ch1, false if it reaches ch2
	 * @throws IOException if the InputStream runs out.
	 */
	public boolean readUntil(InputStream is, char ch1, char ch2) throws IOException
	{
		if(readString(is, ch1, ch2)!=null)
			return true;
		else
			return false;
	}
	
	/**
	 * Reads text from InputStream, if it is Whitespace, it will keep reading until it reaches
	 * a non-white space character and then return it. Otherwise, it will return the min
	 * value of Character if it reaches the given char. If it runs out of things to read,
	 * it will throw an error
	 * @param is the URL's HTML as an InputStream
	 * @param ch the desired char you are searching for
	 * @return the min Char if it finds ch, or the first non-whitespace char.
	 * @throws IOException if it runs out of Stream to read.
	 */
	public char skipSpace(InputStream is, char ch) throws IOException
	{
		boolean endofPage = false;
		boolean stillWhiteSpace = true;
		boolean neverWhiteSpace = false;
		boolean firstRead = true;
		int isOutput;
		do{
			isOutput = is.read();
			if(firstRead)
			{
				neverWhiteSpace = !Character.isWhitespace((char)isOutput);
				firstRead = false;
			}
			if(neverWhiteSpace)
			{
				if(isOutput == -1)
					endofPage = true;
				if((char)isOutput == ch)
					return Character.MIN_VALUE;
				else
					return (char)isOutput;
			}
			else
			{
				if(!Character.isWhitespace((char)isOutput))
					return (char)isOutput;
				if(isOutput == -1)
					endofPage = true;
			}
			
		}while((stillWhiteSpace || neverWhiteSpace) && !endofPage);
		throw new IOException();
	}
	
	/**
	 * Function to read in the InputStream and return the string from the current point to ch1
	 * or null if it reaches ch2 first. If the InputStream runs out of text, it throws an
	 * IOException.
	 * @param is InputStream of the URL's HTML content.
	 * @param ch1 the Character at the end point of the desired String
	 * @param ch2 the Character which triggers a null to be returned.
	 * @return the found String or null.
	 * @throws IOException
	 */
	public String readString(InputStream is, char ch1, char ch2) throws IOException
	{
		boolean endofPage = false;
		String toReturn = "";
		char nextChar;
		while(!endofPage)
		{
			int temp = is.read();
			
			if(temp != -1)
			{
				nextChar = (char)temp;
				toReturn += Character.toString(nextChar);
				if(nextChar == ch1)
					return toReturn;
				if(nextChar == ch2)
					return null;
			}
			else
				endofPage = true;
		}
		throw new IOException();
	}
}

package solution;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {
	private HashMap<String, Integer> passengerNumbers = new HashMap<String, Integer>();
	private String path;
	
	public PassengerNumbersDAO() {
		
	}
	private Connection connect() throws IOException, NullPointerException, DataLoadingException {
        // SQLite connection string
        String url = "jdbc:sqlite:"+path;
                Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            
        }
        catch(NullPointerException ex) {
        	System.out.println(ex.getMessage());
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
   
        return conn;
    }

	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		int numberOfEntries;
		numberOfEntries = 0;
		for (@SuppressWarnings("unused") int v:passengerNumbers.values()) {
			numberOfEntries++;
		}
		return numberOfEntries;
	}


	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		int passengerTotal = -1;
		String hashKey = (date.toString() + " " + String.valueOf(flightNumber));
		if(passengerNumbers.containsKey(hashKey)) {
			passengerTotal = passengerNumbers.get(hashKey);
		}
		return passengerTotal;
	}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException{
		path =p.toAbsolutePath().toString();
		
		String sql = "SELECT * FROM PassengerNumbers";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                String hashKey = (rs.getString("Date") + " " + String.valueOf(rs.getInt("FlightNumber")));
                passengerNumbers.put(hashKey, rs.getInt("Passengers"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new DataLoadingException();
        }

	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		passengerNumbers.clear();
	}

}

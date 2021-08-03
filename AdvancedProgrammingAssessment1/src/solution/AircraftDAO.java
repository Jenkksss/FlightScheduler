package solution;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.Aircraft.Manufacturer;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;


/**
 * The AircraftDAO class is responsible for loading aircraft data from CSV files
 * and contains methods to help the system find aircraft when scheduling
 */
public class AircraftDAO implements IAircraftDAO {
	
	ArrayList<Aircraft> aircraft;
	public AircraftDAO () {
		aircraft = new ArrayList<Aircraft>();
	}
	/**
	 * Loads the aircraft data from the specified file, adding them to the currently loaded aircraft
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
     *
	 * Initially, this contains some starter code to help you get started in reading the CSV file...
	 */
	@Override
	public void loadAircraftData(Path p) throws DataLoadingException {	
		try {
			//open the file
			BufferedReader reader = Files.newBufferedReader(p);
			
			//read the file line by line
			String line = "";
			
			//skip the first line of the file - headers
			reader.readLine();
			
			while( (line = reader.readLine()) != null) {
				//each line has fields separated by commas, split into an array of fields
				String[] fields = line.split(",");
				//put some of the fields into variables: check which fields are where atop the CSV file itself
				String tailcode = fields[0];
				String typeCode = fields[1];
				String Manufacturer = fields [2];
				String model = fields [3];
				int seats = Integer.parseInt(fields[4]);
				int cabinCrewRequired = isNumeric(fields[5]);
				String startingPosition = "";
				if (fields.length>6) {
				startingPosition = fields[6];
				}
				else {
					throw new DataLoadingException();
				}
				Aircraft a = new Aircraft();
				a.setTailCode(tailcode);
				a.setTypeCode(typeCode);
				a.setModel(model);
				a.setSeats(seats);
				a.setCabinCrewRequired(cabinCrewRequired);
				a.setStartingPosition(startingPosition);
				a.setManufacturer(getManufacturer(Manufacturer));
				
				
				aircraft.add(a);
				
				//print a line explaining what we've found
				System.out.println("Aircraft: " + tailcode + " is a " + typeCode + " with " + seats + " seats.");
				
			}

		}
		
		catch (Exception e) {
			//There was a problem reading the file
			throw new DataLoadingException(e);
		}

	}
	
	/**
	 * Returns a list of all the loaded Aircraft with at least the specified number of seats
	 * @param seats the number of seats required
	 * @return a List of all the loaded aircraft with at least this many seats
	 */
	@Override
	public List<Aircraft> findAircraftBySeats(int seats) {
		List<Aircraft> aircraftBySeats = new ArrayList<Aircraft>();
		for (Aircraft a:aircraft) 
		{
			int aircraftSeats = a.getSeats();
			if (seats <= aircraftSeats)
			{
				aircraftBySeats.add(a);
			}
		}
		return aircraftBySeats;
	}

	/**
	 * Returns a list of all the loaded Aircraft that start at the specified airport code
	 * @param startingPosition the three letter airport code of the airport at which the desired aircraft start
	 * @return a List of all the loaded aircraft that start at the specified airport
	 */
	@Override
	public List<Aircraft> findAircraftByStartingPosition(String startingPosition) {
		List <Aircraft> aircraftByStartingPosition = new ArrayList<Aircraft>();
		for(Aircraft a:aircraft)
		{
			if(startingPosition.equalsIgnoreCase(a.getStartingPosition()))
			{
				aircraftByStartingPosition.add(a);
			}
		}
		return aircraftByStartingPosition;
	}

	/**
	 * Returns the individual Aircraft with the specified tail code.
	 * @param tailCode the tail code for which to search
	 * @return the aircraft with that tail code, or null if not found
	 */
	@Override
	public Aircraft findAircraftByTailCode(String tailCode) {
		Aircraft aircraftByTailCode = new Aircraft();
		for(Aircraft a:aircraft) 
		{	
			String  aircraftTailCode = a.getTailCode();
			if(tailCode.equals(aircraftTailCode))
			{
				aircraftByTailCode=a;
			}
			
		}
		return aircraftByTailCode;
	}
		/**
	}
		return null
	 * Returns a List of all the loaded Aircraft with the specified type code
	 * @param typeCode the type code of the aircraft you wish to find
	 * @return a List of all the loaded Aircraft with the specified type code
	 */
	@Override
	public List<Aircraft> findAircraftByType(String typeCode) {
		List<Aircraft> aircraftByTypeCode = new ArrayList<Aircraft>();
		for(Aircraft a:aircraft)
		{
			String aircraftType = a.getTypeCode();
			if(typeCode.equals(aircraftType))
			{
				aircraftByTypeCode.add(a);
			}
		}
		return aircraftByTypeCode;
	}

	/**
	 * Returns a List of all the currently loaded aircraft
	 * @return a List of all the currently loaded aircraft
	 */
	@Override
	public List<Aircraft> getAllAircraft() {

		return aircraft;
	}

	/**
	 * Returns the number of aircraft currently loaded 
	 * @return the number of aircraft currently loaded
	 */
	@Override
	public int getNumberOfAircraft() {
			return aircraft.size();
	}

	/**
	 * Unloads all of the aircraft currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		aircraft.clear();;

	}
	private Manufacturer getManufacturer(String manufacturer) {
		switch (manufacturer) {
		case "Boeing":return Manufacturer.BOEING;
		case "Airbus":return Manufacturer.AIRBUS;
		case "Fokker":return Manufacturer.FOKKER;
		case "Embraer":return Manufacturer.EMBRAER;
		case "Atr":return Manufacturer.ATR;
		case "Bombardier": return Manufacturer.BOMBARDIER;
		default: return Manufacturer.BOEING;
		}
	}
	private int isNumeric (String str) {
		if (str == null) 
			return 0;
			try {
				int number = Integer.parseInt(str);
				return number;
			}
			catch (NumberFormatException ex) {
				return 0;
			}
	}
}

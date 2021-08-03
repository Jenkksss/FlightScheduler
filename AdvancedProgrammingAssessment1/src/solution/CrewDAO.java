package solution;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {
	private List <CabinCrew> cabincrewlist;
	private List <Pilot> pilotlist;
	
	public CrewDAO() {
		cabincrewlist = new ArrayList<CabinCrew>();
		pilotlist = new ArrayList<Pilot>();
	}
	
	
	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
	try	{
		BufferedReader br = Files.newBufferedReader(p);
		String line = "";
		String jsonpilot = "";
		String jsoncrew = "";
		int pilotCounter = 0;
		int crewCounter = 0;
		while((line=br.readLine()) != null) {
			if(pilotCounter>1)
			{
				if (pilotCounter==2) {
					jsonpilot = jsonpilot +"[";
				}
				jsonpilot = jsonpilot + line;
			}
			pilotCounter++;
			if (crewCounter>0) {
				jsoncrew = jsoncrew + line;
				}
			if(line.contains("cabincrew")) {
				jsoncrew = "[";
				crewCounter++;
			}
		}
		jsoncrew = jsoncrew.substring(0, jsoncrew.length()-1);
		JSONArray jsonpilotroot = new JSONArray (jsonpilot);
		JSONArray jsoncrewroot = new JSONArray (jsoncrew);
		for(int i=0; i<jsonpilotroot.length(); i++) {
			JSONObject pilots = jsonpilotroot.getJSONObject(i);
			JSONArray json = pilots.getJSONArray("typeRatings");
			Pilot pilot = new Pilot();
			for (int j=0; j<json.length(); j++)
			{
				pilot.setQualifiedFor(json.getString(j));
			}
			
			pilot.setForename(pilots.getString("forename"));
			pilot.setSurname(pilots.getString("surname"));
			pilot.setHomeBase(pilots.getString("homebase"));
			
			switch (pilots.getString("rank")) {
				case "CAPTAIN":pilot.setRank(Rank.CAPTAIN);break;
				case "FIRST_OFFICER":pilot.setRank(Rank.FIRST_OFFICER);break;
				default:pilot.setRank(Rank.FIRST_OFFICER);break;
			}
			pilotlist.add(pilot);
		}
		
		for (int i=0; i<jsoncrewroot.length(); i++){
			JSONObject crews = jsoncrewroot.getJSONObject(i);
			JSONArray json = crews.getJSONArray("typeRatings");
			CabinCrew crew = new CabinCrew();
			for (int j=0; j<json.length(); j++)
			{
				crew.setQualifiedFor(json.getString(j));
			}
			crew.setForename(crews.getString("forename"));
			crew.setSurname(crews.getString("surname"));
			crew.setHomeBase(crews.getString("homebase"));
			cabincrewlist.add(crew);
			}
		
		
		}catch(Exception e) {
			throw new DataLoadingException(e);
		}
	}
	
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		List<CabinCrew> crewByHomeBase = new ArrayList<CabinCrew>();
		for (CabinCrew c:cabincrewlist)
		{
			if (airportCode.equalsIgnoreCase(c.getHomeBase()))
			{
				crewByHomeBase.add(c);
			}
		}
		return crewByHomeBase;
	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<CabinCrew> crewByHomeBaseAndTypeRating = new ArrayList<CabinCrew>();
		for (CabinCrew c:cabincrewlist)
		{
			if (airportCode.equals(c.getHomeBase()) && c.getTypeRatings().contains(typeCode))
			{
				crewByHomeBaseAndTypeRating.add(c);
				
			}
		}
		return crewByHomeBaseAndTypeRating;
	}
	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		List<CabinCrew> crewByTypeRating = new ArrayList<CabinCrew>();
		for (CabinCrew c:cabincrewlist)
		{
			if (typeCode.equals(c.getTypeRatings()));
			{
				crewByTypeRating.add(c);
			}
		}
		return crewByTypeRating;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		List<Pilot> pilotByHomeBase = new ArrayList<Pilot>();
		for (Pilot p:pilotlist)
		{
			if (airportCode.equals(p.getHomeBase()))
			{
				pilotByHomeBase.add(p);
			}
		}
		return pilotByHomeBase;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<Pilot> pilotByHomeBaseAndTypeRating = new ArrayList<Pilot>();
		for (Pilot p:pilotlist)
		{
			if (airportCode.equals(p.getHomeBase()) && p.getTypeRatings().contains(typeCode))
			{
				pilotByHomeBaseAndTypeRating.add(p);
				
			}
		}
		return pilotByHomeBaseAndTypeRating;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		List<Pilot> pilotByTypeRating = new ArrayList<Pilot>();
		for (Pilot p:pilotlist)
		{
			if (p.getTypeRatings().contains(typeCode))
			{
				
				pilotByTypeRating.add(p);
			}
		}
		return pilotByTypeRating;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		return cabincrewlist;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		List<Crew> allCrew = new ArrayList<Crew>();
		for(CabinCrew c:cabincrewlist) {
			allCrew.add(c);
		}
		for(Pilot p:pilotlist)
		{
			allCrew.add(p);
		}
		return allCrew;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		return pilotlist;
	}

	@Override
	public int getNumberOfCabinCrew() {
		return cabincrewlist.size();
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		return pilotlist.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		cabincrewlist.clear();
		pilotlist.clear();

	}

}

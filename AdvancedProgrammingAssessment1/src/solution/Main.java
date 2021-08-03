package solution;

import java.nio.file.Path;
import java.nio.file.Paths;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {

	public static void main(String[] args) {	
		IAircraftDAO aircraft = new AircraftDAO();
		ICrewDAO crew = new CrewDAO();
		IPassengerNumbersDAO passengers = new PassengerNumbersDAO();
		IRouteDAO route = new RouteDAO();
		try {
			aircraft.loadAircraftData(Paths.get("./data/aircraft.csv"));
			aircraft.loadAircraftData(Paths.get("./data/mini_aircraft.csv"));
			aircraft.loadAircraftData(Paths.get("./data/malformed_aircraft1.csv"));
			aircraft.findAircraftBySeats(329);
			crew.loadCrewData(Paths.get("./data/crew.json"));
			crew.loadCrewData(Paths.get("./data/mini_crew.json"));
			Path db = Paths.get("./data/passengernumbers.db");
			passengers.loadPassengerNumbersData(db);
		route.loadRouteData(Paths.get("./data/routes.xml"));
			
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading aircraft data");
			dle.printStackTrace();
		}
	}

}

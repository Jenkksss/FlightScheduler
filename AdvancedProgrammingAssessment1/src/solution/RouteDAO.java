package solution;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {
	private List <Route> routeList;
	public RouteDAO() {
	routeList = new ArrayList <Route>();
	}
	
	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		List<Route> routes = new ArrayList <Route>();
		for(Route routeitem:routeList) 
		{	
			if(routeitem.getDayOfWeek().equals(dayOfWeek)) {
				routes.add(routeitem);
			}
		}
		return routes;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to searh for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		List<Route> routes = new ArrayList <Route>();
		for(Route routeitem:routeList)
		{
			if(routeitem.getDepartureAirportCode().equals(airportCode) && routeitem.getDayOfWeek().equals(dayOfWeek))
			{
				routes.add(routeitem);
			}
		}
		return routes;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		List<Route> routes = new ArrayList <Route>();
		for(Route routeitem:routeList) 
		{	
			if(routeitem.getDepartureAirportCode().equals(airportCode)) {
				routes.add(routeitem);
			}
		}
		return routes;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		ArrayList<Route> routes = new ArrayList<Route>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E");
		String departureDate = formatter.format(date);
		for(Route routeitem:routeList)
		{
			if (routeitem.getDayOfWeek().equals(departureDate)) {
				routes.add(routeitem);
			}
		}
		return routes;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		// TODO Auto-generated method stub
		return routeList;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		// TODO Auto-generated method stub
		return routeList.size();
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path arg0) throws DataLoadingException {
		
		try {
			NodeList enlist = null; 
			File fXmlFile = new File(arg0.toAbsolutePath().toString());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			enlist = doc.getElementsByTagName("Route");
		
			for (int i = 0; i<enlist.getLength(); i++) {
				Node endnode = enlist.item(i);
				
				if (endnode.getNodeType()==Node.ELEMENT_NODE) {
					Route route = new Route();
					Element element = (Element) endnode;
					route.setFlightNumber(Integer.parseInt(element.getElementsByTagName("FlightNumber").item(0).getTextContent()));
					route.setDayOfWeek((element.getElementsByTagName("DayOfWeek").item(0).getTextContent()));
					route.setDepartureAirport((element.getElementsByTagName("DepartureAirport").item(0).getTextContent()));
					route.setDepartureAirportCode((element.getElementsByTagName("DepartureAirportCode").item(0).getTextContent()));
					route.setArrivalAirport((element.getElementsByTagName("ArrivalAirport").item(0).getTextContent()));
					route.setArrivalAirportCode((element.getElementsByTagName("ArrivalAirportCode").item(0).getTextContent()));
					route.setDuration((Duration.parse(element.getElementsByTagName("Duration").item(0).getTextContent())));
					route.setDepartureTime(LocalTime.parse(element.getElementsByTagName("DepartureTime").item(0).getTextContent()));
					route.setArrivalTime(LocalTime.parse(element.getElementsByTagName("ArrivalTime").item(0).getTextContent()));
					routeList.add(route);
				}
			}
		}
		catch (Exception  ex) {
		System.out.println(ex.getMessage());
		throw new DataLoadingException(ex);
	}
}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		routeList.clear();

	}

}

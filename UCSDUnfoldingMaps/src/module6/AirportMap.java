package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import module6.CommonMarker;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			//System.out.println("ID: " + feature.getId() + ", LOC:" + feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		map.addMarkers(airportList);
		
		
		System.out.println("Fin");
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	@Override
	public void mouseMoved() {
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		}
		// Lines
		//selectMarkerIfHover(routeList);
		// Points 
		selectMarkerIfHover(airportList);
		
	}
	
	public void selectMarkerIfHover(List<Marker> markers) 
	{
		if (lastSelected != null) {
			return;
		}
		else {
			for (Marker mk : markers) {
				CommonMarker marker = (CommonMarker) mk;
				if (marker.isInside(map, this.mouseX, this.mouseY) == true && marker.isSelected() == false) {
					lastSelected = marker;
					marker.setSelected(true);
					return;
				}
			}
		}
	}
	
	@Override
	public void mouseClicked() 
	{
		if (lastClicked != null) {
			unhideAirportMarkers();
			lastClicked = null;
		}
		else {
			checkAirportsForClick();
		}
	}
	
	private void checkAirportsForClick() {
		if (lastClicked != null) return;
		
		for (Marker mk : airportList) {
			AirportMarker am = (AirportMarker) mk;
			if (!am.isHidden() && am.isInside(map, mouseX, mouseY)) {
				lastClicked = am;
				for (Marker ahide: airportList) {
					if (ahide != lastClicked) {
						ahide.setHidden(true);
					}
				}
				for (Marker rhide: routeList) {
					if (rhide.getProperties().get("source").equals(lastClicked.getId())) {
						rhide.setHidden(false);
					}
					else {
						rhide.setHidden(true);
					}

				}
				return;
			}
		}
	}
	
	// loop over and unhide all markers
	private void unhideAirportMarkers() {
		for(Marker marker : airportList) {
			marker.setHidden(false);
		}
	}
	
	

}

package module3;

//Java utilities libraries
import java.util.ArrayList;

//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;
import java.util.HashMap;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.*;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// This is a pretty destructive earthquake
	public static final float THRESHOLD_HEAVY = 7.5f;
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5.0f;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 2.5f;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);
		background(200, 200, 200);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    //TODO (Step 3): Add a loop here that calls createMarker (see below) 
	    // to create a new SimplePointMarker for each PointFeature in 
	    // earthquakes.  Then add each new SimplePointMarker to the 
	    // List markers (so that it will be added to the map in the line below)
	    for (PointFeature pf : earthquakes) 
	    {
	    	SimplePointMarker mk = createMarker(pf);
	    	markers.add(mk);
	    }
	    
	    // Add the markers to the map so that they are displayed
	    map.addMarkers(markers);
	}
		
	/* createMarker: A suggested helper method that takes in an earthquake 
	 * feature and returns a SimplePointMarker for that earthquake
	 * 
	 * In step 3 You can use this method as-is.  Call it from a loop in the 
	 * setup method.  
	 * 
	 * TODO (Step 4): Add code to this method so that it adds the proper 
	 * styling to each marker based on the magnitude of the earthquake.  
	*/
	private SimplePointMarker createMarker(PointFeature feature)
	{  
		// To print all of the features in a PointFeature (so you can see what they are)
		// uncomment the line below.  Note this will only print if you call createMarker 
		// from setup
		//System.out.println(feature.getProperties());
		
		// Create a new SimplePointMarker at the location given by the PointFeature
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		
		// Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	    int yellow = color(255, 255, 0);
		
		// TODO (Step 4): Add code below to style the marker's size and color 
	    // according to the magnitude of the earthquake.  
	    // Don't forget about the constants THRESHOLD_MODERATE and 
	    // THRESHOLD_LIGHT, which are declared above.
	    // Rather than comparing the magnitude to a number directly, compare 
	    // the magnitude to these variables (and change their value in the code 
	    // above if you want to change what you mean by "moderate" and "light")
	    HashMap<String, Integer> rgb = getColorValue(mag);
	    int gradedColor = color(rgb.get("red"), rgb.get("green"), rgb.get("blue"));
	    
	    // Set marker color
	    marker.setColor(gradedColor);
	    
	    // Finally return the marker
	    return marker;
	}
	
	private HashMap<String, Integer> getColorValue(float magnitude)
	{
		HashMap<String, Integer> rgb = new HashMap<String, Integer>();
		rgb.clear();
		if (magnitude < THRESHOLD_LIGHT && magnitude > 0.0f)
		{
			System.out.println("Minor - Light Earthquake");
			rgb = putColorValues(255,255,255);
		}
		else if (magnitude < THRESHOLD_MODERATE && magnitude > THRESHOLD_LIGHT)
		{
			System.out.println("Light - Moderate Earthquake");
			rgb = putColorValues(54 + 100, 24 + 100, 95 + 100);
		}
		else if (magnitude < THRESHOLD_HEAVY && magnitude > THRESHOLD_MODERATE)
		{
			System.out.println("Moderate - Heavy Earthquake");
			rgb = putColorValues(54 + 50, 24 + 50, 95 + 50);
		}
		else
		{
			System.out.println("Heavy - Maximal Earthquake");
			rgb = putColorValues(54, 24, 95);
		}
		return rgb;
	}
	
	private HashMap<String, Integer> putColorValues(int red, int green, int blue)
	{
		HashMap<String, Integer> rgb = new HashMap<String, Integer>();
		rgb.put("red", red);
		rgb.put("green", green);
		rgb.put("blue", blue);
		return rgb;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		fill(255, 255, 255);
		rect(10, 350, 175, 200);
		
		fill(0, 0, 0);
		textSize(16);
		text("magnitude", 55, 370);
		
		int p1X = 35;
		int p1Y = 395;
		fill(255,255,255);
		ellipse(p1X, p1Y, 15, 15);
		fill(0, 0, 0);
		textSize(14);
		text("\t m < 2.5", p1X + 30, p1Y + 5);
		
		int p2X = 35;
		int p2Y = 425;
		fill(54 + 100, 24 + 100, 95 + 100);
		ellipse(p2X, p2Y, 15, 15);
		fill(0, 0, 0);
		textSize(14);
		text("\t2.5 < m < 5.0", p2X + 30, p2Y + 5);
		
		int p3X = 35;
		int p3Y = 455;
		fill(54 + 50, 24 + 50, 95 + 50);
		ellipse(p3X, p3Y, 15, 15);
		fill(0,0,0);
		textSize(14);
		text("\t5.0 < m < 7.5", p3X + 30, p3Y + 5);
		
		int p4X = 35;
		int p4Y = 485;
		fill(54, 24, 95);
		ellipse(p4X, p4Y, 15, 15);
		fill(0,0,0);
		textSize(14);
		text("\t 7.5 < m", p4X + 30, p4Y + 5);
	}
}

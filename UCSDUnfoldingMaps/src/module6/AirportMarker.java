package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	public Feature c;
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		c = city;
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(11);
		pg.ellipse(x, y, 3, 3);
		
		
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
		pg.pushStyle();
		pg.fill(255);
		pg.rect(x + 0.5f*radius, y - 3*radius, 6.3f*this.getName().length(), 20f, 5f);
		pg.fill(0);
		pg.text(this.getName(), x + radius, y - radius);
		pg.popStyle();
		// show routes
		
		
	}
	public String getName() {
		return (String) getProperty("name");
	}
	
	public String getId() {
		return (String) c.getId();
	}
	
}

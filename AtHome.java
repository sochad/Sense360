/**
 * Wrapper classes Point and Visit
 * Class AtHome with function getHome(It calculates and returns the point)
 *
 */
package src;

import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import java.util.HashMap;

/*
 * Wrapper class to specify any given point
 * Data members: latitude and longitude
 * Equals method is override to check for same
 */
class Point {
	float lat;
	float lng;

	public Point() {
		super();
	}
	public Point(float lat, float lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	@Override
	public int hashCode() {
		// Be careful if lat and long are to big it may go out of the limit of Integer.
		// We could multiple it by a large prime number as well.
		return Math.round(lat*lng*1000000);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (this.getLat() == other.getLat() && this.getLng() == other.getLng())
			return true;
		else
			return false;
	}
}
/*
 * Wrapper class Visit used to specify the points visited.
 * Data members address(from Point class), Date and Time of arrival and departure. 
 */
class Visit {
	Point address;
	LocalDateTime arrival;
	LocalDateTime departure;

	public Visit() {
		super();
	}
	public Visit(Point address, LocalDateTime arrival, LocalDateTime departure) {
		super();
		this.address = address;
		this.arrival = arrival;
		this.departure = departure;
	}
	public Point getAddress() {
		return address;
	}
	public void setAddress(Point address) {
		this.address = address;
	}
	public LocalDateTime getArrival() {
		return arrival;
	}
	public void setArrival(LocalDateTime arrival) {
		this.arrival = arrival;
	}
	public LocalDateTime getDeparture() {
		return departure;
	}
	public void setDeparture(LocalDateTime departure) {
		this.departure = departure;
	}	
}
/*
 * Class AtHome used to calculate home location.
 * Has a helper method round (to convert to 3 decimal points)
 * Also has function getHome (which returns point home)
 */
public class AtHome {
	public Point getHome(ArrayList<Visit> placesVisited) {
		Point home = null;
		long timeSpentAtHome = 0;
		HashMap<Point,Long> pointsVisited = new HashMap<Point,Long>();

		for (int i=0;i<placesVisited.size();i++) {
			Point visiting = placesVisited.get(i).getAddress();
			long time = 0;

			LocalDateTime arrival= placesVisited.get(i).getArrival();
			LocalDateTime departure= placesVisited.get(i).getDeparture();
			/*
			 * Calculation to get difference arrival time and departure time 
			 * in the span of 8PM to 8AM, Time zone and day light savings time is not handled.
			 */
			if ((arrival.getHour()) >=20 && departure.getHour()<8) {
				long hours = ChronoUnit.HOURS.between(arrival, departure);

				if (hours<24) {
					time = time + ChronoUnit.MINUTES.between(arrival, departure);
				} else {
					time = time + ChronoUnit.MINUTES.between(arrival, departure) - Math.round(hours/24)*12*60;
				}
			} else if ((arrival.getHour()) >=20 && departure.getHour()>=8) {

				if (departure.getHour()>=20) {
					time = time + (departure.getHour() - 20)*60 + departure.getMinute();
				}
				// Moving the time for easier calculations.
				departure.withHour(8);
				departure.withMinute(0);
				long hours = ChronoUnit.HOURS.between(arrival, departure);
				if (hours<24) {
					time = time + ChronoUnit.MINUTES.between(arrival, departure);
				} else {
					time = time + ChronoUnit.MINUTES.between(arrival, departure) - Math.round(hours/24)*12*60;
				}
			} else if ((arrival.getHour()) <20 && departure.getHour()>=8) {
				// Calculations for arrival time less than 8PM and departure greater than or equal 8AM.
				if (departure.getHour()>=20) {
					time = time + (departure.getHour() - 20)*60 + departure.getMinute();					
				}
				if (arrival.getHour()<8) {
					time = time + (departure.getHour())*60 + departure.getMinute();
				}
				else if (arrival.getHour()==8) {
					time = time + (departure.getHour())*60;
				}
				// Moving the time for easier calculations.
				arrival.withHour(20);
				arrival.withMinute(0);
				departure.withHour(8);
				departure.withMinute(0);
				long hours = ChronoUnit.HOURS.between(arrival, departure);
				if (hours<24) {
					time = time + ChronoUnit.MINUTES.between(arrival, departure);
				} else {
					time = time + ChronoUnit.MINUTES.between(arrival, departure) - Math.round(hours/24)*12*60;
				}
			} else if ((arrival.getHour()) <20 && departure.getHour()<8) {
				if (arrival.getHour()<8) {
					time = time + (departure.getHour())*60 + departure.getMinute();
				}
				else if (arrival.getHour()==8) {
					time = time + (departure.getHour())*60;
				}
				// Moving the time for easier calculations.
				arrival.withHour(20);
				arrival.withMinute(0);
				long hours = ChronoUnit.HOURS.between(arrival, departure);
				if (hours<24) {
					time = time + ChronoUnit.MINUTES.between(arrival, departure);
				} else {
					time = time + ChronoUnit.MINUTES.between(arrival, departure) - Math.round(hours/24)*12*60;
				}
			}					
			visiting.setLat(round(visiting.getLat(),3));
			visiting.setLng(round(visiting.getLng(),3));
			System.out.println(visiting.getLat());
			System.out.println(visiting.getLng());

			if (pointsVisited.containsKey(visiting)) {
				time += pointsVisited.get(visiting);
				pointsVisited.put(visiting, time);
			} else {
				pointsVisited.put(visiting, time);
			} 
		}
		for (Point it : pointsVisited.keySet()) {
			long val = pointsVisited.get(it);

			if(val > 1800 && val > timeSpentAtHome) {
				home = it;
				timeSpentAtHome = val;
			} 
		}
		return home;
	}

	private static float round (float value, int precision) {
		int scale = (int) Math.pow(10, precision);
		return (float) Math.round(value * scale) / scale;
	}

	public static void main (String args[]) {
		AtHome atHome = new AtHome();
		ArrayList<Visit> placesVisited = new ArrayList<Visit>();
		Point p1 = new Point(123.4567f,123.4567f);
		LocalDateTime departure1 = LocalDateTime.now();
		// Format YYYY, MONTH, DD, HH , MM, SS
		LocalDateTime arrival1 = LocalDateTime.of(2017, Month.JULY, 29, 19, 30, 40);

		System.out.println("arrival time:"+arrival1);
		System.out.println("departure time:"+departure1);
		// create visit
		Visit v1 = new Visit(p1,arrival1,departure1);
		placesVisited.add(v1);

		Point p2 = new Point(125.4567f,125.4567f);
		LocalDateTime departure2 = LocalDateTime.now();
		// Format YYYY, MONTH, DD, HH , MM, SS
		LocalDateTime arrival2 = LocalDateTime.of(2017, Month.AUGUST, 29, 19, 30, 40);

		System.out.println("arrival time:"+arrival2);
		System.out.println("departure time:"+departure2);
		// create visit
		Visit v2 = new Visit(p2,arrival2,departure2);
		placesVisited.add(v2);

		Point p3 = new Point(125.4567f,125.4567f);
		LocalDateTime departure3 = LocalDateTime.of(2017, Month.JULY, 29, 19, 30, 40);
		// Format YYYY, MONTH, DD, HH , MM, SS
		LocalDateTime arrival3 = LocalDateTime.of(2017, Month.JANUARY, 29, 19, 30, 40);

		System.out.println("arrival time:"+arrival3);
		System.out.println("departure time:"+departure3);
		// create visit
		Visit v3 = new Visit(p3,arrival3,departure3);
		placesVisited.add(v3);

		Point p4 = new Point(125.4567f,125.4567f);
		LocalDateTime departure4 = LocalDateTime.of(2017, Month.JULY, 29, 19, 30, 40);
		// Format YYYY, MONTH, DD, HH , MM, SS
		LocalDateTime arrival4 = LocalDateTime.of(2017, Month.JANUARY, 29, 19, 30, 40);

		System.out.println("arrival time:"+arrival4);
		System.out.println("departure time:"+departure4);
		// create visit
		Visit v4 = new Visit(p4,arrival4,departure4);
		placesVisited.add(v4);
		/*
		 * latitude: float (e.g. 45.12345)
		 * longitude: float (e.g. -118.12345)
		 * arrival_time_local: datetime (e.g. 5/30/2015 10:12:35)
		 * departure_time_local: datetime (e.g. 5/30/2015 18:12:35)
		 */
		Point home = atHome.getHome(placesVisited);
		if (home == null) {
			System.out.println("There was no point which statisfied the criteria.");
		} else {
			System.out.println("The latitude of the point:"+home.getLat());
			System.out.println("The longitude of the point"+home.getLng());
		}
	}
}

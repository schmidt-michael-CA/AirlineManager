
/*
	File Name: Route.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
	Date: Dec 17, 2019
	Description: Route class
 */

import java.util.*;

public class Route {

   private Airline airline;
   private String id;
   private Airport start;
   private Airport end;
   private int frequency;
   private int timeToNextFlight;
   private double distance;

   private static final int ROUTEID_MIN_LEN = 3;
   private static final int ROUTEID_MAX_LEN = 7;
   
   //constructors
   public Route(Airport start, Airport end, int frequency, Airline airline){
      this.frequency = frequency;
      this.timeToNextFlight = frequency;
      this.id = "RO" + airline.routeList.size();
      this.airline = airline;
      this.start = start;
      this.end = end;
      distance = this.start.getLocation().distance(end.getLocation());
   }

   public Route(String id, String startCode, String endCode, int timeTil, int frequency, Airline airline){
      this.frequency = frequency;
      this.timeToNextFlight = timeTil;
      this.id = id;
      this.airline = airline;
      this.start = airline.searchAirportsByCode(startCode);
      this.end = airline.searchAirportsByCode(endCode);
      distance = this.start.getLocation().distance(end.getLocation());
   }

   // Accessor Methods
   public Airline getAirline () {
      return airline;
   }
   public String getId () {
      return id;
   }
   public Airport getStart () {
      return start;
   }
   public Airport getEnd () {
      return end;
   }
   public int getFrequency () {
      return frequency;
   }
   public int getTimeToNextFlight () {
      return timeToNextFlight;
   }
   public double getDistance(){
      return distance;
   }

   // Mutator Methods
   public void setAirline (Airline airline) {
      this.airline = airline;
   }
   public void setId (String id) {
      this.id = id;
   }
   public void setStart (Airport start) {
      this.start = start;
   }
   public void setEnd (Airport end) {
      this.end = end;
   }
   public void setFrequency (int frequency) {
      this.frequency = frequency;
   }
   public void setTimeToNextFlight (int timeTil) {
      timeToNextFlight = timeTil;
   }

   public void performAction(){
      timeToNextFlight --;
   }
   public void delete(){
      airline.routeList.remove(this);
   }

   //checks if the Route; String: routeId is valid and also formats it (lowercase to uppercase); prints an error message and returns null if routeId is invalid, returns a valid routeId if is valid
   //formerly checkRouteId
   public static String formatRouteId(String routeId) { //routeId must be in format AAXXX where A represents an uppercase letter and X represents a number. Can have 1 to 3 X's. 
      String temp;
      char toCompare; //char for comparing; only needs to be loaded once per comparison
   
      try {
         if (routeId.length() >= ROUTEID_MIN_LEN && routeId.length() <= ROUTEID_MAX_LEN) { //check if routeId is the right length; >=3 and <= 6
            routeId = routeId.toUpperCase(); //changes ALL lowercase chars to uppercase
            for (int i = 0; i < 2; i++) { //check first two chars
               toCompare = routeId.charAt(i); //get toCompare from the routeId String (doing this way only needs to call String.charAt(int) once instead of twice in the if statement)
               if (toCompare < 'A' || toCompare > 'Z') { //check if the char is NOT from A to Z
                  throw new InputMismatchException(); //throw error
               }
               //do nothing if char toCompare is valid
            }
            for (int i = 2; i < routeId.length(); i++) { //check remaining chars
               toCompare = routeId.charAt(i);
               if (toCompare < '0' || toCompare > '9') { //if char toCompare is NOT from 0-9
                  throw new InputMismatchException(); //throw error
               }
               //do nothing if char toCompare is valid
            }
         }
         else { //string length incorrect
            throw new InputMismatchException(); //throw error
         }
         return routeId; //return a correctly formated routeId after routeId has passed all checks
      }
      catch (InputMismatchException ime) { //routeId is bad
         System.out.println("Route ID is not of the correct format."); //output error message
         return null; //return null String
      }
   }

   public String toString(){
      String output = id + " \n";
      output += "Route from: " + start.getCity() + " to " + end.getCity() + "\n";
      output += "Needs a flight every " + frequency + " hour(s) \n";
      output += "Needs a new flight in " + timeToNextFlight + "hour(s) \n";
      output += "With a distance of " + distance + " km \n";
      return output;
   }

}
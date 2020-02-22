
/*
	File Name: Airport.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
	Created: Dec 17, 2019
	Description: Airport class
 */

import java.util.*;

public class Airport {
   private String name; //airport name
   private String code; //airport code
   private String city; //city of airport
   private String country; //country of airport
   private Location location; //location object of where airport is
   private int runwayCapacity; //hourly capacity of takeoffs
   private double badWeatherChance; //double equal to or below 1 representing the chance of bad weather (percent)
   private boolean goodWeather; //true if airport has good weather, false if bad weather
   private boolean isOpen; //boolean signifying if airport is open
   private boolean hasMaintenance; //boolean signifying if airport has maintenance
   private boolean marked = false;
   private ArrayList<Plane> planeList; //ArrayList of planes in airport
   private ArrayList<Route> routeList; //ArrayList of routes leaving this airport
   private Airline airline;
   private final static int IATA_CODE_LEN = 3;

   //constructor
   public Airport(String nameIn, String codeIn, String cityIn, String countryIn, Location locationIn, int runwayCapacityIn, double badWeatherChanceIn,
                  boolean goodWeatherIn, boolean isOpenIn, boolean hasMaintenanceIn, Airline airline) {
      name = nameIn;
      code = codeIn;
      city = cityIn;
      country = countryIn;
      location = locationIn;
      runwayCapacity = runwayCapacityIn;
      badWeatherChance = badWeatherChanceIn;
      goodWeather = goodWeatherIn;
      isOpen = isOpenIn;
      hasMaintenance = hasMaintenanceIn;
      this.airline = airline;
   
      planeList = new ArrayList<>();
      routeList = new ArrayList<>();
   
   }

   //checks if the Airport code is valid and also formats it (lowercase to uppercase)
   //returns a string with correctly formatted code, null if code is invalid
   public static String formatAirportCode(String airportCode) {
      char toCompare; //char for comparing
   
      try {
         if (airportCode.length() == IATA_CODE_LEN) { //check length of airport code
            airportCode = airportCode.toUpperCase(); //change airport code to all uppercase
            for (int i = 0; i < IATA_CODE_LEN; i++) { //check each char of the airport code
               toCompare = airportCode.charAt(i); //get toCompare char
               if (toCompare < 'A' || toCompare > 'Z') { //if toCompare is not a char from A to Z
                  throw new InputMismatchException(); //throw error
               }
               //if toCompare is from A to Z do nothing; valid input
            }
         }
         else { //airportCode length is too short/long
            throw new InputMismatchException(); //throw error
         }
         //all conditions fulfilled
         return airportCode; //return a correctly formatted airportCode
      }
      catch (InputMismatchException ime) { //catch invalid format error
         System.out.println("Airport code is not of the correct format.");
         return null; //return null for invalid airportCode
      }
   }

   //method run each hour to perform airport actions
   public void performAction() {
      //runs method to update weather
      changeWeather();
   }

   //method for chance to change weather
   public void changeWeather() {
      //randomly generate a number from 0-1, check against badWeatherChance and set weather
      if (Math.random() <= badWeatherChance) {
         goodWeather = false;
      } else {
         goodWeather = true;
      }
   }

   //method to tell how much time before a plane can takeoff
   public int nextTime() {
      //sort the planeList by timeToTakeoff
      sortTimeToTakeoff();
   
      //loop through map, search for space (if number is lower than runwayCapacity, or value is not even in the map)
      int hour = 0;
      HashMap<Integer, Integer> hourCount = countHours(); //get the map of hours
   
      while (true) { //is an infinite loop because when it reaches return statements it exits the method
         //the takeoffList was sorted by timeToTakeoff, so the map is also sorted by timeToTakeoff
         try {
            if (hourCount.get(hour) < runwayCapacity) {
               //there is a space
               return hour;
            } else {
               hour++;
            }
         } catch (NullPointerException npx) {
            //value is not in the list, so that means there is a space for a plane to takeoff
            return hour;
         }
      }
   }

   //returns a map that has the count of all takeoff hours
   private HashMap<Integer, Integer> countHours() {
      //sort planeList
      sortTimeToTakeoff();
   
      //map counting the number of planes taking off each hour (key: timeToTakeoff, value: number of planes leaving at that time)
      HashMap<Integer, Integer> hourCount = new HashMap<>();
   
      //loop through and add all the values to to the map
      for (int i = 0; i < planeList.size(); i++) {
         int curr = planeList.get(i).getTimeToTakeoff();
      
         if (hourCount.containsKey(curr)) {
            //already in the map, add one to value stored
            hourCount.put(curr, hourCount.get(curr) + 1);
         } else {
            //not in the map, add the value with count = 1
            hourCount.put(curr, 1);
         }
      }
   
      //return the map
      return hourCount;
   }

   //method that sorts the planeList in order of timeToTakeoff
   private void sortTimeToTakeoff() {
      //insertion sort
      for (int i = 0; i < planeList.size(); i++) {
         Plane curr = planeList.get(i); //get the current plane
         int j = i;
      
         while (j > 0 && curr.getTimeToTakeoff() < planeList.get(j - 1).getTimeToTakeoff()) {
            planeList.set(j, planeList.get(j - 1));
            j--;
         }
      
         planeList.set(j, curr);
      }
   }

   //method returns true if delay is okay, false if delay is not okay
   public boolean checkDelay(int hour) {
      //get map of hours
      HashMap<Integer, Integer> hourCount = countHours();
   
      //check if the value inputted is in the map
      if (hourCount.containsKey(hour)) {
         //map has the hour
         if (hourCount.get(hour) < runwayCapacity) {
            return true;
         } else {
            return false;
         }
      } else {
         //if inputted value is not in map, then no planes are taking off that hour
         return true;
      }
   }

   //method for plane departures, called by plane as it takes off
   public void planeDeparture(String id) {
      //search for plane index in planeList
      int index = searchPlaneListIndexById(id);
   
      if (index != -1) {
         //if the plane exists in list, remove it
         planeList.remove(index);
         planeList.trimToSize();
      
         //get the Plane object in order to access currLocation and destination
         Plane takingOff = planeList.get(index);
      
         //search through routeList for route that the Plane is going on
         for(int i=0; i<routeList.size(); i++) {
            Route current = routeList.get(i);
         
            if (current.getStart() == this && current.getEnd() == takingOff.getDestination()) {
               //both start and end match, this is the route the plane is going on
               //reset timeToNextFlight counter in route
               current.setTimeToNextFlight(0);
            }
         }
      
      } else {
         //if plane does not exist, output an error message
         System.out.println("Error removing plane with id " + id + " from planeList of airport " + name);
      }
   
   }

   //returns index of plane with inputted id in planeList
   private int searchPlaneListIndexById(String id) {
      //sequential search for index
      for (int i=0; i<planeList.size(); i++) {
         if (planeList.get(i).getId().equals(id)) {
            return i;
         }
      }
      return -1;
   }


   //method to delete airport
   public void delete(){
      airline.airportList.remove(this);
   }

   //method to close the airport
   public void closeAirport() {
      isOpen = false;
   }


   //ACCESORS
   public String getName() {
      return name;
   }

   public String getCode(){
      return code;
   }

   public String getCity() {
      return city;
   }

   public String getCountry() {
      return country;
   }

   public Location getLocation() {
      return location;
   }

   public int getRunwayCapacity() {
      return runwayCapacity;
   }

   public double getBadWeatherChance() {
      return badWeatherChance;
   }

   public boolean getGoodWeather() {
      return goodWeather;
   }

   public boolean getIsOpen() {
      return isOpen;
   }

   public boolean getHasMaintenance() {
      return hasMaintenance;
   }

   public boolean getMarked() {
      return marked;
   }

   public ArrayList<Plane> getPlaneList() {
      return planeList;
   }

   public ArrayList<Route> getRouteList() {
      return routeList;
   }

   public static int getIATA_CODE_LEN() {
      return IATA_CODE_LEN;
   }

   //MUTATORS
   public void setName(String name) {
      this.name = name;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   public void setRunwayCapacity(int capacity) {
      runwayCapacity = capacity;
   }

   public void setBadWeatherChance(double weatherChance) {
      badWeatherChance = weatherChance;
   }

   public void setGoodWeather(boolean weather) {
      goodWeather = weather;
   }

   public void setIsOpen(boolean open) {
      isOpen = open;
   }

   public void setHasMaintenance(boolean maintenance) {
      hasMaintenance = maintenance;
   }

   public void setMarked(boolean bool){
      marked = bool;
   }

   //string handling
   public String toString(){
      String output;
      output = "Name: " + name + "\n";
      output += "IATA airport code: " + code + "\n";
      output = output + "City: " + city + "\n";
      output += "Country: " + country + "\n";
      output += location.toString() + "\n";
      output += "Runway capacity (planes/hour): " + runwayCapacity + "\n";
      output += "Route leaving here: " + routeList.size()  + "\n";
      output += "Route in " + routesIn() + "\n";
      output += "Bad weather chance: " + badWeatherChance + "\n";
      if (goodWeather) {
         output += "The weather is good\n";
      }
      else {
         output += "The weather is bad\n";
      }
      if (hasMaintenance) {
         output += "The airport has maintenance facilities\n";
      }
      else {
         output += "The airport does not have maintenance facilities\n";
      }
      for (int i = 0; i < this.planeList.size(); i++) {
         output += "\t" + planeList.get(i).getId() + "\n";
      }
      return output;
   }

   public String save() {
      return name + "\n" + code + "\n" + city + "\n" + country + "\n" + location.save() + "\n" + runwayCapacity + "\n" + badWeatherChance + "\n" + goodWeather + "\n" + isOpen + "\n" + hasMaintenance;
   }

   public int routesIn(){
      int in = 0;
      for (int i = 0; i < airline.routeList.size(); i ++){
         if (airline.routeList.get(i).getEnd().equals(this)){
            in++;
         }
      }
      return in;
   }

}
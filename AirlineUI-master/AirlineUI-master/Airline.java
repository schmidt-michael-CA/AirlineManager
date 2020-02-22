


import java.util.ArrayList;

/*
        File Name: Airline.java
        Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
        Class: ICS4U
        Date: Dec 17, 2019
        Description: Airline class
*/



public class Airline {

   private String name; //name of the airline
   private WorldTime time; //time
   ArrayList<Route> routeList; //list of routes the airline has
   ArrayList<Airport> airportList; //list of airports airline has access to
   ArrayList<Plane> planeList; //list of planes the airline owns

   //CONSTRUCTORS
   //constructor with all values inputted
   public Airline(String name, WorldTime time, ArrayList<Plane> planes, ArrayList<Airport> airports, ArrayList<Route> routes){
      this.name = name;
      this.time = time;
      planeList = planes;
      airportList = airports;
      routeList = routes;
   }

   //constructor with default worldTime, empty lists
   public Airline(String name){
      this.name = name;
      planeList = new ArrayList<>();
      airportList = new ArrayList<>();
      routeList = new ArrayList<>();
      time = new WorldTime(0, 1, 1, 0);
   }

   //constructor with empty lists
   public Airline(String name, WorldTime time){
      this.name = name;
      this.time = time;
      planeList = new ArrayList<>();
      airportList = new ArrayList<>();
      routeList = new ArrayList<>();
   }

   //empty constructor, only a placeholder before info is loaded from files
   public Airline() {
      planeList = new ArrayList<>();
      airportList = new ArrayList<>();
      routeList = new ArrayList<>();
   }

   //method to add route
   public void addRoute(String id, String startStr, String endStr, int timeTil, int frequency, Airline airline){
      Route newRoute = new Route(id, startStr, endStr, timeTil, frequency, airline);
      routeList.add(newRoute);
   
      //adds the route to the airport list of start
      searchAirportsByCode(startStr).getRouteList().add(routeList.get(routeList.size()-1));
      //System.out.println("The length of route list is: " + routeList.size());
   
   }

   //method to add a plane
   public void addPlane (Plane plane) {
      //add into planeList
      planeList.add(plane);
   
      //add the plane into the list of the airport it is located at
      if (plane.getCurrLocation() != null) {
         plane.getCurrLocation().getPlaneList().add(plane);
      }
      //System.out.println("The length of plane  list is: " + planeList.size());
   
   }

   //method to add an airport
   public void addAirport(Airport airport) {
      //add into the airport list
      airportList.add(airport);
      //System.out.println("The length of airport list is: " + airportList.size());
   }

   //deletes route given the id
   public void deleteRoute (String routeId) {
      Route route = searchRoutesById(routeId);
      if (route != null) {
         route.delete();
      } else {
         System.out.println ("Route cannot be found.");
      }
   }

   //method to delete plane
   public void deletePlane (String id) {
      Plane plane = searchPlanesById(id);;
      if (plane != null) {
         plane.delete();
      } else {
         System.out.println("Plane cannot be found.");
      }
   }

   //method to delete airport
   public void deleteAirport (String code) {
      Airport airport = searchAirportsByCode(code);
      if (airport != null) {
         airport.delete();
      } else {
         System.out.println("Airport cannot be found.");
      }
   }

   //close airport
   public void closeAirport (String code) {
      Airport airport = searchAirportsByCode(code);
   
      if (airport != null) {
         airport.closeAirport();
      } else {
         System.out.println("Airport cannot be found.");
      }
   }

   //method for passing time
   public void passTime(int hours){
      for (int j = 0; j < hours; j++) {
         for (int i = 0; i < this.planeList.size(); i ++){
            planeList.get(i).performAction();
         }
         for (int i = 0; i < this.routeList.size(); i ++){
            routeList.get(i).performAction();
         }
         for (int i = 0; i < this.airportList.size(); i ++){
            airportList.get(i).performAction();
         }
         System.out.println();
         System.out.println();
      }
   }


   public void sendToEarlyMaintenance (String planeId) {
      Plane plane = searchPlanesById(planeId);
   
      if (plane != null) {
         plane.setTimeToRepair(0);
      } else {
         System.out.println("Plane cannot be found");
      }
   }

   //outputs the id of planes with hours time to maintenance
   public void searchPlanesByTimeToMaintenance (int hours) {
      for (int i=0; i< planeList.size(); i++) {
         if (planeList.get(i).getTimeToMaintenance() == hours) {
            System.out.println(planeList.get(i).getId());
         }
      }
   }

   //prints out the id of the planes at certain airport
   public void searchPlanesByAirport (String code) {
      Airport airport = searchAirportsByCode(code);
      for (int i = 0; i < airport.getPlaneList().size(); i++) {
         System.out.println (airport.getPlaneList().get(i).getId());
      }
   }

   //searches planes by type inputted, assuming type is correct
   public void searchPlanesByType(String type) {
      System.out.println("Planes: ");
      if (type.charAt(0) == 'S') {
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof SmallPlane) {
               System.out.println (planeList.get(i));
            }
         }
      } else if (type.charAt(0) == 'M'){
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof MediumPlane) {
               System.out.println (planeList.get(i));
            }
         }
      }else {
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof LargePlane) {
               System.out.println (planeList.get(i));
            }
         }
      }
   }

   //searches planes by given id
   public Plane searchPlanesById(String planeId) {
      boolean found = false;
      Plane plane = null;
   
      for (int i=0; i<planeList.size() && !found; i++) {
         if (planeId.equals(planeList.get(i).getId())) {
            found = true;
            plane = planeList.get(i);
         }
      }
      return plane;
   }

   //searches airports by code, returns null if airport doesn't exist
   public Airport searchAirportsByCode(String code){
      for (int i = 0; i < this.airportList.size(); i ++){
         if (code.equals(airportList.get(i).getCode())){
            return airportList.get(i);
         }
      }
      return null;
   }

   //searches routes by given id, returns null if route doesn't exist
   public Route searchRoutesById(String routeId) {
      boolean found = false;
      Route route = null;
   
      for (int i = 0; i < routeList.size() && !found; i++) {
         if (routeId.equals(routeList.get(i).getId())) {
            found = true;
            route = routeList.get(i);
         }
      }
      return route;
   }

   //ground all planes of a certain type
   public void groundPlanesByType (String type) {
      //check the plane type, then search through list and unground planes of that type
      if (type.charAt(0) == 'S') {
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof SmallPlane) {
               planeList.get(i).setGrounded(true);
            }
         }
      } else if (type.charAt(0) == 'M'){
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof MediumPlane) {
               planeList.get(i).setGrounded(true);
            }
         }
      } else {
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof LargePlane) {
               planeList.get(i).setGrounded(true);
            }
         }
      }
   }

   //unground all planes of certain type
   public void ungroundPlanesByType (String type) {
      //check the plane type, then search through list and unground planes of that type
      if (type.charAt(0) == 'S') {
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof SmallPlane) {
               planeList.get(i).setGrounded(false);
            }
         }
      } else if (type.charAt(0) == 'M'){
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof MediumPlane) {
               planeList.get(i).setGrounded(false);
            }
         }
      } else {
         for (int i = 0; i < planeList.size(); i++) {
            if (planeList.get(i) instanceof LargePlane) {
               planeList.get(i).setGrounded(false);
            }
         }
      }
   }

   /**
    * Method designed to delay a plane with a flight planned
    * takes in a String that represents the plane
    * as well as an integer that represents the hours needed to delay
    * @param hours
    * @param planeId
    */
   public void delayFlight (int hours, String planeId) {
   
      //searches for a plane with matching ID
      Plane plane = this.searchPlanesById(planeId);
   
      //if the plane exists
      if (plane != null) {
         plane.setTimeToTakeoff(plane.getTimeToTakeoff() + hours);
      }
   
      //if the plane does not exist
      if (plane == null){
         System.out.println("Could not find plane with given Id");
      }
   
      //PLEASE NOTE THAT IF THE PLANE DOES NOT HAVE A SCHEDULED TAKE OFF TIME THIS METHOD DOES NOTHING
   
   }


   /**
    * below are list methods, they simply output a list of objects using the respective toString methods
    */

   public void listAllPlanes() {
      for (int i = 0; i < planeList.size(); i++) {
         System.out.println("Plane " + i);
         System.out.println (planeList.get(i));
         System.out.println(); //extra line for formatting
      }
   }

   public void listAllAirports() {
      for (int i = 0; i < airportList.size(); i++) {
         System.out.println ("Airport " + i);
         System.out.println (airportList.get(i));
         System.out.println(); //extra line for formatting
      }
   }

   public void listAllRoutes() {
      for (int i = 0; i < routeList.size(); i++) {
         System.out.println ("Route " + i);
         System.out.println (routeList.get(i));
         System.out.println(); //extra line for formatting
      }
   }
   
   //---CHECKING FOR VALID INPUTS---
   
   //checks airport code when creating a new airport (checks format and unique)
   public String checkNewAirportCode(String airportCode) {
      airportCode = Airport.formatAirportCode(airportCode);
      if (airportCode == null) {
       //if code is an invalid format
         return null; //return invalid
      } 
      if (searchAirportsByCode(airportCode) != null) {
       //code is valid but is repeated
         System.out.println("Airport code already exists.");
         return null;
      }
    
      return airportCode; //returns valid code
   }
   
   //checkNewRouteId; checks a routeId when making a NEW Route
   public String checkNewRouteId(String routeId) { //checks if an inputted routeId is valid and has no duplicates; returns null if the routeId fails these conditions, returns a correctly formatted routeId if all conditions are met
      routeId = Route.formatRouteId(routeId); //format the routeId
      if (routeId == null) { //if the routeId is an invalid format
         return null; //return null for invalid
      }
      if (searchRoutesById(routeId) != null) { //check if a Route with the same ID already exists
         System.out.println("Route ID already exists.");
         return null; //if routeId is NOT new and unique, return null for invalid
      }
      return routeId; //return a correct routeId if it is correctly formatted and doesn't already exist in a route
   }

   //check plane ID when creating a new plane (checks format and unique)
   public String checkNewPlaneId(String planeId) {
      planeId = Plane.formatPlaneId(planeId);
      if (planeId == null) {
         //ID is invalid format
         return null;
      } else if (searchPlanesById(planeId) != null) {
         //ID is valid, but repeated
         System.out.println("Plane ID already exists.");
         return null;
      }
      
      //otherwise ID is valid
      return planeId;
   }



   /**
    * BELOW IS A SET OF METHODS DESIGNED TO ALLOW THE USER TO FILL THE AIRLINE WITH AS MANY OBJECTS AS THEY WISH
    * This filler means one does not have to constantly be creating files as it can be done automatically
    * It also allows the logic behind an entire airline to be shown off more effectively, on a large scale as it is meant to be
    */



   public void addFillerAirport() {
   
      //randomly generates an airport based on random field generators seen below
      addAirport(new Airport(stringGen(8), stringGen(3), stringGen(6), "USA", locationGen(), genRunway(), genWeather(),
             true, true, genBool(), this));
   
   
      //This next section deals with the generation of routes heading to and from the aforementioned airport
      //It will generate a minimum of 3 routes into the airport and 3 routes out of the airport
      //of course under the circumstance that there are more than 3 airports in the entire program
   
      //these strings will hold the codes of airports, by nature of the program, they will always be unique from other codes
      String startCode;
      String endCode;
   
      //If the airline is big enough
      if (airportList.size() > 3){
         //will generated 12 routes for the airport, 6 towards and 6 away from
         //Other routes will be added as more airports are added
      
         for (int i = 0; i < 6; i ++){
         
            startCode = airportList.get(airportList.size() - 1).getCode();
         
            do {
               //set the destinations code to be that of a random preexisting airports
               endCode = airportList.get((int)(Math.random() * airportList.size())).getCode();
            
               //Will exit the loop when a whole slew of conditions are met
               // 1. the airport is in range of the largest plane
               // 2. the code is not the same as the start
               // 3. the ending airport does not have an obscene amount of routes already
            } while (!(searchAirportsByCode(startCode).getLocation().inRange(searchAirportsByCode(endCode).getLocation(),6500))
                   ||
                   endCode.equals(startCode)
                   ||
                   searchAirportsByCode(startCode).getRouteList().size() > routeList.size()/(airportList.size()/7 + 1));
         
            int frequency = (int)(Math.random() * 30 + 15);
            int timeTil = frequency - (int)(Math.random() * frequency);
         
            //CREATES TWO ROUTES BASED ON WHAT WAS GENERATED, ONE THERE, ONE BACK
            addRoute(stringGen(4), startCode, endCode, timeTil,frequency,this);
            addRoute((stringGen(4)), endCode, startCode, timeTil,frequency, this);
         
         
            endCode = airportList.get(airportList.size() - 1).getCode();
            do {
               //set the start code to be that of a random preexisting airports
               startCode = airportList.get((int)(Math.random() * airportList.size())).getCode();
            
               //Will exit the loop when a whole slew of conditions are met
               // 1. the airport is in range of the largest plane
               // 2. the code is not the same as the start
               // 3. the ending airport does not have an obscene amount of routes already
            
            } while (!(searchAirportsByCode(startCode).getLocation().inRange(searchAirportsByCode(endCode).getLocation(),6500))
                   ||
                   endCode.equals(startCode)
                   ||
                   searchAirportsByCode(startCode).getRouteList().size() > routeList.size()/((airportList.size()/7) + 1));
         
            //randomly generate route timers
            frequency = (int)(Math.random() * 30 + 15);
            timeTil = frequency - (int)(Math.random() * frequency);
         
            //Creates two routes based on what was generated, one there, one back
            addRoute(stringGen(4), startCode, endCode,timeTil,frequency,this);
            addRoute((stringGen(4)), endCode, startCode, timeTil,frequency, this);
         
         
         }
      }
   
   }


   /**
    * This method is utilized by the filler function when starting the program
    * It is called any number of times and generates a unique plane object every time
    * These objects will always start in an airport (cannot be created in the air)
    * The subclass of plane is also randomly generated
    */
   public void addFillerPlane(){
   
   
      //All aspects of this method are simply filling local variables with randomly generated variables
   
      String id = stringGen(1) + "-" + stringGen(4);
      Airport currLocation = airportList.get((int)(Math.random() * airportList.size()));
      Airport destination = null;
      int timetoMaintenance = (int)((Math.random() * 10)) + 10;
      int timeleftMaintain = timetoMaintenance/20 + 1;
      int timetoarrival = -1;
      int timetotakeoff = -1;
      boolean grounded = false;
   
      //these variables are passed into a constructor of varying type depending on the random method
      if (Math.random() < .35){
         addPlane(new SmallPlane(id, currLocation, destination,timetoMaintenance, timeleftMaintain,timetoarrival,timetotakeoff,grounded, this));
      } else if (Math.random() < .5){
         addPlane(new MediumPlane(id, currLocation, destination,timetoMaintenance, timeleftMaintain,timetoarrival,timetotakeoff,grounded, this));
      } else {
         addPlane(new LargePlane(id, currLocation, destination,timetoMaintenance, timeleftMaintain,timetoarrival,timetotakeoff,grounded, this));
      }
   }

   /**
    * Generates a String of a given length
    * It will also ensure that the string is not the same as any airport code before (since there are less permutaions of the 3 digit codes)
    * @param leng
    * @return
    */
   private String stringGen(int leng){
   
   
      //Creating a string with the whole alphabet which the rest of the method will draw from randomly to create another string
      String alphabet = "ABCDEFGHIJKLMNOPQRSTUV";
      String returning = "";
   
      //pulling random characters and concatenating them
      for (int i = 0; i < leng; i ++){
         returning += alphabet.charAt((int)(Math.random()*(alphabet.length())));
      }
   
      //lazy while loop
      //if the string matches another ID, it will generate again randomly
      //This means as the program approaches the permutation limit, generation will be exponentially slowed
      if (searchAirportsByCode(returning) != null){
         return stringGen(leng);
      }
   
      return returning;
   
   }

   /**
    * A boolean generator
    * USED ONLY FOR MAINTENANCE DETERMINATION NOT AN EVEN CHANCE
    * @return
    */
   private boolean genBool(){
      return Math.random() > .96;
   }

   /**
    * Randomly determines a number which is then used to represent the number of runways at a given airport
    * @return
    */
   private int genRunway(){
      return (int)((Math.random() * 8) + 15);
   }

   /**
    * Generates the chance an airport has to have weatehr conditions that prohibit landing
    * Anything between 0 and 0.4
    * @return
    */
   private double genWeather(){
      return Math.random() * .4;
   }

   /**
    * Generated random values for a geographic coordinate
    * @return
    */
   private Location locationGen(){
      int latDegrees = (int)(Math.random() * 40);
      int latMinutesIn = (int)(Math.random() * 25);
      int latSecondsIn = (int)(Math.random() * 25);
      boolean latIsNorthIn = genBool();
      int longDegreesIn = (int)(Math.random() * 40);
      int longMinutesIn = (int)(Math.random() * 25);
      int longSecondsIn = (int)(Math.random() * 25);
      boolean longIsEastIn = genBool();
      int elevationIn = (int)(Math.random() * 5);
      Location place = new Location(latDegrees,latMinutesIn,latSecondsIn,latIsNorthIn,longDegreesIn,longMinutesIn,longSecondsIn,longIsEastIn,elevationIn);
      return place;
   }

   //accessors
   public String getName(){
      return name;
   }
   public WorldTime getTime() {
      return time;
   }

   //mutators
   public void setName(String name) {
      this.name = name;
   }
   public void setTime(WorldTime time) {
      this.time = time;
   }

}
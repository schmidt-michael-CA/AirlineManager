/*
	File Name: AirlineUI.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
	Created: Dec 17, 2019
   Modified: January 20, 2020; Christian
	Description: AirlineUI class, contains the main method that runs the airplane manager
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AirlineUI {
   //global constants
   private static final String INVALID_INPUT_MSG = "Invalid input. Enter again: "; //error message for invalid input
   
   //---FILE PATHING---
   //file path constants
   private static final String SAVE_DIR = "Saves"; //file path to save directory
   private static final String AIRPORT_DIR = "\\Airports"; //file path from airline to airport directory
   private static final String ROUTE_DIR = "\\Routes"; //file path from airline to routes directory
   private static final String PLANE_DIR = "\\Planes"; //file path from airline to planes directory
   private static final String FILE_AIRPORT_N = "\\airport"; //file name for an airport n. Append n at the end and add the file extension (.txt)
   private static final String FILE_ROUTE_N = "\\route"; //file name for a route n. Append n at the end and add the file extension (.txt)
   private static final String FILE_PLANE_N = "\\plane"; //file name for a plane n. Append n at the end and add the file extension (.txt)
   private static final String FILE_MASTER_CONFIG = "\\masterConfig"; //file name for master config file
   private static final String FILE_CONFIG = "\\config"; //file name for config file
   private static final String FILE_EXT = ".txt"; //file extension for all save files
   
   private static final String MASTER_CONFIG_PATH = SAVE_DIR + FILE_MASTER_CONFIG + FILE_EXT; //master config file path
   
   //file path variables
   private static String airlineDir; //file path from save directory to airline
   
   //---ERROR CATCHING ERROR MESSAGES---
   private static final String ERR_IN = "Error in variable: ";
   private static final String ERR_DES = "Error: ";
   private static final String ERR_END_MSG = "Could not assign value to variable. ";
   
   private static final String ERR_NEGATIVE_NUM = "Invalid integer input data. Negative number not allowed. ";
   
   private static final String ERR_AIRPORT_CODE_FORMAT = "Invalid IATA airport code format. ";
   
   private static final String ERR_LOC_NS = "Invalid North/South input data. ";
   private static final String ERR_LOC_EW = "Invalid East/West input data. ";
   private static final String ERR_TF = "Invalid true/false input data. ";
   
   private static final String ERR_DUPLICATE = "Duplicate already exists. ";
   
   private static final String ERR_DNE_AIRPORT = "The airport referenced does not exist. ";
   private static final String ERR_DNE_PLANE_TYPE = "The plane type referenced does not exist. ";
   
   /*
   Error message format
ERR_IN + "variableName" + "\n" + ERR_DES + ERR_TYPE + "\n" + ERR_END_MSG
   */
   
   //scanner
   private static Scanner sc = new Scanner (System.in); //create scanner to be used in all methods

   public static void main (String[] args) {
      Airline airline = new Airline();
   //       Airline[] airlineArray = {new Airline()};
   //       ArrayList<Airline> airlines;
   
      //load information from database
      loadInfo(airline);
   
      System.out.println("Main: (planes) " + airline.planeList.size());
      System.out.println("Main: (airports) " + airline.airportList.size());
      System.out.println("Main: (routes) " + airline.routeList.size());
   
      addFiller(airline);
   
      System.out.println("Main: (planes) " + airline.planeList.size());
      System.out.println("Main: (airports) " + airline.airportList.size());
      System.out.println("Main: (routes) " + airline.routeList.size());
   
      //starts the menu
      airlineUIMenu(airline);
   }

   /*
   //needed for array; if masterConfig is broken then the program cannot load anything
   public static int savedAirlines() { //gets the number of Airlines stored in the file system
      BufferedReader in
      int numSavedAirlines = -1;
      String masterConfigFilePath = "Saves\\masterConfig.txt";
      try {
         in = new BufferedReader(new FileReader(masterConfigFilePath));
         numSavedAirlines = Integer.parseInt(in.readLine());
         in.close();
      }
      catch (IOException iox) {
         System.out.println("Problem loading master config at ROOT_DIRECTORY" + masterConfigFilePath + ". No data could be loaded. Terminating program. "); 
         System.exit(1); 
      }
      catch (NumberFormatException nfe) {
         System.out.println("Problem reading master config at ROOT_DIRECTORY" + masterConfigFilePath + ". No data could be loaded. Terminating program. ");
         System.exit(1);
      }
      return numSavedAirlines;
   }
   */

   public static void loadInfo(Airline airlineIn) { //initializes an Airline object and loads information from the file system
   
      BufferedReader in; //BufferedReader
   
      //variables for handling of while loops for reading in
      boolean exit = false;
      boolean useCounterLimit = true;
      int counter = 0;
      int airlineCounter = 0; //for single airline
      int numAirlines;
      boolean successfulFileOpen;
      
      //file pathing variables
      String configPath;
      String airportPath;
      String routePath;
      String planePath;
   
      //variables listing the number of Airport, Route, and Plane objects needed to be loaded <- these are the counter limits
      int numAirports = 0;
      int numRoutes = 0;
      int numPlanes = 0;
   
      //---temporary variables for transfer from file contents to constructors---
      //for comparing String inputs
      String inputTemp; //temporary stores information read in from file
   
      //for Airline objects
      String airlineName = null; //will be changed
   
      //for WorldTime objects
      int worldTimeHour = -1;
      int worldTimeDay = -1;
      int worldTimeMonth = -1;
      int worldTimeYear = -1;
   
      //for Location objects
      int latDegrees;
      int latMinutes;
      int latSeconds;
      boolean latIsNorth;
      int longDegrees;
      int longMinutes;
      int longSeconds;
      boolean longIsEast;
      int locationElevation;
   
      //for Airport objects
      String airportName = "";
      String airportCode = "";
      String airportCity = "";
      String airportCountry = "";
      int airportRunwayCapacity; //runway capacity in planes per hour
      double airportBadWeatherChance;
      boolean airportGoodWeather;
      boolean airportIsOpen;
      boolean airportHasMaintenance;
   
      //for Route objects
      String routeId; //eg. BA001 (A318 London City to JFK via Shannon)
      String routeStartAirportCode; //eg. YYZ
      String routeEndAirportCode; //eg. YYZ
      int routeTimeToNextFlight;
      int routeFrequency; //frequency that this route should be serviced, in hours
   
      //for Plane objects
      String planeId;
      String planeType;
      String planeCurrLocationAirportCode;
      String planeDestinationAirportCode;
      int planeTimeToMaintenance;
      int planeTimeToArrival;
      int planeTimeToDeparture;
      boolean planeGrounded;
      Airport currLocation = null;
      Airport destination = null;
   
      //-------------------------------------------------------------------------------------------------------------------------------------------------------
   
      //---FILE LOADING---
      /*
      Primary loading logic is to use a counter to loop through the precise number of Airport/Route/Plane objects stored in the data system
      Backup loading logic is to loop through the Airport/Route/Plane folder until an IOException error is thrown and there is no following Airport/Route/Plane data file
      
      Primary loading logic is able to recover from bad or missing Airport/Route/Plane files. The config file MUST be correct.
      Backup loading logic is able to recover from a bad or missing config file. There must be NO missing Airport/Route/Plane files.
      If the config file is bad or missing and Airport/Route/Plane files are bad or missing, not all information will be loaded into the program.
      
      If there is an unsolvable error reading a file, then it will be skipped and an error message will be output.
      If an unsolvable error occurs while reading the config file, it will create an Airline object with what information it has
      
      ---FILE LOADING ERROR HANDLING---
      IOException is used when the file cannot be found or there is an error in reading the file
      NumberFormatException is used when a value in the file cannot be converted to the correct format (eg. String parse to int or String change to boolean)
      InputMismatchException is thrown when a value has been read correctly but does not pass validity checks (eg. negative time)
      */
   
      //---LOAD MASTER CONFIG FILE---
      successfulFileOpen = false; //reset successfulFileOpen to false; not strictly necessary since is initialized to false, kept for consistency
      try {
         //read in contents of master config file
         in = new BufferedReader(new FileReader(MASTER_CONFIG_PATH));
         successfulFileOpen = true;
         
         //read in and check numAirlines
         numAirlines = Integer.parseInt(in.readLine());
         if (numAirlines < 0) {
            throw new InputMismatchException(ERR_IN + "numAirlines" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG); //throw error
         }
         
         //keep reading file
         //this for loop will only run once, as our program only supports one airline
         //if the user tries to store more than one airline, only the last airline listed in masterConfig.txt will be loaded properly
         //***for expansion, within the for loop, it would call another method to load each airline's information separately
         for (int i = 0; i < numAirlines; i++) {
            airlineName = in.readLine();
         }
         
         in.close(); //close
      } catch (IOException iox) {
         if (successfulFileOpen) { //IOException after file opened
            System.out.println("Error reading master config file at " + MASTER_CONFIG_PATH + ". No data could be loaded. Terminating program.");
            System.out.println("IOX");
            System.exit(1); //terminate program
         }
         else { //file could not be opened
            System.out.println("Error accessing master config file at " + MASTER_CONFIG_PATH + ". No data could be loaded. Terminating program.");
            System.out.println("IOX");
            System.exit(1); //terminate program
         }
      } catch (NumberFormatException nfe) {
         System.out.println("Error reading master config file at " + MASTER_CONFIG_PATH + ". No data could be loaded. Terminating program.");
         System.out.println("NFE");
         System.exit(1); //terminate program
      } catch (InputMismatchException imx) {
         System.out.println("Invalid value in master config file at " + MASTER_CONFIG_PATH + ". ");
         System.out.println("Details");
         System.out.println(imx.getMessage());
         System.out.println("\nNo data could be loaded. Terminating program.");
         System.out.println("IMX");
         System.exit(1);
      }
      
      airlineDir = "\\" + airlineName; //set airlineDir for file pathing
   
      //---LOAD CONFIG FILE---
      configPath = SAVE_DIR + airlineDir + FILE_CONFIG + FILE_EXT; //set file path for config
      try {
         //read in contents of config.txt
         in = new BufferedReader (new FileReader(configPath));
         airlineName = in.readLine();
         
         //read in and check WorldTime
         worldTimeHour = Integer.parseInt(in.readLine());
         worldTimeDay = Integer.parseInt(in.readLine());
         worldTimeMonth = Integer.parseInt(in.readLine());
         worldTimeYear = Integer.parseInt(in.readLine());
         if (worldTimeHour < 0) { //if worldTimeHour is negative throw error with description
            throw new InputMismatchException(ERR_IN + "worldTimeHour" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
         }
         if (worldTimeDay < 0) { //if worldTimeDay is negative throw error with description
            throw new InputMismatchException(ERR_IN + "worldTimeDay" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
         }
         if (worldTimeMonth < 0) { //if worldTimeMonth is negative throw error with description
            throw new InputMismatchException(ERR_IN + "worlTimeMonth" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
         }
         if (worldTimeYear < 0) { //if worldTimeYear is negative throw error with description
            throw new InputMismatchException(ERR_IN + "worldTimeYear" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
         }
         
         //read in and check numAirports
         numAirports = Integer.parseInt(in.readLine());
         if (numAirports < 0) { //if negative throw error with description
            throw new InputMismatchException(ERR_IN + "numAirports" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
         }
      //          System.out.println(numAirports + " airports"); //debug
         
         //read in and check numRoutes
         numRoutes = Integer.parseInt(in.readLine());
         if (numRoutes < 0) { //if negative throw error with description
            throw new InputMismatchException(ERR_IN + "numRoutes" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
         }
      //          System.out.println(numRoutes + " routes"); //debug
         
         //read in and check numPlanes
         numPlanes = Integer.parseInt(in.readLine());
         if (numPlanes < 0) { //if negative throw error with description
            throw new InputMismatchException(ERR_IN + "numPlanes" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
         }
      //          System.out.println(numPlanes + " planes"); //debug
      
         in.close(); //close
         
         airlineIn.setName(airlineName); //set name of airlineIn
         airlineIn.setTime(new WorldTime(worldTimeHour,worldTimeDay,worldTimeMonth,worldTimeYear)); //set time of airlineIn
         
      //          System.out.println(); //debug
      //          System.out.println(airlineIn.getName()); //debug
      //          System.out.println(airlineIn.getTime()); //debug
      //          System.out.println(); //debug
      }
      
      //---CATCH CONFIG FILE ERRORS---
      catch (IOException iox) {
         if (!successfulFileOpen) { //if file could not be opened/found
            //cannot add date and time to airlineIn; leaves values as default
            System.out.println("Error accessing config file for " + airlineName + " at " + configPath + ". Created new Airline object with default values."); //error message
            System.out.println("Number of airports, planes, and routes could not be loaded. Switching to backup loading logic."); //inform user of change to backup loading logic
            useCounterLimit = false; //switch to backup loading logic
         }
         //for IOExceptions when reading in the file
         else if (worldTimeYear == -1) {
            System.out.println("Error reading WorldTime parameters in config file for " + airlineName + " at " + configPath + ". Created new Airline object with specified airline and name default WorldTime."); //error message
            airlineIn.setName(airlineName); //set name of airlineIn
            //leave airlineIn WorldTime as default
            System.out.println("Number of airports, planes, and routes could not be loaded. Switching to backup loading logic."); //inform user of change to backup loading logic
            useCounterLimit = false; //switch to backup loading logic
         }
         else {
            System.out.println("Error reading parameters for primary loading logic in config file for " + airlineName + " at " + configPath + ". Created new Airline object with specified airline name and WorldTime."); //error message
            airlineIn.setName(airlineName); //set name of airlineIn
            airlineIn.setTime(new WorldTime(worldTimeHour,worldTimeDay,worldTimeMonth,worldTimeYear)); //set time of airlineIn
            System.out.println("Number of airports, planes, and routes could not be loaded. Switching to backup loading logic."); //inform user of change to backup loading logic
            useCounterLimit = false; //switch to backup loading logic
         } //close if
         System.out.println(); //blank spacer line
      } //close catch
      
      catch (NumberFormatException nfe) {
         if (worldTimeYear == -1) { //error loading WorldTime
            System.out.println("Error reading WorldTime parameters in config file at " + configPath + ". Created new Airline object with specified airline name and default WorldTime."); //error message
            airlineIn.setName(airlineName);
            System.out.println("Number of airports, planes, and routes could not be loaded. Switching to backup loading logic."); //inform user of change to backup loading logic
            useCounterLimit = false; //switch to backup loading logic
         }
         else { //error loading number of Airports/Routes/Planes
            System.out.println("Error reading parameters for primary loading logic in config file at " + configPath + ". Created new Airline object with specified airline name and WorldTime."); //error message
            airlineIn.setName(airlineName);
            airlineIn.setTime(new WorldTime(worldTimeHour, worldTimeDay, worldTimeMonth, worldTimeYear)); //set time of airlineIn
            System.out.println("Number of airports, planes, and routes could not be loaded. Switching to backup loading logic."); //inform user of change to backup loading logic
            useCounterLimit = false; //switch to backup loading logic
            System.out.println(); //blank spacer line
         } //close if
      } //close catch
      
      catch (InputMismatchException imx) { //input does not comply with variable parameters
         System.out.println("Invalid value in config file at " + configPath + ". ");
         System.out.println("Details");
         System.out.println(imx.getMessage());
         System.out.println("IMX"); 
         System.out.println(); //blank spacer line
      }
      
      //reset all modified variables to default to facilitate error catching for later loading (required for multiple Airlines)
      airlineName = null;
      worldTimeHour = -1; //reset worldTime variables to default
      worldTimeDay = -1;
      worldTimeMonth = -1;
      worldTimeYear = -1;
      //DO NOT RESET COUNTERS
      
   //       System.out.println(useCounterLimit); //debug
      
      //end load config
   
      //---LOAD AIRPORTS---
      counter = 0; //reset counter to 0
      exit = false; //reset exit to false
      while ((!exit && !useCounterLimit) || (useCounterLimit && counter < numAirports)) { //first condition is for failure to read number of Airport/Route/Plane objects from config file and second is if number of planes was successfully read
         successfulFileOpen = false; //reset successfulFileOpen to false
         airportPath = SAVE_DIR + airlineDir + AIRPORT_DIR + FILE_AIRPORT_N + counter + FILE_EXT; //set file path for airportN.txt
      //          System.out.println(airportPath + ": SFO "+ successfulFileOpen); //debug
         try {
            in = new BufferedReader(new FileReader(airportPath));
            successfulFileOpen = true; //file successfully opened
         //             System.out.println(airportPath + ": SFO "+ successfulFileOpen); //debug
            
            //read in from file
            airportName = in.readLine();
            
            //read in and check airport code
            airportCode = in.readLine();
            if (airlineIn.checkNewAirportCode(airportCode) == null) { //if airportCode is invalid throw error
               throw new InputMismatchException(ERR_IN + "airportCode" + "\n" + ERR_DES + ERR_AIRPORT_CODE_FORMAT + "\n" + ERR_END_MSG);
            }
            
            //keep reading in...
            airportCity = in.readLine();
            airportCountry = in.readLine();
            
            //read in and check lat degrees/minutes/seconds
            latDegrees = Integer.parseInt(in.readLine());
            latMinutes = Integer.parseInt(in.readLine());
            latSeconds = Integer.parseInt(in.readLine());
            if (latDegrees < 0) { //if latDegrees is negative throw error with description
               throw new InputMismatchException(ERR_IN + "latDegrees" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            if (latMinutes < 0) { //if latMinutes is negative throw error with description
               throw new InputMismatchException(ERR_IN + "latMinutes" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            if (latSeconds < 0) { //if latSeconds is negative throw error with description
               throw new InputMismatchException(ERR_IN + "latSeconds" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            
            //read in and determine if latIsNorth is true/false/invalid (case sensitive)
            inputTemp = in.readLine(); 
            if (inputTemp.equals("N")) {
               latIsNorth = true;
            } else if (inputTemp.equals("S")) {
               latIsNorth = false;
            } else { //if not N or S throw error
               throw new InputMismatchException(ERR_IN + "latIsNorth" + "\n" + ERR_DES + ERR_LOC_NS + "\n" + ERR_END_MSG); //invalid value in file
            }
            
            //read in and check long degrees/minutes/seconds
            longDegrees = Integer.parseInt(in.readLine());
            longMinutes = Integer.parseInt(in.readLine());
            longSeconds = Integer.parseInt(in.readLine());
            if (longDegrees < 0) { //if longDegrees is negative throw error with description
               throw new InputMismatchException(ERR_IN + "longDegrees" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            if (longMinutes < 0) { //if longMinutes is negative throw error with description
               throw new InputMismatchException(ERR_IN + "longMinutes" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            if (longSeconds < 0) { //if longSeconds is negative throw error with description
               throw new InputMismatchException(ERR_IN + "longSeconds" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            
            //read in and determine if longIsEast is true/false/invalid (case sensitive)
            inputTemp = in.readLine(); 
            if (inputTemp.equals("E")) {
               longIsEast = true;
            } else if (inputTemp.equals("W")) {
               longIsEast = false;
            } else {
               throw new InputMismatchException(ERR_IN + "longIsEast" + "\n" + ERR_DES + ERR_LOC_EW + "\n" + ERR_END_MSG); //invalid value in file
            }
            
            //keep reading in...
            locationElevation = Integer.parseInt(in.readLine());
            
            //read in and determine if airportRunwayCapacity is valid
            airportRunwayCapacity = Integer.parseInt(in.readLine());
            if (airportRunwayCapacity <= 0) { //if airportRunwayCapacity is less than/equal to zero throw error
               throw new InputMismatchException(ERR_IN + "airportRunwayCapacity" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            
            //read in and determine if airportBadWeatehrChance is valid
            airportBadWeatherChance = Double.parseDouble(in.readLine());
            if (airportBadWeatherChance < 0) {//if airportBadWeatehrChance is less than zero throw error
               throw new InputMismatchException(ERR_IN + "airportBadWeatherChance" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            
            //read in and determine if airportGoodWeather is true/false/invalid (case sensitive)
            inputTemp = in.readLine(); 
            if (inputTemp.equals("true")) {
               airportGoodWeather = true;
            }
            else if (inputTemp.equals("false")) {
               airportGoodWeather = false;
            }
            else {
               throw new InputMismatchException(ERR_IN + "airportGoodWeather" + "\n" + ERR_DES + ERR_TF + "\n" + ERR_END_MSG); //invalid value in file
            }
            
            //read in and determine if airportIsOpen is true/false/invalid (case sensitive)
            inputTemp = in.readLine(); 
            if (inputTemp.equals("true")) {
               airportIsOpen = true;
            }
            else if (inputTemp.equals("false")) {
               airportIsOpen = false;
            }
            else {
               throw new InputMismatchException(ERR_IN + "airportIsOpen" + "\n" + ERR_DES + ERR_TF + "\n" + ERR_END_MSG); //invalid value in file
            }
            
            //read in and determine if airportHasMaintenance is true/false/invalid (case sensitive)
            inputTemp = in.readLine(); 
            if (inputTemp.equals("true")) {
               airportHasMaintenance = true;
            }
            else if (inputTemp.equals("false")) {
               airportHasMaintenance = false;
            }
            else {
               throw new InputMismatchException(ERR_IN + "airportHasMaintenance" + "\n" + ERR_DES + ERR_TF + "\n" + ERR_END_MSG); //invalid value in file
            }
            
            in.close(); //close file
            
            //create new airport with location object and everything else and add to airlineIn
            airlineIn.addAirport(new Airport(airportName, airportCode, airportCity, airportCountry, new Location(latDegrees, latMinutes, latSeconds, latIsNorth, longDegrees, longMinutes, longSeconds, longIsEast, locationElevation), airportRunwayCapacity, airportBadWeatherChance, airportGoodWeather, airportIsOpen, airportHasMaintenance, airlineIn));
         }
         
         //---CATCH AIRPORT ERRORS---
         catch (IOException iox) {
            if (!successfulFileOpen) { //file could not be opened
               System.out.println("Error accessing Airport file for " + airlineIn.getName() + " at " + airportPath + ". Airport not loaded."); //output error message
               if (!useCounterLimit) { //if using backup logic
                  System.out.println("Backup loading logic has determined that all Airports have been loaded. " + airlineIn.airportList.size() + " Airports were loaded."); //backup loading logic end message
                  exit = true; //stop loading Airports
               } //close inner if
            } //close outer if
            else { //file opened successfully but error while reading
               System.out.println("Error reading Airport file for " + airlineIn.getName() + " at " + airportPath + ". Airport not loaded."); //output error message
            }
            System.out.println("IOX"); //debug
            System.out.println(); //blank spacer line
         }
         
         catch (NumberFormatException nfe) {
            System.out.println("Error reading Airport file for " + airlineIn.getName() + " at " + airportPath + ". Airport not loaded."); //output error message
            System.out.println("NFE");
            System.out.println(); //blank spacer line
         } //close catch
         
         catch (InputMismatchException imx) { //input does not comply with variable parameters
            System.out.println("Invalid value in Airport file for " + airlineIn.getName() + " at " + airportPath + ". Airport not loaded.");
            System.out.println("Details");
            System.out.println(imx.getMessage());
            System.out.println("IMX");
            System.out.println(); //blank spacer line
         }
         
         counter++; //increment counter to proceed to next Airport file <-- needs to be outside try if there is an IOException while using primary loading logic so the file can be skipped
      } //close while; end load airports
   
   
   
      //---LOAD ROUTES---
      counter = 0; //reset counter
      exit = false; //reset exit to false
      while ((!exit && !useCounterLimit) || (useCounterLimit && counter < numRoutes)) { //first condition is for failure to read number of Airport/Route/Plane objects from config file and second is if number of routes was successfully read
         successfulFileOpen = false; //reset successfulFileOpen to false
         routePath = SAVE_DIR + airlineDir + ROUTE_DIR + FILE_ROUTE_N + counter + FILE_EXT; //set file path for routeN.txt
      //          System.out.println(routePath + ": SFO "+ successfulFileOpen); //debug
         
         try {
            in = new BufferedReader(new FileReader(routePath));
            successfulFileOpen = true;
         //             System.out.println(routePath + ": SFO "+ successfulFileOpen); //debug
            
            //read in and check routeId
            routeId = in.readLine(); 
            if (airlineIn.searchRoutesById(routeId) != null) { //if route with the same name already exists throw error
               throw new InputMismatchException(ERR_IN + "routeId" + "\n" + ERR_DES + ERR_DUPLICATE + "\n" + ERR_END_MSG);
            }
            
            //read in and check start/end airportCode
            routeStartAirportCode = in.readLine(); 
            if (Airport.formatAirportCode(routeStartAirportCode) == null) { //if invalid airport code format
               throw new InputMismatchException(ERR_IN + "routeStartAirportCode" + "\n" + ERR_DES + ERR_AIRPORT_CODE_FORMAT + "\n" + ERR_END_MSG);
            }
            routeEndAirportCode = in.readLine(); 
            if (Airport.formatAirportCode(routeStartAirportCode) == null) { //if invalid airport code format
               throw new InputMismatchException(ERR_IN + "routeStartAirportCode" + "\n" + ERR_DES + ERR_AIRPORT_CODE_FORMAT + "\n" + ERR_END_MSG);
            }
            //routes are allowed to have the same start and end but must have different names
            
            //read in and check routeTimeToNextFlight
            routeTimeToNextFlight = Integer.parseInt(in.readLine()); 
            //routes are allowed to have negative timeToNextFlight
            
            //read in and check routeFrequency
            routeFrequency = Integer.parseInt(in.readLine()); 
            if (routeFrequency < 0) { //if negative routeFrequency throw error
               throw new InputMismatchException(ERR_IN + "routeFrequency" + "\n" + ERR_DES + ERR_NEGATIVE_NUM + "\n" + ERR_END_MSG);
            }
            
            in.close(); //close
            
            airlineIn.addRoute(routeId, routeStartAirportCode, routeEndAirportCode, routeTimeToNextFlight, routeFrequency, airlineIn); //create new Route with specified parameters from input file and add to routeList of airlineIn
         }
         
         catch (IOException iox) {
            if (!successfulFileOpen) { //error opening file
               System.out.println("Error accessing Route file for " + airlineIn.getName() + " at " + routePath + ". Route not loaded."); //output error message
               if (!useCounterLimit) { //if using backup logic
                  System.out.println("Backup loading logic has determined that all Routes have been loaded. " + airlineIn.routeList.size() + " Routes were loaded."); //backup loading logic end message
                  exit = true; //stop loading Routes
               } //close inner if
            }
            else { //IOException while reading file
               System.out.println("Error reading Route file for " + airlineIn.getName() + " at " + routePath + ". Route not loaded."); //output error message
            }
            System.out.println("IOX"); //debug
            System.out.println(); //blank spacer line
         } //close catch
         
         catch (NumberFormatException nfe) {
            System.out.println("Error reading Route file for " + airlineIn.getName() + " at " + routePath + ". Route not loaded."); //output error message
            System.out.println("NFE"); //debug
            System.out.println(); //blank spacer line
         } //close catch 
         
         catch (InputMismatchException imx) {
            System.out.println("Invalid value in Route file for " + airlineIn.getName() + " at " + routePath + ". Route not loaded.");
            System.out.println("Details");
            System.out.println(imx.getMessage());
            System.out.println("IMX"); //debug
            System.out.println(); //blank spacer line
         }
         
         counter++; //increment counter to proceed to next Route file <-- needs to be outside try if there is an IOException while using primary loading logic so the file can be skipped
      } //end load Routes
   
   
   
      //---LOAD PLANES---
      counter = 0; //reset counter
      exit = false; //reset exit to false
      while ((!exit && !useCounterLimit) || (useCounterLimit && counter < numPlanes)) { //first condition is for failure to read number of Airport/Route/Plane objects from config file and second is if number of planes was successfully read
         successfulFileOpen = false; //reset successfulFileOpen to false
         planePath = SAVE_DIR + airlineDir + PLANE_DIR + FILE_PLANE_N + counter + FILE_EXT; //set file path for planeN.txt
      //          System.out.println(planePath + ": SFO "+ successfulFileOpen); //debug
         
         try {
            in = new BufferedReader(new FileReader(planePath));
            successfulFileOpen = true;
         //             System.out.println(planePath + ": SFO "+ successfulFileOpen); //debug            
            
            //read in
            planeId = in.readLine();
            planeType = in.readLine();
            
            //read in and check planeCurrLocationAirportCode
            planeCurrLocationAirportCode = in.readLine();
            if (!(planeCurrLocationAirportCode.equals(""))) { //if plane is at an airport
               if (Airport.formatAirportCode(planeCurrLocationAirportCode) == null) { //invalid airport code format
                  throw new InputMismatchException(ERR_IN + "planeCurrLocationAirportCode" + "\n" + ERR_DES + ERR_AIRPORT_CODE_FORMAT + "\n" + ERR_END_MSG);
               }
               currLocation = airlineIn.searchAirportsByCode(planeCurrLocationAirportCode); //get currLocation from list of airports
               if (currLocation == null) { //airport with the same code does not exist
                  throw new InputMismatchException(ERR_IN + "planeCurrLocationAirportCode" + "\n" + ERR_DES + ERR_DNE_AIRPORT + "\n" + ERR_END_MSG);
               }
            }
            
            //read in and check planeDestinationAirportCode
            planeDestinationAirportCode = in.readLine();
            if (!(planeDestinationAirportCode.equals(""))) { //if the plane has a destination airport
               if (Airport.formatAirportCode(planeDestinationAirportCode) == null) { //invalid airport code format
                  throw new InputMismatchException(ERR_IN + "planeDestinationAirportCode" + "\n" + ERR_DES + ERR_AIRPORT_CODE_FORMAT + "\n" + ERR_END_MSG);
               }
               destination = airlineIn.searchAirportsByCode(planeDestinationAirportCode); //destination from list of airports
               if (destination == null) { //airport with the same code does not exist
                  throw new InputMismatchException(ERR_IN + "planeDestinationAirportCode" + "\n" + ERR_DES + ERR_DNE_AIRPORT + "\n" + ERR_END_MSG);
               }
            }         
            
            //read in
            planeTimeToMaintenance = Integer.parseInt(in.readLine()); 
            planeTimeToArrival = Integer.parseInt(in.readLine()); 
            planeTimeToDeparture = Integer.parseInt(in.readLine()); 
            
            //read in and determine if planeGrounded is true/false/invalid
            inputTemp = in.readLine();
            if (inputTemp.equals("true")) {
               planeGrounded = true;
            }
            else if (inputTemp.equals("false")) {
               planeGrounded = false;
            }
            else {
               throw new InputMismatchException(ERR_IN + "planeGrounded" + "\n" + ERR_DES + ERR_TF + "\n" + ERR_END_MSG); //invalid value in file
            }
            
            in.close(); //close
            
            if (planeType.charAt(0) == 'S'){ //check type of plane inputted
               airlineIn.addPlane(new SmallPlane(planeId, currLocation, destination, planeTimeToMaintenance, 0, planeTimeToArrival, planeTimeToDeparture, planeGrounded, airlineIn));
               //System.out.println("Added small plane");
            } else if (planeType.charAt(0) == 'M'){
               airlineIn.addPlane(new MediumPlane(planeId, currLocation, destination, planeTimeToMaintenance, 0, planeTimeToArrival, planeTimeToDeparture, planeGrounded, airlineIn));
               //System.out.println("Added medium plane");
            } else if (planeType.charAt(0) == 'L'){
               airlineIn.addPlane(new LargePlane(planeId, currLocation, destination, planeTimeToMaintenance, 0, planeTimeToArrival, planeTimeToDeparture, planeGrounded, airlineIn));
               //System.out.println("Added large plane");
            } else {
               throw new InputMismatchException(ERR_IN + "planeType" + "\n" + ERR_DES + ERR_DNE_PLANE_TYPE + "\n" + ERR_END_MSG); //throw error
            }
         
         }
         catch (IOException iox) {
            if (!successfulFileOpen) {
               System.out.println("Error accessing Plane file for " + airlineIn.getName() + " at " + planePath + ". Plane not loaded."); //output error message
               if (!useCounterLimit) { //if using backup logic
                  System.out.println("Backup loading logic has determined that all Planes have been loaded. " + airlineIn.planeList.size() + " Planes were loaded."); //backup loading logic end message
                  exit = true; //stop loading Planes
               } //close inner if
            } //close outer if
            else { //IOException while reading file
               System.out.println("Error reading Plane file for " + airlineIn.getName() + " at " + planePath + ". Plane not loaded."); //output error message
            }
            System.out.println("IOX"); //debug
            System.out.println(); //blank spacer line
         } //close catch
         
         catch (NumberFormatException nfe) {
            System.out.println("Error reading Plane file for " + airlineIn.getName() + " at " + planePath + ". Plane not loaded."); //output error message
            System.out.println("NFE"); //debug
            System.out.println(); //blank spacer line
         } //close catch
         
         catch (InputMismatchException imx) { //input does not comply with variable parameters
            System.out.println("Invalid value in Plane file for " + airlineIn.getName() + " at " + planePath + ". Plane not loaded."); //output error message
            System.out.println("Details");
            System.out.println(imx.getMessage());
            System.out.println("IMX"); //debug
            System.out.println(); //blank spacer line
         }
      
         counter++; //increment counter to proceed to next Plane file <-- needs to be outside try if there is an IOException while using primary loading logic so the file can be skipped
      }
   
      //---LOADING COMPLETE---
   
   } //close loadInfo method


   //---SAVING---
   public static void saveInfo (Airline airline) {
      System.out.println("Saving information and exiting.");
      BufferedWriter out;
      
      //file path variables
      String configPath;
      String airportPath;
      String routePath;
      String planePath;
      
      //save master config
      try {
         out = new BufferedWriter(new FileWriter(MASTER_CONFIG_PATH)); //set out
         
         //write
         out.write("" + 1); //program currently only supports one airline; can be replaced with variables for multi-airline operations
         out.newLine();
         out.write(airline.getName()); //one airline only
         out.close(); //close
      }
      
      catch (IOException iox) {
         System.out.println("Problem saving master config file at " + MASTER_CONFIG_PATH + ". "); //error message
      }
      
      //save config
      configPath = SAVE_DIR + airlineDir + FILE_CONFIG + FILE_EXT;
      try {
         out = new BufferedWriter (new FileWriter (configPath));
         
         //write
         out.write(airline.getName());
         out.newLine();
         out.write(airline.getTime().saveWorldTime());
         out.newLine();
         out.write("" + airline.airportList.size());
         out.newLine();
         out.write("" + airline.routeList.size());
         out.newLine();
         out.write("" + airline.planeList.size());
         out.newLine();
         out.close(); //close
         
      } catch (IOException iox) {
         System.out.println ("Problem saving config file at " + configPath + ". "); //error message
      }
   
      //save airports
      for (int i = 0; i < airline.airportList.size(); i++) {
         airportPath = SAVE_DIR + airlineDir + AIRPORT_DIR + FILE_AIRPORT_N + i + FILE_EXT;
         try {
            out = new BufferedWriter (new FileWriter (airportPath));
            
            //write
            out.write("" + airline.airportList.get(i).getName());
            out.newLine();
            out.write("" + airline.airportList.get(i).getCode());
            out.newLine();
            out.write("" + airline.airportList.get(i).getCity());
            out.newLine();
            out.write("" + airline.airportList.get(i).getCountry());
            out.newLine();
            out.write("" + airline.airportList.get(i).getLocation().save());
            out.newLine();
            out.write("" + airline.airportList.get(i).getRunwayCapacity());
            out.newLine();
            out.write("" + airline.airportList.get(i).getBadWeatherChance());
            out.newLine();
            out.write("" + airline.airportList.get(i).getGoodWeather());
            out.newLine();
            out.write("" + airline.airportList.get(i).getIsOpen());
            out.newLine();
            out.write("" + airline.airportList.get(i).getHasMaintenance());
            out.newLine();
            out.close(); //close
            
         } catch (IOException iox) {
            System.out.println ("Problem saving Airport " + airline.airportList.get(i).getCode() + " at " + airportPath + ". "); //error message
         }
      }
   
      //save routes
      for (int i = 0; i < airline.routeList.size(); i++) {
         routePath = SAVE_DIR + airlineDir + ROUTE_DIR + FILE_ROUTE_N + i + FILE_EXT;
         try {
            out = new BufferedWriter (new FileWriter (routePath));
            
            //write
            out.write("" + airline.routeList.get(i).getId());
            out.newLine();
            out.write("" + airline.routeList.get(i).getStart().getCode());
            out.newLine();
            out.write("" + airline.routeList.get(i).getEnd().getCode());
            out.newLine();
            out.write("" + airline.routeList.get(i).getTimeToNextFlight());
            out.newLine();
            out.write("" + airline.routeList.get(i).getFrequency());
            out.newLine();
            out.close(); //close
            
         } catch (IOException iox) {
            System.out.println ("Problem saving Route " + airline.routeList.get(i).getId() + " at " + routePath + ". ");
         }
      }
   
      //save planes
      for (int i = 0; i < airline.planeList.size(); i++) {
         planePath = SAVE_DIR + airlineDir + PLANE_DIR + FILE_PLANE_N + i + FILE_EXT; //file pathing
         try {
            out = new BufferedWriter (new FileWriter (planePath));
            out.write("" + airline.planeList.get(i).save());
            out.newLine();
            out.close(); //close
            
         } catch (IOException iox) {
            System.out.println("Problem saving Plane " + airline.planeList.get(i).getId() + " at " + planePath + ". ");
         }
      }
   
      //outputs total number of planes, routes and airports
      System.out.println("Planes: " + airline.planeList.size());
      System.out.println("Routes: " + airline.routeList.size());
      System.out.println("Airports: " + airline.airportList.size());
   }

//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

   // AIRLINE MENU
   public static void airlineUIMenu(Airline airline){
   
      int choice = 0;
      boolean exit = false;
      boolean validInput;
   
      while (!exit) {
         //allows time before outputing menu
         try{
            TimeUnit.MILLISECONDS.sleep(1500);
         } catch (InterruptedException iex){
            System.out.println("Could not wait");
         }
      
         //spacing
         for (int i = 0; i < 8; i ++){
            System.out.println();
         }
      
         //outputs menu and allows user to decide which function they would like to perform
         System.out.println ("Airline Menu:");
         System.out.println ("-1: Exit");
         System.out.println ("1: Add Route");
         System.out.println ("2: Add Plane");
         System.out.println ("3: Add Airport");
         System.out.println ("4: Delete Route");
         System.out.println ("5: Delete Plane");
         System.out.println ("6: Delete Airport");
         System.out.println ("7: Close Airport");
         System.out.println ("8: Pass Time");
         System.out.println ("9: Send Plane to early maintenance");
         System.out.println ("10: Search Planes by time to maintenance");
         System.out.println ("11: Search Planes by airport");
         System.out.println ("12: Search Planes by id");
         System.out.println ("13: Search Planes by type");
         System.out.println ("14: Search Routes by id");
         System.out.println ("15: Search Airports by code");
         System.out.println ("16: Ground Planes by type");
         System.out.println ("17: Unground Planes by type");
         System.out.println ("18: Delay Flight");
         System.out.println ("19: List all planes");
         System.out.println ("20: List all airports");
         System.out.println ("21: List all routes");
         System.out.println ();
         System.out.print ("Enter a number to perform a function: ");
         
         validInput = false;
         while (!validInput) {
            try {
               choice = sc.nextInt();
               validInput = true;
            }
            catch (InputMismatchException imx) {
               System.out.print(INVALID_INPUT_MSG);
               sc.nextLine();
            }
         }
      
         //goes through cases and calls correct method depending on choice
         switch (choice) {
            case -1:
               saveInfo(airline);
               exit = true;
               break;
            case 1:
               System.out.println("Adding new route");
               addRoute(airline);
               break;
            case 2:
               System.out.println("Adding new plane");
               addPlane(airline);
               break;
            case 3:
               System.out.println("Adding new airport");
               addAirport(airline);
               break;
            case 4:
               System.out.println("Deleting route");
               deleteRoute(airline);
               break;
            case 5:
               System.out.println("Deleting plane");
               deletePlane(airline);
               break;
            case 6:
               System.out.println("Deleting airport");
               deleteAirport(airline);
               break;
            case 7:
               System.out.println("Closing airport");
               closeAirport(airline);
               break;
            case 8:
               System.out.println("Passing time");
               passTime(airline);
               break;
            case 9:
               System.out.println("Sending plane to maintenance");
               sendToEarlyMaintenance(airline);
               break;
            case 10:
               System.out.println("Searching planes by time to maintenance");
               searchPlanesByTimeToMaintenance(airline);
               break;
            case 11:
               System.out.println("Searching planes by airport");
               searchPlanesByAirport(airline);
               break;
            case 12:
               System.out.println("Searching planes by ID");
               searchPlanesById (airline);
               break;
            case 13:
               System.out.println("Searching planes by type");
               searchPlanesByType(airline);
               break;
            case 14:
               System.out.println("Searching routes by ID");
               searchRoutesById(airline);
               break;
            case 15:
               System.out.println("Searching airports by code");
               searchAirportsByCode(airline);
               break;
            case 16:
               System.out.println("Grounding planes by type");
               groundPlanesByType(airline);
               break;
            case 17:
               System.out.println("Ungrounding planes by type");
               ungroundPlanesByType(airline);
               break;
            case 18:
               System.out.println("Delaying flight");
               delayFlight(airline);
               break;
            case 19:
               System.out.println("Listing all planes");
               airline.listAllPlanes();
               break;
            case 20:
               System.out.println("Listing all airports");
               airline.listAllAirports();
               break;
            case 21:
               System.out.println("Listing all routes");
               airline.listAllRoutes();
               break;
            default:
               System.out.print (INVALID_INPUT_MSG);
         } //close switch
      } //close while loop
      
      System.out.println("Thank you for using the Airline Manager!");
   } //close method

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

   // option 1
   public static void addRoute (Airline airline) {
   
      String routeId = "", startStr = "", endStr = "";
      int timeTil = 0, frequency = 0;
   
      boolean validInput = false;
      sc.nextLine();
   
      //get the routeId
      System.out.print ("Enter the route ID: ");
      while (!validInput) {
         routeId = sc.nextLine();
         routeId = airline.checkNewRouteId(routeId); //checks if the routeId is in a valid format and doesn't already exist in a route
         if (routeId != null) { //if valid input, end while loop
            validInput = true;
         } else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      //get the start Airport code
      validInput = false; //reset validInput
      System.out.print ("Enter the airport code of the start of the route: ");
      while (!validInput) {
         startStr = sc.nextLine();
         startStr = Airport.formatAirportCode(startStr); //checks if the airport code is valid
         if (startStr != null) { //if valid input, continue to check
            if (airline.searchAirportsByCode(startStr) != null) { //searches for the correct airport
               validInput = true;
            } else { //if this airport does not exist, output error message
               System.out.print ("Airport does not exist. Enter again: ");
            }
         }
         else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      //get the destination Airport code
      validInput = false; //reset validInput
      System.out.print("Enter the airport code of the route destination: ");
      while (!validInput) {
         endStr = sc.nextLine();
         endStr = Airport.formatAirportCode(endStr); //checks if the airport code is valid
         if (endStr != null) { //if valid input, continue to check
            if (airline.searchAirportsByCode(endStr) != null) { //searches for the correct airport
               if (!(endStr.equals(startStr))) { //check if start and end are not the same
                  validInput = true;
               }
               else {
                  System.out.print("Cannot start and end at the same airport."); //route cannot start and end at the same airport
               }
            } else { //if this airport does not exist, output error message
               System.out.print ("Airport does not exist. Enter again: ");
            }
         } else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      //get the time until the next flight is needed
      validInput = false; //reset validInput
      System.out.print ("Enter the time until the next flight is needed: ");
      while (!validInput) {
         try {
            timeTil = sc.nextInt();
            if (timeTil < 0) { //if invalid input, throw error and exit try
               throw new InputMismatchException();
            }
            validInput = true;
         } catch (InputMismatchException ime) { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
            sc.nextLine();
         }
      } //close while loop
   
      //get the frequency of service for the route
      validInput = false; //reset validInput
      System.out.print ("Enter how frequent the service is in hours: ");
      while (!validInput) {
         try {
            frequency = sc.nextInt();
            if (frequency <= 0) { //if invalid input, throw error and exit try
               throw new InputMismatchException();
            }
            validInput = true;
         } catch (InputMismatchException ime) { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      airline.addRoute(routeId, startStr,endStr,timeTil,frequency,airline); //add a new Route in airline
      System.out.println("Route added successfully!"); //success message
   }

   // option 2
   public static void addPlane (Airline airline) {
      String planeType = "", planeId = "", currLocationStr = "", destinationStr = "", groundedStr;
      Airport currLocation = null, destination = null;
      int timeToMaintenance = -1, timeLeftMaintain = -1, timeToTakeoff = -1, timeToArrival = -1;
      boolean grounded = false, validInput, validLocations = false;
   
      validInput = false; //reset validInput
      System.out.print ("Enter the type of plane (Small Plane/Medium Plane/Large Plane): ");
      sc.nextLine(); //flush
      while (!validInput) {
         planeType = sc.nextLine();
         //checks if the input is valid
         if (planeType.equalsIgnoreCase("Small Plane") || planeType.equalsIgnoreCase("Medium Plane") || planeType.equalsIgnoreCase("Large Plane")) {
            validInput = true;
         } else { //if invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      //get the plane ID
      validInput = false; //reset validInput
      System.out.print ("Enter the plane id: "); //prompt
      while (!validInput) { //loop until valid input
         planeId = sc.nextLine();
         planeId = airline.checkNewPlaneId(planeId); //format planeId
         
         if (planeId == null) {
             //plane already exists
            System.out.println(INVALID_INPUT_MSG);
         } else {
             //planeId is valid
            validInput = true;
         }
      }
   
      //validLocations is used to ensure the user does not input null for both location and destination
      while (!validLocations) {
            //get the airport code of the current location
         validInput = false; //reset validInput
         System.out.print ("Enter the airport code of the current location (\"null\" if in flight): "); //prompt
         while (!validInput) {
            currLocationStr = sc.nextLine(); //user input
         
            if (currLocationStr.equals("null")) {
                   //set the current location to null
               currLocation = null;
               validInput = true;
            } else {
                   //location is an airport
               currLocationStr = Airport.formatAirportCode(currLocationStr); //checks and formats airportCode; if invalid it will be null
               if (currLocationStr != null) { //good format
                  currLocation = airline.searchAirportsByCode(currLocationStr);
                  if (currLocation != null) { //if Airport referenced by airportCode exists
                     validInput = true; //exit loop
                  }
                  else { //Airport referenced by airportCode does not exist
                     System.out.print(INVALID_INPUT_MSG); //invalid input; loop again
                  }
               }
               else { //bad airportCode format
                  System.out.print(INVALID_INPUT_MSG); //invalid input; loop again
               }
            }
         } //close while
      
            //get the airport code of the destination
         validInput = false; //reset validInput
         System.out.print ("Enter the airport code of the destination (\"null\" if not taking off): "); //prompt
         while (!validInput) {
            destinationStr = sc.nextLine(); //user input
         
            if (destinationStr.equalsIgnoreCase("null")) {
                   //destination is null
               destination = null;
               validInput = true;
            } else {
                   //has destination 
               destinationStr = Airport.formatAirportCode(destinationStr); //checks and formats airportCode; if invalid it will be null
               if (destinationStr != null) { //good format
                  if (!(destinationStr.equals(currLocationStr))) { //if destinationStr is not the same as currLocationStr
                     destination = airline.searchAirportsByCode(destinationStr);
                  
                     if (destination != null) { //if Airport referenced by airportCode exists
                        validInput = true; //exit loop
                     }
                     else { //Airport referenced by airportCode does not exist
                        System.out.print(INVALID_INPUT_MSG); //invalid input; loop again
                     }
                  }
                  else { //if destinationStr is the same as currLocationStr
                     System.out.print(INVALID_INPUT_MSG); //invalid input; loop again
                  }
               }
               else { //bad airportCode format
                  System.out.print(INVALID_INPUT_MSG); //invalid input; loop again
               }
            }
         } //close while
            
         if (currLocation != null || destination != null) {
                //if currLocation and destination are not null, then locations are valid
            validLocations = true;
         } else {
            System.out.println("Plane cannot have no location or destination. Enter again: ");
         }
      }
        
      //get time to maintenance
      System.out.print ("Enter the time to maintenance: ");
      validInput = false; //reset
      while (!validInput) {
         try {
            timeToMaintenance = sc.nextInt();
            
            if (timeToMaintenance < 0) {
               System.out.println("Time to maintenance cannot be less than 0.");
            } else {
               validInput = true;
            }
            
         } catch (InputMismatchException imx) { //if invalid, print error message
            System.out.print (INVALID_INPUT_MSG);
            sc.nextLine(); //flush
         }
      }
   
      //a plane can only have timeLeftMaintain if it is currently undergoing maintenance, so if it is on the ground
      if (currLocation != null && destination == null) { //plane on the ground and not about to takeoff
         validInput = false; //reset validInput
         System.out.print ("Enter the time left to maintain (-1 if plane is not in maintenance): ");
         while (!validInput) {
            try {
               timeLeftMaintain = sc.nextInt();
               validInput = true;
              
              //check if -1 was inputted
               if (timeLeftMaintain == -1) {
                //timeLeftMaintain should default to total time for maintenance for that plane type
                  if (planeType.equalsIgnoreCase("Small Plane")) {
                     timeLeftMaintain = SmallPlane.getMAINTENANCE_DURATION();
                  } else if (planeType.equalsIgnoreCase("Medium Plane")) {
                     timeLeftMaintain = MediumPlane.getMAINTENANCE_DURATION();
                  } else if (planeType.equalsIgnoreCase("Large Plane")){
                     timeLeftMaintain = LargePlane.getMAINTENANCE_DURATION();
                  }
               }
            } catch (InputMismatchException imx) { //if invalid, print error message
               System.out.print (INVALID_INPUT_MSG);
               sc.nextLine(); //flush
            }
         }
      } else {
          //otherwise the plane is not in maintenance, and so timeLeftMaintain should be default 
         if (planeType.equalsIgnoreCase("Small Plane")) {
            timeLeftMaintain = SmallPlane.getMAINTENANCE_DURATION();
         } else if (planeType.equalsIgnoreCase("Medium Plane")) {
            timeLeftMaintain = MediumPlane.getMAINTENANCE_DURATION();
         } else if (planeType.equalsIgnoreCase("Large Plane")){
            timeLeftMaintain = LargePlane.getMAINTENANCE_DURATION();
         }
      }
      
      //timeToTakeoff should only have a value if plane is about to takeoff
      if (currLocation != null && destination != null) {
        //get time to takeoff
         validInput = false; //reset validInput
         System.out.print ("Enter the time to takeoff: "); //prompt
         while (!validInput) {
            try {
               timeToTakeoff = sc.nextInt();
               validInput = true;
            }
            catch (InputMismatchException imx) { //error catching
               System.out.print(INVALID_INPUT_MSG);
               sc.nextLine(); //flush
            }
         }
      } else {
          //if plane is not taking off, set to -1
         timeToTakeoff = -1;
      }
   
      //timeToArrival only exists if plane is currently flying
      if (currLocation == null && destination != null) {
          //get time to arrival
         validInput = false; //reset validInput
         System.out.print ("Enter the time to arrival: "); //prompt
         while (!validInput) {
            try {
               timeToArrival = sc.nextInt();
               validInput = true;
            }
            catch (InputMismatchException imx) { //error catching
               System.out.print(INVALID_INPUT_MSG);
               sc.nextLine(); //flush
            }
         }
      } else {
          //otherwise, timeToArrival should be -1
         timeToArrival = -1;
      }
   
      //get if the plane is grounded or not
      //if the plane is currently flying and grounded status is inputted as true, the plane will fly as planned to destination, and then will
      //remain grounded until grounded status is removed
      System.out.print ("Enter if the plane is grounded or not (t/f): ");
      sc.nextLine(); //flush
      validInput = false; //reset validInput
      while (!validInput) {
         groundedStr = sc.nextLine();
         //if valid input, determine boolean
         if (groundedStr.equals("t") || groundedStr.equals("f")) {
            grounded = trueOrFalse(groundedStr);
            validInput = true;
         } else { //if invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      //create the plane objects and add to airline
      //everything has already been error checked and is valid
      if (planeType.equalsIgnoreCase("Small Plane")) {
         airline.addPlane(new SmallPlane (planeId, currLocation, destination, timeToMaintenance, timeLeftMaintain, timeToArrival, timeToTakeoff, grounded, airline));
      } else if (planeType.equalsIgnoreCase("Medium Plane")) {
         airline.addPlane(new MediumPlane (planeId, currLocation, destination, timeToMaintenance, timeLeftMaintain, timeToArrival, timeToTakeoff, grounded, airline));
      } else if (planeType.equalsIgnoreCase("Large Plane")){
         airline.addPlane(new LargePlane (planeId, currLocation, destination, timeToMaintenance, timeLeftMaintain, timeToArrival, timeToTakeoff, grounded, airline));
      }
      System.out.println("Plane added successfully!"); //success message
   }

   // option 3
   public static void addAirport (Airline airline) {
      String name = "", code = "", city, country, goodWeatherStr = "", maintenanceStr = "", isOpenStr = "";
      Location location;
      int runwayCapacity = 0; //runway capacity in planes per hour
      double badWeatherChance = 0;
      boolean goodWeather = false, isOpen = false, maintenance = false;
      boolean validInput = false;
      
      sc.nextLine();
      
      //get airport name
      validInput = false; //reset validInput to false (not strictly necessary, kept for consistency)
      System.out.print("Enter the airport name: "); //prompt
      name = sc.nextLine(); 
      
      //get airport code
      validInput = false; //reset validInput to false
      System.out.print ("Enter the airport code: "); //prompt
      while (!validInput) {
         code = sc.nextLine();
         code = airline.checkNewAirportCode(code); //check if code is valid
        
         if (code != null) { //code is checked to be good
            validInput = true; //exit loop
         }
         else { //code is bad in some part (could use try-catch with error messages when throwing errorxs to identify which part is bad)
            System.out.print(INVALID_INPUT_MSG); //output error message
         }
      } //close while loop
   
      //get the city and country (both can't be invalid)
      System.out.print ("Enter the city in which the airport is located: ");
      city = sc.nextLine();
      System.out.print ("Enter the country in which the airport is located: ");
      country = sc.nextLine();
   
      //get location from user
      location = locationUserInput();
   
      //get runwayCapacity
      validInput = false; //reset validInput
      System.out.print ("Enter the runway capacity: ");
      while (!validInput) {
         try {
            runwayCapacity = sc.nextInt();
            if (runwayCapacity < 0) { //if invalid input, throw error and exit try
               throw new InputMismatchException();
            }
            validInput = true;
         } catch (InputMismatchException ime) { //invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
            sc.nextLine(); //flush
         }
      } //close while loop
   
      //get badWeatherChance
      validInput = false; //reset validInput
      System.out.print ("Enter the chance for bad weather as a decimal: ");
      while (!validInput) {
         try {
            badWeatherChance = sc.nextDouble();
            if (badWeatherChance < 0 || badWeatherChance > 1) { //if invalid input, throw error and exit try
               throw new InputMismatchException();
            }
            validInput = true;
         } catch (InputMismatchException ime) { //invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
            sc.nextLine(); //flush
         }
      } //close while loop
   
      //get goodWeather
      validInput = false; //reset validInput
      System.out.print ("Enter if there is current good weather (t/f): ");
      sc.nextLine(); //flush
      while (!validInput) {
         goodWeatherStr = sc.nextLine();
         if (goodWeatherStr.equals("t") || goodWeatherStr.equals("f")) {
            goodWeather = trueOrFalse(goodWeatherStr);
            validInput = true;
         } else {
            System.out.print (INVALID_INPUT_MSG);
         }
      }
   
      //get if the airport is open
      validInput = false;
      System.out.print ("Enter if the airport is currently open (t/f): ");
      while (!validInput) {
         isOpenStr = sc.nextLine();
         if (isOpenStr.equals("t") || isOpenStr.equals("f")) {
            isOpen = trueOrFalse(isOpenStr);
            validInput = true;
         } else {
            System.out.print (INVALID_INPUT_MSG);
         }
      }
   
      //get if the airport has maintenance facilities
      validInput = false;
      System.out.print ("Enter the airport has maintenance facilities (t/f): ");
      while (!validInput) {
         maintenanceStr = sc.nextLine();
         if (maintenanceStr.equals("t") || maintenanceStr.equals("f")) {
            maintenance = trueOrFalse(maintenanceStr);
            validInput = true;
         } else {
            System.out.print (INVALID_INPUT_MSG);
         }
      }
      
      //add airport with the specified parameters
      airline.addAirport(new Airport (name, code, city, country, location, runwayCapacity, badWeatherChance, goodWeather, isOpen, maintenance, airline));
      System.out.println("Airport added successfully!"); //output success message
   }

   // option 4
   public static void deleteRoute (Airline airline) {
      String routeId = "";
   
      boolean validInput = false;
   
      //get route id
      System.out.print ("Enter the route id: "); //prompt
      sc.nextLine(); 
      while (!validInput) { //loop until a valid input is entered
         routeId= sc.nextLine();
         try {
            routeId = Route.formatRouteId(routeId); //format routeId
            if (routeId == null) { //if routeId is invalid format
               System.out.println("Route ID is not formatted correctly. "); //error message
               throw new InputMismatchException(); //throw error
            }
            if (airline.searchRoutesById(routeId) == null) { //if the route specified does not exist
               System.out.println("The route specified does not exist. "); //error message
               throw new InputMismatchException(); //throw error
            }
            //routeId has passed all the checks
            validInput = true; //routeId is valid; exit the while loop and proceed to the next step
         }
         catch (InputMismatchException iox) {
            System.out.print(INVALID_INPUT_MSG); //error message
         }
      }
      //routeId input is now valid
      airline.deleteRoute(routeId); //call deleteRoute method using the route id
      System.out.println("Route deleted successfully!"); //output success message
   }

   // option 5
   public static void deletePlane (Airline airline) {
      String planeId = "";
      boolean validInput = false;
      
      sc.nextLine();
      
      //get plane id
      validInput = false; //reset validInput to false (not strictly necessary because validInput is initialized to false; kept for consistency)
      System.out.print ("Enter the plane ID: "); //prompt
      while (!validInput) {
         planeId = sc.nextLine();
         planeId = Plane.formatPlaneId(planeId);
         if (airline.searchPlanesById(planeId) != null) { //plane referenced by planeId exists
            validInput = true; //exit loop
         }
         else { //plane referenced by planeId does not exist
            System.out.print(INVALID_INPUT_MSG); //error message; try again
         }
      }
      //planeId is now valid
      airline.deletePlane(planeId); //call deletePlane method using plane id
      System.out.println("Plane deleted successfully!"); //output success message
   }

   // option 6
   public static void deleteAirport (Airline airline) {
      String airportCode = "";
      boolean validInput = false;
   
      //get airport id
      System.out.print ("Enter the airport code: ");
      sc.nextLine();
      while (!validInput) {
         airportCode = sc.nextLine();
         airportCode = Airport.formatAirportCode(airportCode); //checks if the airport code is valid
         if (airportCode != null && airline.searchAirportsByCode(airportCode) != null) { //if valid input
            validInput = true;
         } else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      airline.deleteAirport(airportCode); //calls deleteAirport method using airport code
      System.out.println("Airport deleted successfully!"); //output success message
   }

   // option 7
   public static void closeAirport (Airline airline) {
      String airportCode = "";
      boolean validInput = false;
   
      //get airport id
      System.out.print ("Enter the airport code: ");
      sc.nextLine();
      while (!validInput) {
         airportCode = sc.nextLine();
         airportCode = Airport.formatAirportCode(airportCode); //checks if the airport code is valid
         if (airportCode != null && airline.searchAirportsByCode(airportCode) != null) { //if valid input, exit while loop
            validInput = true;
         } else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
      airline.closeAirport(airportCode); //calls the closeAirport method using airport code
      System.out.println("Airport closed successfully!");
   }

   // option 8
   public static void passTime (Airline airline) {
      int hours = 0;
      boolean validInput = false;
   
      //get number of hours that are being passed in airline
      System.out.print ("Enter the number of hours being passed: ");
      while (!validInput) {
         try {
            hours = sc.nextInt();
            if (hours < 0) { //if invalid input, throw error and exit try
               throw new InputMismatchException();
            }
            validInput = true;
         } catch (InputMismatchException ime) { //invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
            sc.nextLine(); //flush
         }
      } //close while loop
      airline.passTime(hours); //calls passTime method using number of hours entered
      System.out.println("Time passed successfully!");
   }

   // option 9
   public static void sendToEarlyMaintenance (Airline airline) {
      String planeId = "";
      boolean validInput = false;
      
      sc.nextLine();
   
      //get plane id
      validInput = false; //reset validInput (not strictly necessary, validInput is initialized to false, kept for consistency)
      System.out.print ("Enter the plane id: ");
      while (!validInput) {
         planeId = sc.nextLine();
         Plane.formatPlaneId(planeId); //format the id
        
         if (airline.searchPlanesById(planeId) != null) { //plane is in airline
            validInput = true;
         } else {
            System.out.println("Plane does not exist.");
            System.out.print(INVALID_INPUT_MSG); //error message; try again
         }
      }
      
      
      airline.sendToEarlyMaintenance(planeId); //calls sendToEarlyMaintenance method using number of hours entered
      System.out.println("Plane sent on path to maintenance!"); //output error message
   }

   // option 10
   public static void searchPlanesByTimeToMaintenance (Airline airline) {
      int hours = -1;
      boolean validInput = false;
   
      //get number of hours to maintenance
      System.out.print ("Enter the number of hours to maintenance: ");
      while (!validInput) {
         try {
            hours = sc.nextInt();
            if (hours < 0) { //if invalid input
               System.out.println(INVALID_INPUT_MSG);
            } else {
               validInput = true;
            }
         } catch (InputMismatchException ime) { //invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
            sc.nextLine(); //flush
         }
      } //close while loop
   
      airline.searchPlanesByTimeToMaintenance(hours); //calls searchPlanesByTimeToMaintenance method using the number of hours entered
   }

   // option 11
   public static void searchPlanesByAirport (Airline airline) {
      String airportCode = "";
      boolean validInput = false;
      sc.nextLine();
   
      //get airport code
      System.out.print ("Enter the airport code: ");
      while (!validInput) {
         airportCode = sc.nextLine();
         airportCode = Airport.formatAirportCode(airportCode); //checks if the airport code is valid
         if (airportCode != null && airline.searchAirportsByCode(airportCode) != null) { //if valid input, exit while loop
            validInput = true;
         } else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      airline.searchPlanesByAirport(airportCode); //calls searchPlanesByAirport method using airport code
   }

   // option 12
   public static void searchPlanesById (Airline airline) {
      String planeId;
      sc.nextLine();
   
      //get plane id
      System.out.print ("Enter the airplane id: ");
      planeId = sc.nextLine();
      System.out.println (airline.searchPlanesById(planeId)); //calls searchPlanesById using plane id
   }

   // option 13
   public static void searchPlanesByType (Airline airline) {
      String planeType = " ";
      boolean validInput = false;
   
      //get plane type
      System.out.print ("Enter the plane type (S/M/L): ");
      sc.nextLine();
      while (!validInput) {
         planeType = sc.nextLine();
         //checks if the input is valid
         if (planeType.charAt(0) == 'S' || planeType.charAt(0) == 'M' || planeType.charAt(0) == 'L') { //if valid input, continue
            validInput = true;
         } else { //invalid input, output message
            System.out.println (INVALID_INPUT_MSG);
         }
      }
      airline.searchPlanesByType(planeType); //calls searchPlanesByType using plane type
   }

   // option 14
   public static void searchAirportsByCode (Airline airline) {
      String airportCode = "";
      boolean validInput = false;
      sc.nextLine();
   
      //get airport code
      System.out.print ("Enter the airport code: ");
      while (!validInput) {
         airportCode = sc.nextLine();
         airportCode = Airport.formatAirportCode(airportCode); //checks if the airport code is valid
         if (airportCode != null) { //if valid input, exit while loop
            validInput = true;
         } else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      System.out.println (airline.searchAirportsByCode(airportCode)); //calls searchAirportsByCode method using airport code
   }

   // option 15
   public static void searchRoutesById (Airline airline) {
      boolean validInput = false;
   
      String routeId = "";
      sc.nextLine();
   
      //get routeId
      System.out.print ("Enter the route id: ");
      while (!validInput) {
         routeId = sc.nextLine();
         routeId = Route.formatRouteId(routeId); //checks the routeId and formats it
         if (routeId != null) { //if valid input, end while loop
            validInput = true;
         } else { //if invalid input, output error message
            System.out.print (INVALID_INPUT_MSG);
         }
      } //close while loop
   
      System.out.println (airline.searchRoutesById(routeId)); //calls searchRoutesById method using route id
   }

   // option 16
   public static void groundPlanesByType (Airline airline) {
      String planeType;
      boolean validInput = false;
   
      //get plane type
      System.out.print ("Enter the plane type (S/M/L): ");
      sc.nextLine();
      do {
         planeType = sc.nextLine();
         //checks if the input is valid
         if (planeType.charAt(0) == 'S' || planeType.charAt(0) == 'M' || planeType.charAt(0) == 'L') {
            validInput = true;
         } else { //invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
         }
      } while (!validInput);
      airline.groundPlanesByType(planeType);
      System.out.println("Planes grounded successfully!");
   }

   // option 17
   public static void ungroundPlanesByType(Airline airline) {
      String planeType;
      boolean validInput = false;
   
      //get plane type
      System.out.print ("Enter the plane type (S/M/L): ");
      sc.nextLine();
      do{
         planeType = sc.nextLine();
         //checks if the input is valid
         if (planeType.charAt(0) == 'S' || planeType.charAt(0) == 'M' || planeType.charAt(0) == 'L') {
            validInput = true;
         } else {
            System.out.print (INVALID_INPUT_MSG);
         }
      }while (!validInput);
      airline.ungroundPlanesByType(planeType);
      System.out.println("Planes ungrounded successfully!");
   }

   // option 18
   public static void delayFlight (Airline airline) {
      String planeId = "";
      int hours = 0;
      boolean validInput = false;
      sc.nextLine();
   
      //get plane id
      System.out.print ("Enter the plane id: ");
      //check if plane exists
      while (!validInput) {
         planeId = sc.nextLine();
         planeId = Plane.formatPlaneId(planeId);
         if (airline.searchPlanesById(planeId) != null) { //plane referenced by planeId exists
            validInput = true; //exit loop
         }
         else { //plane referenced by planeId does not exist
            System.out.println("Plane does not exist.");
            System.out.print(INVALID_INPUT_MSG); //error message; try again
         }
      } 
      
      //get number of hours that will be delayed
      System.out.print ("Enter the number of hours: ");
      validInput = false;
      while (!validInput) {
         try {
            hours = sc.nextInt();
            if (hours < 0) { //if invalid input, throw error and exit try
               System.out.println("Hours cannot be negative.");
            } else {
               validInput = true;
            }
         } catch (InputMismatchException ime) { //invalid input, output message
            System.out.print (INVALID_INPUT_MSG);
            sc.nextLine(); //flush
         }
      } //close while loop
   
      airline.delayFlight(hours, planeId); //calls delayFlight using plane id and number of hours
      System.out.println("Flight delayed successfully!");
   }



   //get Location from user input
   public static Location locationUserInput() {
      String inputTemp; //used to compare inputs
      String inputLat = "";
      String inputLong = "";
      double inputDecimalDegrees;
   
      int degrees;
      int minutes;
      int seconds;
      double decimalDegrees;
      boolean isNorth;
      boolean isEast;
   
      boolean useDMS = false;
   
      Latitude latitude = null;
      Longitude longitude = null;
      int elevation = 0; //elevation in meters
   
      boolean validInput = false;
   
      final int LAT_STR_LEN = 10;
      final int LONG_STR_LEN = 11;
   
      System.out.println("Enter a location.");
      System.out.print("Would you like to enter latitude and longtitude in DMS notation or decimal degrees? (DMS/Decimal): ");
      while (!validInput){
         inputTemp = sc.nextLine();
         if (inputTemp.equalsIgnoreCase("DMS")) {
            useDMS = true;
            validInput = true;
         }
         else if (inputTemp.equalsIgnoreCase("Decimal")) {
            useDMS = false;
            validInput = true;
         } else {
            System.out.print(INVALID_INPUT_MSG); //error message for invalid input
         }
      }
   
      validInput = false; //reset validInput
      if (useDMS) { //if using DMS or not using DMS
         System.out.println("Enter the latitude. Use the format XX XX XX (N/S). Add leading zeroes if necessary. Case sensitive.");
         while (!validInput) { //loop until a valid input is entered
            try {
               inputLat = sc.nextLine(); //get the latitude
               if (inputLat.length() == LAT_STR_LEN) { //valid input length
                  degrees = Integer.parseInt(inputLat.substring(0, 1)); //get degrees from input string
                  if (degrees > latitude.getMAX_DEGREES()) { //more degrees than is possible with DMS notation
                     throw new InputMismatchException(); //throw error
                  }
                  minutes = Integer.parseInt(inputLat.substring(3, 4)); //get minutes from input string
                  if (minutes >= latitude.getMINUTES_PER_DEGREE()) { //more minutes than the number of minutes per degree
                     throw new InputMismatchException(); //throw error
                  }
                  seconds = Integer.parseInt(inputLat.substring(6, 7)); //get seconds from input string
                  if (seconds >= latitude.getMINUTES_PER_DEGREE()) { //more seconds than the number of seconds per minute
                     throw new InputMismatchException(); //throw error
                  }
                  if (inputLat.charAt(9) == 'N') { //check if the last char is N for North
                     isNorth = true; //set isNorth to true
                  } else if (inputLat.charAt(9) == 'S') { //check if the last char is S for South
                     isNorth = false; //set isNorth to false
                  } else { //last char is not N or S
                     throw new InputMismatchException(); //throw error
                  }
                  latitude = new Latitude(degrees, minutes, seconds, isNorth); //create new Latitude object
                  validInput = true;
               } 
               
               else {
                  System.out.print(INVALID_INPUT_MSG); //invalid input length
               }
            }catch (InputMismatchException ime) {
               System.out.print(INVALID_INPUT_MSG); //invalid input format
            } catch (NumberFormatException nfe) {
               System.out.print(INVALID_INPUT_MSG); //invalid input format
            }
         }
      
         validInput = false; //reset validInput
         System.out.println("Enter the longitude. Use the format XXX XX XX (E/W). Add leading zeroes if necessary. Case sensitive."); //prompt
         while (!validInput) { //loop until a valid input is entered
            try {
               inputLong = sc.nextLine(); //get user input
               if (inputLong.length() == LONG_STR_LEN) { //if input is the right length
                  degrees = Integer.parseInt(inputLong.substring(0, 2)); //get degrees from input string
                  if (degrees > longitude.getMAX_DEGREES()) { //if degrees is larger than the maximum allowed degrees for a longitude obj.
                     throw new InputMismatchException(); //throw error
                  }
                  minutes = Integer.parseInt(inputLong.substring(4, 5)); //get minutes from input string
                  if (minutes >= longitude.getMINUTES_PER_DEGREE()) { //if more minutes than number of minutes per degree
                     throw new InputMismatchException(); //throw error
                  }
                  seconds = Integer.parseInt(inputLong.substring(7, 8)); //get seconds from input string
                  if (seconds >= longitude.getSECONDS_PER_MINUTE()) { //if more seconds than number of seconds per minute
                     throw new InputMismatchException(); //throw error
                  }
                  if (inputLong.charAt(10) == 'E') { //check if the last char is E for East
                     isEast = true; //set isEast to true
                  } else if (inputLong.charAt(10) == 'W') { //check if the last char is W for West
                     isEast = false; //set isEast to false
                  } else { //last char is not E or W
                     throw new InputMismatchException(); //invalid input; throw error
                  }
                  longitude = new Longitude(degrees, minutes, seconds, isEast); //create new Longitude object
                  validInput = true; //valid longitude input
               
               } else {
                  System.out.print(INVALID_INPUT_MSG); //invalid input length
               }
            } catch (InputMismatchException ime) {
               System.out.print(INVALID_INPUT_MSG); //invalid input format
            }
            catch (NumberFormatException nfe) {
               System.out.print(INVALID_INPUT_MSG); //invalid input format; string could not be parsed to int
            }
         } //close while
      } //close if DMS
      
      else { //useDMS = false
         //get latitude
         System.out.println("Enter the latitude as a decimal. Use positive for North and negative for South."); //prompt
         while (!validInput) {
            try {
               inputDecimalDegrees = sc.nextDouble();
               if (Math.abs(inputDecimalDegrees) > latitude.getMAX_DEGREES()) {
                  throw new InputMismatchException(); 
               }
               latitude = new Latitude(inputDecimalDegrees); //create new latitude object
               validInput = true;
            }
            catch (NumberFormatException nfe) {
               System.out.println(INVALID_INPUT_MSG); //could not parse double from String
            }
            catch (InputMismatchException imx) {
               System.out.println(INVALID_INPUT_MSG); //invalid input
               sc.nextLine();
            }
         } //close while
         
         //get longitude
         validInput = false; 
         System.out.println("Enter the longitude as a decimal. Use positive for East and negative for West."); //prompt
         while (!validInput) {
            try {
               inputDecimalDegrees = sc.nextDouble();
               if (Math.abs(inputDecimalDegrees) > longitude.getMAX_DEGREES()) {
                  throw new InputMismatchException(); 
               }
               longitude = new Longitude(inputDecimalDegrees); //create new longitude object
               validInput = true;
            }
            catch (NumberFormatException nfe) {
               System.out.println(INVALID_INPUT_MSG); //could not parse double from String
            }
            catch (InputMismatchException imx) {
               System.out.println(INVALID_INPUT_MSG); //invalid input
               sc.nextLine();
            }
         } //close while
      } //close else
   
      //get elevation
      validInput = false; //reset validInput
      System.out.print("Enter the elevation: ");
      while (!validInput) {
         try {
            elevation = sc.nextInt();
            validInput = true;
         }
         catch (InputMismatchException ime) {
            System.out.print(INVALID_INPUT_MSG);
            sc.nextLine(); //flush
         }
      }
   
      //create and return a new Location object
      return new Location(latitude, longitude, elevation);
   } //end locationUserInput()

   public static boolean trueOrFalse (String varStr) { 
      //assuming that input is valid
      if (varStr.equals("t")) {
         return true; //returns true if the string is equal to t
      } else {
         return false; //returns false if the string is equal to f
      }
   } //close trueOrFalse

   //method for adding filler planes and airports
   public static void addFiller(Airline airline){
        //Scanner sc = new Scanner (System.in);
      int planes = 0, airports = 0;
      boolean validInput = false;
   
      System.out.print("How many filler planes would you like to add: ");
        //check for valid input
      while (!validInput) {
         try {
            planes = sc.nextInt();
         
            if (planes < 0) {
               System.out.println(INVALID_INPUT_MSG);
            } else {
               validInput = true;
            }
         } catch (InputMismatchException imx) {
            System.out.println(INVALID_INPUT_MSG);
            sc.nextLine();
         }
      }
      
      System.out.print("How many filler airports would you like to add: ");
        //reset validInput boolean and check for valid input
      validInput = false;
      while (!validInput) {
         try {
            airports = sc.nextInt();
         
            if (planes < 0) {
               System.out.println(INVALID_INPUT_MSG);
            } else {
               validInput = true;
            }
         
         } catch (InputMismatchException imx) {
            System.out.println(INVALID_INPUT_MSG);
            sc.nextLine();
         }
      }  
          
        //add the airports and planes
      for (int i = 0; i < airports; i ++){
         airline.addFillerAirport();
      }
      for (int i= 0; i < planes; i ++){
         airline.addFillerPlane();
      }
   } //close addFiller

} //close class
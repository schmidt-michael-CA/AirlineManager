/*
	File Name: Latitude.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
   Created: Dec 21, 2019; Christian
   Modified: Dec 21, 2019; Christian
	Description: Latitude object which extends GeographicCoordinate. Represents a latitude in degrees, minutes, seconds, and north or south
*/

//a degree is ascii code 248

public class Latitude extends GeographicCoordinate {
   //instance fields
   private boolean isNorth;
   private static final int MAX_DEGREES = 90;

   //constructor
   public Latitude(int degreesIn, int minutesIn, int secondsIn, boolean isNorthIn) { //takes in DMS notation; assumes valid inputs
      super(degreesIn, minutesIn, secondsIn); //calls the constructor of GeographicCoordinate. Seconds and minutes will be automatically updated to match DMS notation
      isNorth = isNorthIn;
   }
   
   public Latitude(double decimalDegreesIn) {
      super(decimalDegreesIn);
      if (decimalDegreesIn >= 0) { //if decimalDegrees is greater than/equal to zero
         isNorth = true; //isNorth is true
      }
      else { //if decimalDegrees is less than zero
         isNorth = false; //isNorth is false; ie. it is south
      }
   }
   
   //accessor
   public static int getMAX_DEGREES() {
      return MAX_DEGREES;
   }

   //instance methods
   public double getDecimalDegrees() { //returns a double representing the GeographicCoordinate object in decimal degrees
      double output = degrees + minutes / (double) MINUTES_PER_DEGREE + seconds / (double) SECONDS_PER_MINUTE / (double) MINUTES_PER_DEGREE; //converts minutes and seconds to degrees and adds them to the degrees field
      if (!isNorth) {
         output *= -1; //if the latitude is south, makes the decimal degree value negative
      }
      return output;
   }

   //string handling
   public String toString() {
      String output = "Latitude: " + String.format("%02d", degrees) + DEGREE_SIGN + " " + minutes + "' " + seconds + "\" ";
      if (isNorth) { //if latitude is north
         output += "N";
      }
      else { //latitude is south
         output += "S";
      }
      return output;
   }

   public String save() {
      String output;
      output =  degrees + "\n"+ minutes + "\n"+ seconds;
      if (isNorth) {
         output += "\nN";
      }
      else {
         output += "\nS";
      }
      return output;
   }

}
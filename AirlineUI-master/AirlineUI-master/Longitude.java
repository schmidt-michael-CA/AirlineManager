/*
	File Name: Longitude.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
   Created: Dec 21, 2019; Christian
   Modified: Dec 21, 2019; Christian
	Description: Longitude object which extends GeographicCoordinate. Represents a latitude in degrees, minutes, seconds, and east or west
*/

public class Longitude extends GeographicCoordinate {
   //instance fields
   private boolean isEast;
   private static final int MAX_DEGREES = 180;

   //constructor
   public Longitude(int degreesIn, int minutesIn, int secondsIn, boolean isEastIn) { //assumes valid inputs
      super(degreesIn, minutesIn, secondsIn); //calls the constructor of GeographicCoordinate. Seconds and minutes will be automatically updated to match DMS notation
      isEast = isEastIn;
   }
   
   public Longitude(double decimalDegreesIn) {
      super(decimalDegreesIn);
      if (decimalDegreesIn >= 0) { //if decimalDegreesIn is greater than/equal to zero
         isEast = true; //the location is East
      }
      else { //if decimalDegreesIn is less than zero
         isEast = false; //the location is West
      }
   }

   //accessor
   public static int getMAX_DEGREES() {
      return MAX_DEGREES;
   }
   
   //instance methods
   public double getDecimalDegrees() { //returns a double representing the GeographicCoordinate object in decimal degrees
      double output = degrees + minutes / (double) MINUTES_PER_DEGREE + seconds / (double) SECONDS_PER_MINUTE / (double) MINUTES_PER_DEGREE; //converts minutes and seconds to degrees and adds them to the degrees field
      if (!isEast) {
         output *= -1; //if the longitude is west, makes the decimal degree value negative
      }
      return output;
   }

   //string handling
   public String toString() {
      String output = "Longitude: " + String.format("%02d", degrees) + DEGREE_SIGN + " " + minutes + "' " + seconds + "\" ";
      if (isEast) { //if longitude is north
         output += "E";
      }
      else { //longitude is south
         output += "W";
      }
      return output;
   }

   public String save() {
      String output;
      output =  degrees + "\n"+ minutes + "\n"+ seconds;
      if (isEast) {
         output += "\nE";
      }
      else {
         output += "\nW";
      }
      return output;
   }

}
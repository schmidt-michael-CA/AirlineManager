/* 
	File Name: GeographicCoordinate.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
   Created: Dec 21, 2019; Christian
   Modified: Dec 21, 2019; Christian
	Description: GeographicCoordinate object. Holds a geographic coordinate (extended by latitude and longitude) represented in degrees, minutes, and seconds (DMS notation)
*/

public abstract class GeographicCoordinate {
   //instance fields
   protected int degrees;
   protected int minutes; //sexagesimal subdivision of a degree, also known as an arcminute; unit: '
   protected int seconds; //sexagesimal subdivision of a degree, also known as an arcsecond; unit: "
   protected static final int MINUTES_PER_DEGREE = 60; //a degree is divided into 60 minutes; 60 minutes per degree
   protected static final int SECONDS_PER_MINUTE = 60; //a minute is divided into 60 seconds; 60 seconds per minute

   protected static final char DEGREE_SIGN = (char)248; //holds a char of the degree sign, ASCII value 248. Change the ASCII value to change the value of this class constant

   //constructors
   public GeographicCoordinate(int degreesIn, int minutesIn, int secondsIn) { //takes in degrees, minutes, and seconds; inputs should be valid
      //assign values to instance fields
      degrees = degreesIn;
      minutes = minutesIn;
      seconds = secondsIn;
      //update fields to be consistent with DMS notation <--NOT STRICTLY NECESSARY
   //       while (seconds >= SECONDS_PER_MINUTE) { //ensure seconds are correct
   //          minutes++;
   //          seconds -= SECONDS_PER_MINUTE;
   //       }
   //       while (minutes >= MINUTES_PER_DEGREE) {
   //          degrees++;
   //          minutes -= MINUTES_PER_DEGREE;
   //       }
      //at this point seconds and minutes are updated; degrees should be updated in latitude or longitude (which extend this class) based on their specific rules
   }
   
   public GeographicCoordinate(double decimalDegreesIn) {
      double positiveDecimalDegrees = decimalDegreesIn;
      if (positiveDecimalDegrees < 0) {
         positiveDecimalDegrees *= -1;
      }
      degrees = (int) Math.floor(positiveDecimalDegrees); //get integer degrees
      minutes = (int) Math.floor((positiveDecimalDegrees - degrees) * MINUTES_PER_DEGREE); //get minutes
      seconds = (int)Math.floor((double)(positiveDecimalDegrees - degrees - (minutes / (double) MINUTES_PER_DEGREE)) * (SECONDS_PER_MINUTE * MINUTES_PER_DEGREE)); //get seconds
   }
   
   //accessors
   public static int getMINUTES_PER_DEGREE() {
      return MINUTES_PER_DEGREE;
   }
   
   public static int getSECONDS_PER_MINUTE() {
      return SECONDS_PER_MINUTE;
   }
   
   //instance methods
   abstract public double getDecimalDegrees();
//FOR REFERENCE ONLY
//    public double getDecimalDegree() { //returns a double representing the GeographicCoordinate object in decimal degrees
//       return degrees + minutes * (1 / MINUTES_PER_DEGREE) + seconds * (1 / SECONDS_PER_MINUTE) * (1 / MINUTES_PER_DEGREE); //converts minutes and seconds to degrees and adds them to the degrees field
//    }

   public String toString() {
      return degrees + DEGREE_SIGN + " " + minutes + "' " + seconds + "\"";
   }
}
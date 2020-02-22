/*
	File Name: Location.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
	Created: Dec 17, 2019
   Modified: December 22, 2019; Christian
	Description: Location class. Holds coordinates and calculations that use and compare location's coordinates
*/

public class Location {
    //instance fields
    private Latitude latitude;
    private Longitude longitude;
    private int elevation; //elevation in meters above sea level (ASL)
    private static final int MEAN_EARTH_RADIUS = 6371; //mean earth radius in km

    //constructors
    public Location(Latitude latitudeIn, Longitude longitudeIn, int elevationIn) {
        latitude = latitudeIn;
        longitude = longitudeIn;
        elevation = elevationIn;
    }

    //test constructor
    public Location(int latDegreesIn, int latMinutesIn, int latSecondsIn, boolean latIsNorthIn, int longDegreesIn, int longMinutesIn, int longSecondsIn, boolean longIsEastIn, int elevationIn) {
        latitude = new Latitude (latDegreesIn, latMinutesIn, latSecondsIn, latIsNorthIn);
        longitude = new Longitude (longDegreesIn, longMinutesIn, longSecondsIn, longIsEastIn);
        elevation = elevationIn;
    }

    //instance methods
    //phi is latitude, lambda is longitude
    public int distance(Location other) { //returns the distance between two locations, accurate to +/- 1 km. Uses the spherical law of cosines, which assumes that the earth is a perfect sphere. It is accurate for distances greater than a few meters. This formula itself is accurate to approximately +/- 0.1% (by testing) before rounding
        return (int) Math.round(MEAN_EARTH_RADIUS * Math.acos(Math.sin(Math.toRadians(this.latitude.getDecimalDegrees())) * Math.sin(Math.toRadians(other.latitude.getDecimalDegrees())) + Math.cos(Math.toRadians(this.latitude.getDecimalDegrees())) * Math.cos(Math.toRadians(other.latitude.getDecimalDegrees())) * Math.cos(Math.abs(Math.toRadians(this.longitude.getDecimalDegrees() - other.longitude.getDecimalDegrees()))))); //apply the spherical law of cosines and convert the result to an integer
    }

    public boolean inRange(Location other, int range) { //returns if two Locations are within a specified range
        if (this.distance(other) <= range) { //if distance between two points is less than/equal to the given range, return true
            return true;
        }
        else {
            return false; //else (two locations are out of range) return false
        }
    }

    //string handling
    public String toString() {
        return latitude.toString() + "\n" + longitude.toString();
    }

    public String save() {
        return latitude.save() + "\n" + longitude.save() + "\n" + elevation;
    }

}
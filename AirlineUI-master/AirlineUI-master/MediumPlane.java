

/*
    File Name: MediumPlane.java
    Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
    Class: ICS4U
    Created: Dec 19, 2019
    Description: SmallPlane class
*/

import java.util.ArrayList;

public class MediumPlane extends Plane {

    //instance fields
    private static final int MAX_TIME_TO_MAINTENANCE = 100; //maximum time between maintenance; units: hours
    private static final int MAINTENANCE_DURATION = 4; //duration of maintenance; units: hours
    private static final int RANGE = 4000; //range of the aircraft type in km
    private static final int MAX_PASSENGERS = 195; //maximum passenger capacity of the plane in an all economy configuration
    private static final double SPEED = 600; //speed of the aircraft type in km/h
    private static double FAILURE_CHANCE = 0.0001; //chance of random failure (decimal); calculated from mean time between failures (MTBF); current chance is one in one billion

    /**
     * Constructor
     * Operates by calling the base constructor for a plane
     * The result is a normal plane object with constants already determined by the planes subclass
     * @param id identification code for object
     * @param currLocation current airport
     * @param destination destination airport
     * @param timeToMaintenance time before maintenance is needed on this plane
     * @param timeLeftMaintain time left in the current maintenance (if undergoing)
     * @param timeToArrival time before landing at destination
     * @param timeToTakeoff time before leaving current location
     * @param grounded boolean holding whether the plane can fly or not
     * @param airline the airline the plane belongs to (included to help references)
     */
    public MediumPlane(String id, Airport currLocation, Airport destination, int timeToMaintenance, int timeLeftMaintain,
                       int timeToArrival, int timeToTakeoff, boolean grounded, Airline airline){
        super(id, currLocation, destination, timeToMaintenance, timeLeftMaintain, timeToArrival,timeToTakeoff,grounded, airline);
    }


    //These array lists serve to help with the pathfinding functionality
    private ArrayList<ArrayList<Airport>> options = new ArrayList<ArrayList<Airport>>(0);
    private ArrayList<Airport> smallest = new ArrayList<Airport>(0);

    /** sendToMaintenance
     *  this function
     * @param start beginning airport
     * @return an arraylist storing the best possible route to maintenance
     */
    public ArrayList<Airport> sendToMaintenance(Airport start){

        //creates a blank ArrayList which is used to compare initially generated routes to maintenance
        for (int i = 0; i < this.getAirline().airportList.size() + 1; i ++){
            smallest.add(null);
        }

        //This is a base path, starts with nothing
        ArrayList<Airport> path = new ArrayList<Airport>(0);

        //adds the current location to the path
        path.add(start);
        //mark the current location
        start.setMarked(true);

        //CALL WHICH LEADS THE RECURSIVE PROCESS
        sendToSpot(start, path);

        //clear all possibilities once the smallest has been saved
        options.clear();

        //trim the array
        smallest.trimToSize();

        //return the fastest path
        return smallest;

    }

    /** sendToSpot
     * a recursive method that fills the ArrayList "options" with every possible path to a maintenance facility a plane has
     * @param start the last airport in the path
     * @param path1 the path of airports that has been generated so far in the recursive process
     */
    private void sendToSpot(Airport start, ArrayList <Airport> path1){

        //if the plane finds itself at an airport with maintenance it will save the path it took
        //otherwise (so long as a shorter path has not yet been found) it will continue searching through possibilities

        if (start.getHasMaintenance()){

            //if the current path is faster than the previous smallest
            if (path1.size() <= smallest.size()) {
                //remove the smallest
                smallest.clear();

                //create the new smallest path using the current path
                for (int k = 0; k < path1.size(); k++) {
                    smallest.add(path1.get(k));
                }

            }

            //saving this path to the set of options
            options.add(path1);

        } else if (path1.size() < smallest.size()){

            for (int i = 0; i < this.getAirline().airportList.size(); i ++) {

                //if the airport being checks is in range and not yet part of the path
                if (!(this.getAirline().airportList.get(i).getMarked()) && (this.getAirline().airportList.get(i).getLocation().inRange(start.getLocation(), RANGE))) {

                    //add the airport in question to the path and mark it
                    path1.add(this.getAirline().airportList.get(i));
                    this.getAirline().airportList.get(i).setMarked(true);

                    //recursive call running this method from the new location with the new path
                    sendToSpot(this.getAirline().airportList.get(i), path1);

                    //after that recursive call, the airport in question is removed from the path and unmarked
                    this.getAirline().airportList.get(i).setMarked(false);
                    path1.remove(path1.size() - 1);
                    path1.trimToSize();

                }
            }
        }
    }

    /** decideRoute
     * a method that allows a plane to choose a route based on the most urgently needed route at its airport
     * since the plane is mid sized, it makes no priority decision based on route length and instead always chooses the most urgent
     */
    public void decideRoute() {

        if (getCurrLocation().getRouteList().size() > 0){
            //sets the first route in the airports array of departure routes to the smallest found
            Route smallRoute;
            smallRoute = this.getCurrLocation().getRouteList().get(0);

            //loops through the rest of the airports array of departure routes and stores the route with the most urgent need of a flight
            for (int i = 0; i < this.getCurrLocation().getRouteList().size(); i ++){

                if (this.getCurrLocation().getRouteList().get(i).getTimeToNextFlight() < smallRoute.getTimeToNextFlight()){
                    if (this.getCurrLocation().getRouteList().get(i).getDistance() < RANGE){
                        smallRoute = this.getCurrLocation().getRouteList().get(i);
                    }
                }

            }

            //resets the timer on the route
            smallRoute.setTimeToNextFlight(smallRoute.getFrequency());
            //sets the destination of the selected route to the destination of this plane
            this.setDestination(smallRoute.getEnd());

        } else {
            boolean found = false;
            for (int i = 0; i < getAirline().airportList.size() && !found; i++) {
                if (getAirline().airportList.get(i).getLocation().inRange(getCurrLocation().getLocation(), RANGE)) {
                    setDestination(getAirline().airportList.get(i));
                    found = true;
                }
            }
            if (!found) {
                System.out.println(this.getId() + " cannot leave its airport and has been grounded");
                this.setGrounded(true);
            }
        }
        if (getDestination()!= null){
            //adds this object to its airports take off list
            this.setTimeToTakeoff(getCurrLocation().nextTime());
        }
    }


    /** failure
     * this method is run every hour, will destroy the plane based on pure chance (although very slim)
     */
    public void failure(){

        if (Math.random()*Math.random() < FAILURE_CHANCE){
            //calls on the delete method in this object, effectively removing it from the program
            System.out.println("This plane has crashed");
            this.delete();
        }

    }


    /** resetMaintenance
     * simply allows for maintenance timers to run again from their timer values
     */
    public void resetMaintenance(){
        this.setTimeToRepair(MAX_TIME_TO_MAINTENANCE);
        this.setTimeLeftMaintain(MAINTENANCE_DURATION);

    }



    /** divertPlane
     * if a plane cannot land at an airport this method will run
     * it will change the plans destination to another airport that is open
     * or, if none are in range, it will crash. Tragic
     */
    public void divertPlane() {

        boolean found = false;
        setCurrLocation(getDestination());
        //loops through all airports
        for (int i = 0; i < getAirline().airportList.size() && !found; i ++){

            //determines if they are in range
            if (getCurrLocation().getLocation().inRange(getAirline().airportList.get(i).getLocation(), RANGE)){
                //determines if they are available for landing
                if (getAirline().airportList.get(i).getIsOpen()){
                    //changes its own destination to this new open and in range airport
                    setDestination((getAirline().airportList.get(i)));
                    //determines how long it will take to arrive
                    this.setTimeToArrival((int)(this.getDestination().getLocation().distance(this.getCurrLocation().getLocation())/SPEED + 1));
                    //sets found to true allowing for an exit of the loop
                    found = true;

                }
            }
        }
        //unfortunately, if the plane cannot find anywhere to land, it will crash
        if (!found){
            this.delete();
        }

    }

    /** takeOff
     * method that transfers a plane from the ground to the air, travelling to its destination
     */
    public void takeOff(){

        //removes this plane from the list in its airport that holds it
        this.getCurrLocation().getPlaneList().remove(this);
        //determine how long it will take this plane to arrive at its destination based on its speed and the distance
        this.setTimeToArrival((int)(this.getDestination().getLocation().distance(this.getCurrLocation().getLocation())/SPEED + 1));
        //register this plane is no longer at an airport
        this.setCurrLocation(null);

    }

    /** getPlaneType
     * returns this type of plane
     */
    public String getPlaneType(){
        return "Medium Plane";
    }
    
    public static int getMAINTENANCE_DURATION() {
        return MAINTENANCE_DURATION;
    }
}
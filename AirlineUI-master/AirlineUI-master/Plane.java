
/*
	File Name: Plane.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
	Created: Dec 17, 2019
	Description: Plane class
 */

import java.util.ArrayList;

public abstract class Plane {

   private String id; //a randomly generated string that represents this object
   private Airport currLocation, destination; //the airport the plane resides in as well as the one it it travelling to
   private int timeToMaintenance; //the time left in which the plane can operate before heading to a maintenance facility
   private int timeLeftMaintain; //time left in the maintenance process (if currently undergoing)
   private int timeToArrival; //time before landing at destination airport
   private int timeToTakeoff; //time before leaving current airport
   private boolean grounded; //a boolean that holds whether a certain plane is instructed to stay on the ground
   private Airline airline; //a pointer to the airline that the plane is part of


   /**
    * Constructor
    * called through the subclasses of plane
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
   public Plane(String id, Airport currLocation, Airport destination, int timeToMaintenance, int timeLeftMaintain,
                int timeToArrival, int timeToTakeoff, boolean grounded, Airline airline) {
   
      this.id = id;
      this.currLocation = currLocation;
      this.destination = destination;
      this.timeToMaintenance = timeToMaintenance;
      this.timeLeftMaintain = timeLeftMaintain;
      this.timeToArrival = timeToArrival;
      this.timeToTakeoff = timeToTakeoff;
      this.grounded = grounded;
      this.airline = airline;
   
   }

   //ACCESSORS
   public String getId() {
      return id;
   }

   public Airport getCurrLocation() {
      return currLocation;
   }

   public Airport getDestination() {
      return destination;
   }

   public int getTimeToMaintenance() {
      return timeToMaintenance;
   }

   public int getTimeLeftMaintain() {
      return timeLeftMaintain;
   }

   public int getTimeToArrival() {
      return timeToArrival;
   }

   public int getTimeToTakeoff() {
      return timeToTakeoff;
   }

   public Airline getAirline(){
      return airline;
   }

   public boolean getGrounded() {
      return grounded;
   }

   //MUTATORS
   public void setTimeToRepair(int i){
      this.timeToMaintenance = i;
   }

   public void setCurrLocation(Airport i){
      this.currLocation = i;
   }

   public void setTimeToArrival(int i){
      this.timeToArrival = i;
   }

   public void setTimeLeftMaintain(int i){
      this.timeLeftMaintain = i;
   }

   public void setDestination(Airport airport){
      destination = airport;
   }

   public void setTimeToTakeoff(int i){
      timeToTakeoff = i;
   }

   public void setGrounded(boolean bool){
      grounded = bool;
   }

   //abstract methods
   //The following methods are defined in the subclasses of plane
   //For the most part, these methods are very similar (or even exactly the same in each subclass)
   //However, having them separate allows one to use subclass constants
   //Not only that, but it allows for further future differentiation of subclasses
   //As well as easier construction for front end users with constants already defined by the subclass
   public abstract void decideRoute();
   public abstract void failure();
   public abstract ArrayList sendToMaintenance(Airport airport);
   public abstract void resetMaintenance();
   public abstract void takeOff();
   public abstract void divertPlane();
   public abstract String getPlaneType();


   /** performAction
    * This method is run every hour
    * It contains the logic that determines the behavior of the plane that hour
    */
   public void performAction() {
   
      //decrement time to maintenance
      timeToMaintenance--;

      //determine if the plane encounters a random failure
      //failure();
      //CURRENTLY COMMENTED OUT DUE TO THE DISRUPTIONS IN TESTING IT CAUSES

      //enters this part of the if structure if the plane is in the air
      if (currLocation == null) {
      
         //enters this structure if the plane is due to land at the destination airport
         if (timeToArrival == 0) {
            if (destination != null){
               if (destination.getIsOpen() && destination.getGoodWeather()) {
                  //land plane
                  land();
                  System.out.println(this.id + " landed.");
               } else {
                  //attempts to transfer its landing to another airport
                  divertPlane();
                  System.out.println(this.id + " diverted to " + destination.getCode());
               }
            }
         } else {
            //continue on the flight path
            timeToArrival--;
            System.out.println(this.id + " flew towards " + destination.getCode());
         }
      
      } else if (grounded) {
      
         //the plane will do nothing if it is on the ground and set to remain grounded
         System.out.println(this.id + " is grounded at " + currLocation.getCode());
      
      } else if (timeToMaintenance <=0) {
      
         //when due for maintenance, the plane will head to the nearest airport with maintenance capabilities
         //once arriving it will be maintained
         //the plane will destroy itself should it fail to reach maintenance on time
         //this if structure will also automatically destroy the plane should there be no path available
         if (currLocation.getHasMaintenance()) {

            maintain();
            System.out.println(this.id + " went under maintenance.");

         } else if (timeToMaintenance < -50) {

            System.out.println(this.id + " could not reach maintenance in time");
            delete();

         } else {
            if (sendToMaintenance(currLocation) == null){
               delete();

            } else {
               if (sendToMaintenance(currLocation).get(1) != null){
                  setDestination((Airport)(sendToMaintenance(currLocation).get(1)));
                  takeOff();
                  System.out.println(this.id + " took off for maintenance." + destination.getCity());
               } else {
                  System.out.println(this.id + " could not reach maintenance.");
                  delete();
               }
            }


         }
      
      } else if (destination == null) {
      
         //if plane does not yet have a destination
         decideRoute();
         System.out.println(this.id + " chose a route.");
      
      } else if (timeToTakeoff == 0) {
      
         //if plane should be taking off
         takeOff();
         System.out.println(this.id + " took off.");
      
      
      } else {
      
         //count down to takeoff
         timeToTakeoff--;
         System.out.println(this.id + " sat on tarmac at " + currLocation.getCode());
      
      }
   
   }

   /** maintain
    * this method simulates the timing of a plane undergoing maintenance
    * it counts down hours while the plane is in maintenance
    * when the maintenance is complete, it resets the timer so it may be used again
    */
   public void maintain() {
   
      //decrement hours left
      timeLeftMaintain--;
   
   
      if (timeLeftMaintain <= 0) {
         //if maintenance has been completed, reset counter
         resetMaintenance();
      }
   
   }

   /** land
    *  simulates a plane arriving on an airport runway
    */
   public void land() {
      //adds itself to the plane list
      destination.getPlaneList().add(this);
      //sets this planes current airport to point to the one it just landed at
      this.currLocation = destination;
      //removes its destination as it has arrived
      this.destination = null;
   }

   //formats a planeId, turns all lowercase to uppercase
   public static String formatPlaneId(String planeId) {
      planeId = planeId.toUpperCase();
      return planeId.toUpperCase();
   }

   /**
    * This method returns a string representative of the plane object, designed to be read by a user
    * it is different from the method used in file saving
    * @return
    */
   public String toString(){
      String output;
      output = id +"\n";
      try{
         output += "Going from: " + currLocation.getCity() + " to " + destination.getCity() + "\n";
      } catch (NullPointerException npx){
         try {
            output += "Currently in: " + currLocation.getCity() + "\n";
         } catch (NullPointerException npx2){
            output += "Destination of: " + destination.getCity() + "\n";
         }
      }
      output += "Time before needing maintenance: " + timeToMaintenance + "\n";
      output += "Time left in the maintenance process: " + timeLeftMaintain +"\n";
      output += "Time until arrival: " + timeToArrival + "\n";
      output += "Time until takeoff: " + timeToTakeoff + "\n";
      output += "Grounded status: " + grounded + "\n";
      output += "Airline is: " + airline.getName() + "\n";
      return output;
   }

   /**
    * removes all pointers to this object and leaving it for garbage collection
    */
   public void delete() {
      if (this.getCurrLocation() != null){
         this.getCurrLocation().getPlaneList().remove(this);
      }
      airline.planeList.remove(this);
   }

   /**
    * Returns a string representative of how the object should be written in saving
    * It is used in the saving process as the returned String is added to a text file
    * The method also includes null checking as to avoid errors
    * @return
    */
   public String save() {
      String output = "";
      output += this.id +"\n";
      output += this.getPlaneType() +"\n";
      if (currLocation != null){
         output += this.currLocation.getCode() +"\n";
      } else {
         output += "\n";
      }
      if (destination != null){
         output += this.destination.getCode() +"\n";
      } else {
         output += "\n";
      }
      output += this.timeToMaintenance +"\n";
      output += this.timeToArrival +"\n";
      output += this.timeToTakeoff +"\n";
      output += this.grounded;
      return output;
   }
}
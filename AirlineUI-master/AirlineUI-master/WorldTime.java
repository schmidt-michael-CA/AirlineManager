/*
	File Name: WorldTime.java
	Names: Amy Yao, Christian Wang, Tong Yin Han, Michael Schmidt
	Class: ICS4U
	Created: Dec 19, 2019; Christian
	Modified: Jan 8, 2020; Christian
	Description: WorldTime object; holds time in the form of hour, day, month, and year; used for keeping track of time in the overall program; uses 24-hour time; does NOT take into account leap years
 */

public class WorldTime {
    //instance fields
    private int hour;
    private int day;
    private int month;
    private int year;
    private static final int HOURSPERDAY = 24;
    private static final int[] DAYSPERMONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; //array of days in each month; index 0 is January, index 11 is December
    private static final String[] MONTHNAMES = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}; //array of the names of the 12 months; index 0 is January, index 11 is December
    private static final int MONTHSPERYEAR = 12;

    //constructor
    WorldTime(int hourIn, int dayIn, int monthIn, int yearIn) {
        hour = hourIn;
        day = dayIn;
        month = monthIn;
        year = yearIn;
        this.updateTime(0); //ensures all fields are correctly formatted
    }

    //instance methods
    public boolean updateTime(int hoursElapsed) {
        if (hoursElapsed >= 0) { //valid hoursElapsed input; if input = 0, then updates the format of WorldTime
            hour += hoursElapsed; //updates the hour counter
            while (hour >= HOURSPERDAY) { //if hour variable is greater than the number of hours in a day, go to the next day and subtract the number of hours in a day. Repeat this until the hour variable is less than/equal to the number of hours in a day
                hour -= HOURSPERDAY;
                day++;
            }
            while (day > DAYSPERMONTH[month - 1]) { //if day variable is greater than the number of days in a month, go to the next month and subtract the number of days in a month. Repeat this until the day variable is less than/equal to the number of days in the appropriate month
                day -= DAYSPERMONTH[month - 1];
                month++;
            }
            while (month > MONTHSPERYEAR) { //if month variable is greater than the number of months in a year, go to the next year and subtract the number of months in a year. Repeat this until the month variable is less than/equal to the number of months in a year
                month -= MONTHSPERYEAR;
                year++;
            }
            //WorldTime has been correctly updated
            return true; //successful update
        }
        else { //invalid hoursElapsed input; ie. negative time
            return false; //unsuccessful update
        }
    }

    //string handling
    public String toString() {
        this.updateTime(0); //ensures that the WorldTime fields are properly formatted
        String output;
        output = "Time: "; //time header
        //add time to output string
        if (hour < 10) { //if the hour value is a single digit, ie. 0-9
            output += "0"; //adds a leading zero
            output += hour; //adds hour
        }
        else {
            output += hour; //adds the hour value
        }
        output += "00"; //adds minutes to output (minutes are always zero because not tracked in WorldTime)
        //add date to output string on another line
        output += "\nDate: "; //starts new line and adds date header
        output += MONTHNAMES[month - 1] + " " + day + ", " + year; //adds the date, in the format "-monthName- -day-, -year-"
        //return output string
        return output;
      /*
      Output format:
      Time: XX:XX <-- using 24hr time
      Date: NAMEOFMONTH DAY, YEAR
      */
    }

    public String saveWorldTime() {
        return hour + "\n" + day + "\n" + month + "\n" + year;
    }
}
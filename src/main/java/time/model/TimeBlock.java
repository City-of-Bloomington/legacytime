package time.model;
import java.io.Serializable;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Hashtable;
import java.sql.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.list.*;
import time.utils.*;

public class TimeBlock implements Serializable{

    private String id="", // = id in users table
	date="", earnCode="", clockIn="", // old user or admin
	clockOut="", hours="";
    String error = "";
    //
    static final long serialVersionUID = 59L;
    static Logger logger = LogManager.getLogger(TimeBlock.class);
	
    public TimeBlock(){
    }
    public TimeBlock(String var, 
		     String var2,
		     String var3,
		     String var4,
		     String var5,
		     String var6
		     ){
	setId(var);
	setDate(var2);
	setClockIn(var3);
	setClockOut(var4);
	setHours(var5);
	setEarnCode(var6);								
    }
		

    // getters
    //
    public String getId(){
	return id;
    }	
    public String getEarnCode(){
	return earnCode;
    }
    public String getDate(){
	return date;
    }	
    public String getHours(){
	return hours;
    }			
    public String getClockIn(){
	return clockIn;
    }
    public String getClockOut(){
	return clockOut;
    }

    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setEarnCode(String val){
	if(val != null)
	    earnCode = val;
    }
    public void setDate(String val){
	if(val != null)
	    date = val;
    }
    public void setClockIn(String val){
	if(val != null)
	    clockIn = val;
    }
    public void setClockOut (String val){
	if(val != null)
	    clockOut = val;
    }
    public void setHours(String val){
	if(val != null)		
	    hours = val;
    }

    public String toString(){
	String ret = id+", "+earnCode+", "+date+", "+clockIn+" - "+clockOut+", "+hours;
	return ret;
    }
    @Override
    public int hashCode() {
	int hash = 3;
	hash = 53 * toString().hashCode();
	return hash;
    }
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (!User.class.isAssignableFrom(obj.getClass())) {
	    return false;
	}
	final User other = (User) obj;
	if (!this.toString().equals(obj.toString())) {
	    return false;
	}
	return true;
    }		

}

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

public class User implements Serializable{

    private String id="", // = id in users table
	username="", fullName="", role="", // old user or admin
	dept="",inactive="";
    String error = "";
    //
    static final long serialVersionUID = 59L;
    static Logger logger = LogManager.getLogger(User.class);
	
    private Set<String> deptSet = new HashSet<String>();
    public User(){
    }
    public User(String val){
	setUsername(val);
    }	
    public User(String var, 
		String var2,
		String var3,
		String var4,
		String var5
		){
	setId(var);
	setUsername(var2);
	setFullName(var3);
	setRole(var4);				
	setDept(var5);
    }
		

    // getters
    //
    public String getId(){
	return id;
    }	
    public String getUsername(){
	return username;
    }
    public String getFullName(){
	return fullName;
    }
    public String getDept(){
	return dept;
    }
    public String getRole(){
	return role;
    }
    public boolean hasRole(String val){
	return role.indexOf(val) > -1; 
    }
    public boolean canEdit(){
	return hasRole("Edit");
    }		
    public boolean canDelete(){
	return hasRole("Delete");
    }
    public boolean isAdmin(){
	return hasRole("Admin");
    }
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setUsername (String val){
	if(val != null)
	    username = val;
    }
    public void setFullName (String val){
	if(val != null)
	    fullName = val;
    }
    public void setDept(String val){
	if(val != null)
	    dept = val;
    }
    public void setRole (String val){
	if(val != null)		
	    role = val;
    }
    public boolean isUserFound(){
	return !username.isEmpty() && !fullName.isEmpty();
    }
    public String toString(){
	String ret = getFullName();
	if(ret.equals("")) ret = username;
	return ret;
    }
    @Override
    public int hashCode() {
	int hash = 3;
	hash = 53 * hash + (username != null && !username.equals("")? this.username.hashCode() : 0);
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
	if ((this.username.isEmpty()) ? (other.getUsername().isEmpty()) : !this.username.equals(other.getUsername())) {
	    return false;
	}
	return true;
    }		
    /**
     * for login purpose
     */
    public String doSelect(){
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String msg="", str="";
	String qq = " select * from users where ";
	if(!id.equals("")){
	    qq += " id = ? ";
	}
	else if(!username.equals("")){
	    qq += " username = ? ";
	}
	else{
	    msg = " user id or username not set ";
	    return msg;
	}
	logger.debug(qq);
	try{
	    con = Helper.getConnectionKuali();				
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		if(!id.equals("")){
		    pstmt.setString(1, id);
		}
		else if(!username.equals("")){
		    pstmt.setString(1, username);
		}
		rs = pstmt.executeQuery();
		//
		if(rs.next()){
		    str = rs.getString(1);
		    id = str;
		    str = rs.getString(2);
		    setUsername(str);
		    str = rs.getString(3);
		    setFullName(str);
		    str = rs.getString(4);
		    setRole(str);
		    str = rs.getString(5);
		    setDept(str);
		}
	    }
	}
	catch(Exception ex){
	    msg += " "+ex;
	    logger.error(msg+":"+qq);
	    error += msg;
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return msg;
    }
    public String doSave(){
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String msg="", str="";
	String qq = " insert into users values(0,?,?,?,?,null)";
	if(username.equals("") || fullName.equals("")){
	    msg = "username or fullname not set ";
	    return msg;
	}
	logger.debug(qq);
	try{
	    con = Helper.getConnectionKuali();				
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, username);
		pstmt.setString(2, fullName);
		if(role.equals(""))
		    pstmt.setNull(3,Types.INTEGER);
		else
		    pstmt.setString(3, role);
		if(dept.isEmpty())
		    pstmt.setNull(4,Types.INTEGER);
		else
		    pstmt.setString(4, dept);								
		pstmt.executeUpdate();
		qq = "select LAST_INSERT_ID() ";
		logger.debug(qq);
		pstmt = con.prepareStatement(qq);				
		rs = pstmt.executeQuery();
		if(rs.next()){
		    id = rs.getString(1);
		}
	    }
	}
	catch(Exception ex){
	    msg += " "+ex;
	    logger.error(msg+":"+qq);
	    error += msg;
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return msg;
    }
    public String doUpdate(){
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String msg="", str="";
	String qq = " update users set username=?,fullname=?,role=?,dept=? where id=?";
	if(username.equals("") || fullName.equals("")){
	    msg = "username or first name not set ";
	    return msg;
	}
	logger.debug(qq);
	try{
	    con = Helper.getConnectionKuali();				
	    if(con != null){
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, username);
		pstmt.setString(2, fullName);
		if(role.equals(""))
		    pstmt.setNull(3,Types.INTEGER);
		else
		    pstmt.setString(3, role);
		if(dept.equals(""))
		    pstmt.setNull(4,Types.INTEGER);
		else
		    pstmt.setString(4, dept);								
		pstmt.setString(5, id);
		pstmt.executeUpdate();
	    }
	}
	catch(Exception ex){
	    msg += " "+ex;
	    logger.error(msg+":"+qq);
	    error += msg;
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return msg;
    }

}

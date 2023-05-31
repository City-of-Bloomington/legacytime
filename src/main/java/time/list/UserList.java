package time.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.model.*;
import time.utils.*;



public class UserList{

    String username = "", role="";
    boolean activeOnly = false;
    static final long serialVersionUID = 51L;
    static Logger logger = LogManager.getLogger(UserList.class);
    List<User> users = null;
    //
    public UserList(){

    }
    //
    // setters
    //
    //
    public void setUsername(String val){
	if(val != null)
	    username = val;
    }
    public List<User> getUsers(){
	return users;
    }
    //
    public String find(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	//
	// users table is in kpme DB
	//
	String qq = "select u.id,u.username,u.fullname,u.role,u.dept "+ 
	    " from users u ";
	String qw = "";

	if(!username.equals("")){
	    if(!qw.equals("")) qw += " and ";
	    qw += " u.username = ? ";
	}
	if(!qw.equals(""))
	    qq += " where "+qw;
	qq += " order by u.fullname ";
	String back = "";
	try{
	    logger.debug(qq);
	    con = Helper.getConnectionKuali();				
	    if(con == null){
		back = "Could not connect to DB ";
		return back;
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(!username.equals("")){
		pstmt.setString(jj,username);
	    }
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		String str  = rs.getString(1);
		String str2 = rs.getString(2);
		String str3 = rs.getString(3);
		String str4 = rs.getString(4);
		String str5 = rs.getString(5);
		User user = new User(str, str2, str3, str4, str5);
		if(users == null)
		    users = new ArrayList<>();
		users.add(user);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(pstmt, rs);
	}
	return back;
    }
}























































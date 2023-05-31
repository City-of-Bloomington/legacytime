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

public class Employee implements Serializable{
    private String id="", // principal_id="", // = id in users table
	employee_number="",
	username="", fullName="", // old user or admin
	dept="";
    // needed for search for employee given id
    //
    private String clocker_id="", kuali_id=""; 
    //
    static final long serialVersionUID = 59L;
    static Logger logger = LogManager.getLogger(User.class);
	
    private Set<String> deptSet = new HashSet<String>();
    public Employee(){}
    public Employee(
		    String val,
		    String val2,
		    String val3,
		    String val4,				
		    String val5
		    ){
	setId(val);		
	setUsername(val2);
	setEmployee_number(val3);
	setDept(val4);
	setFullName(val5);
    }	
 
    //
    // getters
    //
    public String getId(){
	return id;
    }	
    public String getUsername(){
	return username;
    }
    public String getEmployee_number(){
	return employee_number;
    }
    public String getFullName(){
	return fullName;
    }
    public String getDept(){
	return dept;
    }	
    //
    // setters
    //
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setEmployee_number(String val){
	if(val != null)
	    employee_number = val;
    }	
    public void setUsername (String val){
	if(val != null)
	    username = val;
    }
    public void setFullName (String val){
	if(val != null)
	    fullName = val;
    }
    public void setDept (String val){
	if(val != null)
	    dept = val;
    }
    public void setClockerEmployeeId(String val){
	if(val != null)
	    clocker_id = val;
    }
    public void setKualiEmployeeId(String val){
	if(val != null)
	    kuali_id = val;
    }
    public String toString(){
	String ret = getFullName();
	if(ret.isEmpty()) ret = username;
	return ret;
    }
    @Override
    public int hashCode() {
	int hash = 3;
	if(!username.isEmpty())
	    hash = 53 * hash + username.hashCode();
	else
	    hash = 53 * hash + getFullName().hashCode();
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
	final Employee other = (Employee) obj;
	if ((this.username.isEmpty()) ? (other.getUsername().isEmpty()) : !this.username.equals(other.getUsername())) {
	    return false;
	}
	return true;
    }
    public String doSelect(){
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String msg="", back="";
	String qq = "select p.prncpl_id,"+
	    "p.prncpl_nm,"+
	    "e.emp_id,"+ // employee_number
	    "e.prmry_dept_cd, "+
	    "concat_ws(' ',t.first_nm,t.last_nm) "+
	    "from krtt.krim_prncpl_t p, "+
	    "krtt.krim_entity_emp_info_t e,"+
	    "krtt.krim_entity_nm_t t "+
	    "where e.entity_id=t.entity_id "+
	    "and p.entity_id=e.entity_id "+
	    "and t.dflt_ind='Y' and p.prncpl_id=?";
				
	String qq2 = "select id,empid,null,null,fullname from users where id=? ";
	try{
	    if(!kuali_id.isEmpty()){
		System.err.println(qq);
		con = Helper.getConnectionKuali();	
		if(con == null){
		    back += "Could not connect to DB ";
		    System.err.println(back);
		    return back;
		}
		pstmt = con.prepareStatement(qq);
		int jj=1;
		pstmt.setString(jj++, kuali_id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    setId(rs.getString(1));
		    setUsername(rs.getString(2)); // username
		    setEmployee_number(rs.getString(3)); // emp num 
		    setDept(rs.getString(4)); // dept_code
		    setFullName(rs.getString(5)); // full name
		}
	    }
	    else if(!clocker_id.isEmpty()){
		logger.debug(qq2);
		System.err.println(qq2);
		con = Helper.getConnectionClocker();	
		if(con == null){
		    back += "Could not connect to DB ";
		    System.err.println(back);
		    return back;
		}
		pstmt = con.prepareStatement(qq2);
		int jj=1;
		pstmt.setString(jj++, clocker_id);
		rs = pstmt.executeQuery();
		if(rs.next()){
		    setId(rs.getString(1));
		    setUsername(rs.getString(2)); // username
		    setEmployee_number(rs.getString(3)); // emp num 
		    setDept(rs.getString(4)); // dept_code
		    setFullName(rs.getString(5)); // full name
		}
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(ex+":"+qq);
	    System.err.println(back);
	}
	finally{
	    Helper.databaseDisconnect(con, pstmt, rs);
	}
	return back;						
				

    }
    /**
     * user as employee
     */
    /**		
		public String selectEmp(){
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String msg="", str="";
		String qq = "select p.prncpl_id,p.prncpl_nm,e.emp_id,e.prmry_dept_cd, "+
		"t.first_nm,t.last_nm "+
		"from krtt.krim_prncpl_t p, krtt.krim_entity_emp_info_t e,krtt.krim_pnd_nm_mt t where e.entity_nm_id=t.entity_id and p.entity_id=e.entity_id and t.dflt_ind='Y' ";
		if(!principal_id.equals("")){
		qq += " and p.prncpl_id = ?";
		}
		else{
		qq += " and p.prncpl_nm = ? ";
		}
		if(debug)
		logger.debug(qq);
		try{
		con = SingleConnect.getConnection();				
		if(con != null){
		pstmt = con.prepareStatement(qq);
		if(!principal_id.equals(""))
		pstmt.setString(1, principal_id);
		else
		pstmt.setString(1, username);					
		rs = pstmt.executeQuery();
		//
		if(rs.next()){
		str = rs.getString(1);
		if(str != null){
		principal_id = str;
		str = rs.getString(2);
		setUsername(str);
		str = rs.getString(3);
		setEmp_num(str);
		str = rs.getString(4);
		setDept(str);
		str = rs.getString(5);
		setFname(str);
		str = rs.getString(6);
		setLname(str);
		userExists = true;
		}
		}
		}
		}
		catch(Exception ex){
		msg += " "+ex;
		logger.error(msg+":"+qq);
		addError(msg);
		}
		finally{
		Helper.databaseDisconnect(pstmt, rs);
		}
		if(!userExists){
		msg += " No match found ";
		}
		return msg;
		}
    */

}

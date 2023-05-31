package time.utils;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.model.*;
import time.list.*;

public class Helper{

    static int c_con = 0;
    final static String bgcolor = "silver";// #bfbfbf gray
    final static String fgcolor = "navy";// for titles
    //
    static Logger logger = LogManager.getLogger(Helper.class);

    //
    // basic constructor
    public Helper(boolean deb){
	//
	// initialize
	//
    }
    final static String bytesToHex(byte in[]) {
	byte ch = 0x00;
	int i = 0; 
	if (in == null || in.length <= 0)
	    return null;
	String pseudo[] = {"0", "1", "2",
	    "3", "4", "5", "6", "7", "8",
	    "9", "A", "B", "C", "D", "E",
	    "F"};
	StringBuffer out = new StringBuffer(in.length * 2);
	while (i < in.length) {
	    ch = (byte) (in[i] & 0xF0); // Strip off high nibble
		
	    ch = (byte) (ch >>> 4);
	    // shift the bits down
	    
	    ch = (byte) (ch & 0x0F);    
	    // must do this is high order bit is on!

	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
	    out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
	    i++;
	}
	String rslt = new String(out);
	return rslt;
    }
    /**
     * Adds escape character before certain characters
     *
     */
    final static String escapeIt(String s) {
		
	StringBuffer safe = new StringBuffer(s);
	int len = s.length();
	int c = 0;
	boolean noEscapeBefore = true;
	while (c < len) {                           
	    if ((safe.charAt(c) == '\'' ||
		 safe.charAt(c) == '"') && noEscapeBefore){
		safe.insert(c, '\\');
		c += 2;
		len = safe.length();
		noEscapeBefore = true;
	    }
	    else if(safe.charAt(c) == '\\'){ // to avoid double \\ before '
		noEscapeBefore = false;
		c++;
	    }
	    else {
		noEscapeBefore = true;
		c++;
	    }
	}
	return safe.toString();
    }
    //
    // users are used to enter comma in numbers such as xx,xxx.xx
    // as we can not save this in the DB as a valid number
    // so we remove it 
    public final static String cleanNumber(String s) {

	if(s == null) return null;
	String ret = "";
	int len = s.length();
	int c = 0;
	int ind = s.indexOf(",");
	if(ind > -1){
	    ret = s.substring(0,ind);
	    if(ind < len)
		ret += s.substring(ind+1);
	}
	else
	    ret = s;
	return ret;
    }
    /**
     * replaces the special chars that has certain meaning in html
     *
     * @param s the passing string
     * @returns string the modified string
     */
    public final static String replaceSpecialChars(String s) {
	char ch[] ={'\'','\"','>','<'};
	String entity[] = {"&#39;","&#34;","&gt;","&lt;"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    public final static String replaceQuote(String s) {
	char ch[] ={'\''};
	String entity[] = {"_"};
	//
	// &#34; = &quot;

	String ret ="";
	int len = s.length();
	int c = 0;
	boolean in = false;
	while (c < len) {             
	    for(int i=0;i< entity.length;i++){
		if (s.charAt(c) == ch[i]) {
		    ret+= entity[i];
		    in = true;
		}
	    }
	    if(!in) ret += s.charAt(c);
	    in = false;
	    c ++;
	}
	return ret;
    }
    /**
     *
     */
    public final static Connection getConnectionKuali(){
	Connection con = null;
	int trials = 0;
	boolean pass = false;
	while(trials < 3 && !pass){
	    try{
		trials++;
		logger.debug("Connection try "+trials);
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/MySQL_kpme");
		con = ds.getConnection();
		if(con == null){
		    String str = " Could not connect to DB ";
		    logger.error(str);
		}
		else{
		    pass = testCon(con);
		    if(pass){
			c_con++;
			logger.debug("Got connection: "+c_con);
			logger.debug("Got connection at try "+trials);
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}
	return con;
    }
    /**
     * to get info from quartz DB
     */
    public final static Connection getConnectionClocker(){
	Connection con = null;
	int trials = 0;
	boolean pass = false;
	while(trials < 3 && !pass){
	    try{
		trials++;
		logger.debug("Connection try "+trials);
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/MySQL_clocker");
		con = ds.getConnection();
		if(con == null){
		    String str = " Could not connect to DB ";
		    logger.error(str);
		}
		else{
		    pass = testCon(con);
		    if(pass){
			c_con++;
			logger.debug("Got connection: "+c_con);
			logger.debug("Got connection at try "+trials);
		    }
		}
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}
	return con;
    }	
	
    final static boolean testCon(Connection con){
	boolean pass = false;
	Statement stmt  = null;
	ResultSet rs = null;
	String qq = "select 1+1";		
	try{
	    if(con != null){
		stmt = con.createStatement();
		logger.debug(qq);
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    pass = true;
		}
	    }
	    rs.close();
	    stmt.close();
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	return pass;
    }
    /**
     * Disconnect the database and related statements and result sets
     * 
     * @param con
     * @param stmt
     * @param rs
     */
    public final static void databaseDisconnect(Connection con,
						Statement stmt,
						ResultSet rs) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null) stmt.close();
	    stmt = null;
	    if(con != null) con.close();
	    con = null;
			
	    logger.debug("Closed Connection "+c_con);
	    c_con--;
	    if(c_con < 0) c_con = 0;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { ; }
		con = null;
	    }
	}
    }
    public final static void databaseClean(Statement stmt,
					   ResultSet rs) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null) stmt.close();
	    stmt = null;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	}
    }	
    /**
     * Disconnect the statement and resultSet as we keep the connection
     * 
     * @param con
     * @param stmt
     * @param rs
     */
    public final static void databaseDisconnect(Statement stmt,
						ResultSet rs) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null) stmt.close();
	    stmt = null;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	}
    }	
    /**
     * Disconnect the database and related statements and result sets
     * 
     * @param con
     * @param stmt
     * @param rs
     */
    public final static void databaseDisconnect(Connection con,
						PreparedStatement stmt,
						ResultSet rs) {
	try {
	    if(rs != null) rs.close();
	    rs = null;
	    if(stmt != null) stmt.close();
	    stmt = null;
	    if(con != null) con.close();
	    con = null;
			
	    logger.debug("Closed Connection "+c_con);
	    c_con--;
	    if(c_con < 0) c_con = 0;
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally{
	    if (rs != null) {
		try { rs.close(); } catch (SQLException e) { ; }
		rs = null;
	    }
	    if (stmt != null) {
		try { stmt.close(); } catch (SQLException e) { ; }
		stmt = null;
	    }
	    if (con != null) {
		try { con.close(); } catch (SQLException e) { ; }
		con = null;
	    }
	}
    }	
    /**
     * Write the number in bbbb.bb format needed for currency.
     * = toFixed(2)
     * @param dd the input double number
     * @returns the formated number as string
     */
    public final static String formatNumber(double dd){
	//
	String str = ""+dd;
	String ret="";
	int l = str.length();
	int i = str.indexOf('.');
	int r = i+3;  // required length to keep only two decimal
	// System.err.println(str+" "+l+" "+r);
	if(i > -1 && r<l){
	    ret = str.substring(0,r);
	}
	else{
	    ret = str;
	}
	return ret;
    }
    String formatNumber(String that){

	int ind = that.indexOf(".");
	int len = that.length();
	String str = "";
	if(ind == -1){  // whole integer
	    str = that + ".00";
	}
	else if(len-ind == 2){  // one decimal
	    str = that + "0";
	}
	else if(len - ind > 3){ // more than two
	    str = that.substring(0,ind+3);
	}
	else str = that;

	return str;
    }
    public final static String limitDecimals(String str, int decimals){
	if(str != null && str.length() < 6) return str;
	String ret = str;
	int index = str.indexOf(".");
	String part1 = "", part2="";
	if(index > 0){
	    part1 = str.substring(0,index);
	    part2 = str.substring(index);
	    if(part2.length() > decimals){
		part2 = part2.substring(0,decimals+1);
		ret = part1+part2;
	    }
	}
	return ret;
    }
    public final static String round2(float in){

	String that = ""+(Math.round(in * 100) / 100f);
	int ind = that.indexOf(".");
	int len = that.length();
	String str = "";
	if(ind == -1){  // whole integer
	    str = that + ".00";
	}
	else if(len-ind == 2){  // one decimal
	    str = that + "0";
	}
	else if(len - ind > 3){ // more than two
	    str = that.substring(0,ind+3);
	}
	else str = that;

	return str;
    }
    public final static int getDateItem(String date, String which){
	int ret = 0;
	try{
	    if(which.equals("Month")){
		ret = Integer.parseInt(date.substring(0,date.indexOf("/")));
	    }
	    else if(which.equals("Day")){
		ret = Integer.parseInt(date.substring(date.indexOf("/")+1,date.lastIndexOf("/")));
	    }
	    else{ // year
		ret = Integer.parseInt(date.substring(date.lastIndexOf("/")+1));
	    }
	}catch(Exception ex){
	    logger.error(ex);
	}
	return ret;
    }
    public final static String getDateAfter(String date, int days){
	String ret = "";
	int year =0, month=0,day =0;
	if(date == null) return ret;
	year = getDateItem(date, "Year");
	month = getDateItem(date, "Month");
	day = getDateItem(date, "Day");
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.YEAR, year);
	cal.set(Calendar.MONTH, month-1);
	cal.set(Calendar.DATE, day);
	cal.add(Calendar.DATE, days);
	ret = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE)+"/"+cal.get(Calendar.YEAR);
	return ret;
    }
    //
    public final static String getToday(){

	String day="",month="",year="";
	Calendar current_cal = Calendar.getInstance();
	int mm =  (current_cal.get(Calendar.MONTH)+1);
	int dd =   current_cal.get(Calendar.DATE);
	year = ""+ current_cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }
    public final static String getDateDaysBefore(int days){

	String day="",month="",year="";
	Calendar cal = Calendar.getInstance();
	cal.add(Calendar.DATE, -days);
	int mm =  (cal.get(Calendar.MONTH)+1);
	int dd =   cal.get(Calendar.DATE);
	year = ""+ cal.get(Calendar.YEAR);
	if(mm < 10) month = "0";
	month += mm;
	if(dd < 10) day = "0";
	day += dd;
	return month+"/"+day+"/"+year;
    }	
    public final static int getThisYear(){

	int year = 0;
	Calendar current_cal = Calendar.getInstance();
	year = current_cal.get(Calendar.YEAR);
	return year;
    }
    public final static int[] getYearsStartingFrom(int year){
	int thisYear = getThisYear();
	int[] years = new int[thisYear-year+1];
	int jj=0;
	for(int y=year;y<thisYear+1;y++){
	    years[jj] = y;
	    jj++;
	}
	return years;
    }
    public final static int[] getYearsEndingAt(int year){
	int thisYear = getThisYear();
	int[] years = new int[thisYear-year+1];
	int jj=0;
	for(int y=thisYear;y > year-1;y--){
	    years[jj] = y;
	    jj++;
	}
	return years;
    }		
    public final static int[] get_today() {
	//
	GregorianCalendar cal = new GregorianCalendar();
	int[] ret_val = new int[3];
	ret_val[0] = cal.get(Calendar.MONTH) + 1;
	ret_val[1] = cal.get(Calendar.DATE);
	ret_val[2] = cal.get(Calendar.YEAR);	      
	return ret_val;
    }
    public final static int get_first_day_of_month_falls_on(int MM, int YYYY) {

	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.YEAR, YYYY);
	cal.set(Calendar.MONTH, MM - 1);
	cal.set(Calendar.DAY_OF_MONTH, 1);
      	return cal.get(Calendar.DAY_OF_WEEK);

    }
    //
    public final static int get_days_in_month(int mm, int yy) {

	GregorianCalendar cal = new GregorianCalendar();
		
	cal.set(Calendar.YEAR, yy);
	cal.set(Calendar.MONTH, mm - 1);
	cal.set(Calendar.DATE, 1);
	//
	// checking when a day is not in that month
	//
	int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
	return days;  
    }
    /**
     * find the last day of the pay period given the mm/dd/yy date
     */
    public final static int[] get_pp_last_day(int dd, int mm, int yy){
		
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.HOUR,4);
	cal.set(Calendar.MINUTE,0);
	cal.set(Calendar.SECOND,0);
	// start pay period 1998 day of the year 11
	cal.set(Calendar.YEAR, 1998);
	cal.set(Calendar.MONTH, 0);
	cal.set(Calendar.DATE, 11);
	long time2 = cal.getTimeInMillis();
		
	cal.set(Calendar.YEAR, yy);
	cal.set(Calendar.MONTH, mm - 1);
	cal.set(Calendar.DATE, dd);
	long time1 = cal.getTimeInMillis();
	long diff = (time1-time2)/(24*1000*60*60);
	long diff2 = diff%14; // days - 1 of the start pay period
	int sub_days = -(int)diff2;
	if(diff2 == 0){ // if this is the last day of pay period
	    sub_days = -14;
	}		
	cal.add(Calendar.DATE, sub_days);
	// if the day of the week is not 2 (Monday) we add 1
	int dow = cal.get(Calendar.DAY_OF_WEEK); // we want this to be 2
	if(dow == 1){
	    cal.add(Calendar.DATE, 1);
	}
	int mm2 = cal.get(Calendar.MONTH) + 1;    
	int dd2 = cal.get(Calendar.DATE);    
	int yy2 = cal.get(Calendar.YEAR);
	// System.err.println(" first day pp "+mm2+"/"+dd2+"/"+yy2);
	// System.err.println(" Day of week "+dow);		
	int add_days = 13;
	cal.add(Calendar.DATE, add_days);
	int[] rv = new int[3];
	rv[0] = cal.get(Calendar.MONTH) + 1;    
	rv[1] = cal.get(Calendar.DATE);    
	rv[2] = cal.get(Calendar.YEAR);
	return rv;
    }
    //
    /**
     * find the first day of pay period
     */
    public final static int[] get_pp_first_day(int[] lday){
	
	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.YEAR, lday[2]);
	cal.set(Calendar.MONTH, lday[0] - 1);
	cal.set(Calendar.DATE, lday[1]);
	//
	// last pp day - 13 to get the start pp day
	//
	cal.add(Calendar.DATE, -13);  
	int vv[] = new int[3];      
      	vv[0] = cal.get(Calendar.MONTH) + 1;
	vv[1] = cal.get(Calendar.DATE);
	vv[2] = cal.get(Calendar.YEAR);
	return vv;
    }	

    //
    // initial cap a word
    //
    final static String initCapWord(String str_in){
	String ret = "";
	if(str_in !=  null){
	    if(str_in.length() == 0) return ret;
	    else if(str_in.length() > 1){
		ret = str_in.substring(0,1).toUpperCase()+
		    str_in.substring(1).toLowerCase();
	    }
	    else{
		ret = str_in.toUpperCase();   
	    }
	}
	// System.err.println("initcap "+str_in+" "+ret);
	return ret;
    }
    //
    // init cap a phrase
    //
    final static String initCap(String str_in){
	String ret = "";
	if(str_in != null){
	    if(str_in.indexOf(" ") > -1){
		String[] str = str_in.split("\\s"); // any space character
		for(int i=0;i<str.length;i++){
		    if(i > 0) ret += " ";
		    ret += initCapWord(str[i]);
		}
	    }
	    else
		ret = initCapWord(str_in);// it is only one word
	}
	return ret;
    }

    public final static String getDay(String dt) {

	String day = "";
		
	String days[] = {"","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	if(!dt.equals("")){
	    GregorianCalendar cal = new GregorianCalendar();
	    try{
		int yy = Integer.parseInt(dt.substring(dt.lastIndexOf("/")+1));
		int mm = Integer.parseInt(dt.substring(0,dt.indexOf("/")));
		int dd = Integer.parseInt(dt.substring(dt.indexOf("/")+1,dt.lastIndexOf("/")));
		cal.set(Calendar.YEAR, yy);
		cal.set(Calendar.MONTH, mm - 1);
		cal.set(Calendar.DATE, dd);
		//
		// days of week start from 1-Sun, 7-Sat
		//
		int dayId = cal.get(Calendar.DAY_OF_WEEK);
		day = days[dayId];
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}
	return day;
    }
    /**
     * get the date in yyyymmdd format
     */
    public final static String getYymmddDate(String dt) {

	String date = "";
	if(!dt.equals("")){
	    try{
		String yy = dt.substring(dt.lastIndexOf("/")+1);
		String mm = dt.substring(0,dt.indexOf("/"));
		if(mm.length() == 1) mm ="0"+mm;
		String dd = dt.substring(dt.indexOf("/")+1,dt.lastIndexOf("/"));
		if(dd.length() == 1) dd ="0"+dd;
		date= yy+mm+dd;
	    }
	    catch(Exception ex){
		logger.error(ex);
	    }
	}
	return date;
    }
		
}























































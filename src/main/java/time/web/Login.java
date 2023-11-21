package time.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.jasig.cas.client.authentication.AttributePrincipal;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.model.*;
import time.list.*;
import time.utils.*;

//
// change to Login for use with CAS
//
@WebServlet(urlPatterns = {"/CasLogin"})
public class Login extends TopServlet{

    //
    static Logger logger = LogManager.getLogger(Login.class);
    /**
     * Generates the login form for all users.
     *
     * @param req the request 
     * @param res the response
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	String username = "", ipAddress = "", message="", id="";
	boolean found = false;
	
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String source = "", action="", name="", value="";
	String [] vals;
	Enumeration<String> values = req.getParameterNames();		
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if(name.equals("source")){
		source = value;
	    }
	    if(name.equals("action")){
		action = value;
	    }
	}
	String userid = null;
	AttributePrincipal principal = null;				
	if (req.getUserPrincipal() != null) {
	    principal = (AttributePrincipal) req.getUserPrincipal();
	    userid = principal.getName();
	}
	if(userid == null || userid.isEmpty()){
	    userid = req.getRemoteUser();
	}
	HttpSession session = null;
	if(userid != null){
	    session = req.getSession();			
	    // setCookie(req, res);
	    User user = getUser(userid);
	    if(user != null && session != null){
		session.setAttribute("user",user);
								
		out.println("<head><title></title><META HTTP-EQUIV=\""+
			    "refresh\" CONTENT=\"0; URL=" + url +
			    "Search.do\"></head>");								
		out.println("<body>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
		return;
	    }
	    else{
		message = " Unauthorized access";
	    }
	}
	else{
	    message += " You can not access this system, check with IT or try again later";
	}
	out.println("<head><title></title><body>");
	out.println("<p><font color=red>");
	out.println(message);
	out.println("</p>");
	out.println("</body>");
	out.println("</html>");
	out.flush();	
    }
	
    /**
     * Procesesses the login and check for authontication.
     * 
     * @param username
     */		
    User getUser(String username){
				
	User user = null;
	try{
	    User one = new User(username);
	    String back = one.doSelect();
	    if(!back.equals("")){
		logger.error(back);
	    }
	    else{
		if(one.isUserFound()){
		    user = one;
		}
	    }
	}
	catch (Exception ex) {
	    logger.error(ex);
	}
	return user;
    }

}























































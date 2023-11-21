package time.web;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.oauth2.sdk.token.*;
import com.nimbusds.openid.connect.sdk.Nonce;
import java.net.URI;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import time.model.*;
import time.list.*;
import time.utils.*;
/**
 *
 * for ADFS login
 * 
 */
// comment this line if you want to use CAS
@WebServlet(urlPatterns = {"/Login","/login"}, loadOnStartup = 1)
public class LoginServlet extends TopServlet {

    static Logger logger = LogManager.getLogger(LoginServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){
	try{
	    HttpSession session = request.getSession();
	    User user = (User)session.getAttribute("user");
	    if(user == null){
		OidcClient oidcClient = OidcClient.getInstance();
		//
		//
		oidcClient.setConfig(config);
		URI redirectUrl = oidcClient.getRequestURI();
		// System.err.println("login auth url "+redirectUrl.toString());
		State state = oidcClient.getState();
		Nonce nonce = oidcClient.getNonce();
		session.setAttribute("state",state.toString());
		session.setAttribute("nonce",nonce.toString());
		// save state in session for verification later
		response.sendRedirect(redirectUrl.toString());
	    }
	    else{
		PrintWriter out = response.getWriter();
		String str ="<head><title></title><META HTTP-EQUIV=\""+
		    "refresh\" CONTENT=\"0; URL=" + url +
		    "Search.do";
		str += "\">";
		out.println(str);				
		out.println("<body>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
	    }
	}
	catch(Exception ex){
	    logger.error(""+ex);
	    System.err.println(" "+ex);
	}
    }
}

package time.utils;
import java.sql.*;
import time.model.User;

public class Inserts{

    //
    public final static String nameTitleArr[] ={"","Mr.","Mrs.","Ms."}; 

    public final static String xhtmlHeaderInc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"+
	"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">";
    //
    // basic constructor
    public Inserts(){

    }
    public final static String jqDateStr(String url){
		
	return "{ nextText: \"Next\",prevText:\"Prev\", buttonText: \"Pick Date\", showOn: \"both\", navigationAsDateFormat: true, buttonImage: \""+url+"js/calendar.gif\"}";		

    }
    //
    // main page banner
    //
    public final static String banner(String url){
	return Inserts.banner(url, false);
    }
    public final static String banner(String url, boolean showRefresh){

	String banner = "<head>\n"+
	    "<meta http-equiv=\"Content-Type\" content=\"application/xhtml+xml; charset=utf-8\" />\n"+
	    "<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n";
	if(showRefresh){
	    banner += "<meta http-equiv=\"refresh\" content=\"60\" />\n";
	}
	banner += "<link rel=\"SHORTCUT ICON\" href=\""+url+"css/favicon.ico\" />\n"+
	    "<style type=\"text/css\" media=\"screen\">\n"+
	    "/*<![CDATA[*/"+			
	    "           @import url(\""+url+"css/skin.css\");\n"+
	    "/*]]>*/\n"+		
	    "</style>\n"+
			
	    "<style type=\"text/css\" media=\"print\">\n"+
	    "/*<![CDATA[*/"+			
	    "      @import url(\""+url+"css/print.css\");\n"+
	    "body { color:black; background-color:white; margin:0px; font-family:sans-serif; font-size:8pt; }\n"+
	    "/*]]>*/\n"+			
	    "</style>\n"+
	    "<link rel=\"stylesheet\" href=\""+url+"js/jquery-ui2.css\" type=\"text/css\" media=\"all\" />\n"+
	    "<link rel=\"stylesheet\" href=\""+url+"js/jquery.ui.theme.css\" type=\"text/css\" media=\"all\" />\n"+
	    //
	    // Java Script common for all pages
	    //
	    " <script type=\"text/javascript\">                 \n"+
	    "//<![CDATA[                                        \n"+
	    "  function checkDate(dt){                          \n"+     
	    "    return true;                                   \n"+
	    " }                                              \n"+
	    " //]]>                                    \n"+			
	    " </script>				                  \n"+
	    "<title>Legacy Kuali & Clocker Time - City of Bloomington, Indiana</title>\n"+
	    "</head>\n"+
	    "<body>\n"+
	    "<div id=\"banner\">\n"+
	    "  <div id=\"application_name\">Legacy Time </div>\n"+
	    "  <div id=\"location_name\">City Of Bloomington, IN</div>\n"+
	    "  <div id=\"navigation\"></div>\n"+
	    "</div>";
	return banner;
    }
    //
    public final static String menuBar(String url){
	String menu = "<div class='menuBar'>\n<ul>";
	menu += "<li><a href=\""+url+"/Logout\">Logout</a></li>\n";
	menu += "</ul></div>\n";
	return menu;
    }
    //
    // sidebar
    //
    public final static String sideBar(String url, User user){

	String ret = "<div id=\"leftSidebar\" class=\"left sidebar\">";
	ret += "<h3 class=\"titleBar\">New</h3>\n"+
	    "<ul>\n"+
	    "<li><a href=\""+url+"Search.do\">Search</a></li>\n";
	ret += "<li><a href=\""+url+"User.do\">Users</a></li>\n";
	ret += "</ul>\n";
	ret += "</div>";
	return ret;
    }
	
}























































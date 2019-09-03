/* ************************************************************************
LEBAH PORTAL FRAMEWORK
Copyright (C) 2007  Shamsul Bahrin

* ************************************************************************ */


package lebah.portal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lebah.portal.velocity.VServlet;
import lebah.portal.velocity.VTemplate;
import lebah.util.Util;


/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public class ControllerServlet2 extends VServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException    {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
		//System.out.println("[ControllerServlet2] Begin doPost.");
		res.setContentType("text/html");
		//res.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        String className = this.getClass().getName();
       


		//synchronized(this) {
			context = (org.apache.velocity.VelocityContext) session.getAttribute("VELOCITY_CONTEXT");
			engine = (org.apache.velocity.app.VelocityEngine) session.getAttribute("VELOCITY_ENGINE");
			if ( context == null ) {
				initVelocity(getServletConfig());
				session.setAttribute("VELOCITY_CONTEXT", context);
				session.setAttribute("VELOCITY_ENGINE", engine);
			}
		//}
		
		/*
		context = (org.apache.velocity.VelocityContext) getServletConfig().getServletContext().getAttribute("VELOCITY_CONTEXT");
		engine = (org.apache.velocity.app.VelocityEngine) getServletConfig().getServletContext().getAttribute("VELOCITY_ENGINE");
		*/

        context.put("util", new Util());
        
		//LABELS Properties
		context.put("label", lebah.db.Labels.getInstance().getTitles());
		
		//Security token for ajax servlet
		String securityToken = (String) session.getAttribute("securityToken");
		if ( securityToken == null || "".equals(securityToken) ) {
			securityToken = lebah.db.UniqueID.getUID();
			session.setAttribute("securityToken", securityToken);
		}
		
		String userAgent = req.getHeader("User-Agent");
        context.put("userAgent", userAgent);
        
        if ( userAgent.indexOf("MSIE") > 0 ) {
            context.put("browser", "ie");
        }
        else if ( userAgent.indexOf("Firefox") > 0 ) {
            context.put("browser", "firefox");
        }
        else if ( userAgent.indexOf("Netscape") > 0 ) {
            context.put("browser", "netscape");
        }
        else if ( userAgent.indexOf("Safari") > 0 ) {
            context.put("browser", "safari");
        }
        else if ( userAgent.indexOf("MIDP") > 0 ) {
            context.put("browser", "midp");
        }
        else {
            System.out.println("["+className+"] userAgent = "+userAgent);
        }
        
        if ( userAgent.indexOf("Windows") > 0 ) {
            PCDeviceController.doService(getServletContext(), getServletConfig(), engine, context, session, req, res);
        }
        else if ( userAgent.indexOf("MIDP") > 0 ) {
        	System.out.println("["+className+"] userAgent = "+userAgent);
        }
        else if ( userAgent.indexOf("MMP") > 0 ) {
        	System.out.println("["+className+"] userAgent = "+userAgent);
        }        
        else {
            //System.out.println(userAgent);
            PCDeviceController.doService(getServletContext(), getServletConfig(), engine, context, session, req, res);
        }
		
	     // CLEANUP VELOCITY CONTEXT: BEGIN 
       
		
		//CleanUpVelocityContext.run(context, "ControllerServlet2");
	}


}

/* ************************************************************************
LEBAH PORTAL FRAMEWORK, http://lebah.sf.net
Copyright (C) 2007  Shamsul Bahrin







but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

* ************************************************************************ */
package lebah.portal.velocity;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import lebah.util.*;

/**
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1.01
 */
public abstract class VTemplate extends javax.servlet.http.HttpServlet {
	
	protected VelocityEngine engine;
	protected VelocityContext context;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected ServletContext servletContext;
	protected ServletConfig servletConfig;	
	protected String id = "";
	
	protected String templateName = "";
	protected String parsedTemplateName = "";
	protected boolean isActionTemplate = false;
	protected boolean showVM = false;
	protected boolean isDiv = false;
	
	protected String submit = "";
	protected String user = "";
	
	protected VTemplate() {
		
	}
	
	protected VTemplate(VelocityEngine engine, VelocityContext context, HttpServletRequest request, HttpServletResponse response) {
		this.engine = engine;
		this.context = context;
		this.request = request;
		this.response = response;
	}
	
	public void setEnvironment(VelocityEngine engine, VelocityContext context, HttpServletRequest request, HttpServletResponse response) {
		this.engine = engine;
		this.context = context;
		this.request = request;
		this.response = response;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setServletContext(ServletContext ctx ) {
		this.servletContext = ctx;
	}
	
	
	//protected ServletContext getServletContext() {
	//	return servletContext;
	//}
	
	public void setServletConfig(ServletConfig cfg ) {
		this.servletConfig = cfg;
	} 
	
	public ServletConfig getServletConfig() {
		return servletConfig;
	}	
	
	//public abstract Template doTemplate() throws Exception;  //remove abstract
	public Template doTemplate() throws Exception {
		//subclass need to implement body
		return null;	
	}
	
	public StringBuffer getBuffer() throws Exception {
		StringBuffer sb = new StringBuffer("");
		try {
			
			//String resourceLoaderPath = (String) engine.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH);
			Template template = doTemplate();
			
			templateName = template.getName();
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			writer.close();
			sb = writer.getBuffer();
		} catch ( Exception ex ) {
			throw ex;			
		}
		return sb;
	}	
	
	public StringBuffer getBuffer(HttpSession session) throws Exception {
		StringBuffer sb = new StringBuffer("");
		try {
			Template template = doTemplate();
			//Tue Feb 6, 2006, shamsul
			//LogActivity.log(this, submit, (String) session.getAttribute("_portal_login"));
			//
			templateName = template.getName();
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			writer.close();
			sb = writer.getBuffer();
		} catch ( Exception ex ) {
		    ex.printStackTrace();
			throw ex;			
		}
		return sb;
	}	
	
	public void setShowVM(boolean b) {
		showVM = b;
	}

	public void print() throws Exception {
		PrintWriter out = response.getWriter();
		out.print(getBuffer());	
		printVM(out);
	}
	
	public void print(HttpSession session) throws Exception {
		PrintWriter out = response.getWriter();
		out.print(getBuffer(session));	
		printVM(out);
	}	

	private void printVM(PrintWriter out) {
		if ( showVM ) {
			out.print("<br/><table width=\"100%\"><tr><td align=\"right\"><font size=\"1\">");
			if ( isActionTemplate ) {
				out.print(parsedTemplateName);
			} else {
				out.print(templateName);
			}
			out.print("</font></tr></td></table>");
		}
	}
	
	//a handy method for getParameter
	protected String getParam(String param) {
		return request.getParameter(param) != null ? request.getParameter(param) : "";
	}	
	
	protected int getParamAsInteger(String param) {
		return getParam(param) != "" ? Integer.parseInt(getParam(param)) : 0;	 
	}
	
	protected boolean post(HttpSession session) {
		return session.getAttribute("doPost") != null ?
			"true".equals((String) session.getAttribute("doPost")) ? true : false : true;
	}
	protected String getParam(HttpServletRequest request, String param) {
		return request.getParameter(param) != null ? request.getParameter(param) : "";
	}

	/**
	 * @return Returns the parsedTemplateName.
	 */
	public String getParsedTemplateName() {
		return parsedTemplateName;
	}

	/**
	 * @param parsedTemplateName The parsedTemplateName to set.
	 */
	public void setParsedTemplateName(String parsedTemplateName) {
		this.parsedTemplateName = parsedTemplateName;
	}

	/**
	 * @return Returns the isActionTemplate.
	 */
	public boolean isActionTemplate() {
		return isActionTemplate;
	}

	/**
	 * @param isActionTemplate The isActionTemplate to set.
	 */
	public void setActionTemplate(boolean isActionTemplate) {
		this.isActionTemplate = isActionTemplate;
	}

	public boolean isDiv() {
		return isDiv;
	}

	public void setDiv(boolean isDiv) {
		this.isDiv = isDiv;
	}	
	
	
}

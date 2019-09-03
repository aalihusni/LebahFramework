# LebahFramework
Light and Easy Business Architecture (LeBAH)

This is a Java based web-application framework.  You can use this framework to develop a web application.  This framework comes with pre-setup portal's data where you can right away open it in the web browser, but first you need to open this as an Eclipse project.  In the ant folder, you shall find the ant build.xml file, where you can use to create a war file to be deployed into your Tomcat server.

This repository contains the full source code of the LeBAH framework. I've started developing this framework since 2005 (about more than 10 years ago, when I was very new with the Java language itself.. so please expect the source codes are not that good..(a smiley face)...)

I am using Derby as database.  You can find the derby data inside the derby folder (IMPORTANT: Don't modify or delete any files in this folder).  You should copy this folder into your preferred location in your file system, and then set the url connection in the dbconnection.properties file to the location of your derby database.

The database url value initially is pointing to my derby folder as below:

url=jdbc:derby:/Users/Admin/Documents/workspace1/lebah4/derby/db;create=true

Replace the "/Users/Admin/Documents/workspace1/lebah4/" to the location of where your derby folder located, for example if you are using Windows environment, and the you copied derby folder into "c:/MyData", then your database url connection should be like this:

url=jdbc:derby:c:/MyData/derby/db;create=true

UPDATE:
There is a serious update on the Velocity Engine initiator in the class VServlet.  This time, a Singleton Velocity Engine is used and being created by the VelocityEngineHolder class.


Thank You.<br/>
Shamsul Bahrin b Abd Mutalib

1.install the liferay ide 2.2
2.import this plugin project as a maven project
3.you may need to change the server profile following your own server config in pom.xml
4.start your server
5.right click on this project and select Liferay->Maven->liferay:deploy
6.open the page: http://localhost:8080/webservices-js-templates-generator in browser and the console will show the message "success:${yourserverhome}\webapps\webservices-js-templates-generator\webservices-js-templates.xml"
7.go to this location and copy the xml file to ide plugins folder
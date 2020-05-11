Demo link: https://youtu.be/34WVjXMIuNs <br/>
Prior to Deployment:
1. Run the included "create_table.sql" to create the movieDB<br/>
2. "create_table.sql" includes extra views for the necessary queries<br/>
3. Populate the movieDB with "movie-data.sql"<br/>
4. Run "stored-procedure.sql" to create the add_movie procedure <br/>
<br/>
Deployment Instructions (WEB-APP): <br/>
1. Clone the project in cmd using: git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10.git <br/>
2. Navigate to the cloned project folder in cmd <br/>
3. Enter: mvn clean <br/>
4. Enter: mvn package <br/>
5. Import the project into Intellij as a Maven project. <br/>
6. To run the project, add a new configuration: Tomcat Server > Local <br/>
7. Mark the artifact(s): cs122b-spring20-team-10:war OR cs122b-spring20-team-10:war exploded <br/>
8. In the application context at the bottom, change the existing text to: /cs122b-spring20-team-10 <br/>
9. Hit apply, then ok <br/>
10. Deploy the Tomcat Server <br/>
<br/>

Deployment Instructions (XMLParse): <br/>
1. Import cs122b-spring20-team-10-parser as a separate Maven project.
2. Download the XML files: mains243.xml, casts124.xml, and actors63.xml from http://infolab.stanford.edu/pub/movies/dtd.html <br/>
3. Place the XML files in the same location as pom.xml <br/>
4. Right click XMLParser in /src/main/java/ and select run(). <br/>
5. Inconsistencies will be output to errors.txt after the parsing is complete <br/>
<br/>

Contributions:<br/>
Project 1 <br/>
  - SQL queries for Single Movie Page: Brian Chau<br/>
  - SQL queries for Single Star Page: Alex Nguyen<br/>
  - SQL queries for Movie List: Alex Nguyen<br/>
  - SQL queries for Browsing:Alex Nguyen<br/>
  - SQL queries for Searching:Alex Nguyen<br/>
  - Demo Recording: Brian Chau<br/>
  
Project 2 <br/>
  - Movie List Page: Alex Nguyen<br/
  - Single Movie Page: Brian Chau<br
  - Single Star Page: Brian Chau<br/
  - Checkout Page:Brian Chau<br/>
  - Confirmation :Brian Chau<br/>
  - Login Filter: Alex Nguyen<br/>
  - Login Page:Alex Nguyen<br/>
  - Shopping List:Brian Chau<br/>
  - HTML Formatting: Brian Chau<br/>
<br/>

Project 3 <br/>
  - PreparedStatement Conversion: Brian Chau <br/>
  - reCAPTCHA Login: Brian Chau <br/>
  - Dashboard Page and add_movie: Brian Chau <br/>
  - DOM XML Parser: Alex Nguyen <br/>
  - DOM XML Parser Optimization: Alex Nguyen <br/>
  - XML Inconsistencies Output: Alex Nguyen <br/>

Substring matching design:
The substring matching design is made to take any occurrence of the user's input and appends wildcard characters to parameter when it is passed. Wildcard characters depend on the search query: <br/>
1. col LIKE %string% //Is a substring match <br/>
2. col LIKE a< 	//Is a starts with match, where a is some value A-Z or 0-9 <br/>
3. col = ~ //Displays all non-alphanumerical characters <br/>
4. col = string //Is a complete match <br/>

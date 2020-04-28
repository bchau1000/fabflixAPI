Demo link: https://youtu.be/jx4Sa04SwRM

Prior to Deployment:

Run the included "create_table.sql" to create the movieDB
"create_table.sql" includes extra views for the necessary queries
Populate the movieDB with "movie-data.sql"
Deployment Instructions:

Clone the project in cmd using: git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10.git
Navigate to the cloned project folder in cmd
Enter: mvn clean
Enter: mvn package
Import the project into Intellij as a Maven project.
To run the project, add a new configuration: Tomcat Server > Local
Mark the artifact(s): cs122b-spring20-team-10:war OR cs122b-spring20-team-10:war exploded
In the application context at the bottom, change the existing text to: /cs122b-spring20-team-10
Hit apply, then ok
Deploy the Tomcat Server

Contributions:

	SQL queries for Single Movie Page: Brian Chau
	SQL queries for Single Star Page: Alex Nguyen
	SQL queries for Movie List: Alex Nguyen
	SQL queries for Browsing:Alex Nguyen
	SQL queries for Searching:Alex Nguyen
	Demo Recording: Brian Chau
	Movie List Page: Alex Nguyen
	Single Movie Page: Brian Chau
	Single Star Page: Brian Chau
	Checkout Page:Brian Chau
	Confirmation :Brian Chau
	Login Filter: Alex Nguyen
	Login Page:Alex Nguyen
	Shopping List:Brian Chau
	HTML Formatting: Brian Chau



Substring matching design:
The substring matching design is made to take any occurrence of the user's input and appends wildcard characters to parameter when it is passed.
Wildcard characters depend on the search query:
	1. col LIKE %string% //Is a substring match
	2. col LIKE a< 	//Is a starts with match, where a is some value A-Z or 0-9
	3. col = ~ //Displays all non-alphanumerical characters
	4. col = string //Is a complete match

- # General
    - #### Team#: 10
    
    - #### Names: Brian Chau, Alex Ba Nguyen
    
    - #### Project 5 Video Demo Link: https://youtu.be/YVxK8m1WS7A

    - #### Instruction of deployment:
        - #### WEB-APP
            1. #### Clone the project in cmd using: `git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10.git`
            2. #### Navigate to the cloned project folder in cmd
            3. #### Enter: `mvn clean`
            4. #### Enter: `mvn package`
            5. #### Import the project into Intellij as a Maven project.
            6. #### To run the project, add a new configuration: `Tomcat Server > Local`
            7. #### Mark the artifact(s): `cs122b-spring20-team-10:war` OR `cs122b-spring20-team-10:war exploded`
            8. #### In the application context at the bottom, change the existing text to: `/cs122b-spring20-team-10`
            9. #### Hit apply, then ok
            10. #### Deploy the Tomcat Server
            
        - #### XML-Parser
            1. #### Import `cs122b-spring20-team-10-parser` as a separate Maven project.
            2. #### Download the XML files: `mains243.xml`, `casts124.xml`, and `actors63.xml` from `http://infolab.stanford.edu/pub/movies/dtd.html` 
            3. #### Place the XML files in the same location as `pom.xml`
            4. #### Right click XMLParser in `/src/main/java/` and select run()
            5. #### Inconsistencies will be output to `/cs122b-spring20-team-10-parser/errors.txt after the parsing` is complete.
            
        - #### Android-App
            1. #### Import `cs122b-spring20-team-10-android` as a separate Gradle project
            2. #### Ensure the URL variable in `Login.java` matches the backend server URL
            3. #### Ensure the variables in the Uri builders in `SingleMovieActivity.java` and `ListViewActivity.java` match the backend server URL (instructions to deploy onto your local server are commented out).
            4. #### In AVD configurations, setup an emulator for the Pixel 3
            5. #### Run the emulator

    - #### Collaborations and Work Distribution:
        - #### Project 1:
            - #### SQL queries for Single Movie Page: Brian Chau
            - #### SQL queries for Single Star Page: Alex Nguyen
            - #### SQL queries for Movie List: Alex Nguyen
            - #### SQL queries for Browsing:Alex Nguyen
            - #### SQL queries for Searching:Alex Nguyen
            - #### Demo Recording: Brian Chau
        - #### Project 2:
            - #### Movie List Page: Alex Nguyen
            - #### Single Movie Page: Brian Chau
            - #### Single Star Page: Brian Chau
            - #### Checkout Page:Brian Chau
            - #### Confirmation :Brian Chau
            - #### Login Filter: Alex Nguyen
            - #### Login Page:Alex Nguyen
            - #### Shopping List:Brian Chau
            - #### HTML Formatting: Brian Chau
        - #### Project 3:
            - #### PreparedStatement Conversion: Brian Chau
            - #### reCAPTCHA Login: Brian Chau
            - #### Dashboard Page and add_movie: Brian Chau
            - #### DOM XML Parser: Alex Nguyen
            - #### DOM XML Parser Optimization: Alex Nguyen
            - #### XML Inconsistencies Output: Alex Nguyen
        - #### Project 4:
            - #### Full-text Search: Alex Nguyen
            - #### Autocomplete: Alex Nguyen
            - #### Android App: Brian Chau


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### The files utilizing or enabling routing queries to Master/Slave SQL include:
        - #### `/src/AndroidList` read requests (Master/Slave)
        - #### `/src/CheckoutPageServlet` read/write requests (Master)
        - #### `/src/ConfirmationServlet` read requests (Master/Slave)
        - #### `/src/DashboardServlet` read requests (Master/Slave)
        - #### `/src/InsertServlet` read/write requests (Master)
        - #### `/src/LoginServlet` read requests (Master/Slave)
        - #### `/src/MainPageServlet` read requests (Master/Slave)
        - #### `/src/MovieListServlet` read requests (Master/Slave)
        - #### `/src/ShoppingListServlet` read requests (Master/Slave)
        - #### `/src/SingleMovieServlet` read requests (Master/Slave)
        - #### `/src/SingleStarServlet` read requests (Master/Slave)
        - #### `/src/SuggestionServlet` read requests (Master/Slave)
        - #### `/WebContent/WEB-INF/context.xml` defines Master/Slave SQL DataSources
        - #### `/WebContent/WEB-INF/web.xml` registers Master/Slave SQL DataSources

    - #### Read/write requests were routed to Master/Slave by defining a new datasource within "context.xml". Servlets that are only required to read utilize the default `jdbc/moviedb` resource, while read/write Servlets utilize `jdbc/masterdb`. 
        - #### `jdbc/moviedb` simply redirects read requests to localhost, which is then handled by the load balancer. 
        - #### `jdbc/masterdb` redirects read/write requests directly to the Master SQL ip.

- # JMeter TS/TJ Time Logs
    - #### Log files are stored in `/logs` as `ts.txt` and `tj.txt`
    - #### Log files for the scaled versions will contain logs for both Master and Slave instances
    - #### Instructions for using `log_processing`:
        1. #### Import log_processing as a separate Maven project
        2. #### Copy and paste the `ts.txt` and `tj.txt` log files you want to process from `/logs`
        3. #### Right click `log_processor.java` and select `Run 'log_processor.main()'`
        4. #### Average Search and JDBC times will output to the console.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 419                        | 341.83                              | 341.61                    | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 3576                       | 3495.47                             | 3495.28                   | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | 3554                       | 3463.02                             | 3462.82                   | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         |                              	   |                           | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 550                        | 471.69                              | 471.26                    | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 2641                       | 2317.96                             | 2317.65                   | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

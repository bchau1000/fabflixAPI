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
        - #### Project 5:
            - #### Enabling Connection Pooling: Brian Chau
            - #### JMeter Testing: Alex Nguyen, Brian Chau
            - #### Log Analysis: Alex Nguyen, Brian Chau
            - #### Log Processor: Alex Nguyen
            - #### Servlet Log Output: Alex Nguyen
            - #### README Questions: Brian Chau, Alex Nguyen

- # Connection Pooling
    - #### The files utilizing JDBC connection pooling include:
        - #### `/src/AndroidList` 
        - #### `/src/CheckoutPageServlet`
        - #### `/src/ConfirmationServlet`
        - #### `/src/DashboardServlet`
        - #### `/src/InsertServlet`
        - #### `/src/LoginServlet`
        - #### `/src/MainPageServlet`
        - #### `/src/MovieListServlet`
        - #### `/src/ShoppingListServlet`
        - #### `/src/SingleMovieServlet`
        - #### `/src/SingleStarServlet`
        - #### `/src/SuggestionServlet`
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
        - #### Connection pooling allows separate requests to utilize the same "connection" between the backend and database. Instead of immediately closing a connection after using it, connection pooling maintains the connections that new requests create. Separate requests will retrieve information more efficiently by continuously "recycling" the same connection to the database over and over again.
            - #### For example, in `src/MovieListServlet`, every movie query sent by the user will call the connection pool, if there is already an existing connection, the connection pool will return that connection. Otherwise, the connection pool will establish a new connection and add it to the pool.
    
    - #### Explain how Connection Pooling works with two backend SQL.
        - #### Connection pools do not share between Master/Slave instances. Read/write requests will always be sent directly to the Master instance, as a result servlets will function in the same way they would if the web application was deployed on a single server/instance. On the other hand, read only requests will be sent based on the load-balancer, which means they may end up in either the Master or the Slave instance. Each read only request will check its respective instance for an existing connection, if one does exist, the connection pool will return that connection, otherwise a new connection will be established and added to the pool.

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
| Case 1: HTTP/1 thread| ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10/blob/master/img/case_1_single.PNG)       | 419                        | 341.83                              | 341.61                    |The standard deviation from the average is 13ms, which indicates that most requests are very close to the average 419ms. The throughput shows that the WEB-APP can process up to 143 requests/min.|
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10/blob/master/img/case_2_single.PNG)   | 3576                       | 3495.47                             | 3495.28                   | The additional 10 threads heavily affected the average, which now sits at 3576ms. The deviation increased to 92ms from the average, which may be due to the fact that all 10 threads are being sent to the same instance. The throughput however increased to 167/min from 143/min, which means there was a 24/min increase to the requests that could be handled.|
| Case 3: HTTPS/10 threads                       | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10/blob/master/img/case_3_single.PNG)   | 3554                       | 3463.02                             | 3462.82                   | In comparison to case 2, there is almost no difference in the data. The deviation increased from 92ms to 132ms, which means the search durations are slightly more inconsistent than case 2.|
| Case 4: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10/blob/master/img/case_4_single.PNG)   | 3651                       | 3586.18                      	   | 3586.01                   |The lack of connection pooling increased the average to 3651ms, this may be the result of having to constantly open and close connections. Furthermore, the deviation has doubled in comparison to case 3, making the query durations even more inconsistent. However, the throughput remains nearly the same sitting at 164 requests/min.|

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10/blob/master/img/case_1_scaled.PNG)   | 550                        | 471.69                              | 471.26                   |In comparison to the single-thread on the single-instance, load-balancing seems to actually hinder performance on a single thread. The average sits at 550ms, which is up from 419ms, and the throughput has reduced to 108/min. |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10/blob/master/img/case_2_scaled.PNG)   | 2641                       | 2317.96                             | 2317.65                  |Increasing the number of threads shows the extra performance capabilities that load-balancing provides. The throughput has nearly doubled from the single-instance version, allowing for 249 requests/min. Furthermore, the average query duration has been reduced to 2396ms, which is a significant improvement from 3576ms.|
| Case 3: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-10/blob/master/img/case_3_scaled.PNG)   | 3413                       | 3002.56                             | 3002.38                  |The lack of connection pooling seems to have affected the scaled-version the most, putting the deviation at 1272ms from the average. This indicates that query durations are very inconsistent. Furthermore, in comparison to case 2 of the scaled-version, the throughput has dropped from 243/min to 175 requests/min. The data shows that connection pooling and load-balancing seem to go hand-in-hand.|

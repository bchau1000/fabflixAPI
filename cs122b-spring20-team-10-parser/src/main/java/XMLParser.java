import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.*;

public class XMLParser {
    Document movieXML;
    Document starXML;
    Document starMovieXML;
    String allErrors = "";

    private void parseXmlFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            movieXML = db.parse("mains243.xml");
            starXML = db.parse("actors63.xml");
            starMovieXML = db.parse("casts124.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseStars(Connection connection) throws SQLException {
        if (connection != null) {
            System.out.println("Connection established @ 'parseStars'");
            System.out.println();
        }

        connection.setAutoCommit(false);

        Element starDoc = starXML.getDocumentElement();
        NodeList starList = starDoc.getElementsByTagName("actor");

        String getMaxId = "SELECT max(id) as 'id' FROM stars;";
        PreparedStatement maxIdStatement = connection.prepareStatement(getMaxId);
        ResultSet maxIdSet = maxIdStatement.executeQuery();
        String strMaxId = "";

        String insertVal = "INSERT INTO stars(id, name, birthYear) VALUES (?, ?, ?)";
        PreparedStatement insert = connection.prepareStatement(insertVal);

        String getStar = "SELECT * FROM stars WHERE name = ? AND birthYear = ?;";
        PreparedStatement starCheck = connection.prepareStatement(getStar);

        if(maxIdSet.next())
            strMaxId = maxIdSet.getString("id");

        int maxIdNum = Integer.parseInt(strMaxId.substring(2));
        maxIdNum++;

        if(starList != null && starList.getLength() > 0)
        {
            for(int i = 0; i < starList.getLength(); i++)
            {
                String starName = getTextValue((Element) starList.item(i), "stagename");
                int starDob = getIntValue((Element) starList.item(i), "dob");
                String maxId = "nm" + maxIdNum;

                starCheck.setString(1, starName);
                starCheck.setInt(2, starDob);
                ResultSet starSet = starCheck.executeQuery();

                if(starSet.next()) {
                    String dupeId = starSet.getString("id");
                    System.out.println("DUPLICATE_FOUND: (" + dupeId  + ", " + starName  + ", " + starDob + ")");
                }
                else {
                    insert.setString(1, maxId);
                    insert.setString(2, starName);

                    if (starDob == -1) {
                        System.out.println("Inserting: (" + maxId + ", " + starName + ", " + "N/A)");
                        insert.setString(3, null);
                    } else {
                        System.out.println("Inserting: (" + maxId + ", " + starName + ", " + starDob + ")");
                        insert.setInt(3, starDob);
                    }

                    insert.execute();
                    maxIdNum++;
                }
            }
            connection.commit();
        }
    }

    private void parseMovie(Connection connection) throws SQLException {
        if (connection != null) {
            System.out.println("Connection established @ 'parseMovie'");
            System.out.println();
        }
        connection.setAutoCommit(false);

        String getMovie = "SELECT * FROM movies WHERE title = ? AND director = ? AND year = ?;";
        PreparedStatement movieCheck = connection.prepareStatement(getMovie);

        String insertVal = "INSERT INTO movies(id, title, director, year) VALUES(?, ?, ?, ?);";
        PreparedStatement insert = connection.prepareStatement(insertVal);

        String insertGiMQuery = "INSERT INTO genres_in_movies(genreId, movieId) VALUES(?, ?);";
        PreparedStatement insertGiM = connection.prepareStatement(insertGiMQuery);

        String getMaxId = "SELECT max(id) as 'id' FROM movies;";
        PreparedStatement maxIdStatement = connection.prepareStatement(getMaxId);
        ResultSet maxIdSet = maxIdStatement.executeQuery();

        String strMaxId = "";
        if(maxIdSet.next()) strMaxId = maxIdSet.getString("id");
        int maxIdNum = Integer.parseInt(strMaxId.substring(2));
        maxIdNum++;

        Element docEle = movieXML.getDocumentElement();

        NodeList directorList = docEle.getElementsByTagName("directorfilms");

        String errors = "";
        if(directorList != null && directorList.getLength() > 0) {
            for(int i = 0; i < directorList.getLength(); i++) {
                String dirName = "";

                if(getTextValue((Element) directorList.item(i),"dirname") == null) {
                    errors += "Error at: " + getTextValue((Element) directorList.item(i), "dirid") + " (Invalid director)\n";
                    continue;
                }
                else
                    dirName = getTextValue((Element) directorList.item(i),"dirname");

                Node item = directorList.item(i);
                Element e = (Element) item;

                NodeList filmList = e.getElementsByTagName("film");

                for(int j = 0; j < filmList.getLength(); j++)
                {

                    try{
                        String maxId = "tt0" + maxIdNum;
                        String movieTitle = getTextValue((Element) filmList.item(j), "t");


                        int movieYear = getIntValue((Element) filmList.item(j), "year");

                        if(movieTitle == null) {
                            errors += "Error at: " + getTextValue((Element) filmList.item(j), "fid") + " (Invalid title)\n";
                            continue;
                        }

                        if(movieYear == -1)
                            errors += "Error at: " + getTextValue((Element) filmList.item(j), "fid") + " (Invalid year) \n";
                        else{
                            insert.setString(1, maxId);
                            insert.setString(2, movieTitle);
                            insert.setString(3, dirName);
                            insert.setInt(4, movieYear);

                            movieCheck.setString(1, movieTitle);
                            movieCheck.setString(2, dirName);
                            movieCheck.setInt(3, movieYear);

                            Node genreItem = filmList.item(j);
                            Element genreEle = (Element) genreItem;
                            NodeList genreList = genreEle.getElementsByTagName("cats");

                            String parseGenre = "";
                            for(int k = 0; k < genreList.getLength(); k++) {
                                if (!getTextValue((Element) genreList.item(k), "cat").equals("null")) {
                                    parseGenre += processGenres(getTextValue((Element) genreList.item(k), "cat"));
                                }
                            }

                            ResultSet getDupes = movieCheck.executeQuery();
                            if(getDupes.next())
                                System.out.println("DUPLICATE_FOUND");
                            else {
                                System.out.println("Inserting: (" + maxId + ", " + movieTitle + ", " + dirName + ", " + parseGenre + ", " + movieYear + ")");
                                insert.execute();

                                String getId = "SELECT * FROM genres WHERE name = ?;";
                                PreparedStatement getIdStatement = connection.prepareStatement(getId);

                                getIdStatement.setString(1, parseGenre);

                                ResultSet getIdSet = getIdStatement.executeQuery();
                                if(getIdSet.next())
                                {
                                    String genreId = getIdSet.getString("id");
                                    System.out.println(maxId + ", " + genreId);
                                    try{
                                        insertGiM.setString(1, genreId);
                                        insertGiM.setString(2, maxId);

                                        insertGiM.execute();
                                    } catch(Exception gExcept){}
                                }

                                maxIdNum++;
                            }
                        }
                    }
                    catch(Exception movieParseE){
                        errors += "Error at: " + getTextValue((Element) filmList.item(j), "fid") + " (Invalid title)\n";
                    }
                }
            }



            connection.commit();
        }
    }

    private String parseStarMovie(Connection connection) throws SQLException, IOException {
        if (connection != null) {
            System.out.println("Connection established @ 'parseStarMovie'");
            System.out.println();
        }

        BufferedWriter out = new BufferedWriter(new FileWriter("data.txt"));

        connection.setAutoCommit(false);

        String smErrors = "Errors @ parseStarMovie: \n";

        String getMaxId = "SELECT max(id) as 'id' FROM movies;";
        PreparedStatement maxIdStatement = connection.prepareStatement(getMaxId);
        ResultSet maxIdSet = maxIdStatement.executeQuery();

        String insertVal = "INSERT INTO stars_in_movies VALUES(?, ?);";
        PreparedStatement insert = connection.prepareStatement(insertVal);

        String strMaxId = "";
        if(maxIdSet.next()) strMaxId = maxIdSet.getString("id");
        int maxIdNum = Integer.parseInt(strMaxId.substring(2));
        maxIdNum++;

        Element starMovieDoc = starMovieXML.getDocumentElement();
        int getCount = 0;

        NodeList directorList = starMovieDoc.getElementsByTagName("dirfilms");
        if(directorList != null && directorList.getLength() > 0) {
            for(int i = 0; i < directorList.getLength(); i++) {

                if(getTextValue((Element) directorList.item(i),"is").equals(null))
                    continue;
                String dirName = getTextValue((Element) directorList.item(i),"is");
                Node n = directorList.item(i);
                Element e = (Element) n;

                NodeList filmList = e.getElementsByTagName("m");

                if(filmList != null && filmList.getLength() > 0) {
                    for (int j = 0; j < filmList.getLength(); j++) {
                        try {
                            String movieTitle = getTextValue((Element) filmList.item(j), "t");
                            String starName = getTextValue((Element) filmList.item(j), "a");
                            if (getTextValue((Element) filmList.item(j), "t").equals(null) || getTextValue((Element) filmList.item(j), "a").equals(null))
                                continue;
                            String movieId = "";
                            String starId = "";

                            String movieQuery = "SELECT * FROM movies WHERE title = ? AND director = ?;";
                            PreparedStatement checkMovie = connection.prepareStatement(movieQuery);
                            checkMovie.setString(1, movieTitle);
                            checkMovie.setString(2, dirName);
                            ResultSet getMovie = checkMovie.executeQuery();

                            if (getMovie.next())
                                movieId = getMovie.getString("id");
                            else
                                movieId = null;


                            String starQuery = "SELECT * FROM stars WHERE name = ?;";
                            PreparedStatement checkStar = connection.prepareStatement(starQuery);
                            checkStar.setString(1, starName);
                            ResultSet getStar = checkStar.executeQuery();

                            if (getStar.next())
                                starId = getStar.getString("id");
                            else
                                starId = null;


                            if (!starId.equals(null) && !movieId.equals(null)) {
                                System.out.println(getCount + ". Inserting: (" + starId + ", " + movieId + ")");
                                insert.setString(1, starId);
                                insert.setString(2, movieId);
                                insert.addBatch();
                                getCount++;
                            }

                        } catch (Exception starMovieE) {
                            smErrors += "Could not insert, star or movie does not exist in moviedb. \n";
                        }
                    }
                }
            }
            insert.executeBatch();
            connection.commit();
        }
        return smErrors;
    }

    private void parseGenres(Connection connection) throws SQLException {
        if (connection != null) {
            System.out.println("Connection established @ 'parseStarMovie'");
            System.out.println();
        }

        connection.setAutoCommit(true);

        Element movieDoc = movieXML.getDocumentElement();

        NodeList genreList = movieDoc.getElementsByTagName("cats");
        String searchGenre = "SELECT * FROM genres WHERE name = ?;";
        PreparedStatement statement = connection.prepareStatement(searchGenre);

        for(int i = 0; i < genreList.getLength(); i++)
        {
            try {
                String genre = processGenres(getTextValue((Element) genreList.item(i), "cat"));
                System.out.print(genre);

                statement.setString(1, genre);
                ResultSet genreSet = statement.executeQuery();

                if(genreSet.next())
                    System.out.println(" " + genreSet.getInt("id"));
                else {
                    System.out.println();

                    String insertGenre = "INSERT INTO genres(name) VALUES(?);";
                    PreparedStatement insertStatement = connection.prepareStatement(insertGenre);

                    insertStatement.setString(1, genre);
                    insertStatement.execute();
                }
            }catch(Exception genreE){}
        }
    }

    private String processGenres(String genre) throws SQLException
    {
        String tempGenre = "None";

        if(genre.toLowerCase().equals("docu"))
            tempGenre = "Documentary";
        else if(genre.toLowerCase().equals("comd"))
            tempGenre = "Comedy";
        else if(genre.toLowerCase().equals("dram"))
            tempGenre = "Drama";
        else if(genre.toLowerCase().equals("horr"))
            tempGenre = "Horror";
        else if(genre.toLowerCase().equals("susp"))
            tempGenre = "Thriller";
        else if(genre.toLowerCase().equals("west"))
            tempGenre = "Western";
        else if(genre.toLowerCase().equals("s.f."))
            tempGenre = "Sci-Fi";
        else if(genre.toLowerCase().equals("advt"))
            tempGenre = "Adventure";
        else if(genre.toLowerCase().equals("myst"))
            tempGenre = "Mystery";
        else if(genre.toLowerCase().equals("tv"))
            tempGenre = "TV-show";
        else if(genre.toLowerCase().equals("tvs"))
            tempGenre = "TV-series";
        else if(genre.toLowerCase().equals("biop"))
            tempGenre = "Bio-Pic";
        else if(genre.toLowerCase().equals("noir"))
            tempGenre = "Black";
        else if(genre.toLowerCase().equals("porn"))
            tempGenre = "Pornography";
        else if(genre.toLowerCase().equals("cnr"))
            tempGenre = "Cops/Robbers";
        else if(genre.toLowerCase().equals("romt"))
            tempGenre = "Romantic";
        else if(genre.toLowerCase().equals("tvm"))
            tempGenre = "TV-miniseries";
        else if(genre.toLowerCase().equals("musc"))
            tempGenre = "Musical";
        else if(genre.toLowerCase().equals("actn"))
            tempGenre = "Action";

        return tempGenre;
    }

    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    private int getIntValue(Element ele, String tagName) {
        int num = -1;
        try{
            num = Integer.parseInt(getTextValue(ele, tagName));
        }
        catch(Exception e){
            return num;
        }
        return num;
    }

    public static void main(String[] args) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");

        Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                Parameters.username, Parameters.password);

        XMLParser dpe = new XMLParser();

        Timestamp timeStart = new Timestamp(System.currentTimeMillis());
        dpe.parseXmlFile();
        dpe.parseGenres(connection);
        dpe.parseMovie(connection);
        dpe.parseStars(connection);
        String starMovieErr = dpe.parseStarMovie(connection);

        Timestamp timeEnd = new Timestamp(System.currentTimeMillis());

        System.out.println();
        System.out.println("Parse started at: " + timeStart);
        System.out.println("Parse ended at: " + timeEnd);


        //BufferedWriter writer = new BufferedWriter(new FileWriter("errors.txt"));
        //writer.write(starMovieErr);

        //writer.close();
        connection.close();
    }

}
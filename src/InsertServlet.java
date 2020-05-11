import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

@WebServlet(name = "InsertServlet", urlPatterns = "/api/insert")
public class InsertServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String insertType = request.getParameter("insertType");

        String title = request.getParameter("title");
        String releaseYear = request.getParameter("releaseYear");
        String director = request.getParameter("director");
        String starName = request.getParameter("name");
        String genre = request.getParameter("genre");

        String stringBirthYear = "";
        int birthYear = 0;

        try {
            Connection dbcon = dataSource.getConnection();
            String maxStarIdQuery = "SELECT max(id) as 'max' FROM stars;";
            PreparedStatement starIdStatement = dbcon.prepareStatement(maxStarIdQuery);
            ResultSet getMaxStarId = starIdStatement.executeQuery();

            if(insertType.equals("star"))
            {
                stringBirthYear = request.getParameter("birthYear");
                birthYear = Integer.parseInt(stringBirthYear);

                if(getMaxStarId.next()) {
                    int maxStarIdNum = Integer.parseInt(getMaxStarId.getString("max").substring(2));
                    maxStarIdNum++;
                    String insertId = "nm" + maxStarIdNum;

                    String insertStar = "INSERT INTO stars" + "(id, name, birthYear) VALUES(?, ?, ?);";

                    PreparedStatement insertStatement = dbcon.prepareStatement(insertStar);
                    insertStatement.setString(1, insertId);
                    insertStatement.setString(2, starName);
                    if(birthYear == 0)
                        insertStatement.setString(3, null);
                    else
                        insertStatement.setInt(3, birthYear);

                    insertStatement.execute();
                    out.write(insertId);
                }
            }
            else if(insertType.equals("movie"))
            {
                String maxMovieIdQuery = "SELECT max(id) as 'max' FROM movies;";
                PreparedStatement maxMovieIdStatement = dbcon.prepareStatement(maxMovieIdQuery);
                ResultSet getMaxMovieId = maxMovieIdStatement.executeQuery();

                String insertStarId = "nm";

                if(getMaxStarId.next())
                {
                    int maxStarIdNum = Integer.parseInt(getMaxStarId.getString("max").substring(2));
                    maxStarIdNum++;
                    insertStarId += "" + maxStarIdNum;
                }

                if(getMaxMovieId.next())
                {
                    int maxMovieIdNum = Integer.parseInt(getMaxMovieId.getString("max").substring(3));
                    maxMovieIdNum++;

                    String insertMovieId = "tt0" + maxMovieIdNum;

                    String insertQuery = "CALL add_movie(?, ?, ? ,? ,? ,? ,?);";
                    PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
                    insertStatement.setString(1, title);
                    insertStatement.setString(2, releaseYear);
                    insertStatement.setString(3, director);
                    insertStatement.setString(4, genre);
                    insertStatement.setString(5, starName);
                    insertStatement.setString(6, insertMovieId);
                    insertStatement.setString(7, insertStarId);

                    ResultSet output = insertStatement.executeQuery();

                    Vector<String> outputVec = new Vector<String>();

                    while(output.next())
                        outputVec.add(output.getString("output"));

                    String outputStr = outputVec.get(0);
                    for(int i = 1; i < outputVec.size(); i++)
                        outputStr += "|" + outputVec.get(i);

                    out.write(outputStr);
                }
            }
            dbcon.close();
        } catch (SQLException e) {
            e.printStackTrace();
            out.close();
        }
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();


            String query1 = "select * from customers where email ='" + username + "'";
            ResultSet rs1 = statement.executeQuery(query1);


            if(rs1.next())
            {
                String email = rs1.getString("email");
                String pass = rs1.getString("password");
                String id = rs1.getString("id");
                JsonObject responseJsonObject = new JsonObject();
                if (username.equals(email) && password.equals(pass)) {
                    User newUser = new User(username, id);
                    request.getSession().setAttribute("user", newUser); //!!!IMPORTANT USER CLASS
                    request.getSession().setAttribute("custSessionId", newUser.getId());

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                } else {
                    responseJsonObject.addProperty("status", "fail");
                    if (!username.equals(email))
                    {
                        responseJsonObject.addProperty("message", "User " + username + " does not exist.");
                    }
                    else {
                        responseJsonObject.addProperty("message", "Invalid password, please try again.");
                    }
                }
                response.getWriter().write(responseJsonObject.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

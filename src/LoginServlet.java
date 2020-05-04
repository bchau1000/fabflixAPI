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
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.PreparedStatement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("captchaStatus", "fail");
                out.write(responseJsonObject.toString());
                out.close();
                return;
            }
            Connection dbcon = dataSource.getConnection();

            String query1 = "select * from customers where email = ?";
            PreparedStatement statement = dbcon.prepareStatement(query1);
            statement.setString(1, username);
            ResultSet rs1 = statement.executeQuery();

            if(rs1.next())
            {
                String email = rs1.getString("email");
                String pass = rs1.getString("password");
                String id = rs1.getString("id");
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("captchaStatus", "success");
                if (username.equals(email) && password.equals(pass)) {
                    User newUser = new User(username, id);
                    request.getSession().setAttribute("user", newUser); //!!!IMPORTANT USER CLASS
                    request.getSession().setAttribute("custSessionId", newUser.getId());

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                } else {
                    responseJsonObject.addProperty("status", "fail");

                    if (!username.equals(email))
                        responseJsonObject.addProperty("message", "User " + username + " does not exist.");
                    else
                        responseJsonObject.addProperty("message", "Invalid password, please try again.");
                }
                response.getWriter().write(responseJsonObject.toString());
                dbcon.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

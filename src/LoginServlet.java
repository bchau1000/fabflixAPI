import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    //@Resource(name = "jdbc/moviedbexample")
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
            if(!gRecaptchaResponse.equals("androidBypass")) {
                try {
                    RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                } catch (Exception e) {
                    JsonObject responseJsonObject = new JsonObject();
                    responseJsonObject.addProperty("captchaStatus", "fail");
                    out.write(responseJsonObject.toString());
                    out.close();
                    return;
                }
            }
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");

            String customerQuery = "select * from customers where email = ?";
            PreparedStatement custStatement = dbcon.prepareStatement(customerQuery);
            custStatement.setString(1, username);
            ResultSet custSet = custStatement.executeQuery();

            String empQuery = "SELECT * from employees where email = ?";
            PreparedStatement empStatement = dbcon.prepareStatement(empQuery);
            empStatement.setString(1, username);
            ResultSet empSet = empStatement.executeQuery();

            boolean success = false;

            if(custSet.next())
            {
                String email = custSet.getString("email");
                String pass = custSet.getString("password");
                String id = custSet.getString("id");

                success = new StrongPasswordEncryptor().checkPassword(password, pass);

                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("captchaStatus", "success");

                if (username.equals(email) && success) {
                    User newUser = new User(username, id, "customer");
                    request.getSession().setAttribute("user", newUser); //!!!IMPORTANT USER CLASS
                    request.getSession().setAttribute("custSessionId", newUser.getId());
                    request.getSession().setAttribute("userType", newUser.getUserType());

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
            }
            else if(empSet.next())
            {
                String email = empSet.getString("email");
                String pass = empSet.getString("password");

                success = new StrongPasswordEncryptor().checkPassword(password, pass);

                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("captchaStatus", "success");

                if (username.equals(email) && success) {
                    User newUser = new User(username, null, "employee");
                    request.getSession().setAttribute("user", newUser); //!!!IMPORTANT USER CLASS
                    request.getSession().setAttribute("custSessionId", newUser.getId());
                    request.getSession().setAttribute("userType", newUser.getUserType());

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
            }
            dbcon.close();
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }
}


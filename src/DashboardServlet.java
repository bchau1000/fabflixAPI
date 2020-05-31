import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            HttpSession session = request.getSession();

            String tableQuery = "show full tables where Table_Type = 'BASE TABLE';";
            PreparedStatement tableStatement = dbcon.prepareStatement(tableQuery);

            ResultSet tableSet = tableStatement.executeQuery();

            JsonArray arr = new JsonArray();
            while(tableSet.next())
            {
                String table_name = tableSet.getString("Tables_in_moviedb");
                JsonObject object = new JsonObject();
                object.addProperty("table_name", table_name);

                String dataQuery = "DESCRIBE " + table_name + ";";
                PreparedStatement dataStatement = dbcon.prepareStatement(dataQuery);
                ResultSet dataSet = dataStatement.executeQuery();

                Vector<String> attributeVec = new Vector<String>();

                while(dataSet.next())
                {
                    String attribute = dataSet.getString("Field");
                    String type = dataSet.getString("Type");

                    attributeVec.addElement(attribute + ", " + type);
                }
                object.addProperty("table_attributes", attributeVec.toString());
                arr.add(object);
            }

            JsonObject object = new JsonObject();
            object.addProperty("userType", "" + session.getAttribute("userType"));
            arr.add(object);

            out.write(arr.toString());

            dbcon.close();
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
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
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(name = "CheckoutPageServlet", urlPatterns = "/api/checkoutpage")
public class CheckoutPageServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String cardNum = request.getParameter("cardnum");
        String expDate = request.getParameter("expdate");

        PrintWriter out = response.getWriter();
        if(!firstName.isEmpty() && !cardNum.isEmpty() && !lastName.isEmpty() && !expDate.isEmpty()) {
            try {
                Connection dbcon = dataSource.getConnection();
                Statement statement1 = dbcon.createStatement();

                String cardQuery = "SELECT * \n" +
                        "FROM creditcards \n" +
                        "WHERE id = '" + cardNum + "'\n" +
                        "AND expiration = '" + expDate + "';";

                ResultSet cardSet = statement1.executeQuery(cardQuery);
                String card = "";
                String exp = "";

                while (cardSet.next()) {
                    card = cardSet.getString("id");
                    exp = cardSet.getString("expiration");
                }

                if (!card.isEmpty() && !exp.isEmpty()) {
                    if(previousItems != null && !previousItems.isEmpty()) {


                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date dateobj = new Date();
                        String newSale = "";
                        String custId = "" + request.getSession().getAttribute("custSessionId");

                        for(int i = 0; i < previousItems.size() - 2; i += 3)
                        {
                            int count = Integer.parseInt(previousItems.get(i+2));

                            for(int j = 0; j < count; j++) {
                                newSale = "insert into sales (customerId, movieId, saleDate)"
                                        + "values (?, ?, ?);";
                                PreparedStatement insertRow = dbcon.prepareStatement(newSale);
                                insertRow.setInt(1, Integer.parseInt(custId));
                                insertRow.setString(2, previousItems.get(i));
                                insertRow.setString(3, df.format(dateobj));

                                insertRow.execute();
                            }
                        }
                        previousItems.clear();
                        out.write("order_success");
                    }
                    else
                        out.write("empty_cart");
                }
                else
                    out.write("invalid_info");


                response.setStatus(200);
                cardSet.close();
                statement1.close();
                dbcon.close();
            } catch (Exception e) {
                out.write(e.getMessage());
                response.setStatus(500);
            }
        }
        else
        {
            out.write("invalid_info");
        }
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

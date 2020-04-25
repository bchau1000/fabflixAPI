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
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.ResultSet;

@WebServlet(name = "ShoppingListServlet", urlPatterns = "/api/shoppinglist")
public class ShoppingListServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedbexample")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("item");
        String title = request.getParameter("title");
        String oper = request.getParameter("op");

        System.out.println(item);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        if(!item.isEmpty()) {
            if (previousItems == null) {
                previousItems = new ArrayList<>();
                previousItems.add(item);
                previousItems.add(title);
                session.setAttribute("previousItems", previousItems);
            } else {
                synchronized (previousItems) {
                    if(oper.equals("add")) {
                        previousItems.add(item);
                        previousItems.add(title);
                    }
                    else if(oper.equals("rem"))
                    {
                        int i = 0;
                        boolean found = false;
                        int size = previousItems.size() - 1;
                        while(!found && i < size)
                        {
                            if(previousItems.get(i).equals(item)) {
                                previousItems.remove(i);
                                previousItems.remove(i);
                                found = true;
                            }
                            i+=2;
                        }
                    }
                }
            }
        }

        response.getWriter().write(String.join("|", previousItems));
    }
}

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;

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

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        if(!item.isEmpty()) {
            if (previousItems == null) {
                previousItems = new ArrayList<>();
                previousItems.add(item);
                previousItems.add(title);
                previousItems.add("1");
                session.setAttribute("previousItems", previousItems);
            } else {
                synchronized (previousItems) {
                    int i = 0;
                    boolean found = false;
                    int size = previousItems.size() - 2;

                    while(!found && i < size)
                    {
                        if (previousItems.get(i).equals(item))
                            found = true;
                        else
                            i += 3;
                    }
                    if(oper.equals("add")) {
                        if(found)
                        {
                            int count = Integer.parseInt(previousItems.get(i + 2));
                            count++;

                            previousItems.set(i + 2, "" + count + "");
                        }
                        else
                        {
                            previousItems.add(item);
                            previousItems.add(title);
                            previousItems.add("1");
                        }
                    }
                    else if(oper.equals("rem"))
                    {
                        if(found)
                        {
                            int count = Integer.parseInt(previousItems.get(i + 2));
                            if(count > 1)
                            {
                                count--;
                                previousItems.set(i + 2, "" + count + "");
                            }
                            else
                            {
                                previousItems.remove(i);
                                previousItems.remove(i);
                                previousItems.remove(i);
                            }
                        }
                    }
                    else if(oper.equals("del"))
                    {
                        if(found)
                        {
                            previousItems.remove(i);
                            previousItems.remove(i);
                            previousItems.remove(i);
                        }
                    }
                }
            }
        }
        response.getWriter().write(String.join("|", previousItems));
    }
}

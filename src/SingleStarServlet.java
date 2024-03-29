import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		String id = request.getParameter("id");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();

		String currentURL = session.getAttribute("currentURL") + "";

		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
			Connection dbcon = ds.getConnection();

			String query = "SELECT s.id as 'starId', s.name as 'name', IFNULL(s.birthYear, 'N/A') as 'birthYear', " +
					"m.id as 'movieId', m.title as 'title', m.year as 'year', m.director as 'director'" +
					"from stars as s, stars_in_movies as sim, movies as m " +
					"where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();

			JsonObject urlObject = new JsonObject();
			urlObject.addProperty("currentURL", currentURL);
			jsonArray.add(urlObject);

			while (rs.next()) {

				String starId = rs.getString("starId");
				String starName = rs.getString("name");
				String starDob = rs.getString("birthYear");

				String movieId = rs.getString("movieId");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("star_id", starId);
				jsonObject.addProperty("star_name", starName);
				jsonObject.addProperty("star_dob", starDob);
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);

				jsonArray.add(jsonObject);
			}

			out.write(jsonArray.toString());
			response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			response.setStatus(500);
		}
		out.close();

	}

}

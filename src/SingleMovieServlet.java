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

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	@Resource(name = "jdbc/moviedbexample")
	private DataSource dataSource;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		HttpSession session = request.getSession();

		String id = request.getParameter("id");
		PrintWriter out = response.getWriter();

		String currentURL = session.getAttribute("currentURL") + "";

		try {
			Connection dbcon = dataSource.getConnection();
			String query = "SELECT * FROM singlemovie WHERE id = ?;";

			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();

			JsonObject urlObject = new JsonObject();
			urlObject.addProperty("currentURL", currentURL);
			jsonArray.add(urlObject);

			while (rs.next()) {
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieDir = rs.getString("director");
				Integer movieYear = rs.getInt("year");
				String starName = rs.getString("name");
				Double movieRating = rs.getDouble("rating");
				String movieGenre = rs.getString("genres");
				String starId = rs.getString("starId");

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_dir", movieDir);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_rating", movieRating);
				jsonObject.addProperty("movie_genre", movieGenre);
				jsonObject.addProperty("star_id", starId);
				jsonObject.addProperty("star_name", starName);

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

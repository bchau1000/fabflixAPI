package edu.uci.ics.fabflixmobile;

public class Movie {
    private String id;
    private String title;
    private short year;
    private String director;

    public Movie(String id, String name, short year, String director) {
        this.title = name;
        this.year = year;
    }

    public String getId() { return id; }

    public String getTitle() {
        return title;
    }

    public short getYear() {
        return year;
    }

    public String getDirector() { return director; }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                '}';
    }
}
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies
(
	id varchar(10) PRIMARY KEY,
    title varchar(100) NOT NULL,
    year integer NOT NULL,
    director varchar(100) NOT NULL
);

CREATE TABLE stars
(
	id varchar(10) PRIMARY KEY,
    name varchar(100) NOT NULL,
    birthYear integer
);

CREATE TABLE stars_in_movies
(
	starId varchar(10) NOT NULL,
    movieID varchar(10) NOT NULL,
    FOREIGN KEY(starID) REFERENCES stars(id),
    FOREIGN KEY(movieID) REFERENCES movies(id)
);

CREATE TABLE genres
(
	id integer PRIMARY KEY AUTO_INCREMENT,
    name varchar(32) NOT NULL
);

CREATE TABLE genres_in_movies
(
	genreId integer NOT NULL,
    movieId varchar(10) NOT NULL,
    FOREIGN KEY(genreId) REFERENCES genres(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards
(
	id varchar(20) PRIMARY KEY,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
);

CREATE TABLE customers
(
	id integer PRIMARY KEY AUTO_INCREMENT,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    ccId varchar(20) NOT NULL,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(20) NOT NULL,
    FOREIGN KEY(ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales
(
	id integer PRIMARY KEY AUTO_INCREMENT,
    customerId integer NOT NULL,
    movieId varchar(10) NOT NULL,
    saleDate date NOT NULL,
    FOREIGN KEY(customerId) REFERENCES customers(id),
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

CREATE TABLE ratings
(
	movieId varchar(10) NOT NULL,
    rating float NOT NULL,
    numVotes integer NOT NULL,
    FOREIGN KEY(movieId) REFERENCES movies(id)
);

DROP VIEW IF EXISTS starsInMovies;
CREATE VIEW starsInMovies as
    SELECT m.id, m.title, m.year, m.director, s.name, s.id as 'starId'
    FROM stars s JOIN stars_in_movies sim JOIN movies m
    WHERE s.id = sim.starId and m.id = sim.movieId
    ORDER BY m.title;

DROP VIEW IF EXISTS avgRatings;
CREATE VIEW avgRatings as
    SELECT m.id, m.title, FORMAT(AVG(r.rating), 1) as 'rating'
    FROM ratings r JOIN movies m
    WHERE r.movieId = m.Id
    GROUP BY m.Id
    ORDER BY rating DESC;

DROP VIEW IF EXISTS genreView; 
CREATE VIEW genreView as
    SELECT GROUP_CONCAT(DISTINCT g.name SEPARATOR ', ') as 'genres', gin.movieId
    FROM genres g JOIN genres_in_movies gin
    WHERE gin.genreId= g.id
    GROUP BY gin.movieId;
    

DROP VIEW IF EXISTS singleMovie;
CREATE VIEW singleMovie as
    SELECT *
    FROM starsInMovies NATURAL JOIN avgRatings JOIN genreView
    WHERE id = genreView.movieId;
    
DROP VIEW IF EXISTS movielist;
CREATE VIEW movielist AS
	SELECT m.id as 'id', m.title as 'title', m.year as 'year', m.director as 'director', FORMAT(AVG(r.rating),1) 'rating',
		SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name SEPARATOR ', '), ',', 3) 'genre',
		SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name SEPARATOR ', '), ',', 3) 'stars',
		SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id SEPARATOR ', '), ',', 3) 'starsId'
	FROM movies m JOIN ratings r JOIN genres_in_movies gin
		JOIN genres g JOIN stars_in_movies sim JOIN stars s
	WHERE m.id = r.movieId AND sim.starId = s.id AND sim.movieID = m.id
		AND g.id = gin.genreId AND gin.movieId = m.id
	GROUP BY r.movieId
	ORDER BY AVG(r.rating) DESC;
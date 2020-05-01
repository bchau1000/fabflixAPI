CREATE DATABASE moviedb;
USE moviedb;

SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

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

DROP VIEW IF EXISTS starringcount;
	CREATE VIEW starringcount AS
	SELECT s.name as 'name', s.id as 'starIdCount', COUNT(sim.starId) as 'count'
	FROM stars AS s JOIN stars_in_movies AS sim
	WHERE s.id = sim.starId
	GROUP BY s.id
	ORDER BY COUNT(sim.starId) DESC, s.name ASC;

DROP VIEW IF EXISTS avgratings;
CREATE VIEW avgratings as
    SELECT m.id, m.title, FORMAT(AVG(r.rating), 1) as 'rating'
    FROM ratings r JOIN movies m
    WHERE r.movieId = m.Id
    GROUP BY m.Id
    ORDER BY rating DESC;

DROP VIEW IF EXISTS genreview; 
CREATE VIEW genreview as
    SELECT GROUP_CONCAT(DISTINCT g.name SEPARATOR ', ') as 'genres', gin.movieId
    FROM genres g JOIN genres_in_movies gin
    WHERE gin.genreId= g.id
    GROUP BY gin.movieId;

DROP VIEW IF EXISTS singlemovie;
	CREATE VIEW singlemovie AS
	SELECT m.id as 'id', m.title as 'title', m.year as 'year', gw.genres as 'genres', 
		ar.rating as 'rating', sc.name as 'name', sim.starId as 'starId', m.director as 'director', sc.count as 'count'
	FROM movies as m JOIN stars_in_movies sim JOIN starringCount as sc JOIN genreview as gw JOIN avgratings as ar
	WHERE m.id = sim.movieId AND sim.starId = sc.starIdCount AND gw.movieId = m.id AND ar.id = m.id
	ORDER BY count DESC, name ASC;

DROP VIEW IF EXISTS starsInMovies;
CREATE VIEW starsInMovies as
    SELECT m.id, m.title, m.year, m.director, s.name, s.id as 'starId'
    FROM stars s JOIN stars_in_movies sim JOIN movies m
    WHERE s.id = sim.starId and m.id = sim.movieId
    ORDER BY m.title;

DROP VIEW IF EXISTS movie_and_rating;
CREATE VIEW movie_and_rating AS
	SELECT id as 'movieId', title, year, director, IFNULL(rating, 0) as 'rating'
	FROM movies as m LEFT JOIN ratings as r 
    ON m.id = r.movieId;

DROP VIEW IF EXISTS movie_and_genre;
CREATE VIEW movie_and_genre AS
	select gim.movieId, GROUP_CONCAT(g.name ORDER BY g.name SEPARATOR ', ' ) as genre
	from genres_in_movies as gim JOIN genres as g
	where gim.genreId = g.Id
    group by gim.movieId;
    
DROP VIEW IF EXISTS star_and_count;
CREATE VIEW star_and_count AS
	select starId, count(*) as 'count'
	from stars_in_movies
	group by starId
	order by count DESC;

DROP VIEW IF EXISTS movie_and_star;
CREATE VIEW movie_and_star AS
	select sim.movieId, 
    GROUP_CONCAT(sim.starId ORDER BY count DESC SEPARATOR ', ') as 'starId', 
    GROUP_CONCAT(name ORDER BY count DESC SEPARATOR ', ') as 'name'
	from stars_in_movies as sim JOIN stars as s JOIN star_and_count as sc
	where sim.starId = s.id AND s.id = sc.starId
    GROUP BY sim.movieId;
    
DROP VIEW IF EXISTS movielist;
CREATE VIEW movielist AS
	SELECT mr.movieId, mr.title, mr.director, ms.name, mg.genre, mr.year, mr.rating
	FROM movie_and_rating as mr JOIN movie_and_genre as mg JOIN movie_and_star as ms
	WHERE mr.movieId = mg.movieId AND ms.movieId = mr.movieId;
use moviedb;

DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$
CREATE PROCEDURE add_movie(IN mTitle varchar(100), IN mYear integer, IN mDirector varchar(100), IN mGenre varchar(32), IN mStar varchar(100), newMovieId varchar(10), newStarId varchar(10))
BEGIN
	DECLARE existGenreId int;
    DECLARE existStarId varchar(10);
	DECLARE newGenreId int;
    SELECT max(id) + 1 FROM genres INTO newGenreId;
    
    DROP TABLE IF EXISTS resultTable;
    CREATE TABLE resultTable (output varchar(100));
    
	IF((select count(*) from movies where title = mTitle AND director = mDirector AND year = mYear) > 0) THEN
		insert into resultTable(output) values('Movie already exists');
	ELSE
		insert into movies(id, title, year, director) values (newMovieId, mTitle, mYear, mDirector);
        insert into resultTable(output) values(CONCAT('Inserting into movies: (', newMovieId, ', ', mTitle, ', ', mDirector, ', ', mYear, ')'));

        IF((select count(*) from genres where name = mGenre) > 0) THEN
			select id from genres where name = mGenre into existGenreId;
            
            insert into genres_in_movies(genreId, movieId) values(existGenreId, newMovieId);
            insert into resultTable(output) values(CONCAT('Genre exists, inserting into genres_in_movies: (', existGenreId, ', ', newMovieId, ')'));
		ELSE
			insert into genres(id, name) values(newGenreId, mGenre);
            insert into resultTable(output) values(CONCAT('Inserting into genres: (', newGenreId, ', ', mGenre, ')'));
            
			insert into genres_in_movies(genreId, movieId) values(newGenreId, newMovieId);
            insert into resultTable(output) values(CONCAT('Inserting into genres_in_movies: (', newGenreId, ', ', newMovieId, ')'));
            
        END IF;
        
        IF((select count(*) from stars where name = mStar) > 0) THEN
			select id from stars where name = mStar INTO existStarId;
            
            insert into stars_in_movies(starId, movieId) values(existStarId, newMovieId);
            insert into resultTable(output) values(CONCAT('Star exists, inserting into stars_in_movies: (', existStarId, ', ', newMovieId, ')'));
		ELSE
			insert into stars(id, name) values(newStarId, mStar);
            insert into resultTable(output) values(CONCAT('Inserting into stars: (', newStarId, ', ', mStar, ')'));
            
            insert into stars_in_movies(starId, movieId) values(newStarId, newMovieId);
            insert into resultTable(output) values(CONCAT('Inserting into stars_in_movies: (', newStarId, ', ', newMovieId, ')'));
		END IF;
	END IF;
    
    select * from resultTable;
    DROP TABLE IF EXISTS resultTable;
END
$$
DELIMITER ;

# TESTING FUNCTIONS

# SET SQL_SAFE_UPDATES = 0;
# CALL add_movie('Test Movie', '2020', 'Test Director', 'Psychological', 'Test Star', 'tt0499470', 'nm9423081');

# SELECT * FROM movies ORDER BY id DESC;
# SELECT * FROM stars ORDER BY id DESC;
# SELECT * FROM genres ORDER BY id DESC;

# SELECT * FROM stars_in_movies ORDER BY movieId DESC;
# SELECT * FROM genres_in_movies ORDER BY movieId DESC;

# SELECT * FROM movielist ORDER BY id DESC;

# DELETE FROM stars_in_movies WHERE movieId > 'tt0499469';
# DELETE FROM genres_in_movies WHERE movieId > 'tt0499469';

# DELETE FROM movies WHERE id > 'tt0499469';
# DELETE FROM stars WHERE id > 'nm9423080';
# DELETE FROM genres WHERE id > '23';

# SELECT * from genres;
# SELECT * from stars;
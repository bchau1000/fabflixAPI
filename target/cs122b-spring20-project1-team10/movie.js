function handleMovieResult(resultData) {
    console.log("handleStarResult: populating movie table from resultData");
    let movieTableBodyElement = jQuery("#movie_table_body");

    for (let i = 0; i < resultData.length; i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_genre"] + "</th>";
        rowHTML += "<th>" + '<a href="single-star.html?id=' + resultData[i]['star_id0'] + '">' + resultData[i]["star_name0"] + '</a>' + ", "
                          + '<a href="single-star.html?id=' + resultData[i]['star_id1'] + '">' + resultData[i]["star_name1"] + '</a>' + ", "
                          + '<a href="single-star.html?id=' + resultData[i]['star_id2'] + '">' + resultData[i]["star_name2"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movies",
    success: (resultData) => handleMovieResult(resultData)
});
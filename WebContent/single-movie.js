function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {
    console.log("handleResult: populating movie info from resultData");
    let movieInfoTitle = jQuery("#movie_title");
    movieInfoTitle.append(resultData[0]["movie_title"])

    let movieInfoElement = jQuery("#movie_info");

    let addButton = jQuery("#addbutton");
    addButton.append('<input class = "btn btn-secondary" type="button" onClick="handleCart(\'' + resultData[0]["movie_id"] + '\', \'' + resultData[0]['movie_title']+ '\')" value = "Add to Cart" />');

    movieInfoElement.append(
        "<p>Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Genre: " + resultData[0]["movie_genre"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_dir"] + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>"
    );

    console.log("handleResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#single_movie_table_body");


    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">' + resultData[i]["star_name"] + '</a>' + "</th>";
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
}


function handleCart(retrieveId, retrieveTitle)
{
    alert('Added ' + retrieveTitle + " to your cart.");
    $.ajax("api/shoppinglist", {
        method: "POST",
        data:"item=" + retrieveId + "&title=" + retrieveTitle +"&op=add"
    });
}

let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleResult(resultData)
});
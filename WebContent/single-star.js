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
    handleNavBar(resultData[0]["currentURL"]);

    let starInfoName = jQuery("#star_info_name");
    starInfoName.append(resultData[1]["star_name"]);

    console.log("handleResult: populating star info from resultData");
    let starInfoElement = jQuery("#star_info");

    starInfoElement.append("<p>Name: " + resultData[1]["star_name"] + "</p>" +
        "<p>Date Of Birth: " + resultData[1]["star_dob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");

    for (let i = 1; i < resultData.length ; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "</tr>";

        movieTableBodyElement.append(rowHTML);
    }
}

function handleNavBar(currentURL)
{
    let list_jump = jQuery("#list_jump");
    list_jump.append('<a class="nav-link" href = "' + currentURL + '">Movie List</a>');
}

let starId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleResult(resultData)
});
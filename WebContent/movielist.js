function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

let title = getParameterByName('title');
let director = getParameterByName('director');
let starName = getParameterByName('star');
let genreName = getParameterByName('genre');
let year = getParameterByName('year');
let page = getParameterByName('page');

function handleListResult(resultData)
{
    let genre_table_body = jQuery("#list_table_body");
    let page_num1 = jQuery("#page_num1");
    let page_num2 = jQuery("#page_num2");
    let pageCount = Math.ceil(parseInt(resultData[0]["query_count"], 10)/100);

    for(let i = 1; i < resultData.length; i++)
    {
        let genres = resultData[i]["genre_name"].split(", ");
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_dir"] + "</th>";
        rowHTML += "<th>" + '<a href="single-star.html?id=' + resultData[i]['star_id0'] + '">' + resultData[i]["star_name0"] + '</a>' + ", "
            + '<a href="single-star.html?id=' + resultData[i]['star_id1'] + '">' + resultData[i]["star_name1"] + '</a>' + ", "
            + '<a href="single-star.html?id=' + resultData[i]['star_id2'] + '">' + resultData[i]["star_name2"] + '</a>' + "</th>";

        rowHTML += "<th>";
        rowHTML += '<a href="movielist.html?title=&genre=' + genres[0] + '&page=1' + '">' + genres[0] + '</a>';
        for(let i = 1; i < genres.length; i++)
            rowHTML += '<a href="movielist.html?title=&genre=' + genres[i] + '&page=1' + '">, ' + genres[i] + '</a>';
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["movie_yr"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        genre_table_body.append(rowHTML);
    }

    let pageLinks = "";
    let currPage = parseInt(page, 10);
    let nextPage = currPage + 1;
    let prevPage = currPage - 1;

    if(currPage > 1)
        pageLinks += "<a href="
            + "movielist.html?title=" + title
            +  "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year + "&page=" + prevPage + ">"
            + "&lt;Prev" + " </a> ";
    else
        pageLinks += "<a href="
            + "movielist.html?title=" + title
            +  "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year + "&page=" + currPage + ">"
            + "&lt;Prev" + " </a> ";

    if(currPage < pageCount)
        pageLinks += "<a href="
            + "movielist.html?title=" + title
            +  "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year + "&page=" + nextPage + ">"
            + "Next&gt;" + " </a> ";
    else
        pageLinks += "<a href="
            + "movielist.html?title=" + title
            +  "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year + "&page=" + currPage + ">"
            + "Next&gt;"
            + " </a> ";



    page_num1.append(pageLinks);
    page_num2.append(pageLinks);
}
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movielist?title=" + title +  "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year + "&page=" + page,
    success: (resultData) => handleListResult(resultData)
});
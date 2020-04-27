function getParameterByName(target) {
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

// Grab all URL parameters
let title = getParameterByName('title');
let director = getParameterByName('director');
let starName = getParameterByName('star');
let genreName = getParameterByName('genre');
let year = getParameterByName('year');
let page = getParameterByName('page');
let resultCount = getParameterByName('count');
let sort1 = getParameterByName('sort1');
let sort2 = getParameterByName('sort2');

// Generates the table based on query results
function handleListResult(resultData)
{
    let genre_table_body = jQuery("#list_table_body");
    let pageCount = Math.ceil(parseInt(resultData[0]["query_count"], 10)/resultCount);

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
        rowHTML += "<a href=" + "movielist.html?title=&director=&star=&genre="+ genres[0] + "&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + genres[0] + "</a>";
        for(let i = 1; i < genres.length; i++)
            rowHTML += "<a href=" + "movielist.html?title=&director=&star=&genre="+ genres[i] + "&year=&page=1&count=50&sort1=ratingD&sort2=titleA>, " + genres[i] + "</a>";
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData[i]["movie_yr"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += '<th><input type="button" onClick="handleCart(\'' + resultData[i]["movie_id"] + '\', \'' + resultData[i]['movie_title']+ '\')" value = "Add" /></th>'

        rowHTML += "</tr>";

        genre_table_body.append(rowHTML);
    }

    handlePageCount(pageCount);
}

// Creates prev/next buttons
function handlePageCount(pageCount)
{
    let currPage = parseInt(page, 10);
    let pageLinks = "";

    if(currPage > 1)
        pageLinks += "<a class = \"btn btn-secondary btn-sm\" href="
            + "movielist.html?title=" + title
            + "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year
            + "&page=" + (currPage - 1) + "&count=" + resultCount + "&sort1=" + sort1 + "&sort2=" + sort2 + ">"
            + "&lt;Prev " + " </a> ";
    else
        pageLinks += "<a class = \"btn btn-secondary btn-sm\">" + "&lt;Prev " + " </a> " ;

    if(currPage < pageCount)
        pageLinks += "<a class = \"btn btn-secondary btn-sm\" href="
            + "movielist.html?title=" + title
            + "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year
            + "&page=" + (currPage + 1) + "&count=" + resultCount + "&sort1=" + sort1 + "&sort2=" + sort2 + ">"
            + "Next&gt;" + " </a> ";
    else
        pageLinks += "<a class = \"btn btn-secondary btn-sm\" >" + "Next&gt;" + " </a> " ;

    jQuery("#page_top").append(pageLinks);
    jQuery("#page_bottom").append(pageLinks);
}

function handleCart(retrieveId, retrieveTitle)
{
    alert('Added ' + retrieveTitle + " to your cart.");
    $.ajax("api/shoppinglist", {
        method: "POST",
        data:"item=" + retrieveId + "&title=" + retrieveTitle +"&op=add"
    });
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/movielist?title=" + title +  "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year
        + "&page=" + page + "&count=" + resultCount + "&sort1=" + sort1 + "&sort2=" + sort2,
    success: (resultData) => handleListResult(resultData)
});
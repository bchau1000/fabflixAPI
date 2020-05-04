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
let resultCount = getParameterByName('count');
let sort1 = getParameterByName('sort1');
let sort2 = getParameterByName('sort2');

function handleListResult(resultData)
{
    let genre_table_body = jQuery("#list_table_body");
    if(resultData == "")
        window.history.back();
    else {
        handlePageCount(parseInt(resultData[0]["query_count"]));
        handleSort();

        for (let i = 1; i < resultData.length; i++) {
            let genres = resultData[i]["genre_name"].split(", ");
            let starIds = resultData[i]["star_id"].split(", ");
            let starNames = resultData[i]["star_names"].split(", ");

            let starsLength = 0;
            if(starIds.length < 3)
                starsLength = starIds.length;
            else
                starsLength = 3;

            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>' + "</th>";
            rowHTML += "<th>" + resultData[i]["movie_dir"] + "</th>";

            rowHTML += "<th>";
            rowHTML += '<a href = "single-star.html?id=' + starIds[0] + '">' + starNames[0] + '</a>';
            for(let i = 1; i < starsLength; i++)
                rowHTML += '<a href = "single-star.html?id=' + starIds[i] + '">, ' + starNames[i] + '</a>';
            rowHTML += '</th>';

            rowHTML += "<th>";
            rowHTML += "<a href=" + "movielist.html?title=&director=&star=&genre=" + genres[0] + "&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + genres[0] + "</a>";
            for (let i = 1; i < genres.length; i++)
                rowHTML += "<a href=" + "movielist.html?title=&director=&star=&genre=" + genres[i] + "&year=&page=1&count=50&sort1=ratingD&sort2=titleA>, " + genres[i] + "</a>";
            rowHTML += "</th>";

            rowHTML += "<th>" + resultData[i]["movie_yr"] + "</th>";
            rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
            rowHTML += '<th><input type="button" onClick="handleCart(\'' + resultData[i]["movie_id"] + '\', \'' + resultData[i]['movie_title'] + '\')" value = "Add" /></th>'

            rowHTML += "</tr>";

            genre_table_body.append(rowHTML);
        }
    }
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
            + "Prev " + " </a> ";
    else
        pageLinks += "<a class = \"btn btn-secondary btn-sm\">" + "Prev " + " </a> " ;

    pageLinks += "<a class = \"btn btn-secondary btn-sm\" href="
        + "movielist.html?title=" + title
        + "&director=" + director + "&star=" + starName + "&genre=" + genreName + "&year=" + year
        + "&page=" + (currPage + 1) + "&count=" + resultCount + "&sort1=" + sort1 + "&sort2=" + sort2 + ">"
        + "Next" + " </a> ";

    jQuery("#page_top").append(pageLinks);
    jQuery("#page_bottom").append(pageLinks);
}

function handleSort()
{
    let sortBy1 = jQuery("#sort1");
    let sortBy2 = jQuery("#sort2");

    let sortOptions1 = '';
    sortOptions1 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=ratingA&sort2=' + sort2 + '">' + 'Rating (ASC)' + '</a>';
    sortOptions1 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=ratingD&sort2=' + sort2 + '">' + 'Rating (DESC)' + '</a>';
    sortOptions1 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=titleA&sort2=' + sort2 + '">' + 'Title (ASC)' + '</a>';
    sortOptions1 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=titleD&sort2=' + sort2 + '">' + 'Title (DESC)' + '</a>';


    let sortOptions2 = '';

    sortOptions2 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=' + sort1 + '&sort2=ratingA">' + 'Rating (ASC)' + '</a>';
    sortOptions2 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=' + sort1 + '&sort2=ratingD">' + 'Rating (DESC)' + '</a>';
    sortOptions2 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=' + sort1 + '&sort2=titleA">' + 'Title (ASC)' + '</a>';
    sortOptions2 += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
                    + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=' + resultCount + '&sort1=' + sort1 + '&sort2=titleD">' + 'Title (DESC)' + '</a>';

    let perPageDrop = jQuery("#perPageDrop");
    let perPageResult = jQuery("#perPageResult");
    let perPageDropOptions = "";

    perPageDropOptions += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
        + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=10&sort1=' + sort1 + '&sort2=' + sort2 + '">' + '10' + '</a>';

    perPageDropOptions += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
        + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=25&sort1=' + sort1 + '&sort2=' + sort2 + '">' + '25' + '</a>';

    perPageDropOptions += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
        + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=50&sort1=' + sort1 + '&sort2=' + sort2 + '">' + '50' + '</a>';

    perPageDropOptions += '<a class = "dropdown-item" href = "movielist.html?title=' + title + '&director=' + director + '&star='
        + starName + '&genre=' + genreName + '&year=' + year + '&page='+ page +'&count=100&sort1=' + sort1 + '&sort2=' + sort2 + '">' + '100' + '</a>';

    perPageDrop.append(resultCount);
    perPageResult.append(perPageDropOptions);

    sortBy1.append(sortOptions1);
    sortBy2.append(sortOptions2);
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

function handleJump()
{

}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star",
    data: {urlTitle: title},
    success: (resultData) => handleJump(resultData)
});
function handleGenreResult(resultData)
{
    let genre_list = jQuery("#genre_list");

    for(let i = 0; i < resultData.length; i++)
    {
        genre_list.append("<a href=" + "movielist.html?title=&director=&star=&genre=" + resultData[i]["genre_name"] + "&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + resultData[i]["genre_name"] + "</a>");
        genre_list.append("<br>");
    }


    for(let i = 0; i < 26; i++)
    {
        genre_list.append("<a href=" + "movielist.html?title=" + String.fromCharCode(65 + i) + "<&director=&star=&genre=&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + String.fromCharCode(65 + i) + "</a>");
        genre_list.append("<br>");
    }

    for(let i = 0; i < 10; i++)
    {
        genre_list.append("<a href=" + "movielist.html?title=" + i + "<&director=&star=&genre=&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + i + "</a>");
        genre_list.append("<br>");
    }

    genre_list.append("<a href=" + "movielist.html?title=" + '&gt;' + "&director=&star=&genre=&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + '*' + "</a>");
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/mainpage",
    success: (resultData) => handleGenreResult(resultData)
});
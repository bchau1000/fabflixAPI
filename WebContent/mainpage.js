function handleGenreResult(resultData)
{
    let genre_list = jQuery("#genre_list");

    for(let i = 0; i < resultData.length; i++)
    {
        genre_list.append("<a href=" + "movielist.html?title=&director=&star=&genre=" + resultData[i]["genre_name"] + "&year=&page=1>" + resultData[i]["genre_name"] + "</a>");
        genre_list.append("<br>");
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/mainpage",
    success: (resultData) => handleGenreResult(resultData)
});
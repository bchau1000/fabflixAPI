function handleGenreResult(resultData)
{
    let j = 0;
    if(resultData[resultData.length - 1]["userType"] == "employee")
        jQuery("#dashboard_link").append('<a class="nav-link" href="dashboard.html">Dashboard</a>');

    for(let i = 1; i <= 5; i++)
    {
        let col = "#genredc" + i
        let resultGenre = jQuery(col);
        let genre_result = "";

        while(j < resultData.length - 1)
        {
            genre_result += "<a class = \"dropdown-item\" href=" +
                "movielist.html?title=&director=&star=&genre=" + resultData[j]["genre_name"] +
                "&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + resultData[j]["genre_name"] + "</a>"
            j++;

            if(j != 0 && j%8 == 0) break;
        }
        resultGenre.append(genre_result)
    }

    j = 0;
    for(let i = 1; i <= 5; i++)
    {
        let col = "#titledc" + i
        let resultTitle = jQuery(col);
        let title_result = "";

        while(j < 26)
        {
            title_result += "<a class = \"dropdown-item\" href=movielist.html?title=" + String.fromCharCode(65 + j)
                + "<&director=&star=&genre=&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + String.fromCharCode(65 + j) + "</a>"

            j++;

            if(j == 26)
                title_result += "<a class = \"dropdown-item\" href=" + "movielist.html?title=" + '~' + "&director=&star=&genre=&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + '*' + "</a>";

            if(j != 0 && j%9 == 0) {
                break;
            }
        }
        resultTitle.append(title_result)
    }

    j = 0;
    for(let i = 1; i <= 2; i++)
    {
        let col = "#numdc" + i
        let resultNum = jQuery(col);
        let num_result = "";

        while(j < 10)
        {
            num_result += "<a class = \"dropdown-item\" href=" + "movielist.html?title=" + j + "<&director=&star=&genre=&year=&page=1&count=50&sort1=ratingD&sort2=titleA>" + j + "</a>";
            j++;

            if(j != 0 && j%5 == 0) break;
        }

        resultNum.append(num_result)
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/mainpage",
    success: (resultData) => handleGenreResult(resultData)
});
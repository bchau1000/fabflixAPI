let storage = window.localStorage;

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

function handleNewLookup(data, query, done)
{
    console.log("AJAX request successful: ")
    console.log(data);

    storage.setItem(query, data);
    let jsonData = JSON.parse(data);


    done({ suggestions: jsonData});
}

function handleExistingLookup(query, done)
{
    console.log("Data request to LocalStorage successful: ");
    console.log(storage.getItem(query));

    let jsonData = JSON.parse(storage.getItem(query));

    done({ suggestions: jsonData});
}

function handleNormalSearch(query) {
    console.log("Performing a normal search with: " + query);
    window.location.href = 'movielist.html?title=' + escape(query) + '&director=&star=&genre=&year=&page=1&count=50&sort1=ratingD&sort2=titleA';
}

function handleSelection(suggestion)
{
    console.log(suggestion["data"] + ", " + suggestion["value"]);
    window.location.href = "single-movie.html?id=" + suggestion["data"];
}

function handleLookup(query, done) {
    console.log("Autocomplete initiated")
    query = query.toLowerCase();

    if(storage.getItem(query) == null) {
        console.log("Sending AJAX request to SuggestionServlet")
        jQuery.ajax({
            "method": "GET",
            "url": "api/suggestion?query=" + escape(query),
            "success": function (data) {
                handleNewLookup(data, query, done)
            },
            "error": function (errorData) {
                console.log("Lookup AJAX error: ")
                console.log(errorData)
            }
        })
    }
    else
    {
        console.log("Query already cached, requesting data from LocalStorage");
        handleExistingLookup(query, done);
    }
}

$('#autocomplete').autocomplete({
    lookup: function (query, done) {
        handleLookup(query, done)
    },
    onSelect: function(suggestion) {
        handleSelection(suggestion)
    },

    deferRequestBy: 300,
    minChars: 3
});

$('#autocomplete').keypress(function(event) {
    if (event.keyCode == 13) {
        handleNormalSearch($('#autocomplete').val())
    }
})

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/mainpage",
    success: (resultData) => handleGenreResult(resultData)
});
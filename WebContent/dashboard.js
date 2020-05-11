let dashboardHeader = jQuery("#dashboard_header");
let dashboardBody = jQuery("#dashboard_table_body");
let insertForm = jQuery("#insert_form");
let statusInfo = jQuery("#status_info");

let submit = true;

function handleTableData(resultData) {
    if (resultData[resultData.length - 1]["userType"] != "employee")
        window.history.back();
    else
    {
        let col1 = jQuery("#tablecol1");
        let col2 = jQuery("#tablecol2");

        let colData = '';
        for (let i = 0; i < resultData.length - 1; i++) {

            let attributes = resultData[i]["table_attributes"].split(", ")
            colData += '<button class = "dropdown-item" onClick = displayAttributes("' + attributes + '")>' + resultData[i]["table_name"] + '</button>';

            if (i == 4) {
                col1.append(colData);
                colData = '';
            }
        }
        col2.append(colData);
    }
}

function displayAttributes(tableAttributes)
{
    emptyPage();

    let firstFix = tableAttributes.replace("[", "");
    let fixTable = firstFix.replace("]", "");

    let header = "";
    header += '<th> Attribute </th>';
    header += '<th> Type </th>';

    dashboardHeader.append(header);

    let tableArr = fixTable.split(",");
    let body = "";

    for(let i = 0; i < tableArr.length - 1; i += 2)
    {
        body += '<tr>'
        body += '<th>' + tableArr[i] + '</th>'
        body += '<th>' + tableArr[i + 1] + '</th>';
        body += '</tr>'
    }

    dashboardBody.append(body);
}

function handleStarForm()
{
    emptyPage();

    let starForm = "<label><b>Name:</b></label>\n" +
        "    <label>\n" +
        "        <input placeholder=\"Enter Star's Name\" name=\"name\" type=\"text\" onSubmit = required()>\n" +
        "    </label>\n" +
        "\n" +
        "    <br>\n" +
        "    <label><b>Birth Year:</b></label>\n" +
        "    <label>\n" +
        "        <input placeholder=\"Enter Birth Year\" name=\"birthYear\" type=\"number\">\n" +
        "    </label>\n" +
        '    <input name="insertType" value = "star" type = "hidden">' +
        "    <br>\n" +
        "    <input type = \"Submit\" value = \"Add\">";

    insertForm.append(starForm);
}

function handleMovieForm()
{
    emptyPage();

    let movieForm = '    <label><b>Title:</b></label>\n' +
        '    <label>\n' +
        '        <input placeholder="Enter Title" name="title" type="text">\n' +
        '    </label>\n' +
        '    <br>\n' +
        '    <label><b>Release Year:</b></label>\n' +
        '    <label>\n' +
        '        <input placeholder="Enter Release Year" name="releaseYear" type="number">\n' +
        '    </label>\n' +
        '    <br>\n' +
        '    <label><b>Director:</b></label>\n' +
        '    <label>\n' +
        '        <input placeholder="Enter Director" name="director" type="text">\n' +
        '    </label>\n' +
        '    <br>\n' +
        '    <label><b>Star\'s Name:</b></label>\n' +
        '    <label>\n' +
        '        <input placeholder="Enter Star\'s Name" name="name" type="text">\n' +
        '    </label>\n' +
        '    <br>\n' +
        '    <label><b>Genre:</b></label>\n' +
        '    <label>\n' +
        '        <input placeholder="Enter Genre" name="genre" type="text">\n' +
        '    </label>\n' +
        '    <input name="insertType" value = "movie" type = "hidden">' +
        '    <br>\n' +
        '    <input type = "Submit" value = "Add">';

    insertForm.append(movieForm);
}

function handleOutputInfo(resultData)
{
    if(submit == true) {
        let resultArray = resultData.split('|');

        let movieStatusInfo = '<textarea class="span6" rows="' + resultArray.length + '" style = "width: 23%" readonly >\n';

        for (let i = 0; i < resultArray.length; i++)
            movieStatusInfo += resultArray[i] + "\n";
        movieStatusInfo += '    </textarea>';

        statusInfo.html("");
        statusInfo.append(movieStatusInfo);
    }
    else
        statusInfo.html("");
}

function required()
{
    if(document.forms["insert_form"]["insertType"].value == "movie")
    {
        if(document.forms["insert_form"]["title"].value == "" || document.forms["insert_form"]["releaseYear"].value == "" ||
            document.forms["insert_form"]["director"].value == "" || document.forms["insert_form"]["genre"].value == "" ||
            document.forms["insert_form"]["name"].value == "") {
            alert("Please enter all required fields.");
            submit = false;
            return false;
        }
        else
            submit = true;
    }
    else if(document.forms["insert_form"]["insertType"].value == "star")
    {
        if(document.forms["insert_form"]["name"].value == "")
        {
            alert("Please enter all required fields.");
            submit = false;
            return false;
        }
        else
            submit = true;

        if(document.forms["insert_form"]["birthYear"].value == "")
            document.forms["insert_form"]["birthYear"].value = 0;

    }
}

function handleSubmit(submitForm) {
    console.log("Success, form submitted");

    submitForm.preventDefault();

    if (document.forms["insert_form"]["insertType"].value == "star")
    {
        $.ajax(
            "api/insert", {
                method: "POST",
                data: insertForm.serialize()
            }
        );

        if(submit)
            document.getElementById("insert_form").reset();
    }
    else if(document.forms["insert_form"]["insertType"].value == "movie")
    {
        $.ajax(
            "api/insert", {
                method: "POST",
                data: insertForm.serialize(),
                success: handleOutputInfo
            }
        );
        if(submit)
            document.getElementById("insert_form").reset();
    }
}

insertForm.submit(handleSubmit);

function emptyPage()
{
    dashboardHeader.html("");
    dashboardBody.html("");
    insertForm.html("");
    statusInfo.html("");
}

jQuery.ajax({
    dataType: "json",
    method: "POST",
    url: "api/dashboard",
    success: (resultData) => handleTableData(resultData)
});
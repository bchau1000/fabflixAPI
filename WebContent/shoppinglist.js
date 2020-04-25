let cart = jQuery("#cart_table_body");

function handleCartData(resultData)
{
    let resultArray = resultData.split('|');
    let results = "";

    if(resultArray[0] != "") {
        for (let i = 0; i < resultArray.length - 1; i+=2) {
            results += '<tr>';
            results += '<th>' + resultArray[i] + '</th>';
            results += '<th>' + resultArray[i + 1] + '</th>';
            results += '<th>' + '<input type="button" onClick="updateList(\'' + resultArray[i] + '\', \'add\',\'' + resultArray[i + 1] + '\')" value = "Add" />' + '</th>';
            results += '<th>' + '<input type="button" onClick="updateList(\'' + resultArray[i] + '\', \'rem\',\'' + resultArray[i + 1] + '\')" value = "Remove" />' + '</th>';
            results += '</tr>'
        }
    }

    cart.html("");
    cart.append(results);
}

function updateList(itemId, oper, title)
{
    $.ajax("api/shoppinglist", {
        method: "POST",
        data:"item=" + itemId + "&title=" + title + "&op=" + oper,
        success: handleCartData
    });
}

$.ajax("api/shoppinglist", {
    method: "POST",
    data:"item=&title=&op=none",
    success: handleCartData
});
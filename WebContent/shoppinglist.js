let cart = jQuery("#cart_table_body");
let checkout = jQuery("#checkout");

function handleCartData(resultData)
{
    let resultArray = resultData.split('|');
    let results = "";
    let total = 0.00;

    if(resultArray[0] != "") {
        for (let i = 0; i < resultArray.length - 2; i+=3) {
            results += '<tr>';
            results += '<th>' + resultArray[i + 1] + '</th>';
            results += '<th>' + resultArray[i + 2] + '</th>';
            results += '<th>' + '$12.00' + '</th>';
            results += '<th>' + '<input type="button" onClick="updateList(\'' + resultArray[i] + '\', \'add\',\'' + resultArray[i + 1] + '\')" value = "+" />' + '</th>';
            results += '<th>' + '<input type="button" onClick="updateList(\'' + resultArray[i] + '\', \'rem\',\'' + resultArray[i + 1] + '\')" value = "-" />' + '</th>';
            results += '<th>' + '<input type="button" onClick="updateList(\'' + resultArray[i] + '\', \'del\',\'' + resultArray[i + 1] + '\')" value = "Delete" />' + '</th>';
            results += '</tr>'

            total += parseInt(resultArray[i + 2]);
        }
        let price = total * 12;
        results += '<tr class = "table-success">';
        results += '<th>' + 'Total:' + '</th>';
        results += '<th>' + total + '</th>';
        results += '<th>' + '$' + price.toFixed(2) + '</th>';
        results += '<th></th>';
        results += '<th></th>';
        results += '<th></th>';
        results += '</tr>'
    }

    for(let i = 0; i < resultArray.length; i++)
        console.log(resultArray[i]);
    console.log("total = " + total);

    cart.html("");
    checkout.html("");
    cart.append(results);

    if(resultArray[0] != "")
        checkout.append("<input class = \"btn btn-success\" type = \"button\" value = \"Checkout\" onclick = checkoutLink() style = \"float: left;\">")
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
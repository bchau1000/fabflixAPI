let orderTable = jQuery("#order_table_body");

function handleConfirmation(resultData)
{
    let orderTableResult = "";
    for(let i = 0; i < resultData.length; i++)
    {
        orderTableResult += "<tr>";
        orderTableResult += "<th>" + resultData[i]["sale_id"] + "</th>";
        orderTableResult += "<th>" + resultData[i]["movie_title"] + "</th>";
        orderTableResult += "<th>$12.00</th>"
        orderTableResult += "<th> </th>";
        orderTableResult += "<th> </th>";
        orderTableResult += "<th> </th>";
        orderTableResult += "</tr>";
    }

    let totalPrice = resultData.length * 12;

    orderTableResult += "<tr class = \"table-success\">";
    orderTableResult += "<th>Total:</th>";
    orderTableResult += "<th> </th>";
    orderTableResult += "<th>$" + totalPrice.toFixed(2) + "</th>";
    orderTableResult += "<th> </th>";
    orderTableResult += "<th> </th>";
    orderTableResult += "<th> </th>";
    orderTableResult += "<tr> </tr>";

    orderTable.append(orderTableResult);
}

jQuery.ajax({
    dataType: "json",
    method: "POST",
    url: "api/confirmation",
    success: (resultData) => handleConfirmation(resultData)
});
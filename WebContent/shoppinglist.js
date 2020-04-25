let cart = jQuery("#cart");

function handleCartData(resultDataString)
{
    let resultArray = resultDataString.split(',');
    let results = ""

    for(let i = 0; i < resultArray.length; i++)
        results += '<li class="list-group-item">' + resultArray[i] + '</li>';

    cart.append(results);
}
function removeItem()
{
    // to do
}

function incrItem()
{
    // to do
}

$.ajax("api/shoppinglist", {
    method: "POST",
    data:"item=",
    success: handleCartData
});
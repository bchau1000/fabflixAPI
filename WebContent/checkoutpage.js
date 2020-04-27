let checkout_form = jQuery("#checkout_form")
let cart = jQuery("#cart_table_body");

function handleList(resultData)
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
            results += '<th></th>';
            results += '<th></th>';
            results += '<th></th>';
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
    cart.append(results);
}

function handleCheckout(resultData)
{
    console.log(resultData);
}

function handleSubmit(submitForm)
{
    console.log("Success, form submitted");

    submitForm.preventDefault();

    $.ajax(
        "api/checkoutpage", {
            method: "POST",
            data: checkout_form.serialize(),
            success: handleCheckout
        }
    );
}

checkout_form.submit(handleSubmit);

$.ajax("api/shoppinglist", {
    method: "POST",
    data:"item=&title=&op=none",
    success: handleList
});
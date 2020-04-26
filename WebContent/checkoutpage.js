let checkout_form = jQuery("#checkout_form")

function handleCheckout(resultData)
{
    console.log(resultData);
}

function testing(submitForm)
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

checkout_form.submit(testing);


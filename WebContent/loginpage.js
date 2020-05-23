let login_form = $("#login_form");
let storage = window.localStorage;
storage.clear();

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    if(resultDataJson["captchaStatus"] === "success") {
        console.log(resultDataJson["captchaStatus"]);
        console.log("handle login response");
        console.log(resultDataJson);
        console.log(resultDataJson["status"]);

        if (resultDataJson["status"] === "success") {
            window.location.replace("mainpage.html");
        } else {
            console.log("show error message");
            console.log(resultDataJson["message"]);
            alert("Invalid information, please try again.");
        }
    }
    else
        alert("reCAPTCHA failed, please try again.");

}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}
login_form.submit(submitLoginForm);

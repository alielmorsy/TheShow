$(document).ready(function () {
    const root = $('#placement');
    $("#accordionSidebar a").on("click", function (event) {

        const href = event.target.getAttribute('dest');


        const path = f(window.location.href);

        if (path !== href && href !== null) {
            root.hide();
            $(".loader").show();
            getResponse(root, href);

        }


    });
});

function getResponse(root, page) {
    let xhr = new XMLHttpRequest();

    xhr.open("GET", page);
    xhr.send(new FormData());
    xhr.onreadystatechange = (function () {
        root.show();
        root.html(this.responseText);
        $(".loader").hide();

    });
}

function f(link) {
    return link.split("/").slice(-1);
}
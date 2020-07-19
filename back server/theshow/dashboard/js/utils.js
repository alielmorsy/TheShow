function calcDuration(durationString) {
    durationString = durationString.replace(" ", "");
    let dd = durationString.split(":");
    let duration = 0;
    if (dd.length === 3) {
        duration += parseInt(dd[0]) * 60 * 60;
        duration += parseInt(dd[1]) * 60;
        duration += parseInt(dd[2]);
    } else {
        duration += parseInt(dd[0]) * 60;
        duration += parseInt(dd[1]);
    }

    return duration;
}

function uploadImage(photo, key) {
    let req = new XMLHttpRequest();
    let formData = new FormData();

    formData.append("file", photo);

    req.open("POST", 'a.php');
    req.send(formData);
    req.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            key.imageName = this.responseText;
            console.log(key.imageName);
        }

    }

}

function replaceAll(str, search, replacement) {
    return str.split(search).join(replacement)
}

function handleCategories() {
    let cats = "";
    const categories = document.getElementById("categories").options;
    for (let i = 0; i < categories.length; i++) {
        let option = categories[i];
        if (option.selected) {
            cats += option.value + ",";
        }
    }
    cats = cats.substring(0, cats.length - 1);

    return cats;
}

function calcEpisodesCount(seasons) {
    let count = 0;
    for (let i = 0; i < seasons.length; i++) {
        count += seasons[0].episodes.length;
    }
    return count;
}

function sendData(data) {
    let xhr = new XMLHttpRequest();
    let form = new FormData();
    form.append("data", JSON.stringify(data));

    xhr.open("POST", '../HandleRequest.php');
    xhr.send(form);
    xhr.onreadystatechange = function () {
        endLoading();
        if (this.readyState === 4 && this.status === 200) {

            console.log(this.responseText);
            model(this.responseText);
        }

    }

}

function endLoading() {

    const html = '<span>Submit </span>';
    $('#submit').prop('disabled', false);
    document.getElementById("submit").innerHTML = html;
}

function model(response) {
    if (response === "Done")
        toastr["success"]("Added Successfully");
    else if (response === "" || response === null)
        toastr['error']("Internal Error");
    else if (response === "tokenError")
        toastr['warn']("Show Added Successfully but token not <br> please go to data list and edit the show with the data not completed");
    else
        toastr['error'](response);
    toastr.options = {
        "closeButton": false,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "positionClass": "toast-top-right",
        "preventDuplicates": false,
        "showDuration": "3000",
        "hideDuration": "2000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }

}
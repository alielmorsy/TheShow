let seasonsN = 1;
let series = new Series();

$(document).ready(function () {
    $('#series-image').change(function () {
        let photo = document.getElementById("series-image").files[0];
        uploadImage(photo, series);

    });
    addSeason(1);
    $('#submit').click(function (e) {
        e.preventDefault();
        const html = '   <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>\n' +
            '  Loading...';
        $('#submit').prop('disabled', true);
        document.getElementById("submit").innerHTML=html;
        collectData();
    });
    $('#add-newSeason').click(function () {
        seasonsN++;
        addSeason(seasonsN);
    });

    $('#categories').selectpicker();
    $('#dp1').datepicker('');

});


function addSeason(number) {
    fetch('seasons.html')
        .then(data => {
            return data.text();
        }).then(html => {
            handleSeasons(html);
            afterLoadSeason(number);
        }
    );

}

function afterLoadSeason(number) {
    let season1 = new Season();
    season1.number = number;
    series.setSeasons(season1);
    for (let i = 0; i < series.seasons.length; i++) {
        let season = series.seasons[i];
        if (season == null)
            continue;
        let image = document.getElementById("season-image-" + season.number);

        $("#season-image-" + season.number).change(function () {
            let im = image.files[0];
            uploadImage(im, season);
        });
        $("#remove-season-" + season.number).click(function () {
            $("#season-" + season.number).remove();
            series.seasons[series.seasons.length - 1] = null;
            seasonsN--;
        });
        $('#add-newEpisode-' + season.number).click(function () {
            fetch('episodes.html')
                .then(data => {
                    return data.text();
                }).then(html => {
                let e = new Episode();
                season.addEpisode(e);
                html = replaceAll(html, 'episode', season.episodes.length);
                html = replaceAll(html, 'seasons', season.number);

                const ep = document.getElementById('episodes-' + season.number);
                ep.insertAdjacentHTML('beforeEnd', html);
//                document.getElementById('episodes-' + season.number).innerHTML = ep + "\n" + html;
            });
        });

    }

}

function handleSeasons(html) {
    html = replaceAll(html, "!", seasonsN + "");

    document.getElementById("newSeason").insertAdjacentHTML('beforeEnd', html);
}

function collectData() {
    toastr.clear();
    series.sName = document.getElementById("name").value;
    series.cats = handleCategories();

    series.rating = document.getElementById("rating").value;
    series.language = document.getElementById("language").value;
    series.year = document.getElementById("release").value;
    series.description = document.getElementById("description").value;
    for (let i = 0; i < series.seasons.length; i++) {
        let season = series.seasons[i];
        for (let j = 0; j < season.episodes.length; j++) {
            let episode = season.episodes[j];
            handleEpisode(episode, i + 1, j + 1);

        }
    }
    createJSON();
}

function createJSON() {
    let data = {};
    data['seriesName'] = series.sName;
    data['seasonCount'] = series.seasons.length;
    data['episodesCount'] = calcEpisodesCount(series.seasons);
    data['rating'] = series.rating;
    data['description'] = series.description;
    data['imageURL'] = series.imageName;
    data['types'] = series.cats;
    data['year'] = series.year;
    data['seasons'] = createSeasons();
    data['type'] = "series";
    data['what'] = "insert";
    data['language'] = document.getElementById("language").value;
    sendData(data);
}

function createSeasons() {
    let seasons = [];
    for (let i = 0; i < series.seasons.length; i++) {
        let season = series.seasons[i];
        if (season == null)
            continue;
        let tmp = {};
        tmp['imageURL'] = season.imageName;
        tmp['number'] = season.number;
        tmp['episodeCount'] = season.episodes.length;
        tmp['episodes'] = createEpisodes(season.episodes);
        seasons.push(tmp);
    }
    return seasons;
}

function createEpisodes(episodes) {
    let es = [];
    for (let i = 0; i < episodes.length; i++) {
        let episode = episodes[i];
        let tmp = {};
        tmp["episodeName"] = episode.eName;
        tmp['rating'] = episode.rating;
        tmp['tokenID'] = episode.tokenID;
        tmp['number'] = i + 1;
        tmp['duration']=episode.duration;
        es.push(tmp);
    }
    return es;
}

function handleEpisode(episode, i, j) {
    episode.tokenID = document.getElementById(`token-${i}-${j}`).value;
    episode.eName = document.getElementById(`name-${i}-${j}`).value;
    episode.rating = document.getElementById(`rating-${i}-${j}`).value;
    const dString = document.getElementById(`duration-${i}-${j}`).value;
    episode.duration = calcDuration(dString);
}




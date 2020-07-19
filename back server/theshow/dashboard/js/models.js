class Movie {
    get imageName() {
        return this.#_imageName;
    }

    set imageName(value) {
        this.#_imageName = value;
    }

    #_imageName = "";
}

class Series {
    get imageName() {
        return this.#_imageName;
    }

    set imageName(value) {
        this.#_imageName = value;
    }

    #_sName = "";
    #_rating = "";
    #_imageName = "";
    #_cats = "";

    #_language = "";
    #_year = "";
    #_description = "";
    #_seasons = [];

    get sName() {
        return this.#_sName;
    }

    set sName(value) {
        this.#_sName = value;
    }

    get rating() {
        return this.#_rating;
    }

    set rating(value) {
        this.#_rating = value;
    }

    get cats() {
        return this.#_cats;
    }

    set cats(value) {
        this.#_cats = value;
    }

    get language() {
        return this.#_language;
    }

    set language(value) {
        this.#_language = value;
    }

    get year() {
        return this.#_year;
    }

    set year(value) {
        this.#_year = value;
    }

    get description() {
        return this.#_description;
    }

    set description(value) {
        this.#_description = value;
    }

    get seasons() {
        return this.#_seasons;
    }

    setSeasons(season) {
        this.#_seasons.push(season);

    }
}

class Season {

    #_imageName = "";
    #_number = "";
    #_episodes = [];
    get episodes() {
        return this.#_episodes;
    }

    get imageName() {
        return this.#_imageName;
    }

    set imageName(value) {
        this.#_imageName = value;
    }

    get number() {
        return this.#_number;
    }

    set number(value) {
        this.#_number = value;
    }

    addEpisode(episode) {
        this.#_episodes.push(episode);
    }

}

class Episode {
    #_eName = "";
    #_rating = "";
    #_number  = "";
    #_tokenID= "";

    get eName() {
        return this.#_eName;
    }

    set eName(value) {
        this.#_eName = value;
    }

    get rating() {
        return this.#_rating;
    }

    set rating(value) {
        this.#_rating = value;
    }

    get number() {
        return this.#_number;
    }

    set number(value) {
        this.#_number = value;
    }

    get tokenID() {
        return this.#_tokenID;
    }

    set tokenID(value) {
        this.#_tokenID = value;
    }
}
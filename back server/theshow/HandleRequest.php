<?php
include 'includes.php';
if (isset($_POST['data'])) {
    new HandleRequest($connectionU, $_POST['data']);
}

class HandleRequest
{
    private $arr;
    private $conn;

    function __construct($conn, $data)
    {
        $this->conn = $conn;
        $this->arr = json_decode($data);

        $type = $this->arr->type;
        $what = $this->arr->what;

        switch ($type) {
            case "series":
                $this->addSeriesIntoDatabase();
                break;
            case "movie":
                $this->handleMovie();
                break;
        }
    }

    private function addSeriesIntoDatabase()
    {
        $name = str_replace("'","\'",$this->arr->seriesName);

        $imageURL = $this->arr->imageURL;
        $types = $this->arr->types;
        $year = $this->arr->year;
        $rating = $this->arr->rating;
        $description = str_replace("'","\'",$this->arr->description);
        $seasonCount = $this->arr->seasonCount;
        $episodesCount = $this->arr->episodesCount;
        $language = $this->arr->language;
        $check = $this->checkIsFound($name, $year, "series");
        if ($check) {
            $id = $this->insertSeries($name, $imageURL, $types, $rating, $description, $seasonCount, $episodesCount, $year, $language);
            $this->handleSeasons($id);
            echo "Done";
        }
    }

    private function insertSeries($name, $imageURL, $types, $rating, $description, $seasonCount, $episodesCount, $year, $language)
    {
        $sql = "INSERT INTO series (id, name, description, type, sessionCount, imageURL, rating, year, language, episodesCount)
        VALUE (NULL,'$name','$description','$types',$seasonCount,'$imageURL',$rating,$year,'$language',$episodesCount)";
        $query = mysqli_query($this->conn, $sql);
        if ($query) {
            $sql = "select id from series where name='$name'";
            $query = mysqli_query($this->conn, $sql);
            return $id = mysqli_fetch_row($query)[0];
        } else
            die("Bad Series Data");
    }

    private function handleSeasons($id)
    {
        $seasons = $this->arr->seasons;
        foreach ($seasons as $season) {
            $this->handleSeason($season, $id);
        }
    }

    private function handleSeason($season, $id)
    {
        $number = $season->number;
        $imageURL = $season->imageURL;
        $episodesCount = $season->episodeCount;
        $id = $this->InsertSeason($number, $imageURL, $episodesCount, $id);

        if ($id) {
            $episodes = $season->episodes;
            foreach ($episodes as $episode) {
                $this->handleEpisode($episode, $id);
            }
        } else
            die("Sorry There are a big fucking problem in the application");
    }

    private function InsertSeason($number, $imageURL, $episodesCount, $id)
    {
        $sql = "insert into series_seasons (id, seriesID, number, imageUrl, episodeCount) VALUE (NULL,$id,$number,'$imageURL',$episodesCount)";
 
        $query = mysqli_query($this->conn, $sql);
        if ($query) {
            $sql = "select id from series_seasons where number=$number and seriesID=$id limit 1";
            $query = mysqli_query($this->conn, $sql);
            if ($query) {
                return mysqli_fetch_row($query)[0];
            } else
                die("Sorry There are a big fucking problem in the application");
        } else
            die("Can't Install Seasons");
    }

    private function handleEpisode($episode, $seasonID)
    {
        $name = $episode->episodeName;
        $rating = $episode->rating;
        $tokenID = $episode->tokenID;
        $number = $episode->number;
        $duration = $episode->duration;
        if($name=="" &&$rating= "" &&$tokenID=""&& $number=="" && $duration==""){
            return;
        }
        $sql = "insert into episdoes_list (id, name, rating, duration, season_id, episode_number) 
value (NULL,'$name',$rating,$duration,$seasonID,$number)";
        $query = mysqli_query($this->conn, $sql);
        if (!$query)
            die("There are an error in insertion episode number $number");
        $sql = "select id from episdoes_list where name='$name' and season_id=$seasonID and episode_number=$number";
        $query = mysqli_query($this->conn, $sql);
        if ($query) {
            $id = mysqli_fetch_row($query)[0];
            $sql = "insert into episodes_videos(episode_id, fileKey) value ($id,'$tokenID')";
            $query = mysqli_query($this->conn, $sql);
            if (!$query) {
                die("There are an error in insertion episode number $number");
            }
        } else {
            die("There are an error in insertion episode number $number");
        }
    }

    private function checkIsFound($name, $year, $type)
    {
        if($name==null || $name==""||$year==null ||$year==""){
            die("Please enter full data don't forget any thing");
        }
        $sql = "select * from $type where name='$name' and year=$year limit 1";
   
        $query = mysqli_query($this->conn, $sql);
        if ($query) {

            if ($query->fetch_row() != null) {
                die("This Show Is already added before");
            } else
                return true;
        } else
            die ('There are problem in server please try again later');
    }

    private function handleMovie()
    {
        $data = $this->arr;
        $name = str_replace("'","\'",$data->name);
        $imageURL = $data->imageURL;
        $duration = $data->duration;
        $year = $data->year;
        $types = $data->types;
        $rating = $data->rating;
        $description = str_replace("'","\'",$data->description);
        $language = $data->language;
        $this->checkIsFound($name, $year, "movies");
        $id = $this->InsertMovie($name, $imageURL, $duration, $year, $types, $rating, $description, $language);

        $this->InsertMovieToken($id, $data->tokenID);
        
        echo 'Done';
    }

    private function InsertMovie($name, $imageURL, $duration, $year, $types, $rating, $description, $languaeg)
    {
        $sql = "insert into movies (id, name, description, type, duration, imageUrl, rating, year, downloads_number, language) 
value  (NULL,'$name','$description','$types',$duration,'$imageURL',$rating,$year,0,'$languaeg')";
        $query = mysqli_query($this->conn, $sql);
        if ($query) {
            $sql = "select id from movies where name='$name' and year=$year";
            $query = mysqli_query($this->conn, $sql);
            if ($query) {
                return mysqli_fetch_row($query)[0];
            } else
                die("Sorry There are error in server please try again ");
        } else
            die("There are bad fields entered");
    }

    private function InsertMovieToken($id, $tokenID)
    {
        $sql = "insert into movies_tokens (id, movie_id, tokenID) VALUE (NULL,$id,'$tokenID')";
        $query = mysqli_query($this->conn, $sql);
        if (!$query)
            die("tokenError");
    }
}
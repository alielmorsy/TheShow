<?php

class Utils
{
    private $conn;

    public function __construct($conn)
    {
        $this->conn = $conn;
    }

    function toJson(array $row, bool $series)
    {
        $arr = array();
        $arr['id'] = intval($row[0]);

        $arr['name'] = utf8_encode(str_replace("\r", "", str_replace("\n", "", $row[1])));

        $arr['description'] = utf8_encode($row[2]);
        $arr['type'] = $row[3];
        if ($series) {
            $arr['sessionCount'] = intval($row[4]);
            $arr['episodesCount'] = intval($row[9]);
            $arr['language'] = $row[8];
        } else {
            $arr['duration'] = intval($row[4]);
            $arr['numberDownloads'] = intval($row[8]);
            $arr['tokenID'] = urldecode($row[10]);
            $arr['language'] = $row[9];
        }
        $arr['imageURL'] = $row[5];
        $arr['rating'] = floatval($row[6]);
        $arr['year'] = intval($row[7]);


        return $arr;
    }

    function getCast($conn, $id, $movie)
    {
        $final = array();
        $sql = "";
        if ($movie)
            $sql = "select `actor_id` from `movies_cast` where `movie_id`=$id";
        else
            $sql = "select `actor_id` from `series_cast` where `series_id`=$id";
        $query = mysqli_query($conn, $sql);

        if ($query) {
            while ($row = mysqli_fetch_row($query)) {
                $quer = "SELECT * FROM `cast_list` where `id`=$row[0]";
                $q = mysqli_query($conn, $quer);
                if ($q) {
                    $row = $this->handleCastList(mysqli_fetch_array($q));
                    $final[] = $row;
                }
            }
        }

        return $final;
    }


    function handleCastList($row)
    {
        $arr = array();
        $arr['id'] = intval($row[0]);
        $arr['name'] = $row[1];
        $arr['ImageUrl'] = $row[2];

        return $arr;
    }

    public function handleEpisodeList($row)
    {
        $arr = array();
        $arr['id'] = intval($row[0]);
        $arr['name'] = $row[1];
        $arr['rating'] = floatval($row[2]);
        $arr['duration'] = intval($row[3]);
        $arr['number'] = intval($row[4]);
        $arr['seasonNumber'] = intval($row[5]);
        $arr['seriesName'] = $row[6];
        $arr['imageUrl'] = $row[8];
        $arr['tokenID'] = $row[9];
        return $arr;
    }

    public function handleSeason($row)
    {
        $arr = array();
        $arr['id'] = intval($row[0]);
        $arr['seriesID'] = intval($row[1]);
        $arr['number'] = intval($row[2]);
        $arr['imageURL'] = $row[3];
        $arr['episodeCount'] = $row[4];
        $arr['episodes'] = $this->getEpisodesById($row[0]);
        return $arr;
    }

    private function getEpisodesById($id)
    {
        $arr = array();
        $sql = "select el.id,el.name,el.rating,el.duration,el.episode_number,ss.number,s.name,s.id,ss.imageUrl,ev.fileKey from episdoes_list as el
    left join series_seasons ss on el.season_id = ss.id left join series s on ss.seriesID = s.id left outer join episodes_videos as ev on el.id = ev.episode_id where el.season_id=$id";

        $query = mysqli_query($this->conn, $sql);
        while ($row = mysqli_fetch_row($query)) {
            $arr[] = $this->handleEpisodeList($row);
        }
        return $arr;
    }

    public function handleRequest($row)
    {
        $arr = array();
        $arr['request'] = $row[2];
        $tmp = $row[1];
        $data = explode("!", $tmp);
        $arr['day'] = $data[0];
        $arr['time'] = $data[1];
        return $arr;

    }
}
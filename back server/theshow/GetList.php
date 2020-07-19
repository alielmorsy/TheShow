<?php
include 'Utils.php';
include 'includes.php';
try {
    $a = new GetList($connectionU);
} catch (Exception $e) {
    echo $e;
}


class GetList
{
    private $conn;
    private $utils;

    /**
     * GetList constructor.
     * @param $conn
     * @throws Exception
     */
    public function __construct($conn)
    {
        $this->conn = $conn;
        $this->utils = new Utils($conn);
        $this->receiveData();
    }

    private function receiveData()
    {
        if (isset($_GET['type'])) {

            $type = $_GET['type'];
            if (isset($_GET['list'])) {
                $list = $_GET['list'];
                $from = $_GET['from'];
                $to = $_GET['to'];
                $this->getList($type, $list, $from, $to);
            }
        } else if (isset($_GET['what'])) {
            $what = $_GET['what'];
            switch ($what) {
                case "types":
                    $this->getCatList();
                    break;
                case "similar":
                    $this->getSimilar();
                    break;
                case "choosenToady":
                    $this->getChoosenToday();
                    break;
                case "seasons":
                    $this->getSeasons();
                    break;
            }
        } else {
            die("Error");
        }
    }

    private function getList($type, $what, $from, $to)
    {

        $arr = array();
        if ($type == "episodes")
            $type = "episdoes_list";
        $what = $this->generateOrderByCommand($what);
        if ($type == "episdoes_list") {
            $sql = "select el.id,el.name,el.rating,el.duration,el.episode_number,ss.number,s.name,s.id,ss.imageUrl,ev.fileKey from episdoes_list as el
    left join series_seasons ss on el.season_id = ss.id left join series s on ss.seriesID = s.id left outer join episodes_videos as ev on el.id = ev.episode_id order by el.id desc limit $from,$to";
            $query = mysqli_query($this->conn, $sql);
            while ($row = mysqli_fetch_row($query)) {
                $arr[] = $this->utils->handleEpisodeList($row);
            }
        } else {
            if ($type == "movies") {
                $sql = "select movies.id, name, description, type, duration, imageUrl, rating, year, downloads_number, language, mt.tokenID
                    from movies left join movies_tokens mt on movies.id = mt.movie_id  $what limit  $from,$to";
            
            } else {
                $sql = "select * from series $what limit $from, $to";

            }

            $query = mysqli_query($this->conn, $sql);

            while ($row = mysqli_fetch_row($query)) {
                $arr[] = $this->utils->toJson($row, $type == "series");

            }

        }

        echo json_encode($arr);
    }

    private function generateOrderByCommand($what)
    {
        switch ($what) {
            case '1':
                return 'order by id desc';
            case '2':
                return 'order by rating desc';
        }
    }

    private function getCatList()
    {

        $type = $_GET['types'];
        $from = $_GET['from'];
        $to = $_GET['to'];
        $too = intval(intval($to) / 2);
        $arr = $this->getCatMovieList($type, $from, $too);
        $tmp = $this->getCatSeriesList($type, $from, $too);
        foreach ($tmp as $a) {
            $arr[] = $a;
        }
        echo json_encode($arr);
    }

    private function getCatMovieList($type, $from, $to)
    {
        $arr = array();
//        $query = mysqli_query($this->conn, "select id from movies order by id desc limit 1");
//        $total = $query->fetch_row()[0];
//        $from = $total - $from;
        $sql = "select movies.id, name, description, type, duration, imageUrl, rating, year, downloads_number, language, mt.tokenID
from movies left join movies_tokens mt on movies.id = mt.movie_id where movies.type like '%$type%' and movies.id>=$from limit $to";
        $query = mysqli_query($this->conn, $sql);
        if ($query) {
            while ($row = mysqli_fetch_row($query)) {
                $row = $this->utils->toJson($row, false);
                $row['sType'] = "movie";
                $arr[] = $row;
            }
        }
        return $arr;
    }

    private function getCatSeriesList($type, $from, $to)
    {
        $arr = array();
        $sql = "select * from series where id>=$from and type like '%$type%' limit  $to";
        $query = mysqli_query($this->conn, $sql);
        if ($query) {
            while ($row = mysqli_fetch_row($query)) {
                $row = $this->utils->toJson($row, true);
                $row["sType"] = "series";
                $arr[] = $row;
            }
        }
        return $arr;
    }

    private function getSimilar()
    {
        $arr = array();
        $type = $_GET['show'];
        $id = $_GET['id'];

        $sql = "select `type` from `$type` where `id`=$id";

        $query = mysqli_query($this->conn, $sql);
        $types = $query->fetch_row()[0];
        $final = $this->generateTypesCommand($types);
        $sql = "select * from `$type` where `id`!=$id and $final";
        if ($type == "movies") {
            $sql = "select movies.id, name, description, type, duration, imageUrl, rating, year, downloads_number, language, mt.tokenID
from movies left join movies_tokens mt on movies.id = mt.movie_id where movies.id !=$id and $final";
        }
        
        $query = mysqli_query($this->conn, $sql);
        if ($query) {
            while ($row = mysqli_fetch_row($query)) {

                $arr[] = $this->utils->toJson($row, $type == "series");
            }
        }
        echo json_encode($arr);
    }

      private function generateTypesCommand($types)
    {

        $split = explode(",", $types);
        $size = sizeof($split);
        if ($size == 0)
            return "";
        if ($size == 1) {
            return "type like \"%$types%\"";
        }
        $sql = "(";
        for ($i = 0; $i < $size - 1; $i++) {
            for ($m = 1; $m < $size; $m++) {
                if ($i == $m) {
                    continue;
                }
                if ($i == $size - 2 && $m == $size - 1)
                    $sql .= "(type like \"%$split[$i]%\" and type like \"%$split[$m]%\" )) ";
                else
                    $sql .= "(type like \" % $split[$i] % \" and type like \" % $split[$m] % \" ) or ";
            }
        }

        return $sql;
    }


    private function getChoosenToday()
    {
        $arr = array();
        $sql = "select * from choosed_today";
        $query = mysqli_query($this->conn, $sql);
        while ($row = mysqli_fetch_row($query)) {
            if ($row[2] == "movies")
                $sql = "select movies.id, name, description, type, duration, imageUrl, rating, year, downloads_number, language, mt.tokenID
from movies left join movies_tokens mt on movies.id = mt.movie_id where movies.id =$row[1] ";
            else
                $sql = "select * from $row[2] where id=$row[1]";
            $quey = mysqli_query($this->conn, $sql);
            $arr[] = $this->utils->toJson(mysqli_fetch_row($quey), $row[1]);
        }
        echo json_encode($arr);
    }

    private function getSeasons()
    {

        $arr = array();
        $id = $_GET['id'];
        $seasonsSql = "select * from series_seasons where seriesID=$id";
        $query = mysqli_query($this->conn, $seasonsSql);
        if ($query) {
            while ($row = mysqli_fetch_row($query)) {
                $arr[] = $this->utils->handleSeason($row);
            }

        }
        //   print_r($arr);
        echo json_encode($arr);
    }
}
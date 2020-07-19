<?php
include 'includes.php';
include 'Utils.php';
new Search($connectionU);

class Search
{
    private $conn;
    private $utils;

    public function __construct($conn)
    {
        $this->conn = $conn;
        $this->utils = new Utils($conn);
        $this->handleRequest();
    }

    private function handleRequest()
    {
        $arr = array();
        if (isset($_GET['query'])) {
            mysqli_set_charset($this->conn,"utf-8");
            $query_string = $_GET['query'];
            $movies = $this->getMoviesList($query_string);
            $series = $this->getSeriesList($query_string);
            $arr = array_merge($movies, $series);
            shuffle($arr);
            echo json_encode($arr);
        } else
            die("Baby Don't be here or i will kill you");
    }

    private function getMoviesList($query)
    {
        $arr = array();
        $sql = "select movies.id, name, description, type, duration, imageUrl, rating, year, downloads_number, language, mt.tokenID
from movies left join movies_tokens mt on movies.id = mt.movie_id where  CONVERT(movies.name USING utf8) like _utf8'%$query%' or 
                                                    CONVERT(movies.name USING utf8) like _utf8'$query'";

        $q = mysqli_query($this->conn, $sql);

        if ($q) {
            while ($row = mysqli_fetch_row($q)) {
                 $row=    $this->utils->toJson($row, false);
               $row['sType']="movie";
                $arr[] =$row;
                
            }
            return $arr;
        } else
            die("Can't Handle Request");
    }

    private function getSeriesList($query)
    {
        $arr = array();
        $sql = "select * from series where CONVERT(name USING utf8) like _utf8'%$query%' or  CONVERT(name USING utf8) like _utf8'$query'";
        $q = mysqli_query($this->conn, $sql);
        if ($q) {
            while ($row = mysqli_fetch_row($q)) {
                  $row=    $this->utils->toJson($row, true);
               $row['sType']="series";
                $arr[] =$row;
            }
            return $arr;
        } else
            die("Can't Handle Request");

    }
}

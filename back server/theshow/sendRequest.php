<?php
if (!isset($_GET['request'])) {
    die("Error ");
}
define('url', "127.0.0.1");
$conn = mysqli_connect(url, "root", "", "theshow");

$request = $_GET['request'];
$sql = "insert into `requests` (`id`,`time`,`request`) value ('',CONCAT (curdate(),'!',current_time()),'$request')";

$query = mysqli_query($conn, $sql);
if ($query)
    echo 'done';
else
    echo 'internal error';
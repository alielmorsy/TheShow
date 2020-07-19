<?php

if (is_uploaded_file($_FILES['fiile']['tmp_name'])) {

    $uploads_dir = '';
    $tmp_name = $_FILES['fiile']['tmp_name'];
    $pic_name = substr(date('dd-mm-yyyy') . '-' . sha1(time()), 0, 16) . ".jpg";
    move_uploaded_file($tmp_name, '../images/'.$pic_name.".jpg");
    echo $pic_name;
} else {
    echo "File not uploaded successfully.";
}


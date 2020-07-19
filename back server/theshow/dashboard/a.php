<?php

if (is_uploaded_file($_FILES['file']['tmp_name'])) {

    $uploads_dir = '';
    $tmp_name = $_FILES['file']['tmp_name'];
    $pic_name = substr(sha1(date('dd-mm-yyyy') . '-' . time()), 0, 18) . ".jpg";
    move_uploaded_file($tmp_name, '../../images/'.$pic_name.'.jpg');
    echo $pic_name;
} else {
    echo "File not uploaded successfully.";
}


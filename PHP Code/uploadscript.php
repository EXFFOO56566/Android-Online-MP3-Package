<?php

error_reporting(0);
if (isset($_POST) and $_SERVER['REQUEST_METHOD'] == "POST") {

    $path = "uploads/"; //set your folder path
    $mp3_local=rand(0,99999)."_".str_replace(" ", "-", $_FILES['mp3_local']['name']);

    $tmp = $_FILES['mp3_local']['tmp_name'];
    
    if (move_uploaded_file($tmp, $path.$mp3_local)) 
    { //check if it the file move successfully.
        echo $mp3_local;
    } else {
        echo "failed";
    }
    exit;
}
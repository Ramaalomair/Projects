<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
$host = "localhost";  // Change if needed
$username = "root";   // Change based on your database credentials
$password = "root";       // Change if you have set a password
$database = "adore_clinic"; 

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>

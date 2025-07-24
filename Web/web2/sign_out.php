<?php
session_start(); // Start the session

// Destroy the session and remove session variables
session_unset();
session_destroy();

// Redirect the user to the home page
header("Location: index.html"); 
exit();
?>

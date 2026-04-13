<?php
$username = $_GET['username'];
$password = $_GET['password'];
// Check if the username and password are valid
if (validateUser($username, $password)) {
// If the user is valid, set the session variables
$_SESSION['user'] = $username;
$_SESSION['pass'] = $password;
}
else {
// If the user is not valid, display an error message
error_message();
}
?>
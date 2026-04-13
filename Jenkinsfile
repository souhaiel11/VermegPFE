<?php

function validateInput($input) {
  return filter_var($input, FILTER_SANITIZE_STRING);
}

$name = validateInput($_POST['name']);

echo 'Hello, $name!';
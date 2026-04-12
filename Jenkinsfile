<?php
class UserAuth {
    public function login(string $email, string $password) {
        $user = User::findOne(['email' => $email]);
        if ($user && password_verify($password, $user->getPassword())) {
            // Log in the user
        } else {
            // Return an error message
        }
    }
}
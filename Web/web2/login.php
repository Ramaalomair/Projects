<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

session_start();
include 'db_connection.php';
header('Content-Type: application/json');

$response = ["status" => "error", "message" => "Invalid credentials"]; // Default error message

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $email = trim($_POST['email']);
    $password = trim($_POST['password']);
    $role = trim($_POST['role']);

    if (empty($email) || empty($password) || empty($role)) {
        echo json_encode($response);
        exit();
    }

    // Determine the table based on role
    $table = ($role === 'doctor') ? 'doctor' : 'patient';
    $sql = "SELECT id, password FROM $table WHERE emailAddress = ?";

    if ($stmt = $conn->prepare($sql)) {
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            $stmt->bind_result($userID, $hashed_password);
            $stmt->fetch();

            if (password_verify($password, $hashed_password)) {
                // Success: Set session variables and send a success response
                $_SESSION['user_id'] = $userID;
                $_SESSION['user_type'] = $role;

                echo json_encode(["status" => "success", "role" => $role]); // Success message
                exit();
            } else {
                // Password mismatch
                $response["message"] = "Incorrect password";
                echo json_encode($response);
                exit();
            }
        } else {
            // User not found
            $response["message"] = "Email not found";
            echo json_encode($response);
            exit();
        }
    } else {
        // SQL error
        $response["message"] = "Database error";
        echo json_encode($response);
        exit();
    }
} else {
    // Invalid request
    $response["message"] = "Invalid request method";
    echo json_encode($response);
    exit();
}

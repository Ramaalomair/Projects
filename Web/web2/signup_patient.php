<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

session_start();
include 'db_connection.php';

// Set the response header to JSON
header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Gather form data
    $id = $_POST['patient_id'];
    $firstName = trim($_POST['first_name']);
    $lastName = trim($_POST['last_name']);
    $gender = trim($_POST['gender']);
    $dob = $_POST['date_of_birth'];
    $email = trim($_POST['email']);
    $password = $_POST['password'];



    // Hash the password
    $hashedPassword = password_hash($password, PASSWORD_BCRYPT);
    
    // Check if ID already exists
    $checkIDQuery = "SELECT id FROM patient WHERE id = ?";
    $stmt = $conn->prepare($checkIDQuery);
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "Patient ID already exists"]);
        exit();
    }
    $stmt->close();

    // Check if email already exists
    $checkQuery = "SELECT id FROM patient WHERE emailAddress = ?";
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->store_result();

    // If email exists, return error
    if ($stmt->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "Email already exists"]);
        exit();
    }

    $stmt->close();

    // Insert new patient data
    $insertQuery = "INSERT INTO patient (id, firstName, lastName, Gender, DoB, emailAddress, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($insertQuery);
    $stmt->bind_param("sssssss", $id, $firstName, $lastName, $gender, $dob, $email, $hashedPassword);

    if ($stmt->execute()) {
        $_SESSION['user_id'] = $id;
        $_SESSION['user_type'] = "patient";
        echo json_encode(["status" => "success", "redirect" => "patient_page.php"]);
    } else {
        // Return error response if insertion fails
        echo json_encode(["status" => "error", "message" => "Signup failed"]);
    }

    $stmt->close();
    $conn->close();
} else {
    // Handle unexpected request method
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
}
?>

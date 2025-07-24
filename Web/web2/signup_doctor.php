<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

session_start();
include 'db_connection.php'; // Ensure this file correctly connects to your database

header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Collect form data safely
    $id = isset($_POST['doctor_id']) ? trim($_POST['doctor_id']) : ''; 
    $firstName = isset($_POST['first_name']) ? trim($_POST['first_name']) : '';
    $lastName = isset($_POST['last_name']) ? trim($_POST['last_name']) : '';
    $specialityName = isset($_POST['speciality']) ? trim($_POST['speciality']) : '';
    $email = isset($_POST['email']) ? trim($_POST['email']) : '';
    $password = isset($_POST['password']) ? $_POST['password'] : '';

    $_SESSION['user_id'] = $id;
    if (empty($id) || empty($firstName) || empty($lastName) || empty($email) || empty($password)) {
        echo json_encode(["status" => "error", "message" => "All fields are required"]);
        exit();
    }

    // Check if ID already exists
    $checkIDQuery = "SELECT id FROM doctor WHERE id = ?";
    $stmt = $conn->prepare($checkIDQuery);
    $stmt->bind_param("s", $id);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "Doctor ID already exists"]);
        exit();
    }
    $stmt->close();

    // Check if email already exists
    $checkQuery = "SELECT id FROM doctor WHERE emailAddress = ?";
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        echo json_encode(["status" => "error", "message" => "Email already exists"]);
        exit();
    }
    $stmt->close();

    // Query to get the SpecialityID from the speciality table
    $specialityQuery = "SELECT id FROM speciality WHERE speciality = ?";
    $stmt = $conn->prepare($specialityQuery);
    $stmt->bind_param("s", $specialityName);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($specialityID);
        $stmt->fetch();
    } else {
        echo json_encode(["status" => "error", "message" => "Speciality not found"]);
        exit();
    }
    $stmt->close();

    // Handle file upload
    $uniqueFileName = null;
    if (isset($_FILES['photo']) && $_FILES['photo']['error'] == 0) {
        $fileTmpPath = $_FILES['photo']['tmp_name'];
        $fileName = $_FILES['photo']['name'];
        $fileExt = pathinfo($fileName, PATHINFO_EXTENSION);
        $allowedExts = ['jpg', 'jpeg', 'png'];

        if (!in_array(strtolower($fileExt), $allowedExts)) {
            echo json_encode(["status" => "error", "message" => "Invalid file type"]);
            exit();
        }

        $uploadDir = "uploads/";
        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0755, true);
        }

        $uniqueFileName = uniqid('doctor_', true) . "." . $fileExt;
        $destination = $uploadDir . $uniqueFileName;

        if (!move_uploaded_file($fileTmpPath, $destination)) {
            echo json_encode(["status" => "error", "message" => "File upload failed"]);
            exit();
        }
    }

    // Hash password
    $hashedPassword = password_hash($password, PASSWORD_BCRYPT);

    // Insert new doctor
    $insertQuery = "INSERT INTO doctor (id, firstName, lastName, uniqueFileName, SpecialityID, emailAddress, password) 
                    VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($insertQuery);
    $stmt->bind_param("sssssss", $id, $firstName, $lastName, $uniqueFileName, $specialityID, $email, $hashedPassword);

    if ($stmt->execute()) {
        $_SESSION['user_id'] = $id;
        $_SESSION['user_type'] = "doctor";
        echo json_encode(["status" => "success", "redirect" => "Doctor.php"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Signup failed"]);
    }

    $stmt->close();
    $conn->close();
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request method"]);
}
?>

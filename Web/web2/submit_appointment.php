<?php
session_start();
include 'db_connection.php'; // Include your database connection

if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['doctor'], $_POST['date'], $_POST['time'], $_POST['reason'])) {
    $patientId = $_SESSION['user_id']; // Assuming user is logged in
    $doctorId = $_POST['doctor'];
    $appointmentDate = $_POST['date'];
    $appointmentTime = $_POST['time'];
    $reason = $_POST['reason'];

    $queryInsert = "INSERT INTO appointment (PatientID, DoctorID, date, time, reason, status) 
                VALUES (?, ?, ?, ?, ?, 'Pending')";
    $stmtInsert = $conn->prepare($queryInsert);

if (!$stmtInsert) {
    die(json_encode(["status" => "error", "message" => "SQL Error: " . $conn->error]));
}

$stmtInsert->bind_param("sssss", $patientId, $doctorId, $appointmentDate, $appointmentTime, $reason);

if (!$stmtInsert->execute()) {
    die(json_encode(["status" => "error", "message" => "Insert Error: " . $stmtInsert->error]));
}

$stmtInsert->close();


    // Redirect with a success message
    header('Location: patient_page.php?message=Appointment Booked Successfully');
    exit();
}

$conn->close();
?>

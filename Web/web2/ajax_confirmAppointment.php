<?php
session_start();
include 'db_connection.php';

header('Content-Type: text/plain');

if (!isset($_SESSION['user_id']) || $_SESSION['user_type'] != 'doctor') {
    echo 'false';
    exit();
}

$doctorId = $_SESSION['user_id'];
$appointmentId = $_POST['appointment_id'] ?? null;

if ($appointmentId) {
    $query = "UPDATE appointment SET status = 'Confirmed' WHERE id = ? AND DoctorID = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("si", $appointmentId, $doctorId);
    if ($stmt->execute()) {
        echo 'true';
    } else {
        echo 'false';
    }
    $stmt->close();
} else {
    echo 'false';
}
?>

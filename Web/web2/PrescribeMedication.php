<?php

session_start();
include 'db_connection.php';

// Check if the doctor is logged in
if (!isset($_SESSION['user_id']) || $_SESSION['user_type'] != 'doctor') {
    header('Location: login.html');
    exit();
}

// Check if the appointment ID is present in the URL
if (!isset($_GET['appointment_id'])) {
    die("Invalid request. Appointment ID is missing.");
}
$appointmentId = $_GET['appointment_id'];

// Query to get the patient's data linked to the appointment
$query = "SELECT p.id AS patient_id, p.firstName, p.lastName, p.DoB, p.Gender
          FROM appointment a
          JOIN patient p ON a.PatientID = p.id
          WHERE a.id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("i", $appointmentId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    die("Patient not found.");
}
$patient = $result->fetch_assoc();
$stmt->close();

// Calculate patient's age
$dob = new DateTime($patient['DoB']);
$today = new DateTime();
$age = $today->diff($dob)->y;

// Query to get available medications
$medicationsQuery = "SELECT * FROM medication";
$medicationsResult = $conn->query($medicationsQuery);

// When the form is submitted
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $medications = $_POST['medications'];

    // Insert new prescriptions
    if (!empty($medications)) {
        foreach ($medications as $medicationId) {
            $prescriptionId = uniqid(); // Unique ID for each medication
            $insertQuery = "INSERT INTO prescription (id, AppointmentID, MedicationID) VALUES (?, ?, ?)";
            $stmt = $conn->prepare($insertQuery);
            $stmt->bind_param("sss", $prescriptionId, $appointmentId, $medicationId);
            $stmt->execute();
            $stmt->close();
        }
    }

    // Update appointment status to "Done"
    $updateAppointmentQuery = "UPDATE appointment SET status = 'Done' WHERE id = ?";
    $stmt = $conn->prepare($updateAppointmentQuery);
    $stmt->bind_param("s", $appointmentId);
    $stmt->execute();
    $stmt->close();

    // Redirect to the doctor's homepage
    header('Location: Doctor.php');
    exit();
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="stylesheet.css">
    <title>Prescribe Medication</title>
</head>
<body>
    <header>
        <div id="headerContainer-patientPage">
            <a href="doctor_homepage.php"><img src="image/logo.jpg" alt="logo" class="logo"></a>
            <p id="webName">ADORE CLINIC</p>
    <a href="sign_out.php" id="signOutLink-patientPage">Sign Out</a>
        </div>
    </header>

    <main>
        <div class="form-wrapper">
            <div class="form-container">
                <h2>Prescribe Medication</h2>
                <form action="PrescribeMedication.php?appointment_id=<?php echo $appointmentId; ?>" method="POST">
                    <label>Patient's Name:</label>
                    <input type="text" value="<?php echo $patient['firstName'] . ' ' . $patient['lastName']; ?>" disabled>
                    
                    <label>Age:</label>
                    <input type="number" value="<?php echo $age; ?>" disabled>
                    
                    <label>Gender:</label>
                    <input type="text" value="<?php echo $patient['Gender']; ?>" disabled>
                    
                    <label>Medications:</label>
                    <div class="medications-group">
                        <?php while ($medication = $medicationsResult->fetch_assoc()): ?>
                            <label>
                                <input type="checkbox" name="medications[]" value="<?php echo $medication['id']; ?>">
                                <?php echo $medication['MedicationName']; ?>
                            </label><br>
                        <?php endwhile; ?>
                    </div>
                    
                    <input type="submit" value="Submit">
                </form>
            </div>
        </div>
    </main>
</body>
</html>
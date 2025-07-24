<?php
session_start();
include 'db_connection.php';

// معالجة طلب الحذف عبر AJAX
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['appointmentId'])) {
    $appointmentId = $_POST['appointmentId'];
    $patientId = $_SESSION['user_id'];

    $queryDelete = "DELETE FROM appointment WHERE id = ? AND PatientID = ?";
    $stmt = $conn->prepare($queryDelete);
    $stmt->bind_param("ss", $appointmentId, $patientId);

    $response = [];
    if ($stmt->execute()) {
        $response['success'] = true;
    } else {
        $response['success'] = false;
    }

    echo json_encode($response);
    $stmt->close();
    $conn->close();
    exit(); // نوقف التنفيذ بعد الرد
}

// تحقق من تسجيل الدخول
if (!isset($_SESSION['user_id']) || $_SESSION['user_type'] != 'patient') {
    header('Location: login.php');
    exit();
}

$patientId = $_SESSION['user_id'];

// بيانات المريض
$queryPatient = "SELECT firstName, lastName, emailAddress FROM patient WHERE id = ?";
$stmtPatient = $conn->prepare($queryPatient);
$stmtPatient->bind_param("s", $patientId);
$stmtPatient->execute();
$stmtPatient->store_result();
$stmtPatient->bind_result($firstName, $lastName, $emailAddress);
$stmtPatient->fetch();

// المواعيد
$queryAppointments = "SELECT appointment.date AS appointmentDate, appointment.time AS appointmentTime, 
                             doctor.firstName AS doctorFirstName, doctor.lastName AS doctorLastName, 
                             doctor.uniqueFileName AS doctorPhoto, appointment.status, appointment.id
                      FROM appointment
                      JOIN doctor ON appointment.DoctorID = doctor.id
                      WHERE appointment.PatientID = ?
                      ORDER BY appointment.date ASC, appointment.time ASC";

$stmtAppointments = $conn->prepare($queryAppointments);
$stmtAppointments->bind_param("s", $patientId);
$stmtAppointments->execute();
$stmtAppointments->store_result();
$stmtAppointments->bind_result($appointmentDate, $appointmentTime, $doctorFirstName, $doctorLastName, $doctorPhoto, $appointmentStatus, $appointmentId);
?>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Patient Homepage</title>
  <link rel="stylesheet" href="stylesheet.css"/>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body>

<header>
    <a href="index.html"><img src="image/logo.jpg" alt="logo" class="logo" /></a>
    <p id="webName">ADORE CLINIC</p>
    <a href="sign_out.php" id="signOutLink-patientPage">Sign Out</a>
</header>

<main>
  <h1 id="patientName-patien-page">Welcome <span id="user-name-patientPage"><?php echo $firstName . ' ' . $lastName; ?></span></h1>
  <p id="welcomeNote-patient-page">Your skin health is our priority. We're here to help!</p>

  <section class="patient-info-patientPage">
    <h2>Your Information</h2>
    <p><strong>Full Name:</strong> <span id="fullName"><?php echo $firstName . ' ' . $lastName; ?></span></p>
    <p><strong>ID:</strong> <span id="ID"><?php echo $patientId; ?></span></p>
    <p><strong>Email:</strong> <span id="email"><?php echo $emailAddress; ?></span></p>
  </section>

  <section class="appointments-patientPage">
    <div id="appointment-header-patientpage">
      <h2 id="appintment-patient-page">Your Appointments</h2>
      <a href="book_appointment.php" class="book-appointment-link-patientPage">Book an Appointment</a>
    </div>

    <table id="appointmentsTable-patientPage">
      <thead>
        <tr>
          <th>Date</th>
          <th>Time</th>
          <th>Doctor's Name</th>
          <th>Doctor's Photo</th>
          <th>Appointment Status</th>
          <th>Cancel</th>
        </tr>
      </thead>
      <tbody id="appointmentsTableBody-patientPage">
        <?php while ($stmtAppointments->fetch()) {
          if ($appointmentStatus !== 'Done') { ?>
          <tr data-id="<?php echo $appointmentId; ?>">
            <td><?php echo $appointmentDate; ?></td>
            <td><?php echo $appointmentTime; ?></td>
            <td><?php echo $doctorFirstName . ' ' . $doctorLastName; ?></td>
            <td><img id="doctorP-photo" src="uploads/<?php echo $doctorPhoto; ?>" alt="Doctor Photo"></td>
            <td><?php echo $appointmentStatus; ?></td>
            <td><a href="#" class="cancel-link" data-id="<?php echo $appointmentId; ?>">Cancel</a></td>

          </tr>
        <?php } } ?>
      </tbody>
    </table>
  </section>
</main>

<footer>
  <div class="footer-container">
    <div class="footer-info">
      <p>&copy; 2025 ADORE CLINIC. All rights reserved.</p>
      <p>123 Health Street, City, Country</p>
    </div>
    <div class="contact-info">
      <p>Contact Us:</p>
      <div class="contact-item"><img src="image/phone.png" alt="Phone Icon" class="icon"><span>+966111111</span></div>
      <div class="contact-item"><img src="image/telephone.png" alt="Mobile Icon" class="icon"><span>+01134657</span></div>
      <div class="contact-item"><img src="image/Email.png" alt="Email Icon" class="icon"><span>info@adoreclinic.com</span></div>
    </div>
  </div>
</footer>

<script>
$(document).ready(function () {
  $(".cancel-link").on("click", function (e) {
    e.preventDefault();

    if (!confirm("Are you sure you want to cancel this appointment?")) return;

    const row = $(this).closest("tr");
    const appointmentId = row.data("id");

    $.post("patient_page.php", { appointmentId: appointmentId }, function (data) {
      if (data.success) {
        row.remove();
      } else {
        alert("Failed to cancel appointment.");
      }
    }, "json").fail(function () {
      alert("An error occurred. Try again.");
    });
  });
});

</script>


</body>
</html>

<?php
$stmtPatient->close();
$stmtAppointments->close();
$conn->close();
?>

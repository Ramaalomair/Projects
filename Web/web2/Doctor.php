
<?php

session_start();
include 'db_connection.php'; // Include your database connection

// Check if the doctor is logged in by checking the session variable
if (!isset($_SESSION['user_id']) || $_SESSION['user_type'] != 'doctor') {
    header('Location: login.html'); // Redirect to login if not logged in
    exit();
}

$doctorId = $_SESSION['user_id']; // Get the doctor’s ID from session

// Query to retrieve doctor’s information
$query = "SELECT d.firstName, d.lastName, d.emailAddress, d.uniqueFileName, s.speciality 
          FROM doctor d 
          JOIN speciality s ON d.SpecialityID = s.id 
          WHERE d.id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $doctorId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $doctor = $result->fetch_assoc();
} else {
    echo json_encode(["status" => "error", "message" => "Doctor not found"]);
    exit();
}

$stmt->close();

// Query to retrieve upcoming appointments (Pending or Confirmed)
$appointmentsQuery = "
    SELECT a.id, a.date, a.time, a.reason, a.status, 
           p.firstName AS patientFirstName, p.lastName AS patientLastName, p.Gender, p.DoB
    FROM appointment a
    JOIN patient p ON a.PatientID = p.id
    WHERE a.DoctorID = ? AND (a.status = 'Pending' OR a.status = 'Confirmed')
    ORDER BY a.date ASC, a.time ASC"; 

// Query to retrieve patients who have had completed appointments (status = 'Done')
$patientsQuery = "SELECT p.firstName, p.lastName, p.DoB, p.Gender, 
                  GROUP_CONCAT(m.MedicationName SEPARATOR ', ') AS medications
                  FROM patient p
                  JOIN appointment a ON p.id = a.PatientID
                  JOIN prescription pr ON a.id = pr.AppointmentID
                  JOIN medication m ON pr.MedicationID = m.id
                  WHERE a.DoctorID = ? AND a.status = 'Done'
                  GROUP BY p.id, p.firstName, p.lastName, p.DoB, p.Gender
                  ORDER BY p.firstName ASC, p.lastName ASC";
// Execute the appointments query
$stmt = $conn->prepare($appointmentsQuery);
$stmt->bind_param("s", $doctorId);
$stmt->execute();
$appointments_result = $stmt->get_result();

// Execute the patients query
$stmt2 = $conn->prepare($patientsQuery);
$stmt2->bind_param("s", $doctorId);
$stmt2->execute();
$patients_result = $stmt2->get_result();
?>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Doctor Homepage</title>
  <link rel="stylesheet" href="stylesheet.css">
   <style>
    .table-header {
      background-color: #f2f2f2;
    }
  </style>
</head>
<body>
  <header>
    <div id="headerContainer-patientPage">
      <a href="index.html"><img src="image/logo.jpg" alt="logo" class="logo"></a>
      <p id="webName">ADORE CLINIC</p>
    <a href="sign_out.php" id="signOutLink-patientPage">Sign Out</a>
    </div>
  </header>

  <main class="doctor-homepage">
    <h1 id="doctor-welcome" style="color:#725b52;">Welcome Dr. <?php echo $doctor['firstName'] . " " . $doctor['lastName']; ?></h1>
    <h2>Doctor Information</h2>

    <!-- Doctor Information Section -->
    <div class="doctor-container">
      <img id="doctorP-photo" src="uploads/<?php echo $doctor['uniqueFileName']; ?>" alt="Doctor Photo">
      <table class="doctor-table">
        <tr><th>Name</th><td><?php echo $doctor['firstName'] . " " . $doctor['lastName']; ?></td></tr>
        <tr><th>Doctor ID</th><td><?php echo $doctorId; ?></td></tr>
        <tr><th>Speciality</th><td><?php echo $doctor['speciality']; ?></td></tr>
        <tr><th>Email</th><td><?php echo $doctor['emailAddress']; ?></td></tr>
      </table>
    </div>

    <!-- Upcoming Appointments Section -->
    <div class="container">
      <h4>Upcoming Appointments</h4>
      <table>
        <tr>
          <th class="table-header">Date</th>
          <th class="table-header">Time</th>
          <th class="table-header">Patient's Name</th>
          <th class="table-header">Age</th>
          <th class="table-header">Gender</th>
          <th class="table-header">Reason for Visit</th>
          <th class="table-header">Appointment Status</th>
        </tr>
        <?php while ($appointment = $appointments_result->fetch_assoc()): ?>
        <tr>
          <td><?php echo $appointment['date']; ?></td>
          <td><i><?php echo $appointment['time']; ?></i></td>
          <td>
  
      <?php echo $appointment['patientFirstName'] . " " . $appointment['patientLastName']; ?>
    



</td>
          <td>
            <?php 
               $dob = new DateTime($appointment['DoB']);
               $today = new DateTime();
               $age = $today->diff($dob)->y;
               echo $age;
            ?>
          </td>
          <td><?php echo $appointment['Gender']; ?></td>
          <td><?php echo $appointment['reason']; ?></td>
          <td>
  <?php if ($appointment['status'] == 'Pending'): ?>
<button class="confirm-btn" data-id="<?php echo $appointment['id']; ?>">Confirm</button>
  <?php elseif ($appointment['status'] == 'Confirmed'): ?>
    <div>
      <span>Confirmed</span><br>
      <a style="color:#d27979" href="PrescribeMedication.php?appointment_id=<?php echo $appointment['id']; ?>">Go to Medications</a>
    </div>
  <?php endif; ?>
</td>
        </tr>
        <?php endwhile; ?>
      </table>
    </div>

    <!-- Doctor's Patients Section -->
    
    <div class="container">
        
      <h4>Doctor's Patients</h4>
      <table>
        <tr>
          <th class="table-header">Patient's Name</th>
          <th class="table-header">Age</th>
          <th class="table-header">Gender</th>
          <th class="table-header">Medications</th>
        </tr>
        <?php while ($patient = $patients_result->fetch_assoc()): ?>
        <tr>
          <td><?php echo $patient['firstName'] . " " . $patient['lastName']; ?></td>
          <td>
            <?php 
              $dob = new DateTime($patient['DoB']);
              $today = new DateTime();
              $age = $today->diff($dob)->y;
              echo $age;
            ?>
          </td>
          <td><?php echo $patient['Gender']; ?></td>
         <td><?php echo $patient['medications']; ?></td>
         
        </tr>
        <?php endwhile; ?>
      </table>
    </div>
  </main>
   <script>
document.addEventListener("DOMContentLoaded", function () {
    if (sessionStorage.getItem('reloadDoctorPage') === 'true') {
        sessionStorage.removeItem('reloadDoctorPage'); 
        location.reload(); 
    }
});
</script>
  <footer>
    <div class="footer-container">
      <p class="copyright">Copyright &copy; 2025 Skyline Clinic. All rights reserved.</p>
      <div class="contact-info">
        <p>Contact Us:</p>
        <div class="contact-item">
          <img src="image/phone.png" alt="Phone Icon" class="icon">
          <span>+966111111</span>
        </div>
        <div class="contact-item">
          <img src="image/telephone.png" alt="Mobile Icon" class="icon">
          <span>+01134657</span>
        </div>
        <div class="contact-item">
          <img src="image/Email.png" alt="Email Icon" class="icon">
          <span>skylineClinic@gmail.com</span>
        </div>
      </div>
    </div>
  </footer> 
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(document).ready(function() {
  $('.confirm-btn').click(function() {
    const btn = $(this);
    const appointmentId = btn.data('id');

    $.ajax({
      url: 'ajax_confirmAppointment.php',
      type: 'POST',
      data: { appointment_id: appointmentId },
      success: function(response) {
        if (response === 'true') {
          btn.parent().html(`
            <div>
              <span>Confirmed</span><br>
              <a style="color:#d27979" href="PrescribeMedication.php?appointment_id=${appointmentId}">Go to Medications</a>
            </div>
          `);
        } else {
          alert('Failed to confirm appointment.');
        }
      },
      error: function() {
        alert('Error occurred. Please try again.');
      }
    });
  });
});
</script>

</body>
</html>

<?php
mysqli_close($conn);
?>

<?php
session_start();
include 'db_connection.php'; // Include your database connection

// Ensure the user is logged in
if (!isset($_SESSION['user_id'])) {
    die("Error: User not logged in.");
}

// Initialize doctors array
$doctors = [];

// Check if an AJAX request was made for fetching doctors
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['specialty']) && isset($_POST['ajax'])) {
    // Sanitize the specialty input
    $specialty = htmlspecialchars($_POST['specialty']);
    
    // Get all doctors of the selected specialty
    $queryDoctors = "SELECT doctor.id, doctor.firstName, doctor.lastName FROM doctor
                     JOIN speciality ON doctor.SpecialityID = speciality.id
                     WHERE speciality.speciality = ?";
    $stmtDoctors = $conn->prepare($queryDoctors);
    $stmtDoctors->bind_param("s", $specialty);
    $stmtDoctors->execute();
    $stmtDoctors->store_result();
    $stmtDoctors->bind_result($doctorId, $doctorFirstName, $doctorLastName);

    while ($stmtDoctors->fetch()) {
        $doctors[] = [
            'id' => $doctorId,
            'name' => $doctorFirstName . ' ' . $doctorLastName
        ];
    }
    $stmtDoctors->close();

    // Return the doctors array as JSON
    header('Content-Type: application/json');
    echo json_encode($doctors);
    exit(); // Stop further execution
}

// Fetch all doctors (for the initial page load)
$queryAllDoctors = "SELECT id, firstName, lastName FROM doctor";
$resultAllDoctors = $conn->query($queryAllDoctors);

// Fetch specialties (for the initial page load)
$querySpecialties = "SELECT id, speciality FROM speciality";
$resultSpecialties = $conn->query($querySpecialties);

// Close the database connection
$conn->close();
?>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Appointment Booking</title>
  <link rel="stylesheet" href="stylesheet.css">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

</head>
<body>
  <header>
    <a href="index.html"><img src="image/logo.jpg" alt="logo" class="logo"></a>
    <p id="webName">ADORE CLINIC</p>
  </header>

  <main>
    <section class="appointment-booking-bookingPage">
      <h2 id="appointment-header-bookingPage">Book an Appointment</h2>

      <!-- First Form: Specialty -->
      <form id="specialtyForm-bookingPage" class="specialty-form" method="POST" action="book_appointment.php">
        <label for="specialty-bookingPage">Select Specialty:</label>
        <select id="specialty-bookingPage" name="specialty">
          <option value="" selected>All Specialties</option>
          <?php while ($row = $resultSpecialties->fetch_assoc()) { ?>
            <option value="<?php echo $row['speciality']; ?>"><?php echo $row['speciality']; ?></option>
          <?php } ?>
        </select>
      </form>

      <!-- Doctor Selection Form (Initially Visible) -->
      <div id="doctorForm">
        <form id="appointmentForm-bookingPage" class="appointment-form-bookingPage" method="POST" action="submit_appointment.php">
          <label for="doctor-bookingPage">Select Doctor:</label>
          <select id="doctor-bookingPage" name="doctor" required>
            <option value="" disabled selected>Select Doctor</option>
            <?php while ($row = $resultAllDoctors->fetch_assoc()) { ?>
              <option value="<?php echo $row['id']; ?>"><?php echo $row['firstName'] . ' ' . $row['lastName']; ?></option>
            <?php } ?>
          </select>

          <label for="date-bookingPage">Select Date:</label>
          <input type="date" id="date-bookingPage" name="date" required>

          <label for="time-bookingPage">Select Time:</label>
          <input type="time" id="time-bookingPage" name="time" required>

          <label for="reason-bookingPage">Reason for Visit:</label>
          <textarea id="reason-bookingPage" name="reason" rows="4" placeholder="Describe the reason for your visit..." required></textarea>

          <button type="submit">Submit</button>
        </form>
      </div>

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
          <span>info@adoreclinic.com</span>
        </div>
      </div>
    </div>
  </footer>

  <script src="script.js"></script>
</body>
</html>

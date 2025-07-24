<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="stylesheet.css">    
    <title>Sign-Up</title>
    <script src="signup.js"></script>    
</head>

<body id="signuppage-body">
<header>
    <a href="index.html"><img src="image/logo.jpg" alt="logo" class="logo"></a>
    <p id="webName">ADORE CLINIC</p>
</header>

<main id="signuppage" style="background-image: url('image\\clinic.png'); background-position: center; background-repeat: no-repeat; background-size: cover; padding:10px;"> 
   <div class="signup-container-signuppage">
        <h2 class="h2-signuppage">Sign-Up</h2>
        
        <div id="role-form-signuppage" class="role-selection-signuppage" class="hidden">
            <h3 class="h3-signuppage" for='role-signuppage'>Select Your Role</h3>
            <div id='role-signuppage'>
                <label class="label-signuppage" name="signrole"><input type="radio" class="role-signuppage" name="role" value="patient" onclick="showForm('patient-form-signuppage')" required>Patient</label>
                <label class="label-signuppage" name="signrole"><input type="radio" class="role-signuppage" name="role" value="doctor" onclick="showForm('doctor-form-signuppage')" required>Doctor</label>
            </div>
        </div>

       <!-- Patient Form -->
        <form id="patient-form-signuppage" class="hidden" action="signup_patient.php" method="POST">
            <h3 class="h3-signuppage">Patient Sign-Up</h3>
            <div>
                <label class="label-signuppage" for="patient-first-name">First Name:</label>
                <input class="input-signuppage" type="text" id="patient-first-name" name="first_name" placeholder="Enter your first name" required>
            </div>
            <div>
                <label class="label-signuppage" for="patient-last-name">Last Name:</label>
                <input class="input-signuppage" type="text" id="patient-last-name" name="last_name" placeholder="Enter your last name" required>
            </div>
            <div>
                <label class="label-signuppage" for="patient-id">ID:</label>
                <input class="input-signuppage" type="text" id="patient-id" name="patient_id" placeholder="Enter your ID" required>
            </div>
            <div>
                <label class="label-signuppage" for="patient-gender">Gender:</label>
                <select class="select-signuppage" id="patient-gender" name="gender" required>
                    <option value="" disabled selected>Select Gender</option>
                    <option value="male">Male</option>
                    <option value="female">Female</option>
                </select>
            </div>
            <div>
                <label class="label-signuppage" for="patient-dateofbirth">Date of Birth:</label>
                <input class="input-signuppage" type="date" id="patient-dateofbirth" name="date_of_birth" required>
            </div>
            <div>
                <label class="label-signuppage" for="patient-email">Email Address:</label>
                <input class="input-signuppage" type="email" id="patient-email" name="email" placeholder="Enter your email" required>
            </div>
            <div>
                <label class="label-signuppage" for="patient-password">Password:</label>
                <input class="input-signuppage" type="password" id="patient-password" name="password" placeholder="Enter your password" required>
            </div>
            <button type="submit" class="signup-btn-signuppage">Sign-Up</button>
        </form>

        <!-- Doctor Form -->
        <form id="doctor-form-signuppage" class="hidden" action="signup_doctor.php" method="POST" enctype="multipart/form-data">
            <h3 class="h3-signuppage">Doctor Sign-Up</h3>
            <div>
                <label class="label-signuppage" for="doctor-first-name">First Name:</label>
                <input class="input-signuppage" type="text" id="doctor-first-name" name="first_name" required>
            </div>
            <div>
                <label class="label-signuppage" for="doctor-last-name">Last Name:</label>
                <input class="input-signuppage" type="text" id="doctor-last-name" name="last_name" required>
            </div>
            <div>
                <label class="label-signuppage" for="doctor-id">ID:</label>
                <input class="input-signuppage" type="text" id="doctor-id" name="doctor_id" required>
            </div>
            <div>
                <label class="label-signuppage" for="doctor-photo">Photo:</label>
                <input class="input-signuppage" type="file" id="doctor-photo" name="photo" accept="image/*" required>
            </div>
            <div>
                <label class="label-signuppage" for="doctor-speciality">Speciality:</label>
                <select class="select-signuppage" id="doctor-speciality" name="speciality" required>
                    <option value="" disabled selected>Select Speciality</option>
                    <!-- Dynamically load specialties from the database -->
                    <?php
                    // Include database connection
                    include 'db_connection.php';
                    $specialityQuery = "SELECT speciality FROM speciality";
                    $result = $conn->query($specialityQuery);

                    // Populate the dropdown with specialties
                    while ($row = $result->fetch_assoc()) {
                        echo "<option value='" . htmlspecialchars($row['speciality']) . "'>" . htmlspecialchars($row['speciality']) . "</option>";
                    }
                    ?>
                </select>
            </div>
            <div>
                <label class="label-signuppage" for="doctor-email">Email Address:</label>
                <input class="input-signuppage" type="email" id="doctor-email" name="email" required>
            </div>
            <div>
                <label class="label-signuppage" for="doctor-password">Password:</label>
                <input class="input-signuppage" type="password" id="doctor-password" name="password" required>
            </div>
            <button type="submit" class="signup-btn-signuppage">Sign-Up</button>
        </form>
    </div>
</main>

<footer>
    <div class="footer-container">
        <div class="footer-info">
            <p>Copyright &copy; 2025 ADORE CLINIC. All rights reserved.</p>
            <p>123 Health Street, City, Country</p>
        </div>
        <div class="contact-info">
            <p>Contact Us:</p>
            <div class="contact-item">
                <img src="image\phone.png" alt="Phone Icon" class="icon">
                <span>+966111111</span>
            </div>
            <div class="contact-item">
                <img src="image\telephone.png" alt="Mobile Icon" class="icon">
                <span>+01134657</span>
            </div>
            <div class="contact-item">
                <img src="image\Email.png" alt="Email Icon" class="icon">
                <span>info@adoreclinic.com</span>
            </div>
        </div>
    </div>
</footer>

<script>
    
    document.addEventListener("DOMContentLoaded", function () {
    // Listen for form submissions only when a form is selected
    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", function (e) {
            e.preventDefault(); // Prevent default form submission

            // Check selected role and get the corresponding form
            let selectedRole = document.querySelector('.role-selection-signuppage input[type="radio"]:checked');
            

            let form = selectedRole.value === "patient" 
                     ? document.getElementById("patient-form-signuppage") 
                     : document.getElementById("doctor-form-signuppage");

            if (!form) {
                alert("Error: Form not found.");
                return;
            }

            let formData = new FormData(form);
            let actionUrl = form.id === "patient-form-signuppage" ? "signup_patient.php" : "signup_doctor.php";

            fetch(actionUrl, {
                method: "POST",
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === "success") {
                    window.location.href = data.redirect; // Redirect to success page
                } else {
                    alert(data.message || "Signup failed!");
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("There was an error processing your request. Please try again.");
            });
        });
    });
});

</script>

</body>

</html>



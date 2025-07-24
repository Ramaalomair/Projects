document.addEventListener("DOMContentLoaded", function () {
    const specialtySelect = document.getElementById("specialty-bookingPage");
    const doctorSelect = document.getElementById("doctor-bookingPage");

    // Ensure elements exist
    if (!specialtySelect || !doctorSelect) {
        console.log("Required elements not found.");
        return;
    }

    specialtySelect.addEventListener("change", function () {
        const specialty = specialtySelect.value.trim();

        // If no specialty selected (e.g., "All Specialties"), reload page
        if (!specialty) {
            window.location.reload();
            return;
        }

        // Send AJAX request to fetch doctors
        fetch("book_appointment.php", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: `specialty=${encodeURIComponent(specialty)}&ajax=true`
        })
        .then(response => response.json())
        .then(doctors => {
            // Clear and repopulate doctor dropdown
            doctorSelect.innerHTML = "<option value='' disabled selected>Select Doctor</option>";

            if (doctors.length > 0) {
                doctors.forEach(doctor => {
                    const option = document.createElement("option");
                    option.value = doctor.id;
                    option.textContent = doctor.name;
                    doctorSelect.appendChild(option);
                });
            } else {
                const option = document.createElement("option");
                option.value = "";
                option.textContent = "No doctors available";
                doctorSelect.appendChild(option);
            }
        })
        .catch(error => {
            console.error("Error fetching doctors:", error);
            doctorSelect.innerHTML = "<option value='' disabled selected>Error loading doctors</option>";
        });
    });
});

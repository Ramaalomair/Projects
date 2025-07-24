

// Signup Page Functions

// Function to show the selected form and hide others
function showForm(formId) {
    const patientForm = document.getElementById("patient-form-signuppage");
    const doctorForm = document.getElementById("doctor-form-signuppage");
    const roleForm = document.getElementById("role-form-signuppage");

    if (patientForm && doctorForm && roleForm) {
        // Hide all forms first
        patientForm.classList.add("hidden");
        doctorForm.classList.add("hidden");
        roleForm.classList.add("hidden"); // Hide role selection

        // Show the selected form
        const selectedForm = document.getElementById(formId);
        if (selectedForm) {
            selectedForm.classList.remove("hidden");
        }
    }
}


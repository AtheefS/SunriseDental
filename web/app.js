// =====================================================
// SUNRISE DENTAL CLINIC - FRONTEND JAVASCRIPT
// =====================================================


// =====================================================
// LOGIN
// =====================================================

const loginForm = document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const username =
            document.getElementById("username").value.trim();

        const password =
            document.getElementById("password").value.trim();

        const message =
            document.getElementById("message");

        message.textContent = "Checking login...";

        try {

            const response = await fetch(
                "/api/login",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        username: username,
                        password: password
                    })
                }
            );

            const data = await response.json();

            if (data.success) {

                message.textContent = "Login successful!";

                sessionStorage.setItem(
                    "username",
                    data.username
                );

                sessionStorage.setItem(
                    "role",
                    data.role
                );

                setTimeout(function () {
                    window.location.href = "dashboard.html";
                }, 500);

            } else {

                message.textContent =
                    data.message ||
                    "Invalid username or password.";

            }

        } catch (error) {

            console.error(error);

            message.textContent =
                "Unable to connect to the Java server. Make sure HttpServer is running.";
        }
    });
}


// =====================================================
// GENERATE BILL
// =====================================================

const billForm = document.getElementById("billForm");

if (billForm) {

    billForm.addEventListener("submit", async function (event) {

        event.preventDefault();

        const appointmentNumber =
            document.getElementById("appointmentNumber").value.trim();

        const message =
            document.getElementById("message");

        const result =
            document.getElementById("billResult");

        if (!appointmentNumber) {

            message.textContent =
                "Please enter an appointment number.";

            return;
        }

        message.textContent =
            "Finding appointment...";

        try {

            // -------------------------------------------------
            // STEP 1: Find appointment
            // -------------------------------------------------

            const appointmentResponse = await fetch(
                "/api/appointments?number="
                + encodeURIComponent(appointmentNumber)
            );

            const appointment =
                await appointmentResponse.json();

            if (!appointment.success) {

                message.textContent =
                    appointment.message ||
                    "Appointment not found.";

                return;
            }


            // -------------------------------------------------
            // STEP 2: Get required IDs
            // -------------------------------------------------

            const appointmentId =
                appointment.appointmentId;

            const treatmentId =
                appointment.treatmentId;


            if (!appointmentId || !treatmentId) {

                message.textContent =
                    "Appointment information is incomplete.";

                return;
            }


            // -------------------------------------------------
            // STEP 3: Generate bill
            // -------------------------------------------------

            message.textContent =
                "Calculating bill...";

            const billResponse = await fetch(
                "/api/bills",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        appointmentId: appointmentId,
                        treatmentId: treatmentId
                    })
                }
            );

            const bill =
                await billResponse.json();


            // -------------------------------------------------
            // STEP 4: Display bill
            // -------------------------------------------------

            if (bill.success) {

                message.textContent =
                    "Bill generated successfully.";

                result.innerHTML = `
                    <div class="bill-card">

                        <h2>Sunrise Dental Clinic</h2>

                        <h3>Patient Bill / Receipt</h3>

                        <p>
                            <strong>Appointment Number:</strong>
                            ${appointment.appointmentNumber}
                        </p>

                        <p>
                            <strong>Patient Name:</strong>
                            ${appointment.patientName}
                        </p>

                        <p>
                            <strong>Dentist:</strong>
                            ${appointment.dentistName}
                        </p>

                        <p>
                            <strong>Treatment:</strong>
                            ${appointment.treatmentName}
                        </p>

                        <hr>

                        <p>
                            <strong>Consultation Fee:</strong>
                            Rs. ${Number(bill.consultationFee).toFixed(2)}
                        </p>

                        <p>
                            <strong>Treatment Cost:</strong>
                            Rs. ${Number(bill.treatmentCost).toFixed(2)}
                        </p>

                        <h2>
                            Total Amount:
                            Rs. ${Number(bill.totalAmount).toFixed(2)}
                        </h2>

                        <button
                            type="button"
                            onclick="window.print()">
                            Print Bill
                        </button>

                    </div>
                `;

            } else {

                message.textContent =
                    bill.message ||
                    "Unable to generate bill.";
            }

        } catch (error) {

            console.error(error);

            message.textContent =
                "Unable to connect to the Java server.";
        }
    });
}
// =====================================
// APPOINTMENT DATE VALIDATION
// =====================================

const appointmentDateInput = document.getElementById("appointmentDate");

if (appointmentDateInput) {

    // Prevent selecting a past date
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    const todayString = `${year}-${month}-${day}`;

    appointmentDateInput.min = todayString;
}
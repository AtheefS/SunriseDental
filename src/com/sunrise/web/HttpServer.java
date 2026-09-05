package com.sunrise.web;
import com.sunrise.controller.BillController;
import com.sunrise.model.Bill;
import com.sunrise.controller.LoginController;
import com.sunrise.controller.AppointmentController;
import com.sunrise.model.User;
import com.sunrise.dao.DentistDAO;
import com.sunrise.dao.TreatmentDAO;
import com.sunrise.model.Dentist;
import com.sunrise.model.Treatment;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class HttpServer {

    public static void main(String[] args) throws Exception {

        com.sun.net.httpserver.HttpServer server =
                com.sun.net.httpserver.HttpServer.create(
                        new InetSocketAddress(8080), 0);

        // ==============================
        // TEST API
        // ==============================
        server.createContext("/api/test", exchange -> {

            String response = """
                    {
                      "success": true,
                      "message": "Sunrise Dental Java Web Service is running"
                    }
                    """;

            sendResponse(exchange, response, 200);
        });


        // ==============================
        // LOGIN API
        // ==============================
        server.createContext("/api/login", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

                sendResponse(
                        exchange,
                        "{\"success\":false,\"message\":\"POST required\"}",
                        405
                );

                return;
            }

            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String username =
                    extractJsonValue(body, "username");

            String password =
                    extractJsonValue(body, "password");

            LoginController controller =
                    new LoginController();

            User user =
                    controller.login(username, password);

            if (user != null) {

                String response = """
                        {
                          "success": true,
                          "message": "Login successful",
                          "username": "%s",
                          "role": "%s"
                        }
                        """.formatted(
                        escapeJson(user.getUsername()),
                        escapeJson(user.getRole())
                );

                sendResponse(exchange, response, 200);

            } else {

                sendResponse(
                        exchange,
                        "{\"success\":false,\"message\":\"Invalid username or password\"}",
                        401
                );
            }
        });


        // ==============================
        // GET DENTISTS
        // ==============================
        server.createContext("/api/dentists", exchange -> {

            DentistDAO dao = new DentistDAO();

            List<Dentist> dentists =
                    dao.getAllDentists();

            StringBuilder json =
                    new StringBuilder("[");

            boolean first = true;

            for (Dentist dentist : dentists) {

                if (!first) {
                    json.append(",");
                }

                json.append("""
                        {
                          "id": %d,
                          "name": "%s"
                        }
                        """.formatted(
                        dentist.getDentistId(),
                        escapeJson(dentist.getDentistName())
                ));

                first = false;
            }

            json.append("]");

            sendResponse(
                    exchange,
                    json.toString(),
                    200
            );
        });


        // ==============================
        // GET TREATMENTS
        // ==============================
        server.createContext("/api/treatments", exchange -> {

            TreatmentDAO dao =
                    new TreatmentDAO();

            List<Treatment> treatments =
                    dao.getAllTreatments();

            StringBuilder json =
                    new StringBuilder("[");

            boolean first = true;

            for (Treatment treatment : treatments) {

                if (!first) {
                    json.append(",");
                }

                json.append("""
                        {
                          "id": %d,
                          "name": "%s",
                          "cost": %s
                        }
                        """.formatted(
                        treatment.getTreatmentId(),
                        escapeJson(treatment.getTreatmentName()),
                        treatment.getTreatmentCost()
                ));

                first = false;
            }

            json.append("]");

            sendResponse(
                    exchange,
                    json.toString(),
                    200
            );
        });


       
// ==============================
// APPOINTMENT API
// ==============================
server.createContext("/api/appointments", exchange -> {

    String method = exchange.getRequestMethod();

    // ==============================
    // POST - REGISTER APPOINTMENT
    // ==============================
    if (method.equalsIgnoreCase("POST")) {

        try {

            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String patientName =
                    extractJsonValue(body, "patientName");

            String address =
                    extractJsonValue(body, "address");

            String contactNumber =
                    extractJsonValue(body, "contactNumber");

            String dentistIdText =
                    extractJsonValue(body, "dentistId");

            String treatmentIdText =
                    extractJsonValue(body, "treatmentId");

            String appointmentDate =
                    extractJsonValue(body, "appointmentDate");

            String appointmentTime =
                    extractJsonValue(body, "appointmentTime");

            int dentistId =
                    Integer.parseInt(dentistIdText);

            int treatmentId =
                    Integer.parseInt(treatmentIdText);

            LocalDate date =
                    LocalDate.parse(appointmentDate);

            LocalTime time =
                    LocalTime.parse(appointmentTime);

            AppointmentController controller =
                    new AppointmentController();

            String result =
                    controller.registerAppointment(
                            patientName,
                            address,
                            contactNumber,
                            dentistId,
                            treatmentId,
                            date,
                            time
                    );

            if (result.startsWith("APT-")) {

                String response = """
                        {
                          "success": true,
                          "message": "Appointment registered successfully",
                          "appointmentNumber": "%s"
                        }
                        """.formatted(
                        escapeJson(result)
                );

                sendResponse(
                        exchange,
                        response,
                        201
                );

            } else {

                String response = """
                        {
                          "success": false,
                          "message": "%s"
                        }
                        """.formatted(
                        escapeJson(result)
                );

                sendResponse(
                        exchange,
                        response,
                        400
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            String response = """
                    {
                      "success": false,
                      "message": "Invalid appointment data. Please check all fields."
                    }
                    """;

            sendResponse(
                    exchange,
                    response,
                    400
            );
        }

        return;
    }


    // ==============================
    // GET - SEARCH APPOINTMENT
    // ==============================
    if (method.equalsIgnoreCase("GET")) {

        try {

            String query =
                    exchange.getRequestURI().getQuery();

            if (query == null || !query.startsWith("number=")) {

                sendResponse(
                        exchange,
                        "{\"success\":false,\"message\":\"Appointment number is required\"}",
                        400
                );

                return;
            }

            String appointmentNumber =
                    java.net.URLDecoder.decode(
                            query.substring(7),
                            StandardCharsets.UTF_8
                    );

            com.sunrise.dao.AppointmentDAO dao =
                    new com.sunrise.dao.AppointmentDAO();

            com.sunrise.model.Appointment appointment =
                    dao.getAppointmentByNumber(
                            appointmentNumber
                    );

            if (appointment == null) {

                sendResponse(
                        exchange,
                        "{\"success\":false,\"message\":\"Appointment not found\"}",
                        404
                );

                return;
            }

            String response = """
                    {
                      "success": true,
                      "appointmentId": %d,
                      "appointmentNumber": "%s",
                      "patientName": "%s",
                      "address": "%s",
                      "contactNumber": "%s",
                      "dentistName": "%s",
                      "treatmentId": %d,
                      "treatmentName": "%s",
                      "appointmentDate": "%s",
                      "appointmentTime": "%s",
                      "status": "%s"
                    }
                   """.formatted(
                    appointment.getAppointmentId(),
                    escapeJson(appointment.getAppointmentNumber()),
                    escapeJson(appointment.getPatientName()),
                    escapeJson(appointment.getAddress()),
                    escapeJson(appointment.getContactNumber()),
                    escapeJson(appointment.getDentistName()),
                    appointment.getTreatmentId(),
                    escapeJson(appointment.getTreatmentName()),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    escapeJson(appointment.getStatus())
        );
            sendResponse(
                    exchange,
                    response,
                    200
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "{\"success\":false,\"message\":\"Unable to search appointment\"}",
                    500
            );
        }

        return;
    }


    // ==============================
    // OTHER METHODS
    // ==============================

    sendResponse(
            exchange,
            "{\"success\":false,\"message\":\"Method not allowed\"}",
            405
    );

});
// ================================
// BILL GENERATION API
// ================================
server.createContext("/api/bills", exchange -> {

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

        sendResponse(
                exchange,
                "{\"success\":false,\"message\":\"POST required\"}",
                405
        );

        return;
    }

    try {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        String appointmentIdText =
                extractJsonValue(body, "appointmentId");

        String treatmentIdText =
                extractJsonValue(body, "treatmentId");

        if (appointmentIdText == null ||
                treatmentIdText == null ||
                appointmentIdText.isBlank() ||
                treatmentIdText.isBlank()) {

            sendResponse(
                    exchange,
                    "{\"success\":false,\"message\":\"appointmentId and treatmentId are required\"}",
                    400
            );

            return;
        }

        int appointmentId =
                Integer.parseInt(appointmentIdText);

        int treatmentId =
                Integer.parseInt(treatmentIdText);

        BillController billController =
                new BillController();

        Bill bill =
                billController.generateBill(
                        appointmentId,
                        treatmentId
                );

        String response =
                "{"
                + "\"success\":true,"
                + "\"message\":\"Bill generated successfully\","
                + "\"appointmentId\":" + appointmentId + ","
                + "\"consultationFee\":" + bill.getConsultationFee() + ","
                + "\"treatmentCost\":" + bill.getTreatmentCost() + ","
                + "\"totalAmount\":" + bill.getTotalAmount()
                + "}";

        sendResponse(exchange, response, 200);

    } catch (NumberFormatException e) {

        sendResponse(
                exchange,
                "{\"success\":false,\"message\":\"Invalid appointment or treatment ID\"}",
                400
        );

    } catch (Exception e) {

        e.printStackTrace();

        sendResponse(
                exchange,
                "{\"success\":false,\"message\":\"Unable to generate bill\"}",
                500
        );
    }
});
// ================================
// STATIC WEB PAGES
// ================================

server.createContext("/", exchange -> {

    String path = exchange.getRequestURI().getPath();

    if (path.equals("/")) {
        path = "/index.html";
    }

    String fileName = path.substring(1);

    // Prevent path traversal
    if (fileName.contains("..")) {
        sendResponse(
                exchange,
                "Invalid file path",
                400
        );
        return;
    }

    try {

        java.nio.file.Path filePath =
                java.nio.file.Path.of("web", fileName);

        if (!java.nio.file.Files.exists(filePath) ||
                java.nio.file.Files.isDirectory(filePath)) {

            sendResponse(
                    exchange,
                    "<h1>404 Not Found</h1><p>Page not found</p>",
                    404
            );

            return;
        }

        byte[] fileBytes =
                java.nio.file.Files.readAllBytes(filePath);

        String contentType = "text/html";

        if (fileName.endsWith(".css")) {
            contentType = "text/css";
        } else if (fileName.endsWith(".js")) {
            contentType = "application/javascript";
        }

        exchange.getResponseHeaders()
                .set("Content-Type", contentType + "; charset=UTF-8");

        exchange.sendResponseHeaders(
                200,
                fileBytes.length
        );

        try (java.io.OutputStream output =
                     exchange.getResponseBody()) {

            output.write(fileBytes);
        }

    } catch (Exception e) {

        e.printStackTrace();

        sendResponse(
                exchange,
                "<h1>500 Internal Server Error</h1>",
                500
        );
    }
});
// ==========================================
// DAILY APPOINTMENT REPORT
// ==========================================

server.createContext("/api/reports/daily", exchange -> {

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

        sendResponse(
                exchange,
                "{\"success\":false,\"message\":\"GET required\"}",
                405
        );

        return;
    }

    try {

        String query =
                exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("date=")) {

            sendResponse(
                    exchange,
                    "{\"success\":false,\"message\":\"Date is required\"}",
                    400
            );

            return;
        }

        String date =
                java.net.URLDecoder.decode(
                        query.substring(5),
                        StandardCharsets.UTF_8
                );

        String sql = """
                SELECT a.appointment_number,
                       p.patient_name,
                       d.dentist_name,
                       t.treatment_name,
                       a.appointment_time,
                       a.status
                FROM appointments a
                JOIN patients p
                    ON a.patient_id = p.patient_id
                JOIN dentists d
                    ON a.dentist_id = d.dentist_id
                JOIN treatments t
                    ON a.treatment_id = t.treatment_id
                WHERE a.appointment_date = ?
                ORDER BY a.appointment_time
                """;

        java.util.List<String> appointments =
                new java.util.ArrayList<>();

        try (
                java.sql.Connection connection =
                        com.sunrise.config.DatabaseConnection.getConnection();

                java.sql.PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setDate(
                    1,
                    java.sql.Date.valueOf(date)
            );

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                String appointment = String.format(
                        """
                        {
                          "appointmentNumber": "%s",
                          "patientName": "%s",
                          "dentistName": "%s",
                          "treatmentName": "%s",
                          "appointmentTime": "%s",
                          "status": "%s"
                        }
                        """,
                        escapeJson(
                                resultSet.getString("appointment_number")
                        ),
                        escapeJson(
                                resultSet.getString("patient_name")
                        ),
                        escapeJson(
                                resultSet.getString("dentist_name")
                        ),
                        escapeJson(
                                resultSet.getString("treatment_name")
                        ),
                        resultSet.getTime("appointment_time"),
                        escapeJson(
                                resultSet.getString("status")
                        )
                );

                appointments.add(appointment);
            }
        }

        String response =
                "{\"success\":true,\"appointments\":["
                + String.join(",", appointments)
                + "]}";

        sendResponse(
                exchange,
                response,
                200
        );

    } catch (Exception e) {

        e.printStackTrace();

        sendResponse(
                exchange,
                "{\"success\":false,\"message\":\"Unable to generate daily report\"}",
                500
        );
    }
});


// ==========================================
// BILLING & REVENUE REPORT
// ==========================================

server.createContext("/api/reports/billing", exchange -> {

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

        sendResponse(
                exchange,
                "{\"success\":false,\"message\":\"GET required\"}",
                405
        );

        return;
    }

    try {

        String sql = """
                SELECT COUNT(*) AS total_bills,
                       COALESCE(SUM(total_amount), 0) AS total_revenue
                FROM bills
                """;

        int totalBills = 0;
        java.math.BigDecimal totalRevenue =
                java.math.BigDecimal.ZERO;

        try (
                java.sql.Connection connection =
                        com.sunrise.config.DatabaseConnection.getConnection();

                java.sql.PreparedStatement statement =
                        connection.prepareStatement(sql);

                java.sql.ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                totalBills =
                        resultSet.getInt("total_bills");

                totalRevenue =
                        resultSet.getBigDecimal("total_revenue");
            }
        }

        String response = """
                {
                  "success": true,
                  "totalBills": %d,
                  "totalRevenue": %s
                }
                """.formatted(
                totalBills,
                totalRevenue
        );

        sendResponse(
                exchange,
                response,
                200
        );

    } catch (Exception e) {

        e.printStackTrace();

        sendResponse(
                exchange,
                "{\"success\":false,\"message\":\"Unable to generate billing report\"}",
                500
        );
    }
});
        // ==============================
        // START SERVER
        // ==============================

        server.start();

        System.out.println("======================================");
        System.out.println("Sunrise Dental Web Service Started");
        System.out.println("http://localhost:8080");
        System.out.println("======================================");
    }


    // ==============================
    // SEND HTTP RESPONSE
    // ==============================

    private static void sendResponse(
            HttpExchange exchange,
            String response,
            int statusCode) throws IOException {

        byte[] bytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(bytes);
        }
    }


    // ==============================
    // READ JSON VALUE
    // ==============================

    private static String extractJsonValue(
            String json,
            String key) {

        String search =
                "\"" + key + "\"";

        int keyPosition =
                json.indexOf(search);

        if (keyPosition == -1) {
            return "";
        }

        int colonPosition =
                json.indexOf(":", keyPosition);

        if (colonPosition == -1) {
            return "";
        }

        // Handle numbers such as dentistId and treatmentId
        int quotePosition =
                json.indexOf("\"", colonPosition);

        if (quotePosition != -1) {

            String beforeQuote =
                    json.substring(
                            colonPosition + 1,
                            quotePosition
                    ).trim();

            if (beforeQuote.isEmpty()) {

                int secondQuote =
                        json.indexOf(
                                "\"",
                                quotePosition + 1
                        );

                if (secondQuote != -1) {

                    return json.substring(
                            quotePosition + 1,
                            secondQuote
                    );
                }
            }
        }

        // Handle numeric values
        String value =
                json.substring(
                        colonPosition + 1
                ).trim();

        int comma =
                value.indexOf(",");

        int closingBrace =
                value.indexOf("}");

        if (comma != -1 &&
                (closingBrace == -1 || comma < closingBrace)) {

            value =
                    value.substring(0, comma);
        } else if (closingBrace != -1) {

            value =
                    value.substring(0, closingBrace);
        }

        return value
                .trim()
                .replace("\"", "");
    }


    // ==============================
    // ESCAPE JSON
    // ==============================

    private static String escapeJson(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
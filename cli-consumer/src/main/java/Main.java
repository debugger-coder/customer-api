import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * CLI Consumer Application for the Customer API.
 * This application provides a command-line interface to interact with the Customer API,
 * allowing users to perform CRUD operations on customer data.
 */
public class Main {
    /** Default base URL for the Customer API */
    private static final String DEFAULT_API_BASE_URL = "http://localhost:8080/api/customers";

    /** Base URL for the Customer API - can be overridden via command line */
    private static String apiBaseUrl = DEFAULT_API_BASE_URL;

    /** Scanner for reading user input */
    private static final Scanner scanner = new Scanner(System.in);

    /** Verbose mode flag */
    private static boolean verboseMode = false;

    /**
     * Main method that starts the CLI application.
     * Displays a menu and processes user choices until the user chooses to exit.
     *
     * @param args Command line arguments:
     *             -u, --url: API base URL (default: http://localhost:8080/api/customers)
     *             -v, --verbose: Enable verbose mode
     *             -h, --help: Show help message
     */
    public static void main(String[] args) {
        // Parse command line arguments
        parseArgs(args);

        System.out.println("Customer API CLI Client");
        System.out.println("API URL: " + apiBaseUrl);
        if (verboseMode) {
            System.out.println("Verbose mode enabled");
        }
        System.out.println();

        boolean running = true;

        while (running) {
            printMenu();
            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        listAllCustomers();
                        break;
                    case 2:
                        getCustomerById();
                        break;
                    case 3:
                        createCustomer();
                        break;
                    case 4:
                        updateCustomer();
                        break;
                    case 5:
                        deleteCustomer();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (IOException e) {
                System.out.println("Error communicating with API: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }

            System.out.println(); // Empty line for readability
        }

        scanner.close();
    }

    /**
     * Displays the main menu options to the user.
     */
    private static void printMenu() {
        System.out.println("\n===== MENU =====");
        System.out.println("1. List all customers");
        System.out.println("2. Get customer by ID");
        System.out.println("3. Create new customer");
        System.out.println("4. Update customer");
        System.out.println("5. Delete customer");
        System.out.println("0. Exit");
    }

    /**
     * Lists all customers by calling the API's GET endpoint.
     *
     * @throws IOException If there's an error communicating with the API
     */
    private static void listAllCustomers() throws IOException {
        String response = sendRequest(apiBaseUrl, "GET", null);
        System.out.println("\nAll Customers:\n" + formatJson(response));
    }

    /**
     * Gets a specific customer by ID by calling the API's GET endpoint with the ID.
     * Prompts the user to enter a customer ID.
     *
     * @throws IOException If there's an error communicating with the API
     */
    private static void getCustomerById() throws IOException {
        String id = getStringInput("Enter customer ID: ");
        try {
            String response = sendRequest(apiBaseUrl + "/" + id, "GET", null);
            System.out.println("\nCustomer Details:\n" + formatJson(response));
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                System.out.println("Customer not found with ID: " + id);
            } else {
                throw e;
            }
        }
    }

    /**
     * Creates a new customer by calling the API's POST endpoint.
     * Prompts the user to enter customer details.
     *
     * @throws IOException If there's an error communicating with the API
     */
    private static void createCustomer() throws IOException {
        System.out.println("\nEnter customer details:");
        String givenName = getStringInput("First Name: ");
        String middleInitial = getStringInput("Middle Initial (optional, press Enter to skip): ");
        String surname = getStringInput("Last Name: ");
        String email = getStringInput("Email: ");
        String phone = getStringInput("Phone Number: ");

        // Create JSON payload for the request
        String json = String.format("{\"givenName\":\"%s\",\"middleInitial\":\"%s\",\"surname\":\"%s\",\"primaryEmail\":\"%s\",\"contactNumber\":\"%s\"}",
                givenName, middleInitial, surname, email, phone);

        String response = sendRequest(apiBaseUrl, "POST", json);
        System.out.println("\nCustomer created successfully:\n" + formatJson(response));
    }

    /**
     * Updates an existing customer by calling the API's PUT endpoint.
     * First retrieves the current customer data, then prompts the user for updated information.
     *
     * @throws IOException If there's an error communicating with the API
     */
    private static void updateCustomer() throws IOException {
        String id = getStringInput("Enter customer ID to update: ");

        try {
            // First get the current customer data
            String currentData = sendRequest(apiBaseUrl + "/" + id, "GET", null);
            System.out.println("\nCurrent customer data:\n" + formatJson(currentData));

            // Now get updated information
            System.out.println("\nEnter updated customer details (press Enter to keep current value):");
            String givenName = getStringInput("First Name: ");
            String middleInitial = getStringInput("Middle Initial: ");
            String surname = getStringInput("Last Name: ");
            String email = getStringInput("Email: ");
            String phone = getStringInput("Phone Number: ");

            // Create JSON payload for the request
            String json = String.format("{\"givenName\":\"%s\",\"middleInitial\":\"%s\",\"surname\":\"%s\",\"primaryEmail\":\"%s\",\"contactNumber\":\"%s\"}",
                    givenName, middleInitial, surname, email, phone);

            String response = sendRequest(apiBaseUrl + "/" + id, "PUT", json);
            System.out.println("\nCustomer updated successfully:\n" + formatJson(response));
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                System.out.println("Customer not found with ID: " + id);
            } else {
                throw e;
            }
        }
    }

    /**
     * Deletes a customer by calling the API's DELETE endpoint.
     * Prompts the user to enter a customer ID to delete.
     *
     * @throws IOException If there's an error communicating with the API
     */
    private static void deleteCustomer() throws IOException {
        String id = getStringInput("Enter customer ID to delete: ");

        try {
            sendRequest(apiBaseUrl + "/" + id, "DELETE", null);
            System.out.println("Customer deleted successfully.");
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                System.out.println("Customer not found with ID: " + id);
            } else {
                throw e;
            }
        }
    }

    /**
     * Sends an HTTP request to the API.
     *
     * @param urlString The URL to send the request to
     * @param method The HTTP method (GET, POST, PUT, DELETE)
     * @param jsonBody The JSON body for POST and PUT requests (null for GET and DELETE)
     * @return The response from the API as a string
     * @throws IOException If there's an error communicating with the API
     */
    private static String sendRequest(String urlString, String method, String jsonBody) throws IOException {
        if (verboseMode) {
            System.out.println("\n[DEBUG] Sending " + method + " request to: " + urlString);
            if (jsonBody != null) {
                System.out.println("[DEBUG] Request body: " + jsonBody);
            }
        }

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        // Add JSON body for POST and PUT requests
        if (jsonBody != null) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        // Check for error responses
        int responseCode = connection.getResponseCode();
        if (verboseMode) {
            System.out.println("[DEBUG] Response code: " + responseCode);
        }

        if (responseCode >= 400) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        // For DELETE requests with 204 No Content response
        if (responseCode == 204) {
            if (verboseMode) {
                System.out.println("[DEBUG] No content in response (204)");
            }
            return "";
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (verboseMode) {
            System.out.println("[DEBUG] Response: " + response.toString());
        }

        return response.toString();
    }

    /**
     * Gets a string input from the user.
     *
     * @param prompt The prompt to display to the user
     * @return The user's input as a string
     */
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Gets an integer input from the user.
     * Continues prompting until a valid integer is entered.
     *
     * @param prompt The prompt to display to the user
     * @return The user's input as an integer
     */
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Formats a JSON string for better readability.
     * This is a simple formatter that adds line breaks and indentation.
     * In a real application, you would use a proper JSON library.
     *
     * @param json The JSON string to format
     * @return The formatted JSON string
     */
    private static String formatJson(String json) {
        return json.replace(",", ",\n  ")
                .replace("{", "{\n  ")
                .replace("}", "\n}");
    }

    /**
     * Parses command line arguments.
     *
     * @param args Command line arguments
     */
    private static void parseArgs(String[] args) {
        if (args.length == 0) {
            return; // Use defaults
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-u":
                case "--url":
                    if (i + 1 < args.length) {
                        apiBaseUrl = args[++i];
                    } else {
                        System.err.println("Error: URL argument is missing");
                        showHelp();
                        System.exit(1);
                    }
                    break;

                case "-v":
                case "--verbose":
                    verboseMode = true;
                    break;

                case "-h":
                case "--help":
                    showHelp();
                    System.exit(0);
                    break;

                default:
                    System.err.println("Unknown argument: " + arg);
                    showHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Displays help information.
     */
    private static void showHelp() {
        System.out.println("Customer API CLI Client");
        System.out.println("Usage: java -jar cli-consumer.jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -u, --url <url>    API base URL (default: " + DEFAULT_API_BASE_URL + ")");
        System.out.println("  -v, --verbose      Enable verbose mode");
        System.out.println("  -h, --help         Show this help message");
    }
}
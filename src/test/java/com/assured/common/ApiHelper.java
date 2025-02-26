package com.assured.common;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;

import java.io.File;

public class ApiHelper {

    // Replace with your actual API endpoints.
    private static final String TOKEN_API_URL = "http://your-domain.com/api/token";
    private static final String LOGIN_API_URL = "http://your-domain.com/api/login";
    private static final String DELETE_API_URL_BASE = "http://your-domain.com/api/delete-data/";

    /**
     * This method runs after all tests have completed, cleaning up account data.
     */
    @AfterClass
    public void cleanUpAccountData() {
        try {
            // Step 1: Get access token from the token API.
            String accessToken = getAccessToken();
            System.out.println("Access Token: " + accessToken);

            // Step 2: Login and retrieve the client id using the access token.
            String clientId = loginAndGetClientId(accessToken);
            System.out.println("Client ID: " + clientId);

            // Step 3: Hit the delete API with the access token and client id.
            deleteAccountData(accessToken, clientId);
            System.out.println("Delete API call executed for client: " + clientId);
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the access token by calling the token API.
     * Payload is read from a JSON file.
     *
     * @return The access token extracted from the JSON response.
     */
    private String getAccessToken() {
        // JSON file for the token API request payload.
        File tokenPayloadFile = new File("src/test/resources/tokenPayload.json");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(tokenPayloadFile)
                .post(TOKEN_API_URL);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get access token. Status code: " + response.statusCode());
        }

        // Extract access token from the JSON response at data.jwt.access
        String accessToken = response.jsonPath().getString("data.jwt.access");
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Access token not found in the response");
        }
        return accessToken;
    }

    /**
     * Logs in using the access token and retrieves the client id from the login response.
     * Payload is read from a JSON file.
     *
     * @param accessToken The bearer token to authorize the login API.
     * @return The client id extracted from extra_data.client_details.id.
     */
    private String loginAndGetClientId(String accessToken) {
        // JSON file for the login API request payload.
        File loginPayloadFile = new File("src/test/resources/loginPayload.json");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(loginPayloadFile)
                .post(LOGIN_API_URL);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to login and get client id. Status code: " + response.statusCode());
        }

        // Extract client id from extra_data.client_details.id in the JSON response.
        String clientId = response.jsonPath().getString("extra_data.client_details.id");
        if (clientId == null || clientId.isEmpty()) {
            throw new RuntimeException("Client id not found in the login response");
        }
        return clientId;
    }

    /**
     * Deletes the account data using the delete API with the provided access token and client id.
     *
     * @param accessToken The bearer token.
     * @param clientId    The client id to be appended to the delete URL.
     */
    private void deleteAccountData(String accessToken, String clientId) {
        String deleteUrl = DELETE_API_URL_BASE + clientId;
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .delete(deleteUrl);

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to delete account data. Status code: " + response.statusCode());
        }
    }
}

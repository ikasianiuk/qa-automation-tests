package clients;

import config.TestConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Booking;

import static io.restassured.RestAssured.given;

public class BookingClient {
    private static RequestSpecification request() {
        return given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType("application/json")
                .accept("application/json");
    }

    public Response createBooking(Booking booking) {
        return request().body(booking).post("/booking");
    }

    public Response getBookingById(int id) {
        return request().get("/booking/{id}", id);
    }
}

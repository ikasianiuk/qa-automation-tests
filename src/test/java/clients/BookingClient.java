package api;

import config.TestConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Booking;

import static io.restassured.RestAssured.given;

public class BookingClient {
    private static RequestSpecification request() {
        return given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    public Response createBooking(Booking booking) {
        return request().body(booking).post("/booking");
    }

    public Response getBookingById(int id) {
        return request().get("/booking/{id}", id);
    }
}

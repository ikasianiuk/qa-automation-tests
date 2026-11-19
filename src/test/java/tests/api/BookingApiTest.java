package tests.api;

import clients.BookingClient;
import io.restassured.response.Response;
import models.Booking;
import models.BookingDates;
import models.CreatedBooking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookingApiTest {
    private final BookingClient bookingClient = new BookingClient();

    private static Booking bookingWithFirstname(String firstname) {
        return new Booking(
                firstname, "Brown", 111, true,
                new BookingDates("2027-01-01", "2027-01-10"),
                "Breakfast");
    }

    @Test
    void createdBookingCanBeRetrievedById() {
        Booking booking = bookingWithFirstname("Jim");

        Response createResponse = bookingClient.createBooking(booking);
        assertEquals(200, createResponse.statusCode());

        int createdBookingId = createResponse.as(CreatedBooking.class).bookingid();
        assertTrue(createdBookingId > 0,
                "Booking id should be positive");

        Response retrieveResponse = bookingClient.getBookingById(createdBookingId);
        assertEquals(200, retrieveResponse.statusCode());

        Booking retrievedBooking = retrieveResponse.as(Booking.class);
        assertEquals(booking, retrievedBooking);
    }

    @Test
    void nonExistingBookingReturnsNotFound() {
        Response response = bookingClient.getBookingById(Integer.MAX_VALUE);

        assertEquals(404, response.statusCode());
    }

    // The docs mark firstname as required but don't define the error status;
    // the API currently answers 500, so we only assert that the request is rejected.
    @Test
    void bookingWithoutFirstnameIsRejected() {
        Booking booking = bookingWithFirstname(null);

        Response response = bookingClient.createBooking(booking);

        assertTrue(response.statusCode() >= 400,
                "Expected an error status but got " + response.statusCode());
    }
}

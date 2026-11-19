# QA Automation Tests

Automated tests for two public demo systems:

- **UI** – [SauceDemo](https://www.saucedemo.com) (login, product sorting, shopping cart), written with Selenide.
- **API** – [Restful Booker](https://restful-booker.herokuapp.com) (create and read a booking), written with REST Assured.

Stack: Java 17, Gradle (wrapper included), JUnit 5, Selenide, REST Assured, Jackson.

## Prerequisites

To run the tests locally:

- JDK 17 or newer
- Google Chrome (for the UI tests)
- Internet access (both systems under test are public sites)

Gradle does not need to be installed: `./gradlew` downloads the correct version.

To run the tests in a container, only Docker is needed.

## Running the tests locally

All tests:

```bash
./gradlew test
```

UI tests only:

```bash
./gradlew test --tests 'tests.ui.*'
```

API tests only:

```bash
./gradlew test --tests 'tests.api.*'
```

Each test prints `PASSED` or `FAILED` in the console. An HTML report is written to `build/reports/tests/test/index.html`.

The browser runs headless by default. To watch the UI tests, set `selenide.headless=false` in `src/test/resources/selenide.properties`. The base URL, timeout and test-id attribute are configured in the same file. The API base URL and the demo credentials live in `src/test/java/config/TestConfig.java`.

## Running in Docker

```bash
docker build -t qa-automation-tests .
docker run --rm qa-automation-tests
```

Notes:

- The build downloads Gradle and the dependencies, so it needs internet access.
- The image is based on `debian:bookworm-slim` and installs Debian's `chromium` and `chromium-driver`, so the browser and driver versions always match. It builds for the host architecture (Intel or Apple Silicon).
- Chrome runs with `--no-sandbox` inside the container (it runs as root there). This is set through `JAVA_TOOL_OPTIONS` in the Dockerfile.
- If Chromium crashes on heavy pages, give the container more shared memory: `docker run --rm --shm-size=1g qa-automation-tests`.
- To run a subset: `docker run --rm qa-automation-tests ./gradlew test --tests 'tests.api.*'`.
- On Windows, `.gitattributes` keeps `gradlew` on LF line endings. Without it, Git may convert the file to CRLF and the container fails to start it.

## Project structure

```
src/test/java
  config/    TestConfig – constants (API URL, demo credentials)
  pages/     Page objects: LoginPage, ProductsPage, CartPage, Header
  clients/   BookingClient – sends the HTTP requests
  models/    Booking, BookingDates, CreatedBooking – Java records mapped to JSON
  tests/ui/  BaseUiTest, LoginTest, ProductCartTest
  tests/api/ BookingApiTest
src/test/resources
  selenide.properties – Selenide settings
```

## Test approach

### Test cases

| Test | What it checks |
|---|---|
| `LoginTest` | A user can log in and the Products page shows products. |
| `ProductCartTest.sortedProductCanBeAddedToCart` | Sort by price (low to high), verify the order, add a product, open the cart and verify the product is there. |
| `ProductCartTest.productsSortedCorrectly` | Parameterized: all four sort options order the list correctly (bonus). |
| `BookingApiTest.createdBookingCanBeRetrievedById` | `POST /booking`, extract the id, `GET /booking/{id}`, and compare every field (names, price, deposit, dates, additional needs). |
| `BookingApiTest.nonExistingBookingReturnsNotFound` | An unknown booking id returns 404 (bonus). |
| `BookingApiTest.bookingWithoutFirstnameIsRejected` | A booking with `firstname` set to `null` (sent as `"firstname":null`) is rejected (bonus). The API also returns 500 when the field is omitted entirely. |

### Locators

SauceDemo exposes `data-test` attributes meant for automation. They do not change with styling, layout or language, so the tests use them (Selenide's `byTestId`, configured with `selenide.test-id.attribute=data-test`). There is no XPath. The add-to-cart button is found inside the product card, so the lookup is scoped to that product. The cart test adds the first product in the list on purpose, since any product proves the flow.

### Synchronization

There are no `Thread.sleep` calls and no manual waits. Selenide elements are lazy and its `should...` assertions retry until the condition is met or the timeout (6 s, set in `selenide.properties`) runs out. `ProductsPage` has a `shouldBeOpened()` check that waits for the URL and a visible title. The cart page is verified by the retrying assertion on its items. The tests do not assert on visible text such as "Products", so a translated page does not break them.

### UI structure

- Page objects hold the locators and the actions. An action that navigates returns the next page object (`loginAs(...)` returns `ProductsPage`), so a test reads like the scenario.
- The cart link lives in a shared `Header` class that pages reuse.
- Assertions that define the scenario stay in the tests, not in the page objects.
- `BaseUiTest` closes the browser after each test, so tests are independent and do not share the cart or the session.

### API structure

- `BookingClient` only builds and sends requests and returns the REST Assured `Response`. Base URL and headers are defined once.
- Java records model the request and response bodies and Jackson converts them to and from JSON. Comparing two `Booking` records checks every field in one assertion.
- The tests own the status checks and the assertions.

## Assumptions and limitations

- Both systems are shared public demo sites. They can change, be slow or be down. The Restful Booker host runs on Heroku and its first call can be slow after idle time.
- The UI tests assume English prices in the form `$29.99` (one currency, no thousands separators).
- The default product order is already A to Z, so the `az` sort case cannot detect a sort that does nothing. The other three cases are stronger.
- Restful Booker answers `418` unless the `Accept` header is exactly `application/json`. The client therefore sets it as a plain string instead of using REST Assured's `ContentType.JSON` constant, which sends several values.
- The API documentation lists all seven request fields as required but does not define error responses. A missing `firstname` returns `500`, so the negative test only asserts that the request is rejected (status 400 or higher), not a specific code.
- `additionalneeds` is documented as required, but the API accepts a booking without it.
- The API does not validate that the check-out date is after the check-in date, and the documentation defines no such rule, so no test covers it.
- The tests create bookings and do not delete them. Cleanup would need an authentication token (`POST /auth`) plus a `DELETE /booking/{id}` call, which was left out to keep the suite simple.
- Chrome runs with `--no-sandbox` in Docker because the container runs as root.

## Possible next steps

- Allure reports and a CI pipeline (for example GitHub Actions running the Docker image).
- Separate Gradle tasks or JUnit tags for UI and API tests.
- Pin the base image tag and package versions in the Dockerfile for more reproducible builds.
- Delete the bookings created by the API tests after each run.

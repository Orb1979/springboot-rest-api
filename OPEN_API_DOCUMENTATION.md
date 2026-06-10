# OpenAPI Documentation

This REST API is documented using SpringDoc OpenAPI. You can access the API documentation at the following URL:

## OpenAPI JSON Documentation

**URL:** `http://localhost:8080/v3/api-docs`

This endpoint provides the complete OpenAPI 3.0 specification in JSON format, which can be used by API documentation tools like Swagger UI.

## Accessing the Documentation

1. Start the application:
   ```bash
   ./gradlew bootRun
   ```

2. Open your browser and navigate to:
   ```
   http://localhost:8080/v3/api-docs
   ```

3. You will see the JSON representation of the API documentation, including:
   - All available endpoints
   - Request/response models
   - Parameters
   - Operation descriptions
   - Tags and groupings

## Using with Swagger UI

To view the documentation in a user-friendly interface, you can use the built-in Swagger UI:

1. Start the application:
   ```bash
   ./gradlew bootRun
   ```
2. Open your browser and navigate to:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

The Swagger UI is now integrated into the application and will automatically connect to the OpenAPI documentation at `http://localhost:8080/v3/api-docs`.

## API Documentation Features

The OpenAPI documentation includes:
- Detailed information about all controllers (AuthorController, BookController, PublisherController)
- Request and response models with field descriptions
- HTTP methods and status codes
- Operation tags for better organization
- Security schemes (if configured)

## Testing

The OpenAPI documentation endpoint is tested in `OpenApiControllerTest.java` to ensure it's always available.
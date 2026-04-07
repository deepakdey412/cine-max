package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.ShowtimeRequest;
import com.moviebooking.dto.ShowtimeResponse;
import com.moviebooking.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/showtimes")
@Tag(name = "Showtimes", description = "Movie showtime scheduling and management APIs")
public class ShowtimeController {
    @Autowired
    ShowtimeService showtimeService;

    @GetMapping
    @Operation(
        summary = "Get all upcoming showtimes",
        description = "Retrieve all showtimes scheduled for future dates and times"
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Showtimes retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    public ResponseEntity<ApiResponse> getAllUpcomingShowtimes() {
        List<ShowtimeResponse> showtimes = showtimeService.getAllUpcomingShowtimes();
        return ResponseEntity.ok(ApiResponse.success("Showtimes retrieved successfully", showtimes));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(
        summary = "Get showtimes by movie",
        description = "Retrieve all showtimes for a specific movie"
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Showtimes retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse> getShowtimesByMovieId(
        @Parameter(description = "Movie ID", required = true, example = "1")
        @PathVariable Integer movieId) {
        List<ShowtimeResponse> showtimes = showtimeService.getShowtimesByMovieId(movieId);
        return ResponseEntity.ok(ApiResponse.success("Showtimes retrieved successfully", showtimes));
    }

    @GetMapping("/movie/{movieId}/date")
    @Operation(
        summary = "Get showtimes by movie and date",
        description = "Retrieve showtimes for a specific movie on a specific date. Useful for filtering showtimes by day."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Showtimes retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "400", description = "Bad Request - Invalid date format"),
        @SwaggerApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse> getShowtimesByMovieIdAndDate(
            @Parameter(description = "Movie ID", required = true, example = "1")
            @PathVariable Integer movieId,
            @Parameter(description = "Date and time in ISO format", required = true, example = "2024-12-25T18:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<ShowtimeResponse> showtimes = showtimeService.getShowtimesByMovieIdAndDate(movieId, date);
        return ResponseEntity.ok(ApiResponse.success("Showtimes retrieved successfully", showtimes));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get showtime by ID",
        description = "Retrieve detailed information about a specific showtime including movie details and available seats"
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Showtime retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "404", description = "Showtime not found")
    })
    public ResponseEntity<ApiResponse> getShowtimeById(
        @Parameter(description = "Showtime ID", required = true, example = "1")
        @PathVariable Integer id) {
        ShowtimeResponse showtime = showtimeService.getShowtimeById(id);
        return ResponseEntity.ok(ApiResponse.success("Showtime retrieved successfully", showtime));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create a new showtime",
        description = "Schedule a new showtime for a movie. Seats are automatically generated based on hall capacity. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Showtime created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "400", description = "Bad Request - Invalid showtime data"),
        @SwaggerApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @SwaggerApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
        @SwaggerApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse> createShowtime(
        @Parameter(description = "Showtime details including movie ID, date/time, hall, and price", required = true)
        @Valid @RequestBody ShowtimeRequest request) {
        ShowtimeResponse showtime = showtimeService.createShowtime(request);
        return ResponseEntity.ok(ApiResponse.success("Showtime created successfully", showtime));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update an existing showtime",
        description = "Update showtime details such as date/time, hall, or price. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Showtime updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "400", description = "Bad Request - Invalid showtime data"),
        @SwaggerApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @SwaggerApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
        @SwaggerApiResponse(responseCode = "404", description = "Showtime not found")
    })
    public ResponseEntity<ApiResponse> updateShowtime(
        @Parameter(description = "Showtime ID", required = true, example = "1")
        @PathVariable Integer id,
        @Parameter(description = "Updated showtime details", required = true)
        @Valid @RequestBody ShowtimeRequest request) {
        ShowtimeResponse showtime = showtimeService.updateShowtime(id, request);
        return ResponseEntity.ok(ApiResponse.success("Showtime updated successfully", showtime));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete a showtime",
        description = "Remove a showtime from the schedule. This will also delete all associated seats. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Showtime deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @SwaggerApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
        @SwaggerApiResponse(responseCode = "404", description = "Showtime not found")
    })
    public ResponseEntity<ApiResponse> deleteShowtime(
        @Parameter(description = "Showtime ID", required = true, example = "1")
        @PathVariable Integer id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok(ApiResponse.success("Showtime deleted successfully"));
    }
}

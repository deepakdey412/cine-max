package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponseDto;
import com.moviebooking.dto.ReservationRequest;
import com.moviebooking.dto.ReservationResponse;
import com.moviebooking.security.JwtUtils;
import com.moviebooking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Movie ticket reservation management APIs")
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    @Autowired
    JwtUtils jwtUtils;

    private Integer getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtils.getUserIdFromJwtToken(token);
        }
        throw new RuntimeException("Unauthorized");
    }

    @PostMapping
    @Operation(
        summary = "Create a new reservation",
        description = "Book multiple seats for a showtime. This operation is transactional - all seats are reserved or none. Requires authentication.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Invalid data or seats already booked"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Showtime or seats not found")
    })
    public ResponseEntity<ApiResponseDto> createReservation(
        @Parameter(description = "Reservation details including showtime and seat IDs", required = true)
        @Valid @RequestBody ReservationRequest request, HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        ReservationResponse reservation = reservationService.createReservation(userId, request);
        return ResponseEntity.ok(ApiResponseDto.success("Reservation created successfully", reservation));
    }

    @GetMapping("/my-reservations")
    @Operation(
        summary = "Get user's reservations",
        description = "Retrieve all reservations (past and upcoming) for the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<ApiResponseDto> getUserReservations(HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        List<ReservationResponse> reservations = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(ApiResponseDto.success("Reservations retrieved successfully", reservations));
    }

    @GetMapping("/my-upcoming-reservations")
    @Operation(
        summary = "Get user's upcoming reservations",
        description = "Retrieve only upcoming reservations for the authenticated user (future showtimes only)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming reservations retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<ApiResponseDto> getUserUpcomingReservations(HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        List<ReservationResponse> reservations = reservationService.getUserUpcomingReservations(userId);
        return ResponseEntity.ok(ApiResponseDto.success("Upcoming reservations retrieved successfully", reservations));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get reservation by ID",
        description = "Retrieve detailed information about a specific reservation. Users can only access their own reservations.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User can only access their own reservations"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<ApiResponseDto> getReservationById(
        @Parameter(description = "Reservation ID", required = true, example = "1")
        @PathVariable Integer id, HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        ReservationResponse reservation = reservationService.getReservationById(id, userId);
        return ResponseEntity.ok(ApiResponseDto.success("Reservation retrieved successfully", reservation));
    }

    @PutMapping("/{id}/cancel")
    @Operation(
        summary = "Cancel a reservation",
        description = "Cancel an upcoming reservation and free up the reserved seats. Only the reservation owner can cancel. Cannot cancel past reservations.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request - Cannot cancel past reservations or already cancelled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User can only cancel their own reservations"),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<ApiResponseDto> cancelReservation(
        @Parameter(description = "Reservation ID", required = true, example = "1")
        @PathVariable Integer id, HttpServletRequest httpRequest) {
        Integer userId = getUserIdFromRequest(httpRequest);
        ReservationResponse reservation = reservationService.cancelReservation(id, userId);
        return ResponseEntity.ok(ApiResponseDto.success("Reservation cancelled successfully", reservation));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all reservations",
        description = "Retrieve all active reservations in the system. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role")
    })
    public ResponseEntity<ApiResponseDto> getAllReservations() {
        List<ReservationResponse> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponseDto.success("Reservations retrieved successfully", reservations));
    }
}

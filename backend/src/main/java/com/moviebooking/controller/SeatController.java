package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.SeatResponse;
import com.moviebooking.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/seats")
@Tag(name = "Seats", description = "Seat availability and management APIs")
public class SeatController {
    @Autowired
    SeatService seatService;

    @GetMapping("/showtime/{showtimeId}")
    @Operation(
        summary = "Get seats for a showtime",
        description = "Retrieve all seats for a specific showtime with their booking status (available/booked). Use this to display seat map to users."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Seats retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "404", description = "Showtime not found")
    })
    public ResponseEntity<ApiResponse> getSeatsByShowtimeId(
        @Parameter(description = "Showtime ID", required = true, example = "1")
        @PathVariable Integer showtimeId) {
        List<SeatResponse> seats = seatService.getSeatsByShowtimeId(showtimeId);
        return ResponseEntity.ok(ApiResponse.success("Seats retrieved successfully", seats));
    }
}

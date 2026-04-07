package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponse;
import com.moviebooking.dto.MovieRequest;
import com.moviebooking.dto.MovieResponse;
import com.moviebooking.service.MovieService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movies", description = "Movie catalog management APIs")
public class MovieController {
    @Autowired
    MovieService movieService;

    @GetMapping
    @Operation(
        summary = "Get all movies",
        description = "Retrieve all movies with optional pagination and sorting. Set paginated=false to get all movies without pagination."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Movies retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "400", description = "Bad Request - Invalid pagination parameters")
    })
    public ResponseEntity<ApiResponse> getAllMovies(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Enable/disable pagination. Set to false to get all movies", example = "true")
            @RequestParam(required = false) Boolean paginated) {
        
        if (paginated != null && !paginated) {
            List<MovieResponse> movies = movieService.getAllMovies();
            return ResponseEntity.ok(ApiResponse.success("Movies retrieved successfully", movies));
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<MovieResponse> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(ApiResponse.success("Movies retrieved successfully", movies));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get movie by ID",
        description = "Retrieve detailed information about a specific movie by its unique identifier"
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Movie retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse> getMovieById(
        @Parameter(description = "Movie ID", required = true, example = "1")
        @PathVariable Integer id) {
        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.success("Movie retrieved successfully", movie));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create a new movie",
        description = "Add a new movie to the catalog. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Movie created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "400", description = "Bad Request - Invalid movie data"),
        @SwaggerApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @SwaggerApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role")
    })
    public ResponseEntity<ApiResponse> createMovie(
        @Parameter(description = "Movie details", required = true)
        @Valid @RequestBody MovieRequest request) {
        MovieResponse movie = movieService.createMovie(request);
        return ResponseEntity.ok(ApiResponse.success("Movie created successfully", movie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update an existing movie",
        description = "Update movie details. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Movie updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "400", description = "Bad Request - Invalid movie data"),
        @SwaggerApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @SwaggerApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
        @SwaggerApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse> updateMovie(
        @Parameter(description = "Movie ID", required = true, example = "1")
        @PathVariable Integer id,
        @Parameter(description = "Updated movie details", required = true)
        @Valid @RequestBody MovieRequest request) {
        MovieResponse movie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponse.success("Movie updated successfully", movie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete a movie",
        description = "Remove a movie from the catalog. Requires ADMIN role.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Movie deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
        @SwaggerApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @SwaggerApiResponse(responseCode = "403", description = "Forbidden - User does not have ADMIN role"),
        @SwaggerApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponse> deleteMovie(
        @Parameter(description = "Movie ID", required = true, example = "1")
        @PathVariable Integer id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success("Movie deleted successfully"));
    }
}

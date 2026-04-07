package com.moviebooking.controller;

import com.moviebooking.dto.ApiResponseDto;
import com.moviebooking.dto.MovieRequest;
import com.moviebooking.dto.MovieResponse;
import com.moviebooking.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // ✅ Get All Movies
    @GetMapping
    @Operation(
            summary = "Get all movies",
            description = "Retrieve all movies with optional pagination and sorting. Set paginated=false to get all movies without pagination."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid pagination parameters")
    })
    public ResponseEntity<ApiResponseDto> getAllMovies(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,

            @Parameter(description = "Enable/disable pagination", example = "true")
            @RequestParam(required = false) Boolean paginated) {

        if (paginated != null && !paginated) {
            List<MovieResponse> movies = movieService.getAllMovies();
            return ResponseEntity.ok(ApiResponseDto.success("Movies retrieved successfully", movies));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<MovieResponse> movies = movieService.getAllMovies(pageable);

        return ResponseEntity.ok(ApiResponseDto.success("Movies retrieved successfully", movies));
    }

    // ✅ Get Movie By ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Get movie by ID",
            description = "Retrieve detailed information about a specific movie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponseDto> getMovieById(
            @Parameter(description = "Movie ID", required = true, example = "1")
            @PathVariable Integer id) {

        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponseDto.success("Movie retrieved successfully", movie));
    }

    // ✅ Create Movie
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new movie",
            description = "Add a new movie. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid movie data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not ADMIN")
    })
    public ResponseEntity<ApiResponseDto> createMovie(
            @Parameter(description = "Movie details", required = true)
            @Valid @RequestBody MovieRequest request) {

        MovieResponse movie = movieService.createMovie(request);
        return ResponseEntity.ok(ApiResponseDto.success("Movie created successfully", movie));
    }

    // ✅ Update Movie
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update movie",
            description = "Update existing movie. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponseDto> updateMovie(
            @Parameter(description = "Movie ID", required = true, example = "1")
            @PathVariable Integer id,

            @Parameter(description = "Updated movie details", required = true)
            @Valid @RequestBody MovieRequest request) {

        MovieResponse movie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponseDto.success("Movie updated successfully", movie));
    }

    // ✅ Delete Movie
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a movie",
            description = "Remove a movie. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public ResponseEntity<ApiResponseDto> deleteMovie(
            @Parameter(description = "Movie ID", required = true, example = "1")
            @PathVariable Integer id) {

        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponseDto.success("Movie deleted successfully"));
    }
}
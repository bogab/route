package io.pwc.route.controller;

import io.pwc.route.dto.Route;
import io.pwc.route.service.RouteService;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class RouteController {

    private static final Logger log = LoggerFactory.getLogger(RouteController.class);

    private final RouteService countryService;

    public RouteController(RouteService countryService) {
        this.countryService = countryService;
    }

    @GetMapping(value = "/routing/{origin}/{destination}")
    public ResponseEntity<Route> calculateRoute(@PathVariable @Length(min = 3, max = 3) String origin, @PathVariable @Length(min = 3, max = 3) String destination) {
        log.info("Route request between {} <-> {}", origin, destination);

        origin = origin.toUpperCase();
        destination = destination.toUpperCase();
        if (origin.equals(destination)) {
            throw new IllegalArgumentException("origin must differ from destination");
        }

        List<String> routes = countryService.breadthFirstSearch(origin, destination);

        log.info("Route found between {} <-> {}: {}", origin, destination, routes);

        Route route = new Route(routes);

        return ResponseEntity.ok(route);
    }

}

package io.pwc.route.service;

import io.pwc.route.RouteApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = RouteApplication.class)
@AutoConfigureMockMvc
class RouteServiceTests {
    @Autowired
    private RouteService routeService;

    @Test
    void findRoute() {
        List<String> route = this.routeService.breadthFirstSearch("CZE", "ITA");
        assertEquals(route, List.of("CZE", "AUT", "ITA"));
    }


    @Test
    void noLandRoute() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            this.routeService.breadthFirstSearch("CZE", "AUS");
        }, "IllegalArgumentException was expected");

        assertEquals("Route not found", thrown.getMessage());
    }


    @Test
    void sameOriginAndDestination() {
        List<String> route = this.routeService.breadthFirstSearch("CZE", "CZE");
        assertEquals(route, List.of("CZE"));
    }

    @Test
    void nonExistentDestination() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            this.routeService.breadthFirstSearch("CZE", "XZX");
        }, "IllegalArgumentException was expected");

        assertEquals("Route not found", thrown.getMessage());
    }

}



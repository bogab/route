package io.pwc.route.controller;

import io.pwc.route.service.RouteService;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
class RouteControllerTests {
    @MockBean
    private RouteService routeService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void findRoute() throws Exception {
        when(routeService.breadthFirstSearch("CZE", "ITA"))
                .thenReturn(List.of("CZE", "AUT", "ITA"));

        mockMvc.perform(get("/routing/CZE/ITA"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"route\":[\"CZE\",\"AUT\",\"ITA\"]}"));
    }

    @Test
    void findRouteValidation() throws Exception {
        mockMvc.perform(get("/routing/CZE/ITALIA"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void noLandCrossing() throws Exception {
        when(routeService.breadthFirstSearch("DEU", "AUS"))
                .thenThrow(new IllegalArgumentException("No Land Route"));

        mockMvc.perform(get("/routing/DEU/AUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(StringContains.containsString("No Land Route")));
    }


    @Test
    void originLengthValidation() throws Exception {
        mockMvc.perform(get("/routing/GERMANY/ITA"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(StringContains.containsString("origin: length must be between 3 and 3")));
    }


    @Test
    void destinationLengthValidation() throws Exception {
        mockMvc.perform(get("/routing/CZE/ITALIA"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(StringContains.containsString("destination: length must be between 3 and 3")));
    }

}

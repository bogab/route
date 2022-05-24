package io.pwc.route.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import io.pwc.route.model.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RouteService {

    private static final Logger log = LoggerFactory.getLogger(RouteService.class);

    private ImmutableGraph<String> graph;

    @PostConstruct
    public void init() throws IOException {
        log.info("Load countries data ...");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File jsonFile = new ClassPathResource("data/countries.json").getFile();
        List<Country> countries = mapper.readValue(jsonFile, new TypeReference<>() {});

        log.info("Countries data loaded successfully");

        ImmutableGraph.Builder<String> graphBuilder = GraphBuilder.undirected().immutable();
        countries.forEach(country -> country.borders().forEach(border -> graphBuilder.putEdge(country.cca3(), border)));
        graph = graphBuilder.build();

        log.info("Countries graph built");
    }

    public List<String> breadthFirstSearch(String origin, String destination) {
        boolean routeFound = false;
        Set<String> visited = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();
        Map<String, String> parentNodes = new HashMap<>();

        visited.add(origin);
        queue.add(origin);

        while (queue.size() != 0 && !routeFound) {
            origin = queue.poll();
            for (String node : graph.adjacentNodes(origin)) {
                if (visited.add(node)) {
                    queue.add(node);
                    parentNodes.put(node, origin);
                }
                if (destination.equals(node)) {
                    routeFound = true;
                    break;
                }
            }
        }
        if (!routeFound) {
            log.info("A land route between {} <-> {} could not be found", origin, destination);
            throw new IllegalArgumentException("Route not found");
        }
        List<String> shortestRoute = new ArrayList<>();
        String node = destination;
        while (node != null) {
            shortestRoute.add(node);
            node = parentNodes.get(node);
        }
        Collections.reverse(shortestRoute);

        return shortestRoute;
    }
}

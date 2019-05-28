package gov.nasa.jpl.ammos.mpsa.aerie.merlincli.commands.impl;

import gov.nasa.jpl.ammos.mpsa.aerie.merlincli.commands.Command;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Command to create a new activity based on a JSON file
 */
public class NewActivityFileCommand implements Command {

    private RestTemplate restTemplate;
    private String planId;
    private String body;
    private int status;
    private String id;

    public NewActivityFileCommand(RestTemplate restTemplate, String planId, String path) throws IOException {
        this.restTemplate = restTemplate;
        this.planId = planId;
        this.status = -1;

        this.body = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    }

    @Override
    public void execute() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestBody = new HttpEntity(this.body, headers);
        try {
            String url = String.format("http://localhost:27183/api/plans/%s/activity_instances", this.planId);
            ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, requestBody, String.class);
            this.status = response.getStatusCodeValue();
            this.id = response.getHeaders().getFirst("location");

        }
        catch (HttpClientErrorException | HttpServerErrorException e) {
            this.status = e.getStatusCode().value();
        }
    }

    public int getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}

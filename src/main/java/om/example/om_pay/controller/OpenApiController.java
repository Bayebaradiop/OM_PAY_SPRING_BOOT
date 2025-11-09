package om.example.om_pay.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller pour servir le fichier OpenAPI YAML personnalisé
 */
@RestController
public class OpenApiController {

    @GetMapping(value = "/openapi.yaml", produces = "application/x-yaml")
    public ResponseEntity<String> getOpenApiYaml() throws IOException {
        Resource resource = new ClassPathResource("openapi.yaml");
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/x-yaml"))
                .body(content);
    }
}

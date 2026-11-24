package com.acm.ecommerce.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dice qué versión está desplegada.
 * - message: texto editable de la demo (application.yaml -> app.message)
 * - commit:  SHA del commit, grabado en la imagen Docker por el pipeline (ENV APP_COMMIT)
 */
@RestController
public class VersionController {

    private final String message;
    private final String commit;

    public VersionController(@Value("${app.message}") String message,
                             @Value("${APP_COMMIT:local}") String appCommit,
                             @Value("${RENDER_GIT_COMMIT:}") String renderCommit) {
        this.message = message;
        this.commit = (appCommit != null && !appCommit.equals("local"))
                ? appCommit
                : (renderCommit != null && !renderCommit.isBlank() ? renderCommit : "local");
    }

    @GetMapping("/version")
    public Map<String, String> version() {
        return Map.of("message", message, "commit", commit);
    }
}

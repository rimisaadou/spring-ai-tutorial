package org.example.controller;

import org.example.ingestion.DocumentService;
import org.example.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class RagController {

    private final DocumentService documentService;
    private final RagService ragService;

    public RagController(DocumentService documentService, RagService ragService) {
        this.documentService = documentService;
        this.ragService = ragService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingest(@RequestBody String text) {
        documentService.ingest(text);
        return ResponseEntity.ok("Document toegevoegd");
    }

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam String question) {
        String answer = ragService.ask(question);
        return ResponseEntity.ok(answer);
    }
}

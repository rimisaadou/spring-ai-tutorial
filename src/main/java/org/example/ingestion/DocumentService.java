package org.example.ingestion;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> store;

    public DocumentService(EmbeddingModel embeddingModel,
                           EmbeddingStore<TextSegment> store) {
        this.embeddingModel = embeddingModel;
        this.store = store;
    }

    public void ingest(String text) {

        Document document = Document.from(text);

        DocumentSplitter splitter =
                DocumentSplitters.recursive(300, 50);

        List<TextSegment> segments = splitter.split(document);

        for (TextSegment segment : segments) {

            Embedding embedding = embeddingModel.embed(segment).content();

            store.add(embedding, segment);
        }
    }
}

package org.example.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public RagService(ChatModel chatModel,
                      EmbeddingModel embeddingModel,
                      EmbeddingStore<TextSegment> embeddingStore) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    public String ask(String question) {

        // 1. Content retriever ophalen uit de embedding store
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();

        // 2. Relevante segmenten ophalen
        List<Content> relevantContent = contentRetriever.retrieve(Query.from(question));

        // 3. Context samenvoegen
        String context = relevantContent.stream()
                .map(c -> c.textSegment().text())
                .collect(Collectors.joining("\n\n"));

        // 4. Augmented prompt bouwen
        String augmentedPrompt = "Beantwoord de vraag op basis van de volgende context:\n\n"
                + context + "\n\nVraag: " + question;

        // 5. Vraag stellen aan het chat model
        return chatModel.chat(augmentedPrompt);
    }
}

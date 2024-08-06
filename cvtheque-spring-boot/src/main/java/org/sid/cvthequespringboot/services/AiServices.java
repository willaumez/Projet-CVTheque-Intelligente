package org.sid.cvthequespringboot.services;



import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiServices {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    //Prompts
    @Value("classpath:/prompts/prompt-general-chat.st")
    private Resource promptGeneralChat;


    //jdbcClient
    private final JdbcClient jdbcClient;

    // Constructor
    public AiServices(ChatClient.Builder builder, VectorStore vectorStore, JdbcClient jdbcClient) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    public String generalChat(String question) {
        List<Document> documents = vectorStore.similaritySearch(question);

        // Collectez les contenus et les métadonnées
        List<String> context = documents.stream()
                .map(doc -> doc.getContent() + " | Metadata: " + doc.getMetadata().toString())
                .collect(Collectors.toList());

        // Affichez les contextes pour débogage
        //System.out.println("context(file); " + context);
        //System.out.println("==========================================documents size: " + context.size());

        // Créez le prompt avec le template
        PromptTemplate promptTemplate = new PromptTemplate(promptGeneralChat);
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));

        // Envoyez le prompt au chatClient et renvoyez la réponse
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    /*public String generalChat(String question) {
        List<Document> documents = vectorStore.similaritySearch(question);
        //List<Document> documents = jdbcClient.sql("select * from SPRING_AI_VECTORS");

        List<String> context = documents.stream().map(Document::getContent).collect(Collectors.toList());
        System.out.println("context(file); "+ context);
        System.out.println("==========================================documents size: " + context.size());

        PromptTemplate promptTemplate = new PromptTemplate(promptGeneralChat);
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));

        return chatClient.prompt(prompt)
                .call()
                .content();
    }*/


}

package org.sid.cvthequespringboot.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.ollama.OllamaEmbeddingModel;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.OracleVectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.io.File;
import java.nio.file.Path;

@Configuration
public class VectorStoreConfig {

    @Value("vectorStore.json")
    private String storeFile;

    private final JdbcClient jdbcClient;

    public VectorStoreConfig(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    //@Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = new SimpleVectorStore(embeddingModel);
        String fileStore = Path.of("src", "main", "resources", "store").toAbsolutePath().toString() + "/" + storeFile;
        File file = new File(fileStore);
        if (!file.exists()) {
            // Initialize vectorStore with empty data or predefined data if necessary
            vectorStore.save(file);
        } else {
            vectorStore.load(file);
        }
        return vectorStore;
    }

    /*@Bean
    public EmbeddingModel embeddingModel() {
        // Créez une instance d'OllamaApi avec la configuration appropriée
        OllamaApi ollamaApi = new OllamaApi();
        System.out.println(" ====================  embeddingModel ===========  "+ollamaApi.toString());
        return new OllamaEmbeddingModel(ollamaApi);
    }*/


    @Bean
    public VectorStore vectorStoreOracle(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return new OracleVectorStore(jdbcTemplate, embeddingModel, true);
    }

    /*@PostConstruct
    public void initStore(){
        Integer single = jdbcClient.sql("select count(*) from SPRING_AI_VECTORS")
                .query(Integer.class).single();

        if (single == 0) {
           System.out.println("No data in vector store");
        }
    }*/




}

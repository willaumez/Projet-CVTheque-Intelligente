package org.sid.cvthequespringboot.config;

import org.springframework.context.annotation.Configuration;


@Configuration
public class VectorStoreConfig {

/*
    @Bean
    public VectorStore vectorStore(Environment env) {
        VectorStore vectorStore = new VectorStore();
        vectorStore.setInitializeSchema(false); // Désactiver l'initialisation du schéma
        vectorStore.setDimensions(Integer.parseInt(env.getProperty("spring.ai.vectorstore.oracle.dimensions")));
        vectorStore.setIndexType(env.getProperty("spring.ai.vectorstore.oracle.index-type"));
        vectorStore.setDistanceType(env.getProperty("spring.ai.vectorstore.oracle.distance-type"));
        return vectorStore;
    }
*/

}

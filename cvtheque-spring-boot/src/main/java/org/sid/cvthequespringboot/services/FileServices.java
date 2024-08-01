package org.sid.cvthequespringboot.services;


import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

//import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.OracleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileServices {

    private final Path fileStorageLocation = Paths.get("src", "main", "resources", "store").toAbsolutePath().normalize();

    private final OracleVectorStore vectorStore;




    public FileServices(OracleVectorStore vectorStore) {
        this.vectorStore = vectorStore;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public void processAndStoreFiles(List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            Resource resource = file.getResource();

            // Use spring-ai-pdf-document-reader to read the PDF content from the Resource
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(resource);
            List<Document> documents = pdfDocumentReader.get();

            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.split(documents);
            if (vectorStore == null) {
                System.out.println("=========================== vectorStore  ==============================0 ");
            }
            assert vectorStore != null;
            vectorStore.add(chunks);

            // Save the vector store to a file
            //String vectorStoreFile = this.fileStorageLocation.resolve("vectorStore.json").toString();
            //vectorStore.save(new File(vectorStoreFile));

            System.out.println("=========================== Save  ==============================0 ");
        }
    }


/*    public void processAndStoreFiles(List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            Resource resource = file.getResource();

            // Use spring-ai-pdf-document-reader to read the PDF content from the Resource
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(resource);
            List<Document> documents = pdfDocumentReader.get();

            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.split(documents);
            vectorStore.add(chunks);

            // Save the vector store to a file
            String vectorStoreFile = this.fileStorageLocation.resolve("vectorStore.json").toString();
            vectorStore.save(new File(vectorStoreFile));

            System.out.println("=========================== Save  ==============================0 ");
        }
    }*/



}

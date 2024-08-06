package org.sid.cvthequespringboot.services;


import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.repositories.FilesRepository;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class FileServices {

    private final OracleVectorStore vectorStore;
    private FilesRepository filesRepository;

    public FileServices(OracleVectorStore vectorStore, FilesRepository filesRepository) {
        this.vectorStore = vectorStore;
        this.filesRepository = filesRepository;
    }


    public void processAndStoreFiles(List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            Resource resource = file.getResource();

            // Enregistrement du fichier dans la base de données
            FileDB fileDB = storeFiles(file);
            // Use spring-ai-pdf-document-reader to read the PDF content from the Resource
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(resource);
            List<Document> documents = pdfDocumentReader.get();
            for (Document document: documents){
                document.getMetadata().put("file_id", fileDB.getId().toString());
            }
            System.out.println("=================================documents(file); "+ documents.getFirst().getMetadata().toString());
            //TextSplitter textSplitter = new TokenTextSplitter();
            //List<Document> chunks = textSplitter.split(documents);

            //System.out.println("=================================chunks(file); "+ chunks.getFirst().getMetadata().toString());
            assert vectorStore != null;
            //vectorStore.add(chunks);
            vectorStore.add(documents);
        }
    }

    public FileDB storeFiles(MultipartFile file) throws IOException {
        FileDB fileDB = FileDB.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .data(file.getBytes())
                .createdAt(new Date())
                .build();
        return filesRepository.save(fileDB);
    }

    public List<FileDB> getFiles() {
        return filesRepository.findAll();
    }

    public File saveReadFile(Long fileId) throws IOException {
        // Récupérer le fichier depuis la base de données
        FileDB fileDB = filesRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        System.out.println("Found file========= " + fileDB);

        // Répertoire des ressources
        Path directoryPath = Paths.get("src/main/resources/files");

        // Créer un fichier temporaire dans un répertoire temporaire
        Path tempDirectoryPath = Paths.get(System.getProperty("java.io.tmpdir"));
        Path tempFilePath = tempDirectoryPath.resolve(fileDB.getName());
        File tempFile = tempFilePath.toFile();

        // Écrire les données du fichier dans le fichier temporaire
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileDB.getData());
        }

        // Supprimer les fichiers existants dans le répertoire des ressources
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path entry : stream) {
                Files.delete(entry); // Supprime chaque fichier
            }
        } catch (IOException e) {
            throw new IOException("Failed to clear the directory", e);
        }

        // Déplacer le fichier temporaire vers le répertoire des ressources
        Path targetFilePath = directoryPath.resolve(fileDB.getName());
        Files.move(tempFilePath, targetFilePath);

        return targetFilePath.toFile();
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

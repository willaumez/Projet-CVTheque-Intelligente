package org.sid.cvthequespringboot.services.FileServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.FileDbDto;
import org.sid.cvthequespringboot.dtos.FolderDto;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.FileVectorStore;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.mappers.FileMappers;
import org.sid.cvthequespringboot.repositories.FileStoreRepository;
import org.sid.cvthequespringboot.repositories.FilesRepository;
import org.sid.cvthequespringboot.repositories.FolderRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.OracleVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileServicesImpl implements FileServices {
    private final FilesRepository filesRepository;
    private final FolderRepository folderRepository;
    private final FileStoreRepository fileStoreRepository;
    private FileVectorStore fileStore;

    private final FileMappers fileMappers;
    private final JdbcClient jdbcClient;

    public FileServicesImpl(FilesRepository filesRepository, FolderRepository folderRepository, FileMappers fileMappers, JdbcClient jdbcClient, FileStoreRepository fileStoreRepository) {
        this.filesRepository = filesRepository;
        this.folderRepository = folderRepository;
        this.fileMappers = fileMappers;
        this.jdbcClient = jdbcClient;
        this.fileStoreRepository = fileStoreRepository;
    }


    /*public void processAndStoreFiles(List<MultipartFile> files, FolderDto folderDto) throws IOException {
        Folder folder = fileMappers.fromFolderDto(folderDto);
        if (folder.getId() == null) {
            folder = folderRepository.save(folder);
        }
        for (MultipartFile file : files) {
            Resource resource = file.getResource();

            // Enregistrement du fichier dans la base de données
            FileDB fileDB = storeFiles(file, folder);
            // Use spring-ai-pdf-document-reader to read the PDF content from the Resource
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(resource);
            List<Document> documents = pdfDocumentReader.get();
            for (Document document : documents) {
                document.getMetadata().put("file_id", fileDB.getId().toString());
                document.getMetadata().put("folder_id", folder.getId().toString());
            }
            //System.out.println("=================================documents(file); " + documents.getFirst().getMetadata().toString());
            //TextSplitter textSplitter = new TokenTextSplitter();
            //List<Document> chunks = textSplitter.split(documents);

            //System.out.println("=================================chunks(file); "+ chunks.getFirst().getMetadata().toString());
            assert vectorStore != null;
            //vectorStore.add(chunks);
            vectorStore.add(documents);
        }
    }*/
    @Override
    public void processAndStoreFiles(MultipartFile file, FolderDto folderDto) throws IOException {
        Folder folder = fileMappers.fromFolderDto(folderDto);
        if (folder.getId() == null) {
            folder = folderRepository.save(folder);
        }
        FileDB fileDB = storeFiles(file, folder);
        Resource resource = file.getResource();
        PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(resource);
        List<Document> documents = pdfDocumentReader.get();
        StringBuilder concatenatedContent = new StringBuilder();
        for (Document document : documents) {
            concatenatedContent.append(document.getContent());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMetadata = objectMapper.writeValueAsString(documents.getFirst().getMetadata());

        FileVectorStore vectorStore = new FileVectorStore();
        vectorStore.setId(UUID.fromString(documents.getFirst().getId()));
        vectorStore.setContent(concatenatedContent.toString());
        vectorStore.setMetadata(jsonMetadata);
        vectorStore.setFileDB(fileDB);
        vectorStore.setFolder(folder);
        fileStoreRepository.save(vectorStore);
    }


    @Override
    public FileDB storeFiles(MultipartFile file, Folder folder) throws IOException {
        System.out.println("=================================folder; " + folder.getId());
        FileDB fileDB = FileDB.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .data(file.getBytes())
                .createdAt(new Date())
                .folder(folder)
                .build();
        return filesRepository.save(fileDB);
    }

    @Override
    public List<FileDbDto> getFiles() {
        // Récupérer tous les fichiers depuis le repository
        List<FileDB> fileDBS = filesRepository.findAll();
        // Conversion de FileDB à FileDbDto
        return fileDBS.stream()
                .map(fileMappers::fromFileDB)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDbDto> getFilesByFolderId(Long folderId) {
        List<FileDB> fileDBS = filesRepository.findAllByFolderId(folderId);
        // Conversion de FileDB à FileDbDto
        return fileDBS.stream()
                .map(fileMappers::fromFileDB)
                .collect(Collectors.toList());
    }

    @Override
    public File saveReadFile(Long fileId) throws IOException {
        // Récupérer le fichier depuis la base de données
        FileDB fileDB = filesRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
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

    @Override
    @Transactional
    public void deleteFiles(List<Long> filesIds) {
        for (Long fileId : filesIds) {
            fileStoreRepository.deleteByFileDB_Id(fileId);
            filesRepository.deleteById(fileId);
        }
    }

    @Override
    @Transactional
    public void transferFiles(List<Long> fileIds, Long folderId) {
        // Vérifier si le dossier cible existe
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));

        // Mettre à jour le dossier de chaque fichier
        for (Long fileId : fileIds) {
            FileDB file = filesRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("File not found"));
            file.setFolder(folder);
            filesRepository.save(file); // Enregistrer les changements

            FileVectorStore fileVectorStore = fileStoreRepository.findByFileDBId(fileId);
            fileVectorStore.setFolder(folder);
            fileStoreRepository.save(fileVectorStore);
        }
    }


     /*@PostConstruct
    public void initStore(){
        Integer single = jdbcClient.sql("select count(*) from SPRING_AI_VECTORS")
                .query(Integer.class).single();

        if (single == 0) {
           System.out.println("No data in vector store");
        }
    }*/



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

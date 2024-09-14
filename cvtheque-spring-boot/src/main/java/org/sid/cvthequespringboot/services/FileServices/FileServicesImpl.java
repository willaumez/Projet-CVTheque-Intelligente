package org.sid.cvthequespringboot.services.FileServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.sid.cvthequespringboot.dtos.*;
import org.sid.cvthequespringboot.entities.Evaluation;
import org.sid.cvthequespringboot.entities.FileDB;
import org.sid.cvthequespringboot.entities.FileVectorStore;
import org.sid.cvthequespringboot.entities.Folder;
import org.sid.cvthequespringboot.mappers.FileMappers;
import org.sid.cvthequespringboot.repositories.EvaluationRepository;
import org.sid.cvthequespringboot.repositories.FileStoreRepository;
import org.sid.cvthequespringboot.repositories.FilesRepository;
import org.sid.cvthequespringboot.repositories.FolderRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.vectorstore.OracleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final EvaluationRepository evaluationRepository;

    private final FileMappers fileMappers;

    public FileServicesImpl(FilesRepository filesRepository, FolderRepository folderRepository, FileMappers fileMappers, FileStoreRepository fileStoreRepository, EvaluationRepository evaluationRepository) {
        this.filesRepository = filesRepository;
        this.folderRepository = folderRepository;
        this.fileMappers = fileMappers;
        this.fileStoreRepository = fileStoreRepository;
        this.evaluationRepository = evaluationRepository;
    }

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
        String originalFilename = file.getOriginalFilename();

        String fileNameWithoutExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(0, originalFilename.lastIndexOf('.'))
                : originalFilename;
        FileDB fileDB = FileDB.builder()
                .name(fileNameWithoutExtension)
                .type(file.getContentType())
                .data(file.getBytes())
                .createdAt(new Date())
                .folder(folder)
                .build();
        return filesRepository.save(fileDB);
    }

    @Override
    public List<FileDto> getFiles() {
        List<FileDB> fileDBS = filesRepository.findAllByOrderByCreatedAtDesc();
        return fileDBS.stream()
                .map(fileMappers::fromFile)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> getFilesByFolderId(Long folderId) {
        List<FileDB> fileDBS = filesRepository.findAllByFolderIdOrderByCreatedAtDesc(folderId);
        // Conversion de FileDB à FileDbDto
        return fileDBS.stream()
                .map(fileMappers::fromFile)
                .collect(Collectors.toList());
    }

    @Override
    public File saveReadFile(Long fileId) throws IOException {
        FileDB fileDB = filesRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
        Path directoryPath = Paths.get("src/main/resources/files");
        Path tempDirectoryPath = Paths.get(System.getProperty("java.io.tmpdir"));
        Path tempFilePath = tempDirectoryPath.resolve(fileDB.getName());
        File tempFile = tempFilePath.toFile();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileDB.getData());
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path entry : stream) {
                Files.delete(entry);
            }
        } catch (IOException e) {
            throw new IOException("Failed to clear the directory", e);
        }
        Path targetFilePath = directoryPath.resolve(fileDB.getName());
        Files.move(tempFilePath, targetFilePath);
        return targetFilePath.toFile();
    }

    @Override
    @Transactional
    public void deleteFiles(List<Long> filesIds) {
        for (Long fileId : filesIds) {
            evaluationRepository.deleteByFileDB_Id(fileId);
            fileStoreRepository.deleteByFileDB_Id(fileId);
            filesRepository.deleteById(fileId);
        }
    }

    /*public void deleteFiles(List<Long> filesIds) {
        for (Long fileId : filesIds) {
            fileStoreRepository.deleteByFileDB_Id(fileId);
            filesRepository.deleteById(fileId);
        }
    }*/

    @Override
    @Transactional
    public void transferFiles(List<Long> fileIds, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Folder not found"));
        for (Long fileId : fileIds) {
            FileDB file = filesRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("File not found"));
            file.setFolder(folder);
            filesRepository.save(file);
            FileVectorStore fileVectorStore = fileStoreRepository.findByFileDBId(fileId);
            fileVectorStore.setFolder(folder);
            fileStoreRepository.save(fileVectorStore);
        }
    }

    @Transactional
    @Override
    public FileDB updateFile(FileDB fileDB) {
        if (fileDB == null || fileDB.getId() == null) {
            return null;
        }
        FileDB fileDBFromDB = filesRepository.findById(fileDB.getId())
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        fileDBFromDB.setName(fileDB.getName());
        return filesRepository.save(fileDBFromDB);
    }

    @Override
    public List<CVStatsDTO> getCvStats() {
        return filesRepository.countCVsByMonthAndFolder();
    }

    @Override
    public EvaluationDto getEvaluationByFileId(Long fileId) {
        Evaluation evaluation = evaluationRepository.findByFileDB_Id(fileId);
        if (evaluation != null) {
            return fileMappers.fromEvaluation(evaluation);
        }
        return null;
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

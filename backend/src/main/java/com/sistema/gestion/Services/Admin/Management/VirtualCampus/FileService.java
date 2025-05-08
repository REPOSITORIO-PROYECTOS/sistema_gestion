package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.File;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.FileRepository;
import com.sistema.gestion.Services.Admin.Management.GoogleDriveCredentialsService;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class FileService {

    private final FileRepository fileRepository;
    //private final GoogleDriveCredentialsService googleDriveCredentialsService;
    private final Drive googleDriveService;

    public FileService(FileRepository filesRepository, Drive googleDriveService){ //GoogleDriveCredentialsService googleDriveCredentialsService) {
        this.fileRepository = filesRepository;
        this.googleDriveService = googleDriveService;
        //this.googleDriveCredentialsService = googleDriveCredentialsService;
    }

    public Flux<File> findAll(ServerWebExchange exchange) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Flux.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findAll(File.class);
        return fileRepository.findAll();
    }

    public Mono<File> findById(ServerWebExchange exchange, String id) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findById(id, File.class);
        return fileRepository.findById(id);
    }

    public Mono<File> create(ServerWebExchange exchange, File file) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.save(file);
        return fileRepository.save(file);
    }    

    // public Mono<String> saveFile(ServerWebExchange exchange, Mono<FilePart> file) {
    //     // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
    //     // if (template == null) {
    //     //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
    //     // }
    //     try {
    //         return subirArchivoADrive(file)
    //             .flatMap(link -> 
    //                 file
    //                     .flatMap(f -> {
    //                         // Crear el archivo de manera reactiva
    //                         File fileToSave = new File(f.filename(), link);
    //                         // Guardar el archivo en el repositorio de manera reactiva
    //                         return fileRepository.save(fileToSave)
    //                             .flatMap(savedFile -> Mono.just(savedFile.getId()));
    //                     })
    //             );
    //     } catch (Exception e) {
    //         return Mono.error(e);
    //     }
    // }

    public Mono<String> saveFile(ServerWebExchange exchange, Mono<FilePart> file) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        try{
            return subirArchivoADrive(file).flatMap(link -> {
                File fileToSave = new File(file.flatMap(f -> Mono.just(f.filename())).block(), link);
                return fileRepository.save(fileToSave).flatMap(savedFile -> Mono.just(savedFile.getId()));
            });
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    // public Mono<File> update(ServerWebExchange exchange, String id, Mono<FilePart> file) {
    //     // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
    //     // if (template == null) {
    //     //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
    //     // }
    //     // file.setId(id);
    //     // return template.save(file);
    //     Mono<File> dbFile = this.findById(exchange, id);
    //     File dbFileFlatVar = dbFile.block();
    //     return dbFile
    //     .flatMap(dbFileFlat -> {
    //         return this.actualizarArchivoEnDrive(
    //             this.obtenerIdDesdeLink(dbFileFlat.getLink()), file);
    //     })
    //     .flatMap(fileUrlUpdated -> {
    //         dbFileFlatVar.setLink(fileUrlUpdated);
    //         return fileRepository.save(dbFileFlatVar);
    //     });
    //     //return fileRepository.save(file);
    // }

    // public Mono<Void> delete(ServerWebExchange exchange, String id) {
    //     // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
    //     // if (template == null) {
    //     //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
    //     // }
    //     // return template.remove(File.class, id);
    //     this.findById(exchange, id).flatMap(file -> this.eliminarArchivoEnDrive(file.getLink())).subscribe();
    //     return fileRepository.deleteById(id);
    // }

    // public Mono<String> subirArchivoADrive(Mono<FilePart> file) {
    //     Mono<Drive> googleDriveService = googleDriveCredentialsService.googleDriveService(); // Obtenemos el servicio de Google Drive de manera reactiva
    //     return googleDriveService  // Obtenemos el servicio de Google Drive de manera reactiva
    //         .flatMap(drive -> file.flatMap(f -> {
    //             // Crear metadatos del archivo en Google Drive
    //             com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
    //             fileMetadata.setName(f.filename());
    //             fileMetadata.setParents(Collections.singletonList("1eUrt3JWRauSeccLMWsI5DqCaRPIdEAv2"));
    
    //             // Crear el contenido del archivo de manera reactiva
    //             // El `FilePart` nos da un DataBuffer, así que tenemos que convertirlo a un byte array
    //             return f.content()  // `content()` devuelve un Flux<DataBuffer>
    //                     .collectList() // Recolectamos todos los DataBuffers en una lista
    //                     .flatMap(dataBuffers -> {
    //                         // Convertir DataBuffers a un byte array
    //                         int totalSize = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
    //                         byte[] bytes = new byte[totalSize];
    //                         int offset = 0;
    //                         for (DataBuffer buffer : dataBuffers) {
    //                             int length = buffer.readableByteCount();
    //                             buffer.read(bytes, offset, length);
    //                             offset += length;
    //                         }
    
    //                         // Ahora que tenemos los datos como un array de bytes, creamos InputStreamContent
    //                         try {
    //                             AbstractInputStreamContent fileContent = new InputStreamContent(
    //                                     f.headers().getContentType().toString(), // Usamos el tipo de contenido de `FilePart`
    //                                     new java.io.ByteArrayInputStream(bytes)
    //                             );
                                
    //                             // Usamos el servicio de Google Drive para subir el archivo
    //                             String urlFile = drive.files()
    //                                 .create(fileMetadata, fileContent)
    //                                 .setFields("id, webViewLink")
    //                                 .execute()
    //                                 .getWebViewLink();
                                
    //                             // Subir el archivo a Google Drive de manera reactiva
    //                             return Mono.just(urlFile); // Retorna la URL del archivo en Drive
    //                         } catch (Exception e) {
    //                             System.out.println("Error al subir el archivo a Google Drive: " + e.getMessage());
    //                             return Mono.error(e);  // Propagamos el error si algo sale mal
    //                         }
    //                     });
    //         }));
    // }

    public Mono<String> subirArchivoADrive(Mono<FilePart> file) {
        return file.flatMap(f -> {
            // Crear metadatos del archivo en Google Drive
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(f.filename());
            fileMetadata.setParents(Collections.singletonList("1NtFfjOnn-rdoMlosR8OKD0WL-DJE0aVY"));
    
            // Crear el contenido del archivo de manera reactiva
            // El `FilePart` nos da un DataBuffer, así que tenemos que convertirlo a un byte array
            return f.content()  // `content()` devuelve un Flux<DataBuffer>
                    .collectList() // Recolectamos todos los DataBuffers en una lista
                    .flatMap(dataBuffers -> {
                        // Convertir DataBuffers a un byte array
                        int totalSize = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
                        byte[] bytes = new byte[totalSize];
                        int offset = 0;
                        for (DataBuffer buffer : dataBuffers) {
                            int length = buffer.readableByteCount();
                            buffer.read(bytes, offset, length);
                            offset += length;
                        }
    
                        // Ahora que tenemos los datos como un array de bytes, creamos InputStreamContent
                        try {
                            AbstractInputStreamContent fileContent = new InputStreamContent(
                                    f.headers().getContentType().toString(), // Usamos el tipo de contenido de `FilePart`
                                    new java.io.ByteArrayInputStream(bytes)
                            );
                            
                            String urlFile = googleDriveService.files()
                            .create(fileMetadata, fileContent)
                            .setFields("id, webViewLink")
                            .execute()
                            .getWebViewLink();
    
                            // Subir el archivo a Google Drive de manera reactiva
                            return Mono.just(urlFile); // Retorna la URL del archivo en Drive
                        } catch (Exception e) {
                            return Mono.error(e);  // Propagamos el error si algo sale mal
                        }
                    });
        });
    }

    // Método para actualizar archivo en Google Drive
    // public Mono<String> actualizarArchivoEnDrive(String fileId, Mono<FilePart> file) {
    //     Mono<Drive> googleDriveService = googleDriveCredentialsService.googleDriveService();
    //     return googleDriveService // Obtener el servicio de Google Drive de manera reactiva
    //         .flatMap(drive -> file.flatMap(f -> {
    //             com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
    //             fileMetadata.setName(f.filename());
    
    //             return f.content().collectList().flatMap(dataBuffers -> {
    //                 int totalSize = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
    //                 byte[] bytes = new byte[totalSize];
    //                 int offset = 0;
    //                 for (DataBuffer buffer : dataBuffers) {
    //                     int length = buffer.readableByteCount();
    //                     buffer.read(bytes, offset, length);
    //                     offset += length;
    //                 }
    
    //                 // Crear el contenido del archivo
    //                 AbstractInputStreamContent fileContent = new InputStreamContent(
    //                         f.headers().getContentType().toString(),
    //                         new java.io.ByteArrayInputStream(bytes)
    //                 );
    
    //                 return Mono.fromCallable(() -> {
    //                     try {
    //                         // Actualizar el archivo en Google Drive
    //                         return drive.files()
    //                                 .update(fileId, fileMetadata, fileContent)
    //                                 .setFields("id, webViewLink")
    //                                 .execute()
    //                                 .getWebViewLink();
    //                     } catch (Exception e) {
    //                         throw new RuntimeException("Error actualizando archivo en Drive", e);
    //                     }
    //                 });
    //             });
    //         }));
    // }
    

    // Método para eliminar archivo en Google Drive
    // public Mono<Void> eliminarArchivoEnDrive(String fileUrl) {
    //     String driveFileId = this.obtenerIdDesdeLink(fileUrl);
    //     Mono<Drive> googleDriveService = googleDriveCredentialsService.googleDriveService();
    //     return googleDriveService // Obtener el servicio de Google Drive de manera reactiva
    //         .flatMap(drive -> Mono.fromRunnable(() -> {
    //             try {
    //                 // Eliminar archivo en Google Drive
    //                 drive.files().delete(driveFileId).execute();
    //             } catch (Exception e) {
    //                 throw new RuntimeException("Error eliminando archivo en Drive", e);
    //             }
    //         }));
    // }
    

    public String obtenerIdDesdeLink(String link) {
        // Ejemplo de enlace: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
        String regex = "https://drive\\.google\\.com/(?:file/d/|open\\?id=|uc\\?id=)([\\w-]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(link);
    
        if (matcher.find()) {
            return matcher.group(1); // El grupo 1 contiene el ID del archivo
        } else {
            throw new IllegalArgumentException("El enlace proporcionado no es válido");
        }
    }
}

package br.com.anibal.captacaofilipeserver.services;

import br.com.anibal.captacaofilipeserver.config.FirebaseService;
import br.com.anibal.captacaofilipeserver.entities.Clinic;
import br.com.anibal.captacaofilipeserver.exceptions.StorageException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.common.io.Files;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ClinicService {

    @Autowired
    private FirebaseService firebaseService;

    private final String COLLECTION = "clinica";

    public List<Clinic> list() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getCollection().get();
        QuerySnapshot query = future.get();
        return query.getDocuments().stream()
                .map(document -> {
                    Clinic clinic = document.toObject(Clinic.class);
                    clinic.setId(document.getId());
                    return clinic;
                })
                .collect(Collectors.toList());
    }

    private Clinic find(String id) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = getCollection().document(id).get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(Clinic.class);
        }

        return null;
    }

    private CollectionReference getCollection() {
        return FirestoreClient.getFirestore().collection(COLLECTION);
    }

    public Clinic save(Clinic clinic) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> futureDocument = getCollection().add(clinic);
        DocumentReference reference = futureDocument.get();
        clinic.setId(reference.getId());
        return clinic;
    }

    public void upload(List<MultipartFile> files, String id) throws ExecutionException, InterruptedException {
        List<String> links = files.stream()
                .map(file -> {
                    try {

                        Blob blob = firebaseService.getBucket()
                                .create("imagens/" + getRandomFileName(file), file.getInputStream(), "image/jpg");

                        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

                        return blob.getMediaLink();
                    } catch (IOException e) {
                        throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
                    }
                }).collect(Collectors.toList());

        updateLinks(links, id);
    }

    private String getRandomFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "." + Files.getFileExtension(file.getOriginalFilename());
    }

    private void updateLinks(List<String> links, String id) throws ExecutionException, InterruptedException {
        List<String> existingLinks = find(id).getImagens();
        if (existingLinks  != null) {
            links.addAll(existingLinks);
        }

        getCollection().document(id).update("imagens", links);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        Clinic clinic = find(id);

        if (clinic.getImagens() != null) {
            apagarImagens(clinic.getImagens());
        }

        getCollection().document(id).delete();
    }

    private void apagarImagens(List<String> imagens) {
        if (imagens == null || imagens.size() == 0) {
            return;
        }

        List<Blob> blobs = firebaseService.getBucket().get(imagens);

        if (blobs != null) {
            blobs.forEach(blob -> {
                if (blob != null) {
                    blob.delete();
                }
            });
        }
    }

    public Optional<Clinic> get(String id) throws ExecutionException, InterruptedException {
        Clinic clinic = find(id);
        return Optional.ofNullable(clinic);
    }
}

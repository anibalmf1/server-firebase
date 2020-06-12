package br.com.anibal.captacaofilipeserver.services;

import br.com.anibal.captacaofilipeserver.entities.Clinic;
import br.com.anibal.captacaofilipeserver.exceptions.StorageException;
import br.com.anibal.captacaofilipeserver.firebase.FirebaseService;
import br.com.anibal.captacaofilipeserver.repositories.ClinicRepository;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.common.io.Files;
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

    @Autowired
    private ClinicRepository clinicRepository;

    private final String COLLECTION = "clinica";

    public List<Clinic> list() throws ExecutionException, InterruptedException {
        return clinicRepository.list();
    }

    public Optional<Clinic> findOne(String id) throws ExecutionException, InterruptedException {
        return clinicRepository.findOne(id);
    }

    public Clinic save(Clinic clinic) throws ExecutionException, InterruptedException {
        return clinicRepository.save(clinic);
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        Optional<Clinic> deleted = clinicRepository.delete(id);

        if (deleted.isPresent()) {
            List<String> imagens = deleted.get().getImagens();

            if (imagens != null) {
                apagarImagens(imagens);
            }
        }
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
        Optional<Clinic> clinic = findOne(id);

        if (clinic.isPresent()){
            List<String> existingLinks = clinic.get().getImagens();
            if (existingLinks  != null) {
                links.addAll(existingLinks);
            }

            clinicRepository.updateField(id, "imagens", links);
        }
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
}

package br.com.anibal.captacaofilipeserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import br.com.anibal.captacaofilipeserver.entities.Clinic;
import br.com.anibal.captacaofilipeserver.services.ClinicService;

@RestController
@RequestMapping(ClinicController.CLINIC_URL)
@CrossOrigin(allowedHeaders = "*")
public class ClinicController {

    public final static String CLINIC_URL = "/clinic";

    @Autowired
    private ClinicService service;

    @GetMapping
    public ResponseEntity<List<Clinic>> list() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clinic> get(@PathVariable String id) throws ExecutionException, InterruptedException {
        Optional<Clinic> clinic = service.findOne(id);

        return clinic.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Clinic> save(@RequestBody @Valid Clinic clinic) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(service.save(clinic));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity upload(@RequestParam List<MultipartFile> files, @PathVariable String id) {
        try {
            service.upload(files, id);
            return ResponseEntity.ok().build();
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable String id) {
        try{
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

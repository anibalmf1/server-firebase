package br.com.anibal.captacaofilipeserver.config;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    public Bucket getBucket() {
        return StorageClient.getInstance().bucket(bucketName);
    }
}

package br.com.anibal.captacaofilipeserver.services;

import br.com.anibal.captacaofilipeserver.entities.User;
import br.com.anibal.captacaofilipeserver.model.UserDetailsImpl;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final String USER_COLLECTION = "user";

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference reference = firestore.collection(USER_COLLECTION).document(username);
        ApiFuture<DocumentSnapshot> future = reference.get();
        DocumentSnapshot document = future.get();
        Optional<User> user = Optional.ofNullable(document.toObject(User.class));
        return user
                .map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


}

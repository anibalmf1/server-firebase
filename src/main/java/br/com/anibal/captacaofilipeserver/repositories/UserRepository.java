package br.com.anibal.captacaofilipeserver.repositories;

import br.com.anibal.captacaofilipeserver.entities.User;
import br.com.anibal.captacaofilipeserver.firebase.FirebaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository extends FirebaseRepository<User> {

    public Optional<User> findOneByUsername(String username) throws ExecutionException, InterruptedException {
        return findOneBy("username", username);
    }
}

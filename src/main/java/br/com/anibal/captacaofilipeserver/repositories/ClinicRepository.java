package br.com.anibal.captacaofilipeserver.repositories;

import br.com.anibal.captacaofilipeserver.entities.Clinic;
import br.com.anibal.captacaofilipeserver.firebase.FirebaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ClinicRepository extends FirebaseRepository<Clinic> {

}

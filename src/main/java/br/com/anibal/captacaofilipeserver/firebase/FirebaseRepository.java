package br.com.anibal.captacaofilipeserver.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebaseRepository<T extends FirebaseEntity> {

    @Autowired
    FirebaseService firebaseService;

    private CollectionReference collection;

    public FirebaseRepository () {
    }

    public List<T> list() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getCollection().get();
        QuerySnapshot query = future.get();
        return query.getDocuments().stream()
                .map(document -> {
                    T record = document.toObject(getEntityClass());
                    record.setId(document.getId());
                    return record;
                })
                .collect(Collectors.toList());
    }

    public Optional<T> findOne(String id) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = getCollection().document(id).get();
        DocumentSnapshot document = future.get();

        return Optional.ofNullable(document.toObject(getEntityClass()));
    }

    protected Optional<T> findOneBy(String field, Object value) throws ExecutionException, InterruptedException {
        Query query = getCollection().whereEqualTo(field, value);
        ApiFuture<QuerySnapshot> future = query.get();
        QuerySnapshot snapshot = future.get();

        List<QueryDocumentSnapshot> documents = snapshot.getDocuments();

        if (documents.size() > 1) {
            throw new RuntimeException("Found more than one record.");
        }

        if (documents.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(documents.get(0).toObject(getEntityClass()));
    }

    public T save(T record) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> futureDocument = getCollection().add(record);
        DocumentReference reference = futureDocument.get();
        record.setId(reference.getId());
        return record;
    }

    public Optional<T> delete(String id) throws ExecutionException, InterruptedException {
        Optional<T> record = findOne(id);

        if (record.isPresent()) {
            getCollection().document(id).delete();
        }

        return record;
    }

    private CollectionReference getCollection() {
        if (collection != null) {
            return collection;
        }

        collection = firebaseService.getCollection(getCollectionNameFromAnnotation());
        return collection;
    }

    private String getCollectionNameFromAnnotation() {
        Class<T> entityClass = getEntityClass();

        if (!entityClass.isAnnotationPresent(FBEntity.class)) {
            throw new RuntimeException("Entity must have an FBEntity annotation");
        }

        return entityClass.getAnnotation(FBEntity.class).value().getName();
    }

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {
        return (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void updateField(String id, String field, Object value) {
        getCollection().document(id).update(field, value);
    }
}

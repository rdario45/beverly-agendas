package repository;

import acl.BeverlyRepo;
import acl.DynamoDB;
import com.google.inject.Inject;
import domain.Cita;
import mapper.CitaMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CitaRepository {

    private DynamoDbClient ddb;

    @Inject
    public CitaRepository(BeverlyRepo beverlyRepo) {
        this.ddb = beverlyRepo.getDdb();
    }

    public Optional<Cita> find(Integer id) {
        return DynamoDB.getItem( ddb, "citas", "id", id.toString())
                .map(valueMap -> new CitaMapper().map(valueMap));
    }

    public List<Cita> findAll() {
        return DynamoDB.getAll(ddb, "citas").stream()
                .map(valueMap -> new CitaMapper().map(valueMap))
                .collect(Collectors.toList());
    }

    public Cita save(Cita cita) {
        return DynamoDB.putItem(ddb, "citas", cita);
    }
}

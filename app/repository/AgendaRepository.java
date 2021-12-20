package repository;

import acl.BeverlyRepo;
import acl.DynamoDB;
import com.google.inject.Inject;
import domain.Agenda;
import mapper.AgendaMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AgendaRepository {

    private DynamoDbClient ddb;

    @Inject
    public AgendaRepository(BeverlyRepo beverlyRepo) {
        this.ddb = beverlyRepo.getDdb();
    }

    public Optional<Agenda> find(Integer id) {
        return DynamoDB.getItem( ddb, "agendas", "id", id.toString())
                .map(valueMap -> new AgendaMapper().map(valueMap));
    }

    public List<Agenda> findAll() {
        return DynamoDB.getAll(ddb, "agendas").stream()
                .map(valueMap -> new AgendaMapper().map(valueMap))
                .collect(Collectors.toList());
    }

    public Agenda save(Agenda agenda) {
        return DynamoDB.putItem(ddb, "agendas", agenda);
    }

}

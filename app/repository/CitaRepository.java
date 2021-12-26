package repository;

import acl.BeverlyDB;
import domain.Cita;
import mapper.CitaMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CitaRepository {

    public Optional<Cita> find(String id) {
        return BeverlyDB.getItem("citas", "id", id)
                .map(valueMap -> new CitaMapper().map(valueMap));
    }

    public List<Cita> findAll() {
        return BeverlyDB.getAll("citas").stream()
                .map(valueMap -> new CitaMapper().map(valueMap))
                .collect(Collectors.toList());
    }

    public Cita save(Cita cita) {
        return BeverlyDB.putItem("citas", cita);
    }

    public void remove(String id) {
        BeverlyDB.removeItem("citas", "id", id);
    }
}

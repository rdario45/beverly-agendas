package repository;

import acl.BeverlyDB;
import domain.Agenda;
import domain.Servicio;
import mapper.ServicioMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServicioRepository {

    public Optional<Servicio> find(String id) {
        return BeverlyDB.getItem("servicios", "id", id)
                .map(valueMap -> new ServicioMapper().map(valueMap));
    }

    public List<Servicio> findAll() {
        return BeverlyDB.getAll("servicios").stream()
                .map(valueMap -> new ServicioMapper().map(valueMap))
                .collect(Collectors.toList());
    }

    public Agenda save(Agenda agenda) {
        return BeverlyDB.putItem("servicios", agenda);
    }

}

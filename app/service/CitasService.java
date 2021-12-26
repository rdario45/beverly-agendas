package service;

import acl.BeverlySQS;
import com.google.inject.Inject;
import domain.Agenda;
import domain.Cita;
import play.libs.Json;
import repository.CitaRepository;
import sqs.events.CitaActualizada;
import sqs.events.CitaCreada;
import sqs.events.CitaEliminada;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CitasService {

    private CitaRepository repository;

    @Inject
    public CitasService(CitaRepository repository) {
        this.repository = repository;
    }

    public Optional<Cita> find(String id) {
        return repository.find(id);
    }

    public List<Cita> findAll() {
        return repository.findAll();
    }

    public Cita save(Cita cita) {
        cita.setId(UUID.randomUUID().toString());
        CitaCreada citaCreada = new CitaCreada(cita);
        BeverlySQS.send(Json.toJson(citaCreada).toString());
        return repository.save(cita);
    }

    public Optional<Cita> update(Cita cita, String id) {
        return repository.find(id).map(found -> {
            cita.setId(found.getId());
            CitaActualizada citaActualizada = new CitaActualizada(cita);
            BeverlySQS.send(Json.toJson(citaActualizada).toString());
            return repository.save(cita);
        });
    }

    public Optional<Cita> delete(String id) {
        return repository.find(id).map(cita -> {
            CitaEliminada citaEliminada = new CitaEliminada(cita);
            repository.remove(id);
            BeverlySQS.send(Json.toJson(citaEliminada).toString());
            return cita;
        });
    }
}

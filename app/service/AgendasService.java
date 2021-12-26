package service;

import com.google.inject.Inject;
import domain.Agenda;
import repository.AgendaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AgendasService {

    private AgendaRepository repository;

    @Inject
    public AgendasService(AgendaRepository repository) {
        this.repository = repository;
    }

    public Optional<Agenda> find(String id) {
        return repository.find(id);
    }

    public List<Agenda> findAll() {
        return repository.findAll();
    }

    public Agenda save(Agenda agenda) {
        agenda.setId(UUID.randomUUID().toString());
        return repository.save(agenda);
    }

    public Optional<Agenda> update(Agenda agenda, String id) {
        return repository.find(id).map(found -> {
            agenda.setId(found.getId());
            return repository.save(agenda);
        });
    }

    public Optional<Agenda> findAnyAgenda(String agenda, String fecha) {
        return repository.findAnyAgenda(agenda, fecha);
    }

    public List<Agenda> findAllByFecha(String date) {
        return repository.findAllByFecha(date);
    }
}

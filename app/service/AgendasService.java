package service;

import com.google.inject.Inject;
import domain.Agenda;
import repository.AgendaRepository;

import java.util.List;
import java.util.Optional;

public class AgendasService {

    private AgendaRepository repository;

    @Inject
    public AgendasService(AgendaRepository repository) {
        this.repository = repository;
    }

    public Optional<Agenda> find(Integer id) {
        return repository.find(id);
    }

    public List<Agenda> findAll() {
        return repository.findAll();
    }

    public Agenda save(Agenda agenda) {
        return repository.save(agenda);
    }

}

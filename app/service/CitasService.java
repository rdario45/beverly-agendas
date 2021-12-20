package service;

import com.google.inject.Inject;
import domain.Cita;
import repository.CitaRepository;

import java.util.List;
import java.util.Optional;

public class CitasService {

    private CitaRepository repository;

    @Inject
    public CitasService(CitaRepository repository) {
        this.repository = repository;
    }

    public Optional<Cita> find(Integer id) {
        return repository.find(id);
    }

    public List<Cita> findAll() {
        return repository.findAll();
    }

    public Cita save(Cita cita) {
        return repository.save(cita);
    }

}

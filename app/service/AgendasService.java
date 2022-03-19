package service;

import com.google.inject.Inject;
import domain.Agenda;
import domain.Cita;
import domain.Servicio;
import repository.AgendaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AgendasService {

    private AgendaRepository repository;

    @Inject
    public AgendasService(AgendaRepository repository) {
        this.repository = repository;
    }

    /*
    * used by AgendasController
    * */
    public Agenda save(Agenda agenda) {
        agenda.setId(UUID.randomUUID().toString());
        return repository.save(agenda);
    }

    public Optional<Agenda> update(Agenda agenda, String id) {
        return repository.findFirst(id).map(found -> {
            agenda.setId(found.getId());
            return repository.save(agenda);
        });
    }

    public Optional<Agenda> findFirstAgenda(String agenda, String fecha) {
        return repository.findFirstAgendaByFecha(agenda, fecha);
    }

    public List<Agenda>  findByRange(String startDate, String finalDate) {
        return repository.findByRange(startDate, finalDate);
    }

    public Map<String, Long> getBalanceWeek(String startDate, String finalDate) {
        List<Agenda> byRange = repository.findByRange(startDate, finalDate);
        Map<String, List<Agenda>> map = byRange.stream().collect(Collectors.groupingBy(Agenda::getManicurista));
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> sumAgendas(e.getValue())));
    }

    private Long sumAgendas(List<Agenda> agendas) {
        return   agendas.stream().map(Agenda::getCitas).flatMap(List::stream).map(Cita::getServicios)
                .flatMap(List::stream)
                .map(Servicio::getValor)
                .map(Long::parseLong)
                .reduce(0l, Long::sum);
    }
}

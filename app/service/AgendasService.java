package service;

import com.google.inject.Inject;
import domain.Agenda;
import domain.Servicio;
import repository.AgendaRepository;
import scala.Tuple2;

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

    public List<Agenda> findByRange(String startDate, String finalDate) {
        return repository.findByRange(startDate, finalDate);
    }

    public Map<String, Tuple2<Long, Long>> getBalancePieChart(String startDate, String finalDate) {
        List<Agenda> byRange = repository.findByRange(startDate, finalDate);
        return byRange.stream().collect(Collectors.groupingBy(Agenda::getManicurista)).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> sumAgendasbyPorcentaje(e.getValue())));
    }

    private Tuple2<Long, Long> sumAgendasbyPorcentaje(List<Agenda> agendas) {
        List<Tuple2<Long, Long>> collect = agendas.stream()
                .map(Agenda::getCitas)
                .flatMap(List::stream)
                .map(cita -> Tuple2.apply(cita.getPorcentaje(), cita.getServicios())).map(porcentajeServicios -> {
                    List<Servicio> servicios = porcentajeServicios._2();
                    Double porcentaje = Integer.parseInt(porcentajeServicios._1()) * 0.01;

                    Long subtotal = servicios.stream()
                            .map(Servicio::getValor)
                            .map(Long::parseLong)
                            .reduce(0l, Long::sum);

                    return Tuple2.apply(
                            (long) (subtotal * porcentaje),
                            (long) (subtotal * (1 - porcentaje))
                    );
                }).collect(Collectors.toList());

        Long left = collect.stream().map(Tuple2::_1).reduce(0l, Long::sum);
        Long rigth = collect.stream().map(Tuple2::_2).reduce(0l, Long::sum);

        return Tuple2.apply(left, rigth);
    }
}

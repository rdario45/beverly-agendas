package service;

import com.google.inject.Inject;
import domain.Agenda;
import domain.Cita;
import domain.Servicio;
import repository.AgendaRepository;
import scala.Tuple2;

import java.text.Normalizer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Map<String, Long> getBalanceBarChart(String startDate, String finalDate) {
        Map<String, Long> barChartData = new HashMap<>();

        repository.findByRange(startDate, finalDate)
                .stream()
                .collect(Collectors.groupingBy(Agenda::getFecha))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> sumAgendas(e.getValue())))
                .forEach((key, subtotal) -> {

                    String displayName = Instant.ofEpochMilli(Long.parseLong(key))
                            .atZone(ZoneId.of("America/Bogota"))
                            .getDayOfWeek()
                            .getDisplayName(TextStyle.FULL, new Locale("es", "CO")).toUpperCase();

                    String displayWeekName = Normalizer
                            .normalize(displayName, Normalizer.Form.NFD)
                            .replaceAll("[^\\p{ASCII}]", "");

                    if (barChartData.containsKey(displayWeekName)) {
                        barChartData.put(displayWeekName, (barChartData.get(displayWeekName) + subtotal));

                    } else {
                        barChartData.put(displayWeekName, subtotal);
                    }
                });

        return barChartData;
    }

    private Long sumAgendas(List<Agenda> agendas) {
        return agendas.stream()
                .map(Agenda::getCitas)
                .flatMap(List::stream)
                .map(Cita::getServicios)
                .flatMap(List::stream)
                .map(Servicio::getValor)
                .map(Long::parseLong)
                .reduce(0l, Long::sum);
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

    public Optional<Integer> findHowMany() {
        List<Cita> all = repository.getAll();

        return Optional.of(all.size());
    }

    public Optional<Long> findHowMuch() {
        Stream<Cita> allStream = repository.getAll().stream();

        Stream<Servicio> servicesStream = allStream.flatMap(cita1 -> cita1.getServicios().stream());

        Stream<Long> longStream = servicesStream.map(Servicio::getValor).map(Long::valueOf);

        Long reduce = longStream.reduce(0l, Long::sum);

        return Optional.of(reduce);
    }
}

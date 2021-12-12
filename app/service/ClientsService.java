package service;

import com.google.inject.Inject;
import database.ClientRepository;
import domain.Client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClientsService {

    private ClientRepository repository;

    @Inject
    public ClientsService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> findAll() {
        return repository.findAll();
    }

    public Client registerClient(Client client) {
        Client saved = repository.save(client);
        calcDiscounts(saved);
        return saved;
    }

    private List<Client> findAllReferred(Client client) {
        return repository.getAllReferred(client.getId());
    }

    private Client findClient(Integer id) {
        return repository.find(id);
    }

    private void calcDiscounts(Client client) {
        if (client.getReferred() != null ) {
            Client parent = findClient(client.getReferred());
            CompletableFuture.runAsync(()-> {
                double discount = calcDiscount(0, parent);
                parent.setDiscount(discount);
                repository.update(parent);
            });
            calcDiscounts(parent);
        }
    }

    private double calcDiscount(Integer level, Client client) {
        if ( level < 4) { // max 3
            List<Client> referred = findAllReferred(client);
            if ( referred.size() > 2 ) { // min 3
                return 0.05 + referred.stream()
                        .map(c -> calcDiscount(level + 1, c))
                        .reduce(Double::sum).get();
            }
        }
        return 0.0;
    }

}

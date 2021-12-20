package modules;

import acl.BeverlyRepo;
import com.google.inject.AbstractModule;

public class Global extends AbstractModule {

    public Global() { }

    @Override
    public void configure() {
            bind(BeverlyRepo.class).asEagerSingleton();
    }

}
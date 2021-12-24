package modules;

import acl.BeverlyDB;
import acl.BeverlySQS;
import com.google.inject.AbstractModule;
import sqs.ActionsEvent;

public class Global extends AbstractModule {

    public Global() { }

    @Override
    public void configure() {
        bind(BeverlyDB.class).asEagerSingleton();
        bind(BeverlySQS.class).asEagerSingleton();
        bind(ActionsEvent.class).asEagerSingleton();
    }
}
package modules;

import acl.BeverlyDynamoDB;
import acl.BeverlySQS;
import com.google.inject.AbstractModule;
import sqs.BeverlyActionsEvent;

public class Global extends AbstractModule {

    public Global() { }

    @Override
    public void configure() {
        bind(BeverlyDynamoDB.class).asEagerSingleton();
        bind(BeverlySQS.class).asEagerSingleton();
        bind(BeverlyActionsEvent.class).asEagerSingleton();
    }
}

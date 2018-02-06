package be.ict.mb.spring.akka.demo;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.actor.Props;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GreetingActor extends AbstractActor {
	
	static Props props(String name) {
		return Props.create(GreetingActor.class, () -> new GreetingActor());
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(String.class, s -> getSender().tell("Hello, " + s, getSelf()))
				.build();
	}

}

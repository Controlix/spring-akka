package be.ict.mb.spring.akka.demo.calc;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import lombok.Value;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Calculator extends AbstractActor {
	
	private int accumulator;

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Add.class, req -> add(req.value))
				.match(Result.class, req -> getSender().tell(accumulator, getSelf()))
				.build();
	}

	private void add(int value) {
		accumulator += value;
	}

	@Value
	public static class Add {
		int value;
	}
	
	@Value
	public static class Result {
	}
}

package be.ict.mb.spring.akka.demo.greeting;

import static akka.pattern.Patterns.ask;
import static akka.util.Timeout.durationToTimeout;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.compat.java8.FutureConverters;
import scala.concurrent.duration.FiniteDuration;

@RestController
public class HelloController {
	
	private final ActorSystem actorSystem;
	
	private HelloController(final ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
	}

	@GetMapping("/hello")
	public CompletableFuture<Object> sayHello(@RequestParam(defaultValue = "World") String name) {
		ActorRef actorRef = actorSystem.actorOf(GreetingActor.props(name));

		return FutureConverters
				.toJava(ask(actorRef, name, durationToTimeout(FiniteDuration.create(1, SECONDS))))
				.toCompletableFuture();
	}

}

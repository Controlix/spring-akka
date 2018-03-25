package be.ict.mb.spring.akka.demo.calc;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import be.ict.mb.spring.akka.boot.SpringExtension;
import scala.compat.java8.FutureConverters;
import scala.concurrent.duration.FiniteDuration;

@RestController
@RequestMapping("calc")
public class CalculatorController {

	private final ActorRef calculator;

	private CalculatorController(final ActorSystem actorSystem) {
		this.calculator = actorSystem
				.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props("calculator"), "calculator");
	}

	@GetMapping("/add")
	public void add(@RequestParam("value") int value) {
		calculator.tell(new Calculator.Add(value), null);
	}

	@GetMapping("/result")
	public CompletableFuture<Object> result() {
		FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
		Timeout timeout = Timeout.durationToTimeout(duration);

		return FutureConverters.toJava(Patterns.ask(calculator, new Calculator.Result(), timeout))
				.toCompletableFuture();
	}
}

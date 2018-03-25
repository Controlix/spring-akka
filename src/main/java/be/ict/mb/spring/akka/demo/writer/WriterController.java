package be.ict.mb.spring.akka.demo.writer;

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
@RequestMapping("writer")
public class WriterController {
	
	private final ActorRef writer;

	private WriterController(ActorSystem actorSystem) {
		this.writer = actorSystem
				.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props("writer"), "writer");
	}

	@GetMapping("show")
	public CompletableFuture<Object> show() {
		FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
		Timeout timeout = Timeout.durationToTimeout(duration);

		return FutureConverters.toJava(Patterns.ask(writer, new Writer.Show(), timeout))
				.toCompletableFuture();

	}
	
	@GetMapping("/write")
	public void write(@RequestParam("text") String text) {
		writer.tell(new Writer.Write(text), null);
	}
	
}

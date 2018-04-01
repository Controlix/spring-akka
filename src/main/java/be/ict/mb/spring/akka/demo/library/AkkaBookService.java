package be.ict.mb.spring.akka.demo.library;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import be.ict.mb.spring.akka.boot.SpringExtension;
import be.ict.mb.spring.akka.demo.library.Library.AllBooks;
import scala.compat.java8.FutureConverters;
import scala.concurrent.duration.FiniteDuration;

@Service
public class AkkaBookService implements BookService {
	
	private final ActorSystem actorSystem;
	private final ActorRef library;
	
	private AkkaBookService(final ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
		this.library = actorSystem
				.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props("library"), "library");
	}

	@Override
	public UUID create(String title, String author) {
		UUID id = UUID.randomUUID();
		ActorRef book = actorSystem.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props("book", id, title, author), "book-" + id);
		library.tell(new Library.AddBook(book), null);
		return id;
	}

	@Override @Async
	public CompletableFuture<Collection<BookDetails>> allBooks() {
		FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
		Timeout timeout = Timeout.durationToTimeout(duration);
		return FutureConverters.toJava(Patterns.ask(library, new Library.ListBooks(), timeout)
				.map(AllBooks.class::cast, actorSystem.dispatcher())
				.map(AllBooks::getBookDetails, actorSystem.dispatcher()))
				.toCompletableFuture();
	}

}

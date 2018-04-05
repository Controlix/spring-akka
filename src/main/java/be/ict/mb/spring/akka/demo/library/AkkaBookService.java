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
import be.ict.mb.spring.akka.demo.library.Library.BookId;
import scala.compat.java8.FutureConverters;
import scala.concurrent.duration.FiniteDuration;

@Service
public class AkkaBookService implements BookService {

	private static Timeout TIMEOUT = Timeout.durationToTimeout(FiniteDuration.create(1, TimeUnit.SECONDS));

	private final ActorSystem actorSystem;
	private final ActorRef library;
	
	private AkkaBookService(final ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
		this.library = actorSystem
				.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props("library"), "library");
	}

	@Override
	public CompletableFuture<UUID> create(String title, String author) {
		return FutureConverters.toJava(Patterns.ask(library, new Library.AddBook(title, author), TIMEOUT)
				.map(BookId.class::cast, actorSystem.dispatcher())
				.map(BookId::getId, actorSystem.dispatcher()))
				.toCompletableFuture();
	}

	@Override @Async
	public CompletableFuture<Collection<BookDetails>> allBooks() {
		return FutureConverters.toJava(Patterns.ask(library, new Library.ListBooks(), TIMEOUT)
				.map(AllBooks.class::cast, actorSystem.dispatcher())
				.map(AllBooks::getBookDetails, actorSystem.dispatcher()))
				.toCompletableFuture();
	}

}

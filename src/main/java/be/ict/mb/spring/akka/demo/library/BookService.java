package be.ict.mb.spring.akka.demo.library;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BookService {

	CompletableFuture<UUID> create(String title, String author);

	CompletableFuture<Collection<BookDetails>> allBooks();

}

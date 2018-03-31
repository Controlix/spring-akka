package be.ict.mb.spring.akka.demo.library;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {
	
	private final BookService svc; 
	
	private BookController(final BookService svc) {
		this.svc = svc;
	}

	@PostMapping
	public int create(@RequestBody CreateBookCommand cmd) {
		return svc.create(cmd.title, cmd.author);
	}
	
	@GetMapping
	public CompletableFuture<Collection<BookDetails>> listAll() {
		return svc.allBooks();
	}
}

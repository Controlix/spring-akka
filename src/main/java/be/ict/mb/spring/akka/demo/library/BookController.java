package be.ict.mb.spring.akka.demo.library;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {
	
	private final BookService svc; 
	
	private BookController(final BookService svc) {
		this.svc = svc;
	}

	@PostMapping
	@ResponseBody
	public int create(CreateBookCommand cmd) {
		return svc.create(cmd.title, cmd.aithor).getId();
	}
}

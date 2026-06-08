package xyz.ianworley.keepsake.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
class SpaController {
	private final Resource index = new ClassPathResource("static/index.html");

	@RequestMapping(
			value = {
					"/",
					"/{path:^(?!api|assets|favicon\\.ico).*$}",
					"/**/{path:^(?!api|assets|favicon\\.ico).*$}"
			},
			method = {RequestMethod.GET, RequestMethod.HEAD})
	ResponseEntity<Resource> index() {
		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_HTML)
				.body(index);
	}
}

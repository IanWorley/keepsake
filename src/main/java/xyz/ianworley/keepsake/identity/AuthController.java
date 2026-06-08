package xyz.ianworley.keepsake.identity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
class AuthController {
	private final AuthenticationManager authenticationManager;
	private final HttpSessionSecurityContextRepository securityContextRepository =
			new HttpSessionSecurityContextRepository();

	AuthController(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/login")
	UserSummary login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		securityContextRepository.saveContext(context, httpRequest, httpResponse);
		KeepsakeUserDetails user = (KeepsakeUserDetails) authentication.getPrincipal();
		return new UserSummary(user.id(), user.username(), user.displayName(), user.role());
	}

	@GetMapping("/me")
	UserSummary me(@AuthenticationPrincipal KeepsakeUserDetails user) {
		return new UserSummary(user.id(), user.username(), user.displayName(), user.role());
	}

	@PostMapping("/logout")
	void logout(HttpServletRequest request) {
		SecurityContextHolder.clearContext();
		if (request.getSession(false) != null) {
			request.getSession(false).invalidate();
		}
	}

	record LoginRequest(@NotBlank String username, @NotBlank String password) {
	}
}

package baekgwa.blogserver.global.config;

import static org.springframework.http.HttpMethod.*;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import baekgwa.blogserver.global.entrypoint.CustomAuthenticationEntryPoint;
import baekgwa.blogserver.global.environment.UrlProperties;
import baekgwa.blogserver.global.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : baekgwa.blogserver.global.config
 * FileName    : SecurityConfig
 * Author      : Baekgwa
 * Date        : 2025-05-30
 * Description : 
 * =====================================================================================================================
 * DATE          AUTHOR               NOTE
 * ---------------------------------------------------------------------------------------------------------------------
 * 2025-05-30     Baekgwa               Initial creation
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UrlProperties urlProperties;
	private final AuthenticationFilter authenticationFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// ✅ 보안 관련 설정 (CSRF, CORS, 세션)
			.csrf(AbstractHttpConfigurer::disable)

			// ✅ 기본 인증 방식 비활성화 (JWT 사용)
			.httpBasic(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			// ✅ Cors Setting
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			// ✅ End-point Setting
			.authorizeHttpRequests(authorize -> authorize
				// 프론트엔드에서 적용될 예외 포인트 설정
				.requestMatchers("/error", "/favicon.ico").permitAll()
				// Swagger 문서 접근 허용
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
					"/swagger-ui.html").permitAll()
				.requestMatchers(GET, "/health").permitAll()

				// Authentication
				.requestMatchers(POST, "/auth/login").permitAll()
				.requestMatchers(POST, "/auth/logout").permitAll()

				// Category
				.requestMatchers(GET, "/category").permitAll()

				// Tag
				.requestMatchers(GET, "/tag").permitAll()

				// Post
				.requestMatchers(GET, "/post/detail").permitAll()
				.requestMatchers(GET, "/post").permitAll()

				// Stack
				.requestMatchers(GET, "/stack/post/{postId}").permitAll()
				.requestMatchers(GET, "/stack").permitAll()

				.anyRequest().authenticated());

		// ❗ 인증 Filter 추가
		http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		// ❗ AuthenticationEntryPoint Custom Handler
		http.exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(urlProperties.getFrontend(), urlProperties.getBackend()));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowCredentials(true);
		// configuration.setAllowedHeaders(List.of("Authorization")); // 필요에 따라 open 예정.
		// configuration.setExposedHeaders(List.of("Authorization")); // 필요에 따라 open 예정.
		configuration.setAllowedHeaders(List.of("Content-Type"));
		configuration.setMaxAge(3600L);

		return request -> configuration;
	}
}


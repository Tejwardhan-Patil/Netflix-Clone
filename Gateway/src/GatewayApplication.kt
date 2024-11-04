package com.netflixclone.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

@SpringBootApplication
@EnableDiscoveryClient
class GatewayApplication

fun main(args: Array<String>) {
    SpringApplication.run(GatewayApplication::class.java, *args)
}

@Bean
fun routeLocator(builder: RouteLocatorBuilder): RouteLocator = builder.routes()
    .route("user-service") { r ->
        r.path("/users/**")
            .filters { f -> f.filter(AuthenticationFilter()) }
            .uri("lb://USER-SERVICE")
    }
    .route("video-service") { r ->
        r.path("/videos/**")
            .filters { f -> f.filter(AuthenticationFilter()) }
            .uri("lb://VIDEO-SERVICE")
    }
    .route("recommendation-service") { r ->
        r.path("/recommendations/**")
            .filters { f -> f.filter(AuthenticationFilter()) }
            .uri("lb://RECOMMENDATION-SERVICE")
    }
    .route("analytics-service") { r ->
        r.path("/analytics/**")
            .filters { f -> f.filter(AuthenticationFilter()) }
            .uri("lb://ANALYTICS-SERVICE")
    }
    .build()

// Logging configuration
@Component
class RequestLoggingFilter : WebFilter {

    private val logger: Logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        logger.info("Request path: ${exchange.request.path}")
        return chain.filter(exchange)
    }
}

// Custom Authentication Filter
class AuthenticationFilter : AbstractGatewayFilterFactory<AuthenticationFilter.Config>(Config::class.java) {

    class Config {
        // Configuration properties for authentication filter
        var enabled: Boolean = true
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            ReactiveSecurityContextHolder.getContext().flatMap { securityContext ->
                val principal = securityContext.authentication.principal
                if (principal is Jwt) {
                    val claims = principal.claims
                    val userId = claims["sub"] ?: throw IllegalStateException("JWT Token invalid")
                    exchange.request.mutate().header("X-User-Id", userId.toString())
                }
                chain.filter(exchange)
            }
        }
    }
}

// Security Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/login", "/register").permitAll()
            .anyExchange().authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .and()
            .build()
    }
}

// Exception Handling Filter
@Component
class ExceptionHandlerFilter : AbstractGatewayFilterFactory<ExceptionHandlerFilter.Config>(Config::class.java) {

    class Config {
        // Configuration properties for exception handling
        var logStackTrace: Boolean = true
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            chain.filter(exchange).onErrorResume { throwable ->
                handleException(exchange, throwable, config)
            }
        }
    }

    private fun handleException(exchange: ServerWebExchange, throwable: Throwable, config: Config): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        response.headers.add(HttpHeaders.CONTENT_TYPE, "application/json")
        val errorResponse = "{\"error\": \"Internal Server Error\", \"message\": \"${throwable.message}\"}"
        val buffer = response.bufferFactory().wrap(errorResponse.toByteArray())
        if (config.logStackTrace) {
            LoggerFactory.getLogger(ExceptionHandlerFilter::class.java).error("Exception: ", throwable)
        }
        return response.writeWith(Mono.just(buffer))
    }
}

// OAuth2 Configuration for user authentication
@Component
class JwtAuthFilter : AbstractGatewayFilterFactory<JwtAuthFilter.Config>(Config::class.java) {

    class Config {
        // Configuration properties for JWT authentication
        var enabled: Boolean = true
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            ReactiveSecurityContextHolder.getContext().flatMap { context ->
                val authentication = context.authentication
                if (authentication is Jwt) {
                    val jwt: Jwt = authentication
                    exchange.request.mutate().header("Authorization", "Bearer ${jwt.tokenValue}")
                }
                chain.filter(exchange)
            }
        }
    }
}

// Global Exception Handler
@RestController
class GlobalExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @GetMapping("/error")
    fun handleError(): String {
        logger.error("Error occurred")
        return "An error occurred"
    }
}

// Health Check Endpoint
@RestController
class HealthCheckController {

    @GetMapping("/health")
    fun healthCheck(): String {
        return "API Gateway is running"
    }
}

// CORS Configuration
@Component
class CorsConfigurationFilter : AbstractGatewayFilterFactory<CorsConfigurationFilter.Config>(Config::class.java) {

    class Config {
        // Configuration properties for CORS
        var allowedOrigins: String = "*"
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val headers = exchange.response.headers
            headers.add("Access-Control-Allow-Origin", config.allowedOrigins)
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
            headers.add("Access-Control-Allow-Headers", "Authorization, Content-Type")
            chain.filter(exchange)
        }
    }
}

// Custom Filter to Handle X-Request-ID
@Component
class RequestIdFilter : AbstractGatewayFilterFactory<RequestIdFilter.Config>(Config::class.java) {

    class Config {
        // Configuration properties for request ID handling
        var generateNewId: Boolean = true
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val requestId = exchange.request.headers.getFirst("X-Request-ID")
                ?: if (config.generateNewId) generateRequestId() else ""
            exchange.request.mutate().header("X-Request-ID", requestId)
            chain.filter(exchange)
        }
    }

    private fun generateRequestId(): String {
        return java.util.UUID.randomUUID().toString()
    }
}

// Load Balancer Retry Configuration
@Bean
fun retryGatewayFilterFactory(): RetryGatewayFilterFactory {
    return RetryGatewayFilterFactory()
}

// Retry Logic in case of service failure
@Component
class RetryGatewayFilterFactory : AbstractGatewayFilterFactory<RetryGatewayFilterFactory.Config>(Config::class.java) {

    class Config {
        var retries: Int = 3
        var backoff: Duration = Duration.ofSeconds(2)
    }

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            chain.filter(exchange)
                .retryWhen { it.take(config.retries).delayElements(config.backoff) }
        }
    }
}
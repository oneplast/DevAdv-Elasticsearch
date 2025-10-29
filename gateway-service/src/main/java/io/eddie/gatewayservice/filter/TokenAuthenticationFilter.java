package io.eddie.gatewayservice.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.eddie.core.model.web.TokenAuthorizationResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Optional;

@Component
public class TokenAuthenticationFilter extends AbstractGatewayFilterFactory<TokenAuthenticationFilter.Config> {

    @Value("${custom.jwt.secrets.app-key}")
    private String secretKey;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper om = new ObjectMapper();

    public static class Config {}

    public TokenAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (  !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION) && !request.getCookies().containsKey("token") ) {

                log.error("토큰 혹은 쿠키가 비어있습니다!");

                return response.writeWith(
                        Flux.just(
                                writeUnauthorizedResponseBody(response)
                        )
                );

            }

            Optional<String> tokenOptional = resolveToken(request);

            String token = tokenOptional.orElseThrow(() -> new IllegalStateException("토큰은 비어있을 수 없습니다!"));

            if ( !isValidToken(token) ) {
                return response.writeWith(
                        Flux.just(
                                writeUnauthorizedResponseBody(response)
                        )
                );
            }

            Jws<Claims> claims = getClaims(token);

            String accountCode = claims.getPayload().get("accountCode").toString();

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-CODE", accountCode)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        };
    }

    private DataBuffer writeUnauthorizedResponseBody(ServerHttpResponse response) {

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        TokenAuthorizationResponse body = new TokenAuthorizationResponse("인증이 필요합니다!");

        return response.bufferFactory().wrap(writeResponseBody(body));

    }

    private byte[] writeResponseBody(TokenAuthorizationResponse response) {
        try {
            return om.writeValueAsBytes(response);
        } catch (JsonProcessingException e) {
            log.error("Serialization 오류");
            throw new RuntimeException(e);
        }
    }

    private Optional<String> resolveToken(ServerHttpRequest request) {

        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if ( bearerToken != null && bearerToken.startsWith("Bearer ") ) {
            return Optional.of(bearerToken.substring(7));
        }

        HttpCookie tokenCookie = request.getCookies().getFirst("token");
        if ( tokenCookie != null ) {
            return Optional.of(tokenCookie.getValue());
        }

        return Optional.empty();
    }

    private boolean isValidToken(String token) {

        try {
            getClaims(token);
            return true;
        } catch ( JwtException e ) {
            log.info("Invalid JWT Token was detected: {}  msg : {}", token ,e.getMessage());
        } catch ( IllegalArgumentException e ) {
            log.info("JWT claims String is empty: {}  msg : {}", token ,e.getMessage());
        } catch ( Exception e ) {
            log.error("an error raised from validating token : {}  msg : {}", token ,e.getMessage());
        }

        return false;
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token);
    }


}

package de.opengamebackend.gateway;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class AuthLoginResponseFilter extends ZuulFilter {
    @Autowired
    private JWTConfig jwtConfig;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        return request.getRequestURI().equals("/open-game-backend-auth/login");
    }

    @Override
    public Object run() throws ZuulException {
        // https://spring.io/guides/gs/routing-and-filtering/
        // https://www.baeldung.com/zuul-filter-modifying-response-body

        RequestContext context = RequestContext.getCurrentContext();

        if (context.getResponseStatusCode() != 200) {
            return null;
        }

        try (InputStream in = context.getRequest().getInputStream()) {
            String requestBody = StreamUtils.copyToString(in, Charset.forName("UTF-8"));

            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest request = objectMapper.readValue(requestBody, LoginRequest.class);

            if (request == null) {
                return null;
            }

            String token = JWT.create()
                    .withSubject(request.getPlayerId())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getJwtTokenExpirationTime()))
                    .sign(HMAC512(jwtConfig.getJwtSecret().getBytes()));
            LoginResponse response = new LoginResponse(token);
            String responseBody = objectMapper.writeValueAsString(response);

            context.setResponseBody(responseBody);
        } catch (IOException e) {
            throw new ZuulException(e, 500, e.getMessage());
        }

        return null;
    }
}

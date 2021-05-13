package de.opengamebackend.gateway;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import de.opengamebackend.auth.model.responses.AuthTokenResponse;
import de.opengamebackend.auth.model.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class AuthLoginResponseFilter extends ZuulFilter {
    private GatewayConfig gatewayConfig;

    @Autowired
    public AuthLoginResponseFilter(GatewayConfig gatewayConfig) {
        this.gatewayConfig = gatewayConfig;
    }

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

        try (final InputStream serviceResponseStream = context.getResponseDataStream()) {
            ObjectMapper objectMapper = new ObjectMapper();

            String serviceResponseBody = StreamUtils.copyToString(serviceResponseStream, StandardCharsets.UTF_8);
            LoginResponse response = objectMapper.readValue(serviceResponseBody, LoginResponse.class);

            String[] roles = new String[response.getRoles().size()];
            response.getRoles().toArray(roles);

            String token = JWT.create()
                    .withSubject(response.getPlayerId())
                    .withArrayClaim("roles", roles)
                    .withExpiresAt(new Date(System.currentTimeMillis() + gatewayConfig.getJwtTokenExpirationTime()))
                    .sign(HMAC512(gatewayConfig.getJwtSecret().getBytes()));

            AuthTokenResponse gatewayResponse = new AuthTokenResponse(response.isLocked() ? "" : token);
            gatewayResponse.setProvider(response.getProvider());
            gatewayResponse.setProviderUserId(response.getProviderUserId());
            gatewayResponse.setLocked(response.isLocked());
            gatewayResponse.setFirstTimeSetup(response.isFirstTimeSetup());

            String responseBody = objectMapper.writeValueAsString(gatewayResponse);

            context.setResponseBody(responseBody);

        } catch (IOException e) {
            throw new ZuulException(e, 500, e.getMessage());
        }

        return null;
    }
}

package id.ac.ui.cs.advprog.gatra.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ActuatorSecurityFilter implements Filter {

    @Value("${gatra.monitoring.token:lokal-token-123}")
    private String secretToken;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (req.getRequestURI().startsWith("/actuator")) {
            String providedToken = req.getHeader("X-Grafana-Token");

            // Cek apakah token cocok dengan environment variable
            if (!secretToken.equals(providedToken)) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("Unauthorized Access to Metrics");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
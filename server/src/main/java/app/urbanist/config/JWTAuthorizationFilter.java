package app.urbanist.config;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static app.urbanist.config.SecurityConstants.AUTHORIZATION_HEADER;
import static app.urbanist.config.SecurityConstants.SECRET;
import static app.urbanist.config.SecurityConstants.TOKEN_PREFIX;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserDetailsService userDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserDetailsService userDetailsService) {
        super(authManager);
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(AUTHORIZATION_HEADER);

        System.out.println("HEADER: " + header);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        System.out.println(authentication);
        System.out.println("AUTHORITIES: " + authentication.getAuthorities());
        System.out.println("PRINCIPAL : " + authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        System.out.println("TOKEN : " +  token);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (user != null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(user);
                return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
            }
            return null;
        }
        return null;
    }
}

package in.deepak.authify.util;

import in.deepak.authify.service.impl.AppUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final AppUserDetailService appUserDetailService;
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of("/register","/login","/logout","/reset-password","/send-reset-otp");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if(PUBLIC_URLS.contains(path)){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = null;
        String email = null;

        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);
        }

        if(jwt == null){
            Cookie[] cookies = request.getCookies();
            if(cookies != null){
                for(Cookie cookie:cookies){
                    if(cookie.getName().equals("jwt")){
                        jwt = cookie.getValue();
                    }
                }
            }
        }
        System.out.println("jwt token "+jwt);
        //3. Validate the token and set Security Context

        if(jwt != null){
            email = jwtUtil.extractEmail(jwt);
            System.out.println("email "+email);
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = appUserDetailService.loadUserByUsername(email);
                System.out.println("userDetails : "+userDetails);
                if(jwtUtil.isTokenValid(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    System.out.println(SecurityContextHolder.getContext().getAuthentication());
                }
            }
        }
        System.out.println("just before doFilter forward");

        filterChain.doFilter(request,response);
    }
}




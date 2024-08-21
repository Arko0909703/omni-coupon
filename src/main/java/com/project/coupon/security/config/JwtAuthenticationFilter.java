package com.project.coupon.security.config;

import com.project.coupon.security.services.JwtService;
import com.project.coupon.security.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestHeader=request.getHeader("Authorization");

        String username=null;
        String token=null;

        if(requestHeader!=null && requestHeader.startsWith("Bearer"))
        {
            //Token looking good
            token=requestHeader.substring(7);
            try{

                username= jwtService.extractUserName(token);

            }
            catch(IllegalArgumentException ex){
                logger.info("Illegal Arguments While fetching Username");
                ex.printStackTrace();
            }
            catch(ExpiredJwtException ex){
                logger.info("Jwt Token is Expired!!");
                ex.printStackTrace();
            }
            catch(MalformedJwtException ex){
                logger.info("Some changes  had been done in token!! Invalid Token");
                ex.printStackTrace();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else {
            logger.info("Invalid Header Value");
        }

        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
        {
            //Fetching user detail with the help of username
            UserDetails userDetails=this.userService.userDetailsService().loadUserByUsername(username);
            Boolean validateToken=this.jwtService.isTokenValid(token,userDetails);

            if(validateToken)
            {
                //Setting authentication in Security Context
                UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            else{
                logger.info("Authentication Failed!!");
            }
        }
        //This method is for continuing the Filter chain and validate the request
        filterChain.doFilter(request,response);

    }
}
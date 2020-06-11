package br.com.anibal.captacaofilipeserver.controller;

import br.com.anibal.captacaofilipeserver.model.AuthenticationRequest;
import br.com.anibal.captacaofilipeserver.model.AuthenticationResponse;
import br.com.anibal.captacaofilipeserver.services.MyUserDetailsService;
import br.com.anibal.captacaofilipeserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(allowedHeaders = "*")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    public static final String AUTHENTICATION_URL = "/authenticate";

    @PostMapping(AUTHENTICATION_URL)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(new AuthenticationResponse(jwtUtil.generateToken(userDetails)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

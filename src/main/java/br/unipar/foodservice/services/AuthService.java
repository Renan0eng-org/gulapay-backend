package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.LoginRequest;
import br.unipar.foodservice.dtos.LoginResponse;
import br.unipar.foodservice.entities.Usuario;
import br.unipar.foodservice.repositories.UsuarioRepository;
import br.unipar.foodservice.security.JwtProperties;
import br.unipar.foodservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.login(), request.senha()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Login ou senha inválidos.");
        }

        Usuario usuario = usuarioRepository.findByLogin(request.login())
                .orElseThrow(() -> new BadCredentialsException("Login ou senha inválidos."));

        String token = tokenProvider.generate(usuario.getLogin(), usuario.getId(), usuario.getPerfil().name());
        return new LoginResponse(
                token,
                "Bearer",
                jwtProperties.expirationMinutes(),
                usuario.getId(),
                usuario.getLogin(),
                usuario.getNome(),
                usuario.getPerfil());
    }
}

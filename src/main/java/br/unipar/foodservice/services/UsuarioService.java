package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.UsuarioCreateRequest;
import br.unipar.foodservice.entities.Usuario;
import br.unipar.foodservice.enums.Perfil;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario criar(UsuarioCreateRequest request) {
        if (repository.existsByLogin(request.login())) {
            throw new BusinessException("Já existe um usuário com o login '" + request.login() + "'.");
        }
        validarComissaoConsistente(request.perfil(), request.percentualComissao() != null);

        Usuario usuario = Usuario.builder()
                .login(request.login())
                .nome(request.nome())
                .senha(passwordEncoder.encode(request.senha()))
                .perfil(request.perfil())
                .percentualComissao(request.perfil() == Perfil.GARCOM ? request.percentualComissao() : null)
                .ativo(true)
                .build();
        return repository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorLogin(String login) {
        return repository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + login));
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorPerfil(Perfil perfil) {
        return repository.findByPerfil(perfil);
    }

    private void validarComissaoConsistente(Perfil perfil, boolean comissaoInformada) {
        if (perfil == Perfil.GARCOM && !comissaoInformada) {
            throw new BusinessException("percentualComissao é obrigatório quando perfil = GARCOM.");
        }
        if (perfil != Perfil.GARCOM && comissaoInformada) {
            throw new BusinessException("percentualComissao só é aplicável a perfil = GARCOM.");
        }
    }
}

package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.ClienteCreateRequest;
import br.unipar.foodservice.dtos.ClientePatchRequest;
import br.unipar.foodservice.dtos.ClienteUpdateRequest;
import br.unipar.foodservice.dtos.EnderecoDto;
import br.unipar.foodservice.entities.Cliente;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional
    public Cliente criar(ClienteCreateRequest request) {
        String telefone = normalizarTelefone(request.telefone());
        if (repository.existsByTelefone(telefone)) {
            throw new BusinessException("Já existe um cliente com o telefone " + telefone + ".");
        }
        EnderecoDto end = request.endereco() == null ? EnderecoDto.vazio() : request.endereco();
        Cliente cliente = Cliente.builder()
                .nome(request.nome())
                .telefone(telefone)
                .email(request.email())
                .enderecoLogradouro(end.logradouro())
                .enderecoNumero(end.numero())
                .enderecoComplemento(end.complemento())
                .enderecoBairro(end.bairro())
                .enderecoCidade(end.cidade())
                .enderecoUf(end.uf())
                .enderecoCep(end.cep())
                .ativo(true)
                .build();
        return repository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar(boolean apenasAtivos) {
        return apenasAtivos ? repository.findByAtivoTrue() : repository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorTelefone(String telefone) {
        String normalizado = normalizarTelefone(telefone);
        return repository.findByTelefone(normalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado para o telefone: " + normalizado));
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteUpdateRequest request) {
        Cliente cliente = buscarPorId(id);
        String telefone = normalizarTelefone(request.telefone());
        if (!cliente.getTelefone().equals(telefone) && repository.existsByTelefone(telefone)) {
            throw new BusinessException("Já existe outro cliente com o telefone " + telefone + ".");
        }
        EnderecoDto end = request.endereco() == null ? EnderecoDto.vazio() : request.endereco();
        cliente.setNome(request.nome());
        cliente.setTelefone(telefone);
        cliente.setEmail(request.email());
        cliente.setEnderecoLogradouro(end.logradouro());
        cliente.setEnderecoNumero(end.numero());
        cliente.setEnderecoComplemento(end.complemento());
        cliente.setEnderecoBairro(end.bairro());
        cliente.setEnderecoCidade(end.cidade());
        cliente.setEnderecoUf(end.uf());
        cliente.setEnderecoCep(end.cep());
        cliente.setAtivo(request.ativo());
        return cliente;
    }

    @Transactional
    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
    }

    /**
     * Atualização parcial. Telefone informado é re-normalizado e revalidado
     * quanto a unicidade. Endereço, se presente, atualiza apenas os campos
     * informados (null mantém o valor atual).
     */
    @Transactional
    public Cliente patch(Long id, ClientePatchRequest req) {
        Cliente cliente = buscarPorId(id);
        if (req.nome() != null) cliente.setNome(req.nome());
        if (req.telefone() != null) {
            String tel = normalizarTelefone(req.telefone());
            if (!cliente.getTelefone().equals(tel) && repository.existsByTelefone(tel)) {
                throw new BusinessException("Já existe outro cliente com o telefone " + tel + ".");
            }
            cliente.setTelefone(tel);
        }
        if (req.email() != null) cliente.setEmail(req.email());
        if (req.endereco() != null) {
            EnderecoDto e = req.endereco();
            if (e.logradouro()  != null) cliente.setEnderecoLogradouro(e.logradouro());
            if (e.numero()      != null) cliente.setEnderecoNumero(e.numero());
            if (e.complemento() != null) cliente.setEnderecoComplemento(e.complemento());
            if (e.bairro()      != null) cliente.setEnderecoBairro(e.bairro());
            if (e.cidade()      != null) cliente.setEnderecoCidade(e.cidade());
            if (e.uf()          != null) cliente.setEnderecoUf(e.uf());
            if (e.cep()         != null) cliente.setEnderecoCep(e.cep());
        }
        if (req.ativo() != null) cliente.setAtivo(req.ativo());
        return cliente;
    }

    /**
     * Remove tudo que não for dígito do telefone para uso canônico (chave única,
     * link wa.me). O usuário pode digitar com formatação; armazenamos limpo.
     */
    private String normalizarTelefone(String entrada) {
        String somenteDigitos = entrada == null ? "" : entrada.replaceAll("\\D", "");
        if (somenteDigitos.length() < 8 || somenteDigitos.length() > 15) {
            throw new BusinessException("Telefone inválido após normalização: '" + somenteDigitos + "'.");
        }
        return somenteDigitos;
    }
}

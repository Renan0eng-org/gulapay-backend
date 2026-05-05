package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Cliente;

public record ClienteResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String linkWhatsApp,
        EnderecoDto endereco,
        Boolean ativo
) {
    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(
                c.getId(),
                c.getNome(),
                c.getTelefone(),
                c.getEmail(),
                "https://wa.me/" + c.getTelefone(),
                new EnderecoDto(
                        c.getEnderecoLogradouro(),
                        c.getEnderecoNumero(),
                        c.getEnderecoComplemento(),
                        c.getEnderecoBairro(),
                        c.getEnderecoCidade(),
                        c.getEnderecoUf(),
                        c.getEnderecoCep()),
                c.getAtivo());
    }
}

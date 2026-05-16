# FoodService API

API REST em Java/Spring Boot para o Sistema de Comandas e Gestão para Food Service.

> Contexto, decisões de domínio e roadmap completos estão em `../CLAUDE.md` e `../ANALISE_INICIAL.md`.

## Estado atual — Sprint 0 + Sprint 1 entregues

### Etapa 1.0 — Identidade
- Esqueleto Maven + Spring Boot 3.3.4 + Java 21.
- Estrutura de pacotes por camada técnica.
- Perfis `dev` (Postgres), `test` (H2 in-memory), `prod` (Postgres com env vars).
- Flyway com migrations `V1` (usuario) + `V2` (alter usuario adiciona percentualComissao).
- Cadastro de usuário, login JWT, perfil corrente, listagem por perfil.
- Spring Security + filtro JWT (HS256, expiração configurável, default 8h).
- Auditoria via `BaseEntity` + `JpaAuditingConfig`.
- Tratamento global de erros no padrão RFC 7807.
- Swagger UI em `/swagger-ui.html`, health em `/actuator/health`.
- Seed automático de admin em `dev` (`admin / admin123`).

### Etapa 1.1 — Cadastros básicos
- Migrations `V3` a `V7` (categoria, produto, cliente, mesa, entregador).
- Entidades, repositórios, services e controllers para: `Categoria`, `Produto`, `Cliente`, `Mesa`, `Entregador`.
- Enum `TipoProduto` (UNITARIO/COMPOSTO/COMBO; só UNITARIO funcional nesta etapa).
- Enum `SetorProducao` (COZINHA/BAR/BALCAO).
- Enum `StatusMesa` (LIVRE/OCUPADA/AGUARDANDO_PAGAMENTO/FECHADA).
- `Usuario` estendido com `percentualComissao` (validação: obrigatório quando perfil=GARCOM).
- **Catálogo público (`GET /catalogo`)** — RF11, sem auth, lista produtos ativos agrupados por categoria.
- Telefone do `Cliente` normalizado a apenas dígitos para uso na chave única e no link WhatsApp (`https://wa.me/<digitos>`).
- Endereço do Cliente embutido na própria tabela (refatoração para 1:N na Sprint de Comanda).
- Soft delete em todos os cadastros (campo `ativo`), preservando histórico (RNF08).
- Testes unitários: `UsuarioService` (5 cenários), `CategoriaService` (2), `ClienteService` (3), além do `contextLoads`.

### Sprint 1 — Estoque (Etapas 2.0 → 2.3)
- Migrations `V8` a `V12` (unidade_medida com seed, insumo, lote, movimentacao_estoque, alter produto add insumo_id com **backfill automático** de Insumo-espelho para os UNITARIO existentes).
- Cadastro `UnidadeMedida` com `tipoMedida` (MASSA/VOLUME/UNIDADE) e `fatorParaBase`. Conversão centralizada em `UnidadeMedidaService.converter()`.
- Cadastro `Insumo` com `unidadePadrao` (FK), `estoqueMinimo` e cálculo derivado de `estoqueAtual` (soma dos lotes ativos).
- Cadastro `Lote` com validade, `quantidadeRestante` em unidade-padrão, `custoUnitario`. Repository com query FEFO.
- `MovimentacaoEstoque` com 6 tipos (ENTRADA_COMPRA, ENTRADA_TROCA, SAIDA_VENDA, SAIDA_PERDA_VALIDADE, SAIDA_PERDA_QUEBRA, AJUSTE_INVENTARIO).
- Lógica FEFO em saídas com **split entre múltiplos lotes** (uma saída de 400 g pode gerar 2 movimentações se atravessar 2 lotes).
- `SAIDA_VENDA` bloqueado via API manual — reservado para o fechamento de comanda (Sprint 2).
- `AJUSTE_INVENTARIO` substitui o saldo do lote indicado pela quantidade informada.
- Endpoint `GET /insumos?abaixoDoMinimo=true` para alertas operacionais (RF43).
- `Produto` agora valida `insumoId` conforme `tipoProduto` (obrigatório para UNITARIO, proibido para COMPOSTO/COMBO).
- Testes adicionados: `UnidadeMedidaServiceTest` (5 cenários cobrindo conversão entre unidades, mesmo tipo, tipos incompatíveis), `MovimentacaoEstoqueServiceTest` (3 cenários cobrindo bloqueio de SAIDA_VENDA, FEFO atravessando múltiplos lotes, saldo insuficiente).

## Pré-requisitos

- JDK 21
- Maven 3.9+
- PostgreSQL 14+ (apenas para dev/prod; testes usam H2)

## Banco de dados (perfil dev)

Crie o banco antes de subir a aplicação:

```sql
CREATE DATABASE foodservice;
```

Variáveis de ambiente (todas têm default em dev):

| Variável | Default | Descrição |
|----------|---------|-----------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/foodservice` | URL JDBC |
| `DB_USER` | `postgres` | Usuário do Postgres |
| `DB_PASSWORD` | `postgres` | Senha do Postgres |
| `JWT_SECRET` | `CHANGE_ME_DEV_SECRET_USE_AT_LEAST_32_CHARS_FOR_HS256` | Segredo HS256 (mín. 32 chars) |
| `JWT_EXPIRATION_MINUTES` | `480` | Validade do token em minutos |
| `SERVER_PORT` | `8080` | Porta HTTP |
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil ativo |

## Rodando

```bash
# build + testes
mvn clean verify

# subir em modo dev (Postgres precisa estar rodando)
mvn spring-boot:run

# ou com perfil explícito
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

Aplicação sobe em `http://localhost:8080`.

## Endpoints (Etapas 1.0 + 1.1)

| Método | Rota | Auth | Perfil |
|--------|------|------|--------|
| GET  | `/actuator/health` | público | — |
| GET  | `/swagger-ui.html` | público | — |
| GET  | `/catalogo`        | público | — |
| POST | `/auth/login`      | público | — |
| POST | `/usuarios`        | JWT | ADMINISTRADOR |
| GET  | `/usuarios`        | JWT | ADMINISTRADOR |
| GET  | `/usuarios/me`     | JWT | qualquer |
| POST/PUT/DELETE | `/categorias`, `/categorias/{id}` | JWT | ADMINISTRADOR |
| GET             | `/categorias`, `/categorias/{id}` | JWT | qualquer |
| POST/PUT/DELETE | `/produtos`, `/produtos/{id}`     | JWT | ADMINISTRADOR |
| GET             | `/produtos`, `/produtos/{id}`     | JWT | qualquer |
| POST/PUT | `/clientes`, `/clientes/{id}` | JWT | ADM/CAIXA/GARCOM |
| GET      | `/clientes`, `/clientes/{id}` | JWT | qualquer |
| DELETE   | `/clientes/{id}`              | JWT | ADM/CAIXA |
| POST/PUT/DELETE | `/mesas`, `/mesas/{id}`         | JWT | ADMINISTRADOR |
| GET             | `/mesas`, `/mesas/{id}`         | JWT | qualquer |
| POST/PUT | `/entregadores`, `/entregadores/{id}` | JWT | ADM/CAIXA |
| GET      | `/entregadores`, `/entregadores/{id}` | JWT | qualquer |
| DELETE   | `/entregadores/{id}`                  | JWT | ADMINISTRADOR |

## Quickstart manual

```bash
# 1. login com o admin gerado em dev
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"login":"admin","senha":"admin123"}'

# resposta inclui accessToken — use nas próximas chamadas
# 2. criar um garçom
curl -X POST http://localhost:8080/usuarios \
  -H "Authorization: Bearer <accessToken>" \
  -H 'Content-Type: application/json' \
  -d '{"login":"joao.garcom","nome":"João Silva","senha":"senha123","perfil":"GARCOM"}'

# 3. consultar quem está logado
curl http://localhost:8080/usuarios/me \
  -H "Authorization: Bearer <accessToken>"
```

Coleção pronta para Postman: `POSTMAN.md` na raiz do projeto.

## Próximas etapas

- **Sprint 2** — Comanda (3 origens, transferência/edição/cancelamento, auditoria via `EventoItemComanda`, link WhatsApp). Inclui geração automática de `SAIDA_VENDA` no fechamento, atravessando ficha técnica para os COMPOSTOS.
- **Sprint 3** — Rateio (5 estratégias, incluindo `POR_ITEM`).
- **Sprint 4+** — Pagamentos, Caixa, Financeiro completo, Relatórios, Impressão térmica via `.jrxml`.
- **Sprint 5** — Ficha técnica e Combos (destrava `tipoProduto = COMPOSTO` e `COMBO`).

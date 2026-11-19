# SocialMetrics — Sistema de Gestão e Inteligência de Mídias Sociais

Projeto Integrador de Programação Web (ESEG — 2º Semestre de 2026)  
**Aluno:** Fabrício Saraiva Borba de Meneses Filho (RA: 42842)

---

## 1. Visão Geral
Aplicação web full stack desenvolvida em **Java + Spring Boot 3.5.7** para modernização do sistema legado de gestão de mídias sociais (`19_midias_sociais.xlsx`), contemplando modelagem relacional/JPA, carga oficial de dados, CRUDs completos, autenticação baseada em sessão, controle de acesso por perfil (RBAC) e interface responsiva com Bootstrap 5 e Thymeleaf.

### Tecnologias Utilizadas
* **Backend:** Java 21+, Spring Boot 3.5.7 (Spring Web MVC, Spring Data JPA, Hibernate 6, Lombok)
* **Frontend:** Thymeleaf, Bootstrap 5.3.3, Bootstrap Icons, CSS customizado (`estilo-midias.css`)
* **Banco de Dados:** H2 Database (arquivo local persistente em `./database/midias_sociais.mv.db`, schema `APP`)
* **Carga Inicial:** `src/main/resources/data.sql` (40 contas, 140 campanhas, 180 perfis sociais, 120 influenciadores, 707 parcerias, 498 posts, 500 comentários e 2 usuários padrão)

---

## 2. Pré-requisitos e Execução

### Pré-requisitos
* **JDK 21 ou 22** (ex.: Amazon Corretto 22) configurado em `JAVA_HOME`
* Maven Wrapper incluído no repositório (`mvnw.cmd` no Windows / `./mvnw` no Linux/Mac)

### Como Executar
1. Clone o repositório:
   ```bash
   git clone https://github.com/Febas17/MidiasSociais_ProjPWeb.git
   cd MidiasSociais_ProjPWeb
   ```
2. Inicie a aplicação:
   ```powershell
   # Windows (PowerShell)
   .\mvnw.cmd spring-boot:run
   ```
   ```bash
   # Linux / macOS
   ./mvnw spring-boot:run
   ```
3. Acesse no navegador:
   * **Aplicação:** [http://localhost:8082](http://localhost:8082)
   * **Console H2:** [http://localhost:8082/h2-console](http://localhost:8082/h2-console)
     * **JDBC URL:** `jdbc:h2:file:./database/midias_sociais;AUTO_SERVER=TRUE`
     * **User:** `sa` | **Password:** *(em branco)*

### Como Rodar os Testes Automatizados
```powershell
.\mvnw.cmd test
```

---

## 3. Credenciais de Acesso (RBAC)

| Perfil | Usuário / E-mail | Senha | Permissões |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `fabricio` / `fabricio@empresa.com.br` | `admin123` | Acesso total a todos os módulos + Gestão de Usuários (`/usuarios`) |
| **ANALISTA** | `analista` / `analista@empresa.com.br` | `analista123` | Acesso operacional (Contas, Campanhas, Perfis, Posts, Moderação, Influenciadores); bloqueado em `/usuarios` |

---

## 4. Estrutura do Projeto
```text
src/main/java/br/edu/eseg/midias_sociais/
├── config/          # AutenticacaoInterceptor e WebMvcConfig (proteção de rotas)
├── controller/      # Controllers MVC (Home, Login, Conta, Campanha, Perfil, Post, Comentario, Influenciador, Usuario)
├── model/           # Entidades JPA (@Entity) e Enum PerfilAcesso
├── repository/      # Interfaces Spring Data JPA
└── service/         # Regras de negócio e transações (@Service, @Transactional)
```

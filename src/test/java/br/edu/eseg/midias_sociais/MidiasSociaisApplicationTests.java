package br.edu.eseg.midias_sociais;

import br.edu.eseg.midias_sociais.repository.ContaRepository;
import br.edu.eseg.midias_sociais.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MidiasSociaisApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveCarregarContextoEDadosIniciais() {
        assertThat(contaRepository.count()).isGreaterThanOrEqualTo(40);
        assertThat(usuarioRepository.count()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void deveRedirecionarParaLoginQuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void deveBloquearAcessoAUsuariosParaPerfilAnalista() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .param("username", "analista")
                        .param("senha", "analista123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mockMvc.perform(get("/contas").session(session))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuarios").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?semPermissao=true"));
    }

    @Test
    void devePermitirAcessoAUsuariosParaPerfilAdmin() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .param("username", "fabricio")
                        .param("senha", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mockMvc.perform(get("/usuarios").session(session))
                .andExpect(status().isOk());
    }
}

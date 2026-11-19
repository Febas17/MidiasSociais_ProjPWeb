package br.edu.eseg.midias_sociais.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AutenticacaoInterceptor autenticacaoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacaoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login/**", "/logout", "/css/**", "/js/**", "/images/**", "/h2-console/**", "/error");
    }
}

package com.luxury_sales.ms_usuario.config;

import com.luxury_sales.ms_usuario.model.Usuario;
import com.luxury_sales.ms_usuario.repository.usuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final usuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info(">>> ms-usuario: BD ya tiene datos, se omite la carga inicial.");
            return;
        }

        usuarioRepository.save(new Usuario(null, "Alex", "Duoc123"));
        usuarioRepository.save(new Usuario(null, "Mateo", "Duoc1234"));

        log.info(">>> ms-usuario: {} usuarios insertados.", usuarioRepository.count());
    }
}
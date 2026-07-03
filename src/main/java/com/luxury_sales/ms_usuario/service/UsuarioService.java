package com.luxury_sales.ms_usuario.service;

import com.luxury_sales.ms_usuario.dto.UsuarioRequestDTO;
import com.luxury_sales.ms_usuario.dto.UsuarioResponseDTO;
import com.luxury_sales.ms_usuario.model.Usuario;
import com.luxury_sales.ms_usuario.repository.usuarioRepository; 
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService { 

    private final usuarioRepository usuarioRepo;

   
    public UsuarioService(usuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepo.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        return usuarioRepo.findById(id)
                .map(this::convertirAResponseDTO);
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        if (usuarioRepo.findAll().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(dto.getUsername()))) {
            throw new RuntimeException("El usuario '" + dto.getUsername() + "' ya existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword()); 

        Usuario usuarioGuardado = usuarioRepo.save(usuario);
        return convertirAResponseDTO(usuarioGuardado);
    }

    public Optional<UsuarioResponseDTO> actualizar(Long id, UsuarioRequestDTO dto) {
        return usuarioRepo.findById(id).map(usuarioExistente -> {
            usuarioExistente.setUsername(dto.getUsername());
            usuarioExistente.setPassword(dto.getPassword());
            Usuario usuarioActualizado = usuarioRepo.save(usuarioExistente);
            return convertirAResponseDTO(usuarioActualizado);
        });
    }

    public void eliminar(Long id) {
        usuarioRepo.deleteById(id);
    }

    private UsuarioResponseDTO convertirAResponseDTO(Usuario usuario) {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(usuario.getId());               
        response.setUsername(usuario.getUsername());   
        return response;
    }
}
package com.luxury_sales.ms_usuario;

import com.luxury_sales.ms_usuario.dto.UsuarioRequestDTO;
import com.luxury_sales.ms_usuario.dto.UsuarioResponseDTO;
import com.luxury_sales.ms_usuario.model.Usuario;
import com.luxury_sales.ms_usuario.repository.usuarioRepository;
import com.luxury_sales.ms_usuario.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private usuarioRepository usuarioRepo;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario1;
    private Usuario usuario2;
    private UsuarioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("carlos123");
        usuario1.setPassword("pass123");

        usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("maria456");
        usuario2.setPassword("pass456");

        requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsername("carlos123");
        requestDTO.setPassword("pass123");
    }

    // --- TEST 1: obtener todos los usuarios ---
    @Test
    void obtenerTodos_debeRetornarListaDeUsuarios() {
        // GIVEN
        when(usuarioRepo.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        // WHEN
        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("carlos123", resultado.get(0).getUsername());
        assertEquals("maria456", resultado.get(1).getUsername());
        verify(usuarioRepo, times(1)).findAll();
    }

    // --- TEST 2: obtener usuario por ID que existe ---
    @Test
    void obtenerPorId_cuandoExiste_debeRetornarUsuario() {
        // GIVEN
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario1));

        // WHEN
        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(1L);

        // THEN
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("carlos123", resultado.get().getUsername());
    }

    // --- TEST 3: obtener usuario por ID que NO existe ---
    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornarEmpty() {
        // GIVEN
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(99L);

        // THEN
        assertFalse(resultado.isPresent());
    }

    // --- TEST 4: guardar usuario nuevo exitosamente ---
    @Test
    void guardar_usuarioNuevo_debeCrearYRetornarUsuario() {
        // GIVEN — la lista está vacía, username no existe
        when(usuarioRepo.findAll()).thenReturn(Collections.emptyList());
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuario1);

        // WHEN
        UsuarioResponseDTO resultado = usuarioService.guardar(requestDTO);

        // THEN
        assertNotNull(resultado);
        assertEquals("carlos123", resultado.getUsername());
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
    }

    // --- TEST 5: guardar usuario duplicado lanza excepción ---
    @Test
    void guardar_usuarioDuplicado_debeLanzarExcepcion() {
        // GIVEN — ya existe un usuario con ese username
        when(usuarioRepo.findAll()).thenReturn(Arrays.asList(usuario1));

        UsuarioRequestDTO duplicado = new UsuarioRequestDTO();
        duplicado.setUsername("carlos123"); // mismo username
        duplicado.setPassword("otrapass");

        // WHEN / THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            usuarioService.guardar(duplicado)
        );
        assertTrue(ex.getMessage().contains("carlos123"));
        assertTrue(ex.getMessage().contains("ya existe"));
    }

    // --- TEST 6: actualizar usuario existente ---
    @Test
    void actualizar_cuandoExiste_debeActualizarYRetornarUsuario() {
        // GIVEN
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuario1));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuario1);

        UsuarioRequestDTO updateDTO = new UsuarioRequestDTO();
        updateDTO.setUsername("carlos_updated");
        updateDTO.setPassword("newpass123");

        // WHEN
        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizar(1L, updateDTO);

        // THEN
        assertTrue(resultado.isPresent());
        verify(usuarioRepo, times(1)).findById(1L);
        verify(usuarioRepo, times(1)).save(any(Usuario.class));
    }

    // --- TEST 7: actualizar usuario que NO existe devuelve empty ---
    @Test
    void actualizar_cuandoNoExiste_debeRetornarEmpty() {
        // GIVEN
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<UsuarioResponseDTO> resultado = usuarioService.actualizar(99L, requestDTO);

        // THEN
        assertFalse(resultado.isPresent());
    }

    // --- TEST 8: eliminar usuario ---
    @Test
    void eliminar_debeEjecutarseSinError() {
        // GIVEN
        doNothing().when(usuarioRepo).deleteById(1L);

        // WHEN
        assertDoesNotThrow(() -> usuarioService.eliminar(1L));

        // THEN
        verify(usuarioRepo, times(1)).deleteById(1L);
    }
}
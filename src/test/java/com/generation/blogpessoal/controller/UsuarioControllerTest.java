package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Test
	@Order(1)
	@DisplayName("Cadastrar apenas um Usuário")
	public void  deveCadastrarUmUsuario() { 
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Marjory Matos", "marjory@test.com.br", "123456789", "https://google.com.br" ));
		
		ResponseEntity<Usuario> resposta = testRestTemplate
				.exchange("/usuario/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
		assertEquals(requisicao.getBody().getEmailUsuario(), resposta.getBody().getEmailUsuario());
	}
	
	@Test
	@Order(2)
	@DisplayName("Não deve permitir duplicação de uruário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.CadastrarUsuario(
				new Usuario(0L, "Marjory Matos", "marjory@test.com.br", "123456789", "https://google.com.br"));
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Marjory Matos", "marjory@test.com.br", "123456789", "https://google.com.br"));
		
		ResponseEntity<Usuario> resposta = testRestTemplate
				.exchange("/usuario/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
	}
	
	@Test
	@Order(3)
	@DisplayName("Alterar um Usuário")
	public void deveAlterarUmUsuario() {
		
		Optional<Usuario> usuarioCreate = usuarioService
				.CadastrarUsuario(new Usuario(0L, "Marjory Matos", "marjory@teste.com.br", "123456789", ""));
		
		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(),
					"Marjory B Matos", "marjory_matos@teste.com.br", "1234567897", "http://google.com.br");
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> resposta = testRestTemplate.withBasicAuth("root", "root")
				.exchange("/usuario/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals(usuarioUpdate.getNome(), resposta.getBody().getNome());
		assertEquals(usuarioUpdate.getEmailUsuario(), resposta.getBody().getEmailUsuario());
		assertEquals(usuarioUpdate.getFoto(), resposta.getBody().getFoto());
	}
	
	@Test
	@Order(4)
	@DisplayName("Listar todos os usuarios")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.CadastrarUsuario(new Usuario(
				0L, "Marjory Matos", "marjory@test.com.br", "123456789", ""));
		
		usuarioService.CadastrarUsuario(new Usuario(
				0L, "Yhuri Gross", "yhuri@test.com.br", "123456789", ""));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuario/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	

}

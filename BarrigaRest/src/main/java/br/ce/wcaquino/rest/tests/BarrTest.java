package br.ce.wcaquino.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;

public class BarrTest extends BaseTest {
	
	private String TOKEN;
	
	@Before
	public void login() {
		
	//Criando Map para enviar o token para ter acesso a aplicação e poder inserir uma conta
		Map<String, String> login = new HashMap<>();
		 login.put("email", "leandro.nares@gmail.com");
		 login.put("senha", "123");	
			//Variável que receberá o Token	
		    TOKEN = given() 
			//Enviado o usuário e senha para a API
				.body(login)
				.when()
					.post("/signin")
				.then()
					.statusCode(200)
					//Extraindo o Token
					.extract().path("token");	
	}

	@Test
	public void naoDeveAcessarApiSemToken() {	
		given()
		.when()
			//Requisição em /contas
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		
		//Criando a conta após logar na aplicação
		given()
			//Enviado o token para a aplicação
			.header("Authorization", "JWT " + TOKEN) //Aplicações mais recentes usam "bearer" no lugar de JWT
			//Enviado em um body o nome da conta que será cadastrada
			.body("{\"nome\": \"conta qualquer\"}")
		.when()
			//Rota em que a conta será cadastrada
			.post("/contas")
		.then()
			.statusCode(201)
		;		
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body("{\"nome\": \"conta depois da alteração\"}")
		.when()
			.put("/contas/30893")// id da conta que fiz a alteração
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", is("conta depois da alteração"))
		;		
	}	
	
	@Test
	public void naoDeveInserirContaMesmoNome() {
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body("{\"nome\": \"conta depois da alteração\"}")
		.when()
			.post("/contas")// id da conta que fiz a alteração
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
			
		;		
	}	
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(30893);
		//mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descricao da Movimentacao");
		mov.setEnvolvido("Envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao("01/01/2000");
		mov.setData_pagamento("10/05/2010");
		mov.setValor(100f);
		mov.setStatus(true);
		
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			//.body("error", is("Já existe uma conta com esse nome!"))
			
		;		
	}	
}

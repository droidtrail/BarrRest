package br.ce.wcaquino.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;

public class BarrTest extends BaseTest {
	
	private String TOKEN;
	
	@Before
	public void login() {
		
	//Criando Map para enviar o token para ter acesso a aplica��o e poder inserir uma conta
		Map<String, String> login = new HashMap<>();
		 login.put("email", "leandro.nares@gmail.com");
		 login.put("senha", "123");	
			//Vari�vel que receber� o Token	
		    TOKEN = given() 
			//Enviado o usu�rio e senha para a API
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
			//Requisi��o em /contas
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}
	
	@Test
	public void deveIncluirContaComSucesso() {
		
		//Criando a conta ap�s logar na aplica��o
		given()
			//Enviado o token para a aplica��o
			.header("Authorization", "JWT " + TOKEN) //Aplica��es mais recentes usam "bearer" no lugar de JWT
			//Enviado em um body o nome da conta que ser� cadastrada
			.body("{\"nome\": \"conta qualquer\"}")
		.when()
			//Rota em que a conta ser� cadastrada
			.post("/contas")
		.then()
			.statusCode(201)
		;		
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body("{\"nome\": \"conta depois da altera��o\"}")
		.when()
			.put("/contas/30893")// id da conta que fiz a altera��o
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", is("conta depois da altera��o"))
		;		
	}	
}

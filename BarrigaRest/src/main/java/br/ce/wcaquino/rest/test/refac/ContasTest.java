package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.utils.BarrUtils;

public class ContasTest extends BaseTest {
	
	@Test
	public void deveIncluirContaComSucesso() {
		
		//Criando a conta após logar na aplicação
		given()
			
			//Enviado em um body o nome da conta que será cadastrada
			.body("{\"nome\": \"Conta Inserida\"}")
		.when()
			//Rota em que a conta será cadastrada
			.post("/contas")
		.then()
			.statusCode(201)
		;		
	}
	
	@Test
	public void deveAlterarContaComSucesso() {
		
		Integer CONTA_ID = BarrUtils.getIdContaPeloNome("Conta para alterar");
		
		given()
			.body("{\"nome\": \"Conta alterada\"}")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")// id da conta que fiz a alteração
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", is("Conta alterada"))
		;		
	}	
	
	@Test
	public void naoDeveInserirContaMesmoNome() {
		given()
			.body("{\"nome\": \"Conta mesmo nome\"}")
		.when()
			.post("/contas")// id da conta que fiz a alteração
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
			
		;		
	}

}

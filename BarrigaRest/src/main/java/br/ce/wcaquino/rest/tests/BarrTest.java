package br.ce.wcaquino.rest.tests;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;

public class BarrTest extends BaseTest {

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
}

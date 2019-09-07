package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest {
	
	//Esse método de teste não tem Token
		@Test
		public void t11_naoDeveAcessarApiSemToken() {	
			FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
			req.removeHeader("Authorization");
			given()
			.when()
				//Requisição em /contas
				.get("/contas")
			.then()
				.statusCode(401)
			;
		}

}

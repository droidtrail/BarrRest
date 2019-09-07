package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest {
	
	//Esse m�todo de teste n�o tem Token
		@Test
		public void t11_naoDeveAcessarApiSemToken() {	
			FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
			req.removeHeader("Authorization");
			given()
			.when()
				//Requisi��o em /contas
				.get("/contas")
			.then()
				.statusCode(401)
			;
		}

}

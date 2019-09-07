package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AuthTest extends BaseTest {
	
	@BeforeClass
	public static void login() {
		
	//Criando Map para enviar o token para ter acesso a aplica��o e poder inserir uma conta
		Map<String, String> login = new HashMap<>();
		 login.put("email", "leandro.nares@gmail.com");
		 login.put("senha", "123");	
		 
			//Vari�vel que receber� o Token	
		    String TOKEN = given() 
			//Enviado o usu�rio e senha para a API
				.body(login)
				.when()
					.post("/signin")
				.then()
					.statusCode(200)
					//Extraindo o Token
					.extract().path("token");	
		    
		    RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
		    RestAssured.get("/reset").then().statusCode(200);
	}
	
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

package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;

import io.restassured.RestAssured;

public class ContasTest {
	
	@BeforeClass
	public static void login() {
		
	//Criando Map para enviar o token para ter acesso a aplicação e poder inserir uma conta
		Map<String, String> login = new HashMap<>();
		 login.put("email", "leandro.nares@gmail.com");
		 login.put("senha", "123");	
		 
			//Variável que receberá o Token	
		    String TOKEN = given() 
			//Enviado o usuário e senha para a API
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

}

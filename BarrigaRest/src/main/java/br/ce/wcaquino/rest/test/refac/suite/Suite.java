package br.ce.wcaquino.rest.test.refac.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.test.refac.AuthTest;
import br.ce.wcaquino.rest.test.refac.ContasTest;
import br.ce.wcaquino.rest.test.refac.MovimentacaoTest;
import br.ce.wcaquino.rest.test.refac.SaldoTest;
import io.restassured.RestAssured;

//Executar como suite
@RunWith(org.junit.runners.Suite.class)
//Quais classes serão executadas suite
@SuiteClasses({
	//As classes serão executadas na ordem
	ContasTest.class,
	MovimentacaoTest.class,
	SaldoTest.class,
	AuthTest.class

})
public class Suite extends BaseTest {

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

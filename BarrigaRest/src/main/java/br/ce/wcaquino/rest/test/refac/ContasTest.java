package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import io.restassured.RestAssured;

public class ContasTest extends BaseTest {
	
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
		
		Integer CONTA_ID = getIdContaPeloNome("Conta para alterar");
		
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
	//Pesquisar pelo nome da conta para obter o ID dela
	public Integer getIdContaPeloNome(String nome) {
		
		return RestAssured.get("/contas?nome="+nome).then().extract().path("id[0]");
		 
	}

}

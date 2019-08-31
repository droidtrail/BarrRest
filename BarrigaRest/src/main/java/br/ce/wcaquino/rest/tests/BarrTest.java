package br.ce.wcaquino.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
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
	
	@Test
	public void naoDeveInserirContaMesmoNome() {
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body("{\"nome\": \"conta depois da altera��o\"}")
		.when()
			.post("/contas")// id da conta que fiz a altera��o
		.then()
			.statusCode(400)
			.body("error", is("J� existe uma conta com esse nome!"))
			
		;		
	}	
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		
		Movimentacao mov = getMovimentacaoVlida();
		
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			//.body("error", is("J� existe uma conta com esse nome!"))	
		;		
	}	
	
	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body("{}")//Enviar objeto vazio
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", Matchers.hasSize(8))	//Na ra�z($) tive um retorno de 8 valores
			.body("msg", hasItems(
					"Data da Movimenta��o � obrigat�rio",
					"Data do pagamento � obrigat�rio",
					"Descri��o � obrigat�rio",
					"Interessado � obrigat�rio",
					"Valor � obrigat�rio",
					"Valor deve ser um n�mero",
					"Conta � obrigat�rio",
					"Situa��o � obrigat�rio"
				))
		;		
	}	
	
	@Test
	public void naoDeveInserirMovimentacaoComDataFutura() {
		
		Movimentacao mov = getMovimentacaoVlida();
		mov.setData_transacao("07/09/3020");//Data de transacao deve ser futura
		
		given()
			.header("Authorization", "JWT " + TOKEN) 
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", hasItems("Data da Movimenta��o deve ser menor ou igual � data atual"))
			.body("$", hasSize(1))
		;		
	}
	
	@Test
	public void naoDeveRemoverContaComMovimentacao() {
		
		given()
			.header("Authorization", "JWT " + TOKEN) 
		.when()
			.delete("/contas/30893")//ID da conta
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))	
		;		
	}
	
	@Test
	public void deveCalcularSaldoContas() {
		
		given()
			.header("Authorization", "JWT " + TOKEN) 
		.when()
			.get("/saldo")//ID da conta
		.then()
			.statusCode(200)
			.body("find{it.conta_id == 30893}.saldo", is("100.00"))	
		;		
	}
	
	private Movimentacao getMovimentacaoVlida() {
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
		return mov;
	}
	
}

package br.ce.wcaquino.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.awt.image.DataBufferUShort;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.utils.DateUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrTest extends BaseTest {
	
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
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
	}

	@Test
	public void t05_deveInserirMovimentacaoComSucesso() {
		
		Movimentacao mov = getMovimentacaoVlida();
		
		MOV_ID = given() 
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
				
		;		
	}	
	
	@Test
	public void t06_deveValidarCamposObrigatoriosMovimentacao() {
		
		given() 
			.body("{}")//Enviar objeto vazio
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", Matchers.hasSize(8))	//Na raíz($) tive um retorno de 8 valores
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
				))
		;		
	}	
	
	@Test
	public void t07_naoDeveInserirMovimentacaoComDataFutura() {
		
		Movimentacao mov = getMovimentacaoVlida();
		mov.setData_transacao(DateUtils.getDataDiferencaDias(2));//Data de transacao deve ser futura
		
		given() 
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", hasItems("Data da Movimentação deve ser menor ou igual à data atual"))
			.body("$", hasSize(1))
		;		
	}
	
	@Test
	public void t08_naoDeveRemoverContaComMovimentacao() {
		
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")//ID da conta
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))	
		;		
	}
	
	@Test
	public void t09_deveCalcularSaldoContas() {
		
		given() 
		.when()
			.get("/saldo")//ID da conta
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))	
		;		
	}
	
	@Test
	public void t10_deveRemoverMovimentacao() {
		
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}") //ID da conta
		.then()
			.statusCode(204)	
		;		
	}
	
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
	
	private Movimentacao getMovimentacaoVlida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		//mov.setUsuario_id(usuario_id);
		mov.setDescricao("Descricao da Movimentacao");
		mov.setEnvolvido("Envolvido na movimentacao");
		mov.setTipo("REC");
		mov.setData_transacao(DateUtils.getDataDiferencaDias(-1)); //Data de Ontem
		mov.setData_pagamento(DateUtils.getDataDiferencaDias(5));
		mov.setValor(100f);
		mov.setStatus(true);
		return mov;
	}
	
}

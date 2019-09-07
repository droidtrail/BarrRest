package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.tests.Movimentacao;
import br.ce.wcaquino.rest.utils.BarrUtils;
import br.ce.wcaquino.rest.utils.DateUtils;

public class MovimentacaoTest extends BaseTest {
	
	@Test
	public void deveInserirMovimentacaoComSucesso() {
		
		Movimentacao mov = getMovimentacaoVlida();
		
		given() 
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)	
		;		
	}	
	
	@Test
	public void deveValidarCamposObrigatoriosMovimentacao() {
		
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
	public void naoDeveInserirMovimentacaoComDataFutura() {
		
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
	public void naoDeveRemoverContaComMovimentacao() {
		
		Integer CONTA_ID = BarrUtils.getIdContaPeloNome("Conta com movimentacao");
		
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
	public void deveRemoverMovimentacao() {
		
		Integer MOV_ID = BarrUtils.getIdMovPelaDescricao("Movimentacao para exclusao");
		
		given()
			.pathParam("id", MOV_ID)
		.when()
			.delete("/transacoes/{id}") //ID da conta
		.then()
			.statusCode(204)	
		;		
	}
	
	
	private Movimentacao getMovimentacaoVlida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(BarrUtils.getIdContaPeloNome("Conta para movimentacoes"));
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

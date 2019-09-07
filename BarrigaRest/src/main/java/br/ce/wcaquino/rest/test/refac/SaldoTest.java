package br.ce.wcaquino.rest.test.refac;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.wcaquino.rest.core.BaseTest;
import br.ce.wcaquino.rest.utils.BarrUtils;

public class SaldoTest extends BaseTest {
	
	
	@Test
	public void deveCalcularSaldoContas() {
		
		Integer CONTA_ID = BarrUtils.getIdContaPeloNome("Conta para saldo");
		
		given() 
		.when()
			.get("/saldo")//ID da conta
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))	
		;		
	}
	
}

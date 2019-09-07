package br.ce.wcaquino.rest.utils;

import io.restassured.RestAssured;

public class BarrUtils {

	// Pesquisar pelo nome da conta para obter o ID dela
	public static Integer getIdContaPeloNome(String nome) {

		return RestAssured.get("/contas?nome=" + nome).then().extract().path("id[0]");

	}

	// Pesquisar pelo id da descrição
	public static Integer getIdMovPelaDescricao(String desc) {

		return RestAssured.get("/transacoes?descricao=" + desc).then().extract().path("id[0]");

	}

}

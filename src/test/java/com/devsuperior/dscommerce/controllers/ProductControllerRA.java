package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ProductControllerRA {

	private Long existingProductId;
	private Long nonExistingProductId;
	private Long dependentProductId;
	private String productName;
	private String adminToken,clientToken,invalidToken;
	private String clienteUsername,clientPassword,adminUsername,adminPassword;
	
	private Map<String,Object> postProductInstance;
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		productName = "Macbook";
		
		clienteUsername = "maria@gmail.com";
		clientPassword = "123456";
		adminUsername = "alex@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccesToken(clienteUsername, clientPassword);
		adminToken = TokenUtil.obtainAccesToken(adminUsername, adminPassword);
		invalidToken = adminToken + "sjfgh";
		//adminToken = "eyJraWQiOiIzODdjOGRlNS04M2QyLTQ1MGMtYmIxYy1mZGFmNmM4ZTVjY2UiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJteWNsaWVudGlkIiwiYXVkIjoibXljbGllbnRpZCIsIm5iZiI6MTY4NzM1OTA4MSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwiZXhwIjoxNjg3NDQ1NDgxLCJpYXQiOjE2ODczNTkwODEsImF1dGhvcml0aWVzIjpbIlJPTEVfQ0xJRU5UIiwiUk9MRV9BRE1JTiJdLCJ1c2VybmFtZSI6ImFsZXhAZ21haWwuY29tIn0.HwtLiEJQq_kYZfmJEQhF8_7qtkhKut82iA3XkgLCEGcZX_6RXTTMKsIurSuaLT9nHWNyoaV2Tp2gV1nTmCqTlMy3CtFXYsFHYSpkg628wkjUrYZtGGvtJ6VyEC5P0u0OcrpDD7M6Cb0hp-xp7tien1Zsoi5SG_luj5nLemgebSxBKjYIVfdKH1TxIjpihX_UXA2Fg0NOfxjx1k_NS2PU7ZC3UEoR7mJGbigz__22bzLHlrxPdgzIhTS1C40BZ0nzeLTDMVysLHHvgUSUj_yy80UnKcLs-P7jqCGoA3IkhS5cpC1gSQMmLsXLHU4157cz1V5M94pdqFL6VYMpYaJXPg";
		
		postProductInstance = new HashMap<>();
		postProductInstance.put("name", "Meu produto");
		postProductInstance.put("description", "jdlhfojdshfgojshdoghsjdkhgjksfhfgj");
		postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg");
		postProductInstance.put("price", 50.0);
		List<Map<String,Object>> categories = new ArrayList<>();
		Map<String,Object> cat1 = new HashMap<>();
		cat1.put("id", 2);
		//cat1.put("name", "Meu produto");
		Map<String,Object> cat2 = new HashMap<>();
		cat2.put("id", 3);
		//cat2.put("name", "Meu produto");
		categories.add(cat1);
		categories.add(cat2);
		postProductInstance.put("categories", categories);
		
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() {
		existingProductId = 2L;
		
		given()
			.get("/products/{id}", existingProductId)
		.then()
			.statusCode(200)
			.body("id", is(2))
			.body("name", equalTo("Smart TV"))
			.body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
			.body("price", is(2190.0F))
			.body("categories.id", hasItems(2,3))
			.body("categories.name", hasItems("Eletrônicos","Computadores"));
	}
	
	@Test
	public void findAllShouldReturnPageProductsWhenProductNameIsEmpty() {
		given()
		.get("/products?page=0")
	.then()
		.statusCode(200)
		.body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));
	}

	@Test
	public void findAllShouldReturnPageProductsWhenProductNameIsNotEmpty() {
		given()
		.get("/products?page=0&name={productName}",productName)
	.then()
		.statusCode(200)
		.body("content.id[0]", is(3))
		.body("content.name[0]", equalTo("Macbook Pro"))
		.body("content.price[0]", is(1250.0F))
		.body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"))
		;
	}

	@Test
	public void findAllShouldReturnPageProductsWithPriceGreaterThan2000() {
		given()
		.get("/products?size=25")
	.then()
		.statusCode(200)
		.body("content.findAll{ it.price> 2000}.name", hasItems("Smart TV","PC Gamer Weed"));
	}

	@Test
	public void insertShouldReturnProductCreatedWhenAdminLogged() {
		adminToken = TokenUtil.obtainAccesToken(adminUsername, adminPassword);
		JSONObject newProduct = new JSONObject(postProductInstance);
		
		given()
			.header("Content-type","application/json")
			.header("Authorization","Bearer " + adminToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/products")
		.then()
			.statusCode(201)
			.body("name", equalTo("Meu produto"))
			.body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
			.body("price", is(50.0F))
			.body("categories.id", hasItems(2,3));
		
	}
	
}

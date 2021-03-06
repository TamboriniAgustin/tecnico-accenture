package com.tecnico.accenture.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.tecnico.accenture.models.Album;
import com.tecnico.accenture.models.Comment;
import com.tecnico.accenture.models.Photo;
import com.tecnico.accenture.models.Post;

@Service
public class ExternalService {
	@Value("${external.api.hostname}")
	private String baseUrl;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private Gson gson;
	
	/*
		Realiza el pedido de usuarios a la API externa.
		
		NOTA: No se considera el uso de models ya que lo único que hace la API 
	 	es mostrar la información. En caso de tener que utilizar la información 
	 	lo correcto sería el uso de un Adapter para poder obtener un objeto 
	 	modelo que sea parte de nuestro dominio que nos sirva para manipularlo.
	*/
	public String getUsers() {
		ResponseEntity<String> jsonResponse = restTemplate.getForEntity(baseUrl + "users", String.class);
		return jsonResponse.getBody();
	}
	
	/*
	 	Realiza el pedido de albums de un determinado usuario a la API externa.
	 	
	 	NOTA: en este caso si aplicamos el adapter a un modelo de nuestro dominio 
	 	ya que luego vamos a utilizar la información de los albums obtenidos para 
	 	traer las fotos del usuario.
	*/
	public List<Album> getUserAlbums(String userId) {
		ResponseEntity<String> jsonResponse = restTemplate.getForEntity(baseUrl + "users/" + userId + "/albums", String.class);
		
		//Convertimos el Json recibido en un array de nuestro modelo
		Album[] userAlbums = gson.fromJson(jsonResponse.getBody(), Album[].class);
		
		return Arrays.asList(userAlbums);
	}
	
	/*
	 	Realiza el pedido de fotos de un determinado album a la API externa.
	*/
	public List<Photo> getAlbumPhotos(String albumId) {
		ResponseEntity<String> jsonResponse = restTemplate.getForEntity(baseUrl + "albums/" + albumId + "/photos", String.class);
		
		//Convertimos el Json recibido en un array de nuestro modelo
		Photo[] albumPhotos = gson.fromJson(jsonResponse.getBody(), Photo[].class);
		
		return Arrays.asList(albumPhotos);
	}
	
	/*
	 	Realiza el pedido de posts de un usuario específico a la API externa
	*/
	public List<Post> getUserPosts(String userId) {
		ResponseEntity<String> jsonResponse = restTemplate.getForEntity(baseUrl + "users/" + userId + "/posts", String.class);
		
		//Convertimos el Json recibido en un array de nuestro modelo
		Post[] userPosts = gson.fromJson(jsonResponse.getBody(), Post[].class);
		
		return Arrays.asList(userPosts);
	}
	
	/*
	 	Realiza el pedido de comentarios a la API externa
	*/
	public List<Comment> getComments(String attr, String value) {
		//Si utiliza algun tipo de filtro, agrego el query param a la consulta
		String path = baseUrl + "comments";
		if(attr != null) {
			path = path.concat("?" + attr + "=" + value);
		}
		ResponseEntity<String> jsonResponse = restTemplate.getForEntity(path, String.class);
		
		//Convertimos el Json recibido en un array de nuestro modelo
		Comment[] comments = gson.fromJson(jsonResponse.getBody(), Comment[].class);
		
		return Arrays.asList(comments);
	}
}
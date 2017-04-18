package controllers;

import java.util.List;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.map.HashedMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ning.http.client.Request;
import java.io.File;

import models.Photo;
import models.User;
import models.UsuarioF;
import play.Logger;
import play.libs.OAuth;
import play.libs.OAuth.ServiceInfo;
import play.libs.OAuth2;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Controller;
import play.mvc.results.Redirect;

public class Flickr extends Controller {
	private static final String PROTECTED_RESOURCE_URL = "https://api.flickr.com/services/rest/";
	private static final String PHOTOS_URL = "http://www.flickr.com/photos/";
	private static OAuth1RequestToken requestToken;
	private static OAuth1AccessToken accessToken;

	// Os aplicativos de upload podem chamar o método
	// flickr.people.getUploadStatus
	// na API regular para obter limites de arquivo e largura de banda para o
	// usuário.

	private static final String URL_POST = "https://api.flickr.com/services/upload/";

	// DADOS PARA ENTRAR NA CONTA DO FLICKR: 
	// USER: waltercreator@gmail.com
	// PASSWORD: 1237vezes
	
	final static OAuth10aService service = new ServiceBuilder().apiKey("30e667f87d1ecbca4e18846dffff86a2")
			.apiSecret("961b22cca7d087a5").callback("http://localhost:9000/flickr/autenticado")
			.build(FlickrApi.instance());

	public static void home() {
		render();
	}

	public static Document chamadaDeMethodPost(List key, List value)
			throws IOException, ParserConfigurationException, SAXException {
		final OAuthRequest requestP = new OAuthRequest(Verb.POST, URL_POST, service.getConfig());
		for (int i = 0; i < key.size(); i++)
			requestP.addParameter(key.get(i).toString(), value.get(i).toString());
		service.signRequest(accessToken, requestP);
		final Response response = service.execute(requestP);

		String xml = response.getBody();
		InputSource is = new InputSource(new StringReader(xml));
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}

	// <form enctype="multipart/form-data" method="post"
	// action="http://api.flickr.com/services/upload/">
	// <input type="file" name="photo"/>
	// <input type="text" name="title" value=""/>
	// <input type="text" name="content_type" value="1"/>
	// <input type="text" name="hidden" value="2"/>
	// <input type="hidden" name="api_key" value=""/>
	// <input type="hidden" name="auth_token" value=""/>
	// <input type="hidden" name="api_sig" value=""/>
	// <input type="submit" name ="submit" value="Upload"/>
	// </form>
	
	// SITE ONDE PEGUEI O COD...
	
	// https://code.tutsplus.com/tutorials/the-ultimate-guide-to-decoding-the-flickr-api--net-5981

	
	
	public static void uploadPhoto(File photo) 
		throws SAXException, IOException, ParserConfigurationException {
		List key = new ArrayList<>();
		List value = new ArrayList<>();
		key.add("upload"); value.add("flickr.people.getUploadStatus");
		chamadaDeMethodPost(key, value);
		listarPhotos();
	}

	public static Document chamadaDeMethod(List key, List value)
			throws IOException, ParserConfigurationException, SAXException {
		final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service.getConfig());
		for (int i = 0; i < key.size(); i++)
			request.addParameter(key.get(i).toString(), value.get(i).toString());
		service.signRequest(accessToken, request);
		final Response response = service.execute(request);

		String xml = response.getBody();
		InputSource is = new InputSource(new StringReader(xml));
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}

	public static void deletePhoto(String photo_id) throws SAXException, IOException, ParserConfigurationException {
		List key = new ArrayList<>();
		List value = new ArrayList<>();
		key.add("method");
		value.add("flickr.photos.delete");
		key.add("photo_id");
		value.add(photo_id);
		chamadaDeMethod(key, value);
		listarPhotos();
	}

	public static List getPhotos() throws IOException, SAXException, ParserConfigurationException {
		List key = new ArrayList<>();
		List value = new ArrayList<>();
		key.add("method");
		value.add("flickr.people.getPhotos");
		key.add("user_id");
		value.add(accessToken.getParameter("user_nsid").replace("%40", "@"));

		Document doc = chamadaDeMethod(key, value);
		NodeList nList = doc.getElementsByTagName("photo");
		List photos = new ArrayList<>();
		Photo photo = null;
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				photo = new Photo(eElement.getAttribute("id"), eElement.getAttribute("secret"),
						eElement.getAttribute("owner"), eElement.getAttribute("title"),
						PHOTOS_URL + eElement.getAttribute("id") + "_" + eElement.getAttribute("secret") + ".jpg");
			}
			photos.add(photo);
		}
		return photos;
	}

	public static void listarPhotos() throws SAXException, IOException, ParserConfigurationException {
		List photos = getPhotos();
		render(photos);
	}

	public static void autenticado(String oauth_token, String oauth_verifier)
			throws IOException, SAXException, ParserConfigurationException {
		// troca token de solicitaÃ§Ã£o por token de acesso
		accessToken = service.getAccessToken(requestToken, oauth_verifier);
		Main.flickrAutenticado = true;
		session.put("auth", oauth_token);
		listarPhotos();
	}

	public static void autenticar() throws IOException {
		// obtÃ©m token de solicitaÃ§Ã£o
		requestToken = service.getRequestToken();
		// retorna URL de autorizaÃ§Ã£o
		String authorizationUrl = service.getAuthorizationUrl(requestToken);
		// adiciona tipo de permissÃ£o a URL de autorizaÃ§Ã£o
		authorizationUrl = authorizationUrl + "&perms=delete";
		redirect(authorizationUrl);
	}
}
package controllers;

import java.io.IOException;
import java.util.Scanner;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.test.TestInterface;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import play.mvc.Controller;

public class Flickrs extends Controller {

	private final static String apiKey = "30e667f87d1ecbca4e18846dffff86a2";
	private final static String sharedSecret = "961b22cca7d087a5";
	private static final String PROTECTED_RESOURCE_URL = "http://api.flickr.com/services/rest/";

	public static void main() throws IOException {
		// Replace these with your own api key and secret
		final String apiSecret = sharedSecret;
		final OAuth10aService service = new ServiceBuilder().apiKey(apiKey).apiSecret(apiSecret)
				.build(FlickrApi.instance());
		final Scanner in = new Scanner(System.in);

		System.out.println("=== Flickr's OAuth Workflow ===");
		System.out.println();

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		final OAuth1RequestToken requestToken = service.getRequestToken();
		System.out.println("Got the Request Token!");
		System.out.println();

		System.out.println("Now go and authorize ScribeJava here:");
		final String authorizationUrl = service.getAuthorizationUrl(requestToken);
		System.out.println(authorizationUrl + "&perms=read");
		System.out.println("And paste the verifier here");
		System.out.print(">>");
		final String oauthVerifier = in.nextLine();
		System.out.println();

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
		System.out.println("Got the Access Token!");
		System.out.println("(if your curious it looks like this: " + accessToken + ", 'rawResponse'='"
				+ accessToken.getRawResponse() + "')");
		System.out.println();

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service.getConfig());
		request.addQuerystringParameter("method", "flickr.test.login");
		service.signRequest(accessToken, request);
		final Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getBody());

		System.out.println();
		System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");

	}
}

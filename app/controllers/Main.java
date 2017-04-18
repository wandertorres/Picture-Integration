package controllers;

import play.mvc.Controller;

public class Main extends Controller {
	public static boolean flickrAutenticado;
	public static boolean facebookAutenticado;
	public static boolean plusAutenticado;
	
	public static void home() {
        render();
    }
	
	public static void login() {
	    render();
	}
	
	public static void logoff() {
		session.clear();
		login();
	}
	
	public static void upload() {
		render();
	}

}

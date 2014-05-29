package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

	final static Form<User> userForm = form( User.class );

	/**
	 * Display the home page.
	 */
	public static Result index() {
		return ok( index.render( userForm ));
	}





}

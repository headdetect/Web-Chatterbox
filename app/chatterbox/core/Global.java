package chatterbox.core;

import akka.util.Duration;
import chatterbox.utils.Logger;
import models.Robot;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import play.mvc.Result;
import play.mvc.Results;

import java.util.concurrent.TimeUnit;

import static play.mvc.Controller.flash;

public class Global extends GlobalSettings {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private static Logger logger = new Logger();
    private static Application application;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public static Application getApplication() {
        return application;
    }
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onStart(Application app) {
        Global.application = app;

        System.out.println("Started Logger");
        Akka.system().scheduler().scheduleOnce(Duration.create(0, TimeUnit.MILLISECONDS), logger);

        System.out.println("Adding System Users");
        User.addOnlineUser(User.SYSTEM);
    }

    @Override
    public void onStop(Application app) {

        try {
            logger.kill();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result onHandlerNotFound(play.mvc.Http.RequestHeader uri) {
        flash("error", "Page not found");
        return Results.notFound(views.html.index.render(null));
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}

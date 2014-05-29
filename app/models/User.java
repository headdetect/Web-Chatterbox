package models;

import chatterbox.core.user.Permission;
import chatterbox.utils.Logger;
import com.avaje.ebean.Ebean;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.WebSocket;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "o_users")
public class User extends Model {

    // ===========================================================
    // Constants
    // ===========================================================

    public final static User SYSTEM;
    // ===========================================================
    // Fields
    // ===========================================================
    public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);
    private static ArrayList<User> onlineUsers = new ArrayList<User>();


    public String username;
    public String password;

    public String email;

    @Id
    public long ID;

    public Permission permission = Permission.Member;


    //------------------
    //- Runtime Elements
    //------------------

    @Transient
    public WebSocket.Out<JsonNode> outSocket;

    @Transient
    public WebSocket.In<JsonNode> inSocket;

    @Transient
    public String ipAddress;

    // --------------
    // - Options
    // --------------


    public boolean useFullTime;

    public boolean showEmotes;




    // ===========================================================
    // Constructors
    // ===========================================================

    public User(String string) {
        this();
        this.username = string;

    }

    public User() {

    }

    static {
        SYSTEM = new User("System");
        SYSTEM.ID = 1;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    //--------------
    //- Registration
    //--------------
    public static boolean registrationExists(User user) {
        return find.byId(user.ID) != null;
    }

    public static void registerUser(User user) {
        if (!registrationExists(user)) {
            Ebean.save(user);

            Logger.log("New user registered: " + user.username + ", ID=" + user.ID + ", IP=" + user.ipAddress);
        }
    }

    public static void unregisterUser(User user) {
        find.ref(user.ID).delete();
    }

    public static List<User> getRegisteredUsers() {
        return find.all();
    }

    public static boolean onlineUserExists(User user) {
        return getOnlineUserById(user.ID) != null;
    }

    public static void addOnlineUser(User user) {

        //Is system or robot.
        if (user.ID == 1 || user.ID == 2) {
            onlineUsers.add(user);
            return;
        }

        if (!onlineUserExists(user)) {

            User dbUser = find.where().eq("username", user.username).findUnique();

            if (dbUser == null) {
                return;
            }

            //Online User - DB user
            //ID 1 = SYSTEM, 2 = ROBOT
            user.ID = dbUser.ID;

            onlineUsers.add(user);
            Logger.log("New user added: " + user.username + ", ID=" + user.ID + ", IP=" + user.ipAddress);
        }
    }

    public static boolean removeOnlineUser(User user) {
        if (onlineUserExists(user)) {

            /*
                Because play and ebean have their on ID impl
                it causes errors when trying to get the index of a Model in
                a list. So as a result, we have to loop through all items and remove from index.
            */
            for (int i = 0; i < onlineUsers.size(); i++) {
                User from = onlineUsers.get(i);

                if (from == user) {
                    onlineUsers.remove(i);
                    return true;
                }
            }
        }

        return false;
    }

    public static List<User> getAllOnlineUsers() {
        return onlineUsers;
    }

    //----------------
    //- Online
    //---------------

    public static User getOnlineUserById(long id) {
        for (int i = 0; i < onlineUsers.size(); i++) {
            User user = onlineUsers.get(i);
            if (user.ID == id) return user;
        }
        return null;
    }

    public static void sendGlobalMessage(String kind, User from, String message, Object... options) {
        for (User user : getAllOnlineUsers()) {
            user.sendMessage(kind, from, message, options);
        }
    }

    public static void sendGlobalMessage(User from, String msg) {
        sendGlobalMessage("talk", from, msg);
    }

    public static void sendGlobalMessage(String message) {
        sendGlobalMessage(User.SYSTEM, message);
    }

    public static void sendListUpdate() {
        for (User user : onlineUsers) {


            if (user == SYSTEM || user.ID == 2 /* Is robot? */ || user.outSocket == null) {
                // we don't care about the user list. k?
                continue;
            }

            ObjectNode event = Json.newObject();
            event.put("kind", "membersUpdate");

            // Add all members to array node
            ArrayNode mNode = event.putArray("members");
            for (int i = 0; i < onlineUsers.size(); i++) {
                if (onlineUsers.get(i) != SYSTEM)
                    mNode.add(StringEscapeUtils.escapeHtml4(onlineUsers.get(i).username));
            }

            user.outSocket.write(event);
        }

    }

    // -------- Utils ------------



    public void sendMessage(String kind, User from, String asText, Object... options) {
        if (this == SYSTEM || outSocket == null) {
            // Just log the messages sent to the system

            Logger.log(kind + " -> (" + from.username + "): " + asText, Logger.Log.LOG_LEVEL_INFO);
            return;
        }

        ObjectNode event = Json.newObject();
        event.put("kind", kind);
        event.put("user", from != SYSTEM ? from.username : "");
        event.put("message", asText);


        ArrayNode mNode = event.putArray("options");
        if (options != null) {
            for (int i = 0; i < options.length; i++) {
                mNode.add(options[i].toString());
            }
        }

        outSocket.write(event);
    }

    public void sendMessage(User from, String asText) {
        sendMessage("talk", from, asText);
    }

    public void sendMessage(String text) {
        sendMessage(this, text);
    }

    public void kick() {
        ChatRoom.defaultRoom.tell(new ChatRoom.Quit(ID));
    }

    public void tell(String message) {
        ChatRoom.defaultRoom.tell(new ChatRoom.Talk(ID, message));
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}

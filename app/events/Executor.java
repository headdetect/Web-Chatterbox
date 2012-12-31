package events;

public interface Executor {
    public void execute( Listener listen, Event event ) throws Exception;
}


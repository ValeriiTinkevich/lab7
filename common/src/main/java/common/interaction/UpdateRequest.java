package common.interaction;

import java.io.Serializable;

public class UpdateRequest extends Request{
    String id;

    public UpdateRequest(String commandName, Serializable commandArgument, int userID, String id) {
        super(commandName, commandArgument, userID);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

package splitwise.server.commands;

import splitwise.server.exceptions.FriendshipException;
import splitwise.server.services.FriendshipService;

public class AddFriendCommand extends Command {
    public static final String USER_NOT_FOUND = "%s is not found. Check friend's username and try again.";
    public static final String ESTABLISHED_FRIENDSHIP = "Friendship is established." + START_SPLITTING;
    public static final String ALREADY_FRIENDS = "You are already friends.";
    public static final String FRIENDSHIP_CANNOT_BE_ESTABLISHED = "Friendship cannot be established due to unexpected error. Try again later.";
    
    private String friendUsername;
    private FriendshipService friendshipCreator;
    
    public AddFriendCommand(String command, FriendshipService friendshipCreator) {
	super(friendshipCreator);
	this.friendshipCreator = friendshipCreator;
	initializeCommandParameters(command);
    }
    
    private void initializeCommandParameters(String command) {
	String[] commandParts = command.split("\\s+");
	friendUsername = commandParts[1];
    }
    
    @Override
    public String execute() {
	if(isCommandInvokerLoggedIn) {
	    if(isFriendPresent()) {
		if(areAlreadyFriends()) {
		    return ALREADY_FRIENDS;
		} else {
		    String commandResult = createFriendship();
		    return commandResult;
		}
	    } else {
		return String.format(USER_NOT_FOUND, friendUsername);
	    }
	}
	return LOGIN_OR_REGISTER;
    }
    
    private boolean isFriendPresent() {
	return friendshipCreator.checkIfRegistered(friendUsername) && (!commandInvokerUsername.equals(friendUsername));
    }
    
    private boolean areAlreadyFriends() {
	return friendshipCreator.areFriends(commandInvokerUsername, friendUsername);
    }
    
    private String createFriendship() {
	String result;
	try {
	    friendshipCreator.createFriendship(commandInvokerUsername, friendUsername);
	    result = ESTABLISHED_FRIENDSHIP;
	} catch(FriendshipException e) {
	    result = FRIENDSHIP_CANNOT_BE_ESTABLISHED;
	}
	return result;
    }
}

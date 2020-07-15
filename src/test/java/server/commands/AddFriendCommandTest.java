package server.commands;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import splitwise.server.commands.AddFriendCommand;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.services.UserService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static server.TestConstants.*;
import static splitwise.server.commands.AddFriendCommand.FRIENDSHIP_CANNOT_BE_ESTABLISHED;
import static splitwise.server.commands.AddFriendCommand.USER_NOT_FOUND;

public class AddFriendCommandTest {
    public static String ADD_FRIEND_COMMAND = "add-friend " + TEST_USERNAME;

    private static UserService userService;
    private static AddFriendCommand addFriendCommand;

    @BeforeClass
    public static void setUp() {
        userService = Mockito.mock(UserService.class);
    }

    @After
    public void resetDependencies(){
        reset(userService);
    }


    @Test
    public void testThatAddFriendCommandInvocationWithoutBeingLoggedInReturnsLoginOrRegisterMessage() {
        when(userService.getCurrentSessionsUsername()).thenReturn(null);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND, userService);

        String assertMessage = """
                Not right response is returned when not logged in user attempts to
                invoke add-friend command""";

        String actualResponse = addFriendCommand.execute();

        assertEquals(assertMessage, LOGIN_OR_REGISTER, actualResponse);
    }

    @Test
    public void addingNotExistingUserShouldReturnNotRegisteredMessage() {
        when(userService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME2);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND, userService);
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(false);

        String assertMessage = "Adding not registered user should return right message.";
        String expectedResult = String.format(USER_NOT_FOUND, TEST_USERNAME);
        String actualResult = addFriendCommand.execute();

        assertEquals(assertMessage, expectedResult, actualResult);
    }

    @Test
    public void testThatIfUserServiceThrowExceptionFailedCommandMessageIsReturned() throws UserServiceException {
        when(userService.getCurrentSessionsUsername()).thenReturn(TEST_USERNAME2);
        when(userService.checkIfRegistered(TEST_USERNAME)).thenReturn(true);
        doThrow(new UserServiceException("dummy message", new Throwable())).when(userService).createFriendship(TEST_USERNAME2,TEST_USERNAME);
        addFriendCommand = new AddFriendCommand(ADD_FRIEND_COMMAND,userService);

        String assertMessage = "When UserServiceException is thrown not right command failure response is returned";
        String actualResult = addFriendCommand.execute();

        assertEquals(assertMessage,FRIENDSHIP_CANNOT_BE_ESTABLISHED,actualResult);
    }

}

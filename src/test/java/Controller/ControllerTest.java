package Controller;

import org.nicolas.controller.UserController;
import org.nicolas.model.Librarian;
import org.nicolas.view.UserView;

import java.io.Console;
import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ControllerTest {

    @Nested
    @ExtendWith(MockitoExtension.class)
    class LibrarianTest {

        @Mock
        Librarian mockLibrarian;
        @Mock
        Console mockConsole;
        @Mock
        UserView mockView;

        private UserController testController;
        private ResourceBundle messages = ResourceBundle.getBundle("messages");

        @BeforeEach
        void setUp () {
            testController = new UserController(null, mockView, messages);
        }

        //ADD BOOK//////////////////////////////////////////////////////////////////////////////////////////////////////
        @Test
        void testAddBook () {
            when(mockConsole.readLine())
                    .thenReturn("1234567890") //isbn
                    .thenReturn("title")
                    .thenReturn("author")
                    .thenReturn("5");

            doNothing().when(mockLibrarian).addBook(anyString(), anyString(), anyString(), anyInt(), any(Console.class));
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView, never()).setErrorMessage(anyString());

            verify(mockLibrarian).addBook(
                    eq("1234567890"),
                    eq("title"),
                    eq("author"),
                    eq(5),
                    any(Console.class)
            );

            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        //Invalid ISBN--------------------------------------------------------------------------------------------------
        @Test
        void testInvalidISBN01 () {
            when(mockConsole.readLine()).thenReturn("ABC");
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN02 () {
            when(mockConsole.readLine()).thenReturn("ABCABCABCABCA"); //10 letters
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN03 () {
            when(mockConsole.readLine()).thenReturn("ABCABCABCABCA"); //13 letters
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN04 () {
            when(mockConsole.readLine()).thenReturn("123456789A"); //9 digits + last letter
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN05 () {
            when(mockConsole.readLine()).thenReturn("123456789012A"); //12 digits + last letter
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN06 () {
            when(mockConsole.readLine()).thenReturn("123-456-7890"); //with '-'
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN07 () {
            when(mockConsole.readLine()).thenReturn("123-456-7890-123"); //with '-'
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN08 () {
            when(mockConsole.readLine()).thenReturn(null);
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN09 () {
            when(mockConsole.readLine()).thenReturn("");
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongISBN"));
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN10 () {
            when(mockConsole.readLine()).thenReturn("1234567890"); //VALID
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView, never()).setErrorMessage(anyString());
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }

        @Test
        void testInvalidISBN11 () {
            when(mockConsole.readLine()).thenReturn("1234567890123"); //VALID
            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView, never()).setErrorMessage(anyString());
            assertEquals(UserController.MenuState.LIBRARIAN_MAIN, testController.getCurrentState());
        }


        //Invalid Title-------------------------------------------------------------------------------------------------
        @Test
        void testInvalidTitle01() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn(null)
                    .thenReturn("Author")
                    .thenReturn("5");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidTitle02() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("")
                    .thenReturn("Author")
                    .thenReturn("5");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidTitle03() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title") // VALID
                    .thenReturn("Author")
                    .thenReturn("5");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView, never()).setErrorMessage(anyString());
        }

        //Invalid Author------------------------------------------------------------------------------------------------
        @Test
        void testInvalidAuthor01() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn(null)
                    .thenReturn("5");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidAuthor02() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn("")
                    .thenReturn("5");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidAuthor03() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn("Author") // VALID
                    .thenReturn("5");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView, never()).setErrorMessage(anyString());
        }

        //Invalid Copies------------------------------------------------------------------------------------------------
        @Test
        void testInvalidCopies01() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn("Author")
                    .thenReturn(null);

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidCopies02() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn("Author")
                    .thenReturn("");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidCopies03() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn("Author")
                    .thenReturn("five");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidCopies04() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn("Author")
                    .thenReturn("0");

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }

        @Test
        void testInvalidCopies05() {
            when(mockConsole.readLine())
                    .thenReturn("1234567890")
                    .thenReturn("Title")
                    .thenReturn("Author")
                    .thenReturn("5"); // VALID

            testController.librarianAddBook(mockLibrarian, mockConsole);
            verify(mockView).setErrorMessage(messages.getString("error.message.wrongNumCopies"));
        }
    }
}

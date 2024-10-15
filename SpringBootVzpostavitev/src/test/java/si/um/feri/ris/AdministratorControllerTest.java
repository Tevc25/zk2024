package si.um.feri.ris;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import si.um.feri.ris.controllers.AdministratorController;
import si.um.feri.ris.models.Administrator;
import si.um.feri.ris.models.Nesreca;
import si.um.feri.ris.models.Oskodovanec;
import si.um.feri.ris.repository.AdministratorRepository;
import si.um.feri.ris.repository.ListNesrec;
import si.um.feri.ris.repository.PregledOskodovancev;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdministratorControllerTest {

    @Mock
    private ListNesrec nesrecaDAO;

    @Mock
    private AdministratorRepository administratorDAO;

    @Mock
    private PregledOskodovancev oskodovanciDAO;

    @InjectMocks
    private AdministratorController administratorController;

    public AdministratorControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for adding a new accident through an administrator
    @Test
    public void testDodajNesreco() {
        // Create a new accident and an administrator
        Administrator admin = new Administrator();
        admin.setId(1L);

        Nesreca novaNesreca = new Nesreca(new Date(), "Test accident", "Test location");
        novaNesreca.setAdministrator(admin);

        // Simulate finding the administrator
        when(administratorDAO.findById(1L)).thenReturn(Optional.of(admin));

        // Simulate saving the accident
        when(nesrecaDAO.save(novaNesreca)).thenReturn(novaNesreca);

        // Call the controller method to add the accident
        ResponseEntity<Nesreca> response = administratorController.dodajNesreco(novaNesreca);

        // Verify that the administrator was found and the accident was saved
        verify(administratorDAO, times(1)).findById(1L);
        verify(nesrecaDAO, times(1)).save(novaNesreca);

        // Assert that the response is OK and the accident was added
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test accident", response.getBody().getOpis());
    }

    @Test
    public void testIzbrisiNesreco() {
        // Create a sample accident
        Nesreca nesrecaZaBrisanje = new Nesreca(new Date(), "Test accident for delete", "Test location");
        nesrecaZaBrisanje.setId(1L);

        // Simulate finding and deleting the accident
        when(nesrecaDAO.findById(1L)).thenReturn(Optional.of(nesrecaZaBrisanje));
        doNothing().when(nesrecaDAO).deleteById(1L);

        // Call the controller method to delete the accident
        ResponseEntity<String> response = administratorController.izbrisiNesreco(1L);

        // Verify that the accident was found and deleted
        verify(nesrecaDAO, times(1)).findById(1L);
        verify(nesrecaDAO, times(1)).deleteById(1L);

        // Assert that the response is OK and the accident was deleted
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nesreča uspešno izbrisana", response.getBody());
    }

    @Test
    void testPrijavaAdmina_Success() {
        Administrator admin = new Administrator();
        admin.setId(1L);
        admin.setUporabniskoIme("testUser");
        admin.setGeslo("testPassword");

        when(administratorDAO.findByUporabniskoImeAdmin("testUser")).thenReturn(Collections.singletonList(admin));

        ResponseEntity<String> response = administratorController.prijavaAdmina(admin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", response.getBody());
    }

    @Test
    void testPrijavaAdmina_WrongPassword() {
        Administrator admin = new Administrator();
        admin.setUporabniskoIme("testUser");
        admin.setGeslo("wrongPassword");

        Administrator existingAdmin = new Administrator();
        existingAdmin.setId(1L);
        existingAdmin.setUporabniskoIme("testUser");
        existingAdmin.setGeslo("testPassword");

        when(administratorDAO.findByUporabniskoImeAdmin("testUser")).thenReturn(Collections.singletonList(existingAdmin));

        ResponseEntity<String> response = administratorController.prijavaAdmina(admin);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Napačno geslo", response.getBody());
    }

    @Test
    void testDodajOskodovanca_Success() {
        Oskodovanec oskodovanec = new Oskodovanec();

        when(oskodovanciDAO.save(any(Oskodovanec.class))).thenReturn(oskodovanec);

        ResponseEntity<Oskodovanec> response = administratorController.dodajOskodovanca(oskodovanec);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(oskodovanec, response.getBody());
    }
}

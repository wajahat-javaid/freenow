package com.freenow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.freenow.dataaccessobject.CarRepository;
import com.freenow.dataaccessobject.DriverRepository;
import com.freenow.domainobject.CarDO;
import com.freenow.domainobject.DriverDO;
import com.freenow.domainvalue.CarStatus;
import com.freenow.domainvalue.EngineType;
import com.freenow.domainvalue.OnlineStatus;
import com.freenow.exception.CarAlreadyInUseException;
import com.freenow.exception.ConstraintsViolationException;
import com.freenow.exception.EntityNotFoundException;
import com.freenow.service.DriverService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DriverServiceFunctionalTests
{

    private static long driverIdExisting;
    private static long driverIdNonExisting;
    private static long driverIdWithCar;
    private static long driverIdCreated;
    private static String username;
    private static String password;
    private static long carId;
    private static long carIdInUse;

    @Autowired
    private DriverService driverService;

    @Autowired
    private MockMvc mvc;

    @MockBean
    DriverRepository driverRepository;

    @MockBean
    CarRepository carRepository;


    @BeforeClass
    public static void init()
    {
        driverIdExisting = 500l;
        driverIdNonExisting = 1l;
        driverIdCreated = 20l;
        driverIdWithCar = 34l;

        username = "driver01";
        password = "pass01";
        carId = 6l;
        carIdInUse = 10l;

    }


    @Before
    public void setUp()
    {
        Mockito.when(driverRepository.findByIdAndDeleted(driverIdExisting, false)).thenReturn(generateDriverDoForMock(driverIdExisting, OnlineStatus.OFFLINE, carId));
        Mockito.when(driverRepository.findByIdAndDeleted(driverIdNonExisting, false)).thenReturn(Optional.ofNullable(null));
        Mockito.when(driverRepository.findByIdAndDeleted(driverIdWithCar, false)).thenReturn(generateDriverDoForMock(driverIdWithCar, OnlineStatus.ONLINE, carId));

        Mockito.when(carRepository.findByIdAndDeleted(carId, false)).thenReturn(generateCarDoForMock(carId, CarStatus.IN_USE));

    }


    //Tests mainly Covering the Service Layer
    @Test
    public void whenFindDriverByExistingId_thenCorrect() throws EntityNotFoundException
    {

        DriverDO driverDO = driverService.find(driverIdExisting);

        assertThat(driverDO.getId(), is(driverIdExisting));
        assertThat(driverDO.getUsername(), is(username));
        assertThat(driverDO.getPassword(), is(password));

    }


    @Test(expected = EntityNotFoundException.class)
    public void whenFindCarByNonExistingId_thenExcption() throws EntityNotFoundException
    {

        Long driverId = 1l;

        driverService.find(driverId);

    }


    @Test
    public void whenDeleteDriverByExistingId_thenCorrect() throws EntityNotFoundException
    {
        driverService.delete(driverIdExisting);
        driverRepository.findByIdAndDeleted(driverIdExisting, true);

    }


    @Test(expected = EntityNotFoundException.class)
    public void whenDeleteDriverByNonExistingId_thenException() throws EntityNotFoundException
    {
        driverService.delete(driverIdNonExisting);
    }


    @Test
    public void whenCreateDriver_thenCorrectCreatedId() throws ConstraintsViolationException
    {
        setUpSaveDao();

        DriverDO driver = new DriverDO(username, password);
        DriverDO carCreated = driverService.create(driver);

        assertThat(carCreated.getId(), is(driverIdCreated));
    }


    //This covers all the DataIntegrityViolationException from DAO layer
    @Test(expected = ConstraintsViolationException.class)
    public void whenCreateDriver_givenNullUsername_thenException() throws ConstraintsViolationException
    {
        setUpSaveException();

        DriverDO driver = new DriverDO(null, password);
        driverService.create(driver);

    }


    //Select Car Tests
    @Test(expected = EntityNotFoundException.class)
    public void givenCarNotExists_WhenSelectCar_thenException() throws ConstraintsViolationException, EntityNotFoundException, CarAlreadyInUseException
    {
        setUpCarNotFoundRepository();

        DriverDO driver = new DriverDO(username, password);
        driver.setId(driverIdExisting);
        driverService.selectCar(driver, carId);

    }


    @Test(expected = CarAlreadyInUseException.class)
    public void givenCarIsInUse_WhenSelectCar_thenException() throws ConstraintsViolationException, EntityNotFoundException, CarAlreadyInUseException
    {

        setUpCarInUse();

        DriverDO driver = new DriverDO(username, password);
        driver.setId(driverIdExisting);
        driverService.selectCar(driver, carId);

    }


    //Logic in Controller, Mock MVC is used to verify that
    @WithMockUser("USER")
    @Test
    public void givenDriverOffline_WhenSelectCar_thenInCorrect() throws Exception
    {
        MvcResult result = mvc.perform(put(String.format("/v1/drivers/%s/cars/%s", driverIdExisting, carId))).andExpect(status().is(400)).andReturn();
        String message = result.getResolvedException().getMessage();
        assertThat(result.getResponse().getStatus(), is(400));
        assertThat(message, is("Driver must be ONLINE to select a car"));

    }

    @WithMockUser("USER")
    @Test
    public void givenDriverHasCar_WhenSelectCar_thenExceptionResponse() throws Exception
    {

        MvcResult result = mvc.perform(put(String.format("/v1/drivers/%s/cars/%s", driverIdWithCar, carId))).andExpect(status().is(400)).andReturn();
        String message = result.getResolvedException().getMessage();
        assertThat(result.getResponse().getStatus(), is(400));
        assertThat(message, is("Driver has already selected a Car"));

    }


    //DAO Data
    private Optional<DriverDO> generateDriverDoForMock(long id, OnlineStatus onlineStatus, long carId)
    {
        CarDO car = new CarDO("123-abc", 6, 4, false, EngineType.HYBRID, "BMW");

        DriverDO driver = new DriverDO(username, password);
        driver.setId(id);
        driver.setCar(car);
        driver.setOnlineStatus(onlineStatus);

        return Optional.of(driver);
    }


    private Optional<CarDO> generateCarDoForMock(long id, CarStatus carStatus)
    {
        CarDO car = new CarDO("123-abc", 6, 4, false, EngineType.HYBRID, "BMW");

        car.setCarStatus(carStatus);
        return Optional.of(car);
    }


    private void setUpCarInUse()
    {
        Mockito.when(carRepository.findByIdAndDeleted(carIdInUse, false)).thenReturn(generateCarDoForMock(carIdInUse, CarStatus.IN_USE));

    }


    private void setUpSaveException()
    {
        DriverDO driver = new DriverDO(null, password);
        Mockito.doThrow(new DataIntegrityViolationException("Invalid Data")).when(driverRepository).save(driver);
    }


    private void setUpSaveDao()
    {
        DriverDO driver = new DriverDO(username, password);
        Mockito.when(driverRepository.save(driver)).thenReturn(generateDriverDoForMock(driverIdCreated, OnlineStatus.OFFLINE, carId).get());
    }


    private void setUpCarNotFoundRepository()
    {
        Mockito.when(carRepository.findByIdAndDeleted(carId, false)).thenReturn(Optional.ofNullable(null));

    }
}

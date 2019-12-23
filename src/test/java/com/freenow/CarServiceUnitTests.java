package com.freenow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import com.freenow.dataaccessobject.CarRepository;
import com.freenow.domainobject.CarDO;
import com.freenow.domainvalue.EngineType;
import com.freenow.exception.ConstraintsViolationException;
import com.freenow.exception.EntityNotFoundException;
import com.freenow.service.CarService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CarServiceUnitTests
{

    @MockBean
    CarRepository carRepository;

    @Autowired
    CarService carService;

    private static long carIdExisting;
    private static long carIdNonExisting;
    private static long carIdCreated;
    private static int seatCount;
    private static int rating;
    private static String manufacturer;
    private static String licensePlate;
    private static String licensePlateDuplicate;
    private static EngineType engineType;
    private static boolean convertible;


    @BeforeClass
    public static void init()
    {
        carIdExisting = 500l;
        carIdNonExisting = 1l;
        carIdCreated = 20l;
        licensePlate = "35353-gdg-5656";
        licensePlateDuplicate = "1234-abcd";
        seatCount = 4;
        rating = 5;
        manufacturer = "Ford";
        engineType = EngineType.GAS;
        convertible = true;

    }


    @Before
    public void setUp()
    {
        Mockito.when(carRepository.findByIdAndDeleted(carIdExisting, false)).thenReturn(generateCarDoForMock(carIdExisting));
        Mockito.when(carRepository.findByIdAndDeleted(carIdNonExisting, false)).thenReturn(Optional.ofNullable(null));

    }


    //Tests mainly Covering the Service Layer
    @Test
    public void whenFindCarByExistingId_thenCorrect() throws EntityNotFoundException
    {

        CarDO carDO = carService.find(carIdExisting);

        assertThat(carDO.getId(), is(carIdExisting));
        assertThat(carDO.getLicensePlate(), is(licensePlate));
        assertThat(carDO.getRating(), is(rating));
        assertThat(carDO.getSeatCount(), is(seatCount));
        assertThat(carDO.isConvertible(), is(convertible));
        assertThat(carDO.getEngineType(), is(engineType));
        assertThat(carDO.getManufacturer(), is(manufacturer));

    }


    @Test(expected = EntityNotFoundException.class)
    public void whenFindCarByNonExistingId_thenExcption() throws EntityNotFoundException
    {

        Long carId = 1l;

        CarDO carDO = carService.find(carId);

    }


    @Test
    public void whenDeleteCarByExistingId_thenCorrect() throws EntityNotFoundException
    {
        carService.delete(carIdExisting);
        Optional<CarDO> findByIdAndDeleted = carRepository.findByIdAndDeleted(carIdExisting, true);

    }


    @Test(expected = EntityNotFoundException.class)
    public void whenDeleteCarByNonExistingId_thenException() throws EntityNotFoundException
    {
        carService.delete(carIdNonExisting);
    }


    @Test
    public void whenSaveCar_thenCorrectCreatedId() throws ConstraintsViolationException
    {
        setUpSaveDao();

        CarDO car = new CarDO(licensePlate, seatCount, rating, convertible, engineType, manufacturer);
        CarDO carCreated = carService.create(car);

        assertThat(carCreated.getId(), is(carIdCreated));
    }


    //This covers all the DataIntegrityViolationException from DAO layer
    @Test(expected = ConstraintsViolationException.class)
    public void whenSaveCar_givenDuplicateLicensePlate_thenException() throws ConstraintsViolationException
    {
        setUpSaveException();

        CarDO car = new CarDO(licensePlateDuplicate, seatCount, rating, convertible, engineType, manufacturer);
        CarDO carCreated = carService.create(car);

    }


    //DAO Data
    private Optional<CarDO> generateCarDoForMock(long id)
    {
        CarDO car = new CarDO(licensePlate, seatCount, rating, convertible, engineType, manufacturer);
        car.setId(id);
        return Optional.of(car);
    }


    private void setUpSaveException()
    {
        CarDO carDuplicate = new CarDO(licensePlateDuplicate, seatCount, rating, convertible, engineType, manufacturer);
        Mockito.doThrow(new DataIntegrityViolationException("Invalid Data")).when(carRepository).save(carDuplicate);
    }


    private void setUpSaveDao()
    {
        CarDO car = new CarDO(licensePlate, seatCount, rating, convertible, engineType, manufacturer);
        Mockito.when(carRepository.save(car)).thenReturn(generateCarDoForMock(carIdCreated).get());
    }

}

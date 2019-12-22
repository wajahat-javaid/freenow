package com.freenow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.freenow.controller.mapper.CarMapper;
import com.freenow.controller.mapper.DriverMapper;
import com.freenow.datatransferobject.CarDTO;
import com.freenow.datatransferobject.DriverDTO;
import com.freenow.domainobject.CarDO;
import com.freenow.domainobject.DriverDO;
import com.freenow.domainvalue.EngineType;
import com.freenow.domainvalue.GeoCoordinate;

public class MappersTest
{

    @Test
    public void WhenCarDOConvertedToDTO_thenCorrect()
    {

        CarDO car = new CarDO("plate-123", 2, 4, true, EngineType.GAS, "Ford");
        car.setId(200l);

        CarDTO carDTO = CarMapper.makeCarDTO(car);

        assertEquals(carDTO.getEngineType(), car.getEngineType());
        assertEquals(carDTO.getId(), car.getId());
        assertEquals(carDTO.getLicensePlate(), car.getLicensePlate());
        assertEquals(carDTO.getManufacturer(), car.getManufacturer());
        assertEquals(carDTO.getRating(), car.getRating());
        assertEquals(carDTO.getSeatCount(), car.getSeatCount());
        assertEquals(carDTO.isConvertible(), car.isConvertible());

    }


    @Test
    public void WhenCarDTOConvertedToDO_thenCorrect()
    {
        CarDTO carDTO =
            CarDTO
                .newBuilder()
                .setConvertible(true)
                .setEngineType(EngineType.ELECTRIC)
                .setLicenseplate("lisence-123")
                .setManufacturer("KIA")
                .setRating(2)
                .setSeatcount(4)
                .createCarDTO();

        CarDO car = CarMapper.makeCarDO(carDTO);

        assertEquals(car.getEngineType(), carDTO.getEngineType());
        assertEquals(car.getLicensePlate(), carDTO.getLicensePlate());
        assertEquals(car.getManufacturer(), carDTO.getManufacturer());
        assertEquals(car.getRating(), carDTO.getRating());
        assertEquals(car.getSeatCount(), carDTO.getSeatCount());
        assertEquals(car.isConvertible(), carDTO.isConvertible());

    }


    @Test
    public void WhenDriverDOConvertedToDTO_thenCorrect()
    {

        DriverDO driver = new DriverDO("User1", "Pass1");
        driver.setId(400l);
        driver.setCoordinate(new GeoCoordinate(44.0, 32.0));

        DriverDTO driverDTO = DriverMapper.makeDriverDTO(driver);

        assertThat(driverDTO.getCoordinate(), is(driver.getCoordinate()));
        assertEquals(driverDTO.getId(), driver.getId());
        assertEquals(driverDTO.getPassword(), driver.getPassword());
        assertEquals(driverDTO.getUsername(), driver.getUsername());

    }


    @Test
    public void WhenDriverDTOConvertedToDO_thenCorrect()
    {
        DriverDTO driverDTO =
            DriverDTO
                .newBuilder()
                .setPassword("testPass")
                .setUsername("testUser")
                .createDriverDTO();

        DriverDO driver = DriverMapper.makeDriverDO(driverDTO);

        assertEquals(driverDTO.getPassword(), driver.getPassword());
        assertEquals(driverDTO.getUsername(), driver.getUsername());

    }

}

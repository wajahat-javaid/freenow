package com.freenow.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.freenow.controller.mapper.DriverMapper;
import com.freenow.datatransferobject.DriverDTO;
import com.freenow.domainobject.DriverDO;
import com.freenow.domainvalue.OnlineStatus;
import com.freenow.exception.CarAlreadyInUseException;
import com.freenow.exception.ConstraintsViolationException;
import com.freenow.exception.EntityNotFoundException;
import com.freenow.query.search.RsqlVisitor;
import com.freenow.service.DriverService;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

/**
 * All operations with a driver will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("v1/drivers")
public class DriverController
{

    private final DriverService driverService;


    @Autowired
    public DriverController(final DriverService driverService)
    {
        this.driverService = driverService;
    }


    @GetMapping("/{driverId}")
    public DriverDTO getDriver(@PathVariable long driverId) throws EntityNotFoundException
    {
        return DriverMapper.makeDriverDTO(driverService.find(driverId));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDTO createDriver(@Valid @RequestBody DriverDTO driverDTO) throws ConstraintsViolationException
    {
        DriverDO driverDO = DriverMapper.makeDriverDO(driverDTO);
        return DriverMapper.makeDriverDTO(driverService.create(driverDO));
    }


    @DeleteMapping("/{driverId}")
    public void deleteDriver(@PathVariable long driverId) throws EntityNotFoundException
    {
        driverService.delete(driverId);
    }


    @PutMapping("/{driverId}")
    public void updateLocation(
        @PathVariable long driverId, @RequestParam double longitude, @RequestParam double latitude)
        throws EntityNotFoundException
    {
        driverService.updateLocation(driverId, longitude, latitude);
    }


    //TODO only for ADMIN
    @GetMapping( produces = { "application/hal+json" })
    public List<DriverDTO> findDrivers( @RequestParam(value = "search") String search )
    {

            Node rootNode = new RSQLParser().parse(search);
            Specification<DriverDO> spec = rootNode.accept(new RsqlVisitor<DriverDO>());
            return DriverMapper.makeDriverDTOList(driverService.findAll(spec));

    }


    @PutMapping("/{driverId}/cars/{carId}")
    public void selectCar(@PathVariable long driverId, @PathVariable long carId) throws EntityNotFoundException, ConstraintsViolationException, CarAlreadyInUseException
    {
        DriverDO driver = driverService.find(driverId);
        if (OnlineStatus.OFFLINE == driver.getOnlineStatus())
        {
            throw new ConstraintsViolationException("Driver must be ONLINE to select a car");

        }
        if (driver.getCar() != null)
        {
            throw new ConstraintsViolationException("Driver has already selected a Car ");
        }
        try
        {
            driverService.selectCar(driver, carId);
        }
        catch (ObjectOptimisticLockingFailureException ex)
        {
            throw new ConstraintsViolationException(String.format("Car %s is already selected or Driver %s has already booked a car ", carId, driverId));

        }
    }


    @DeleteMapping("/{driverId}/cars/{carId}")
    public void deselectCar(@PathVariable long driverId, @PathVariable long carId) throws EntityNotFoundException, ConstraintsViolationException
    {
        DriverDO driver = driverService.find(driverId);
        if (OnlineStatus.OFFLINE == driver.getOnlineStatus())
        {
            throw new ConstraintsViolationException("Driver must be ONLINE to deselect a car");

        }
        if (driver.getCar() == null)
        {
            throw new ConstraintsViolationException("Driver has not selected a Car ");
        }
        driverService.deselectCar(driver, carId);

    }

}

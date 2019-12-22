package com.freenow.dataaccessobject;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import com.freenow.domainobject.CarDO;
import com.freenow.domainvalue.CarStatus;

/**
 * Database Access Object for Car table.
 * <p/>
 */
public interface CarRepository extends CrudRepository<CarDO, Long>
{

    List<CarDO> findByCarStatusAndDeleted(CarStatus carStatus, Boolean deleted);


    Optional<CarDO> findByIdAndDeleted(Long id, Boolean deleted);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default Optional<CarDO> findByIdAndDeletedWithLock(Long id, Boolean deleted){
        return findByIdAndDeleted(id, deleted);
    }
}

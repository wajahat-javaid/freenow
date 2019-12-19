package com.freenow.dataaccessobject;

import com.freenow.domainobject.DriverDO;
import com.freenow.domainvalue.OnlineStatus;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

/**
 * Database Access Object for driver table.
 * <p/>
 */
public interface DriverRepository extends CrudRepository<DriverDO, Long>
{

    List<DriverDO> findByOnlineStatusAndDeleted(OnlineStatus onlineStatus, Boolean deleted);
    Optional<DriverDO> findByIdAndDeleted(Long id, Boolean deleted);
}

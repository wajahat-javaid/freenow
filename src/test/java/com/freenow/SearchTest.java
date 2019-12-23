package com.freenow;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import com.freenow.domainobject.DriverDO;
import com.freenow.query.search.RsqlVisitor;
import com.freenow.service.DriverService;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchTest
{

    //These Tests are like Integration Tests, using original DB
    //Need a step for Creating In-Memory test only Instance of DB
    
    @Autowired
    DriverService driverService;
    
    
    @Test
    public void givenOnlineStatus_whenSearched_thenMatch()
    {
        //given
        String searchExpression = "onlineStatus==ONLINE";
        Node rootNode = new RSQLParser().parse(searchExpression);
        Specification<DriverDO> spec = rootNode.accept(new RsqlVisitor<DriverDO>());
        //search
        List<DriverDO> searchResults = driverService.findAll(spec);
        
        //Result
        assertThat( searchResults, hasSize(6));
    }
    
    @Test
    public void givenCarAttributes_whenSearched_thenMatch()
    {
        //given license Plate AND rating Greater Than
        String searchExpression = "licensePlate==111-44-s;rating=gt=2";
        Node rootNode = new RSQLParser().parse(searchExpression);
        Specification<DriverDO> spec = rootNode.accept(new RsqlVisitor<DriverDO>());
        //search
        List<DriverDO> searchResults = driverService.findAll(spec);
        
        //Result
        assertThat( searchResults, hasSize(1));
        assertThat( searchResults.get(0).getUsername(), is("driver10"));
        
    }
    
    
    @Test
    public void givenCarAndDriverAttributes_whenSearched_thenMatch()
    {
        //given license Plate AND ONLINE OR EngineType
        String searchExpression = "onlineStatus==ONLINE;licensePlate==111-44-s,engineType==ELECTRIC";
        Node rootNode = new RSQLParser().parse(searchExpression);
        Specification<DriverDO> spec = rootNode.accept(new RsqlVisitor<DriverDO>());
        //search
        List<DriverDO> searchResults = driverService.findAll(spec);
        
        //Result
        assertThat( searchResults, hasSize(2));
        assertThat( searchResults, containsInAnyOrder(new DriverDO("driver09", "driver09pw"), new DriverDO("driver10", "driver10pw")));
        
    }
    
    @Test
    public void givenCarAttributesOnly_whenSearched_thenMatch()
    {
        //given rating > 3 AND EngineType is ELECTRIC
        String searchExpression = "rating=gt=3;engineType==ELECTRIC";
        Node rootNode = new RSQLParser().parse(searchExpression);
        Specification<DriverDO> spec = rootNode.accept(new RsqlVisitor<DriverDO>());
        //search
        List<DriverDO> searchResults = driverService.findAll(spec);
        
        //Result
        assertThat( searchResults, hasSize(1));
        assertThat( searchResults, contains(new DriverDO("driver09", "driver09pw")));
        
    }
    
}

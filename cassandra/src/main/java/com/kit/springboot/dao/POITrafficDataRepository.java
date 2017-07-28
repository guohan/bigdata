package com.kit.springboot.dao;

import com.kit.springboot.dao.entity.POITrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;


/**
 * DAO class for poi_traffic 
 * 
 * @author gh
 *
 */
@Repository
public interface POITrafficDataRepository extends CassandraRepository<POITrafficData>{
	 
}

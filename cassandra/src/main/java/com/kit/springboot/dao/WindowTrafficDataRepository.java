package com.kit.springboot.dao;

import com.kit.springboot.dao.entity.WindowTrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * DAO class for window_traffic 
 * 
 * @author gh
 *
 */
@Repository
public interface WindowTrafficDataRepository extends CassandraRepository<WindowTrafficData>{
	
	@Query("SELECT * FROM traffickeyspace.window_traffic WHERE recorddate = ?0 ALLOW FILTERING")
	 Iterable<WindowTrafficData> findTrafficDataByDate(String date);

}

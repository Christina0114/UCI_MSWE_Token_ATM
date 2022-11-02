package com.capstone.tokenatm.service;

import com.capstone.tokenatm.entity.SpendLogEntity;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called LogRepository
// CRUD refers Create, Read, Update, Delete

public interface LogRepository extends CrudRepository<SpendLogEntity, Integer> {

}

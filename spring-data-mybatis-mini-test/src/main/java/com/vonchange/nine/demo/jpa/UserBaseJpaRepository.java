package com.vonchange.nine.demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserBaseJpaRepository extends JpaRepository<UserBase, Long> {

  @Query(value="from UserBase where userName = ?1 ")
  List<UserBase> findList(String userName);

}
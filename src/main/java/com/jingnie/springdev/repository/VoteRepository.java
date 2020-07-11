package com.jingnie.springdev.repository;

import com.jingnie.springdev.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote,Long> {

}

package com.jingnie.springdev.repository;

import com.jingnie.springdev.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link,Long> {

}

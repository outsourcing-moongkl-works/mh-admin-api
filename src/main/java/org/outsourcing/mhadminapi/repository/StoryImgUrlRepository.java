package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.StoryImgUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface StoryImgUrlRepository extends JpaRepository<StoryImgUrl, UUID> {

}

package com.vidya.studyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vidya.studyapp.entity.Feedback;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
	List<Feedback> findByUserId(Long userId);

	List<Feedback> findByUserIdIn(List<Long> userIds);

}


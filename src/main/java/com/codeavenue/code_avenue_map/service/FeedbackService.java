package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.FeedbackNotFoundException;
import com.codeavenue.code_avenue_map.model.EvaluationQuestion;
import com.codeavenue.code_avenue_map.model.Feedback;
import com.codeavenue.code_avenue_map.model.User;
import com.codeavenue.code_avenue_map.model.dto.FeedbackDTO;
import com.codeavenue.code_avenue_map.model.dto.FeedbackRequestDTO;
import com.codeavenue.code_avenue_map.repository.FeedbackRepository;
import com.codeavenue.code_avenue_map.repository.UserRepository;
import com.codeavenue.code_avenue_map.repository.EvaluationQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final EvaluationQuestionRepository evaluationQuestionRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository, EvaluationQuestionRepository evaluationQuestionRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.evaluationQuestionRepository = evaluationQuestionRepository;
    }

    private FeedbackDTO mapToDTO(Feedback feedback) {
        return new FeedbackDTO(
                feedback.getId(),
                feedback.getUser().getId(),
                feedback.getQuestion().getId(),
                feedback.getFeedbackChoice()
        );
    }

    public FeedbackDTO createFeedback(FeedbackRequestDTO feedbackRequestDTO) {
        User user = userRepository.findById(feedbackRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        EvaluationQuestion question = evaluationQuestionRepository.findById(feedbackRequestDTO.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setQuestion(question);
        feedback.setFeedbackChoice(feedbackRequestDTO.getFeedbackChoice());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return mapToDTO(savedFeedback);
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<FeedbackDTO> getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .map(this::mapToDTO);
    }

    public List<FeedbackDTO> getFeedbacksByUser(Long userId) {
        return feedbackRepository.findByUser_Id(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }





    public FeedbackDTO updateFeedback(Long id, FeedbackRequestDTO updatedFeedbackDTO) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException(id));

        feedback.setFeedbackChoice(updatedFeedbackDTO.getFeedbackChoice());
        Feedback updatedFeedback = feedbackRepository.save(feedback);

        return mapToDTO(updatedFeedback);
    }

    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException(id));

        feedbackRepository.delete(feedback);
    }
}
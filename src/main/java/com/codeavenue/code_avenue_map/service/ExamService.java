package com.codeavenue.code_avenue_map.service;

import com.codeavenue.code_avenue_map.exception.ExamException;
import com.codeavenue.code_avenue_map.exception.ExamSubmissionException;
import com.codeavenue.code_avenue_map.exception.ExamValidationException;
import com.codeavenue.code_avenue_map.model.*;
import com.codeavenue.code_avenue_map.model.dto.*;
import com.codeavenue.code_avenue_map.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamResultRepository examResultRepository;
    private final MajorRepository majorRepository;
    private final MajorService majorService;
    private final UserRepository userRepository;

    public ExamService(ExamRepository examRepository, ExamQuestionRepository examQuestionRepository,
                       ExamResultRepository examResultRepository, MajorRepository majorRepository,
                       MajorService majorService, UserRepository userRepository) {
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examResultRepository = examResultRepository;
        this.majorRepository = majorRepository;
        this.majorService = majorService;
        this.userRepository = userRepository;
    }

    public ExamDTO createExam(ExamDTO examDTO) {
        if (examDTO.getExamName() == null || examDTO.getExamName().trim().isEmpty()) {
            throw new ExamValidationException("Exam name cannot be empty");
        }

        if (examRepository.existsAnyExam()) {
            throw new ExamValidationException("An exam already exists. Only one exam instance is allowed.");
        }

        Exam exam = new Exam();
        exam.setExamName(examDTO.getExamName());
        Exam savedExam = examRepository.save(exam);
        return mapExamToDTO(savedExam);
    }

    @Transactional
    public ExamResultDTO submitExam(ExamSubmissionDTO submission) {
        try {
            if (submission == null) {
                throw new ExamValidationException("Submission data cannot be null");
            }
            if (submission.getUserId() == null) {
                throw new ExamValidationException("User ID cannot be null");
            }
            if (submission.getExamId() == null) {
                throw new ExamValidationException("Exam ID cannot be null");
            }
            if (submission.getUserAnswers() == null || submission.getUserAnswers().isEmpty()) {
                throw new ExamValidationException("User answers cannot be empty");
            }

            User user = userRepository.findById(submission.getUserId())
                    .orElseThrow(() -> new ExamValidationException("User not found with ID: " + submission.getUserId()));

            Exam exam = examRepository.findById(submission.getExamId())
                    .orElseThrow(() -> new ExamValidationException("Exam not found with ID: " + submission.getExamId()));

            List<MajorScore> majorScores = calculateAllMajorScores(submission, exam);
            if (majorScores.isEmpty()) {
                throw new ExamValidationException("No major scores calculated");
            }

            MajorScore topMajor = majorScores.get(0);

            ExamResult examResult = new ExamResult();
            examResult.setUser(user);
            examResult.setExam(exam);
            examResult.setSuggestedMajor(majorRepository.getReferenceById(topMajor.major().getId()));
            examResult.setScore(Math.round(topMajor.percentage() * 100.0) / 100.0);

            ExamResult savedResult = examResultRepository.saveAndFlush(examResult);

            if (savedResult.getId() == null) {
                throw new ExamSubmissionException("Failed to save exam result - ID is null");
            }

            ExamResult verified = examResultRepository.findById(savedResult.getId())
                    .orElseThrow(() -> {
                        examResultRepository.delete(savedResult);
                        return new ExamSubmissionException("Failed to verify exam result persistence");
                    });

            List<ExamMajorPercentageDTO> majorPercentages = majorScores.stream()
                    .map(score -> new ExamMajorPercentageDTO(
                            score.major().getId(),
                            score.major().getName(),
                            Math.round(score.percentage() * 100.0) / 100.0
                    ))
                    .collect(Collectors.toList());

            return mapExamResultToDTO(verified, majorPercentages);
        } catch (ExamValidationException | ExamSubmissionException e) {
            throw e;
        } catch (Exception e) {
            throw new ExamException("Exam processing failed: " + e.getMessage(), e);
        }
    }

    public ExamQuestion getQuestionById(Long id) {
        return examQuestionRepository.findById(id)
                .orElseThrow(() -> new ExamValidationException("Question not found with ID: " + id));
    }

    public ExamQuestionDTO updateQuestion(ExamQuestionDTO questionDTO) {
        ExamQuestion question = getQuestionById(questionDTO.getId());


        if (questionDTO.getQuestionText() != null && !questionDTO.getQuestionText().trim().isEmpty()) {
            question.setQuestionText(questionDTO.getQuestionText());
        }


        if (questionDTO.getFirstMajorId() != null) {
            Major firstMajor = majorRepository.findById(questionDTO.getFirstMajorId())
                    .orElseThrow(() -> new ExamValidationException("First major not found with ID: " + questionDTO.getFirstMajorId()));
            question.setFirstMajor(firstMajor);
        }


        if (questionDTO.getSecondMajorId() != null) {
            Major secondMajor = majorRepository.findById(questionDTO.getSecondMajorId())
                    .orElseThrow(() -> new ExamValidationException("Second major not found with ID: " + questionDTO.getSecondMajorId()));
            question.setSecondMajor(secondMajor);
        } else {
            question.setSecondMajor(null);
        }

        ExamQuestion updatedQuestion = examQuestionRepository.save(question);
        return mapQuestionToDTO(updatedQuestion);
    }

    public void deleteQuestion(Long questionId) {
        ExamQuestion question = examQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ExamValidationException("Question not found with ID: " + questionId));
        examQuestionRepository.delete(question);
    }

    public ExamDTO getFixedExam() {
        Exam exam = examRepository.findById(1L)
                .orElseThrow(() -> new ExamValidationException("Fixed exam not found"));
        return mapExamToDTO(exam);
    }

    public List<ExamQuestionDTO> getFixedExamQuestions() {
        List<ExamQuestion> questions = examQuestionRepository.findByExamId(1L);
        if (questions.isEmpty()) {
            throw new ExamValidationException("No questions found for the fixed exam");
        }
        return mapQuestionsToDTOs(questions);
    }

    public ExamQuestionDTO addQuestionToFixedExam(ExamQuestionDTO questionDTO) {
        if (questionDTO.getQuestionText() == null || questionDTO.getQuestionText().trim().isEmpty()) {
            throw new ExamValidationException("Question text cannot be empty");
        }
        if (questionDTO.getFirstMajorId() == null) {
            throw new ExamValidationException("First major ID cannot be null");
        }

        Exam exam = examRepository.findById(1L)
                .orElseThrow(() -> new ExamValidationException("Fixed exam not found"));

        Major firstMajor = majorRepository.findById(questionDTO.getFirstMajorId())
                .orElseThrow(() -> new ExamValidationException("First major not found with ID: " + questionDTO.getFirstMajorId()));

        Major secondMajor = null;
        if (questionDTO.getSecondMajorId() != null) {
            secondMajor = majorRepository.findById(questionDTO.getSecondMajorId())
                    .orElseThrow(() -> new ExamValidationException("Second major not found with ID: " + questionDTO.getSecondMajorId()));
        }

        ExamQuestion question = new ExamQuestion();
        question.setQuestionText(questionDTO.getQuestionText());
        question.setExam(exam);
        question.setFirstMajor(firstMajor);
        question.setSecondMajor(secondMajor);

        ExamQuestion savedQuestion = examQuestionRepository.save(question);
        return mapQuestionToDTO(savedQuestion);
    }

    private ExamDTO mapExamToDTO(Exam exam) {
        List<ExamQuestionDTO> questionDTOs = mapQuestionsToDTOs(exam.getQuestions());
        return new ExamDTO(exam.getId(), exam.getExamName(), questionDTOs);
    }

    private List<ExamQuestionDTO> mapQuestionsToDTOs(List<ExamQuestion> questions) {
        return questions.stream()
                .map(this::mapQuestionToDTO)
                .collect(Collectors.toList());
    }

    private ExamQuestionDTO mapQuestionToDTO(ExamQuestion question) {
        return new ExamQuestionDTO(
                question.getId(),
                question.getQuestionText(),
                question.getExam().getId(),
                question.getFirstMajor().getId(),
                question.getSecondMajor() != null ? question.getSecondMajor().getId() : null,
                Collections.emptyList()
        );
    }

    private ExamResultDTO mapExamResultToDTO(ExamResult examResult, List<ExamMajorPercentageDTO> majorPercentages) {
        if (examResult == null) {
            throw new ExamValidationException("ExamResult cannot be null");
        }

        return new ExamResultDTO(
                examResult.getId(),
                examResult.getScore(),
                examResult.getUser().getId(),
                examResult.getExam().getId(),
                examResult.getSuggestedMajor().getId(),
                examResult.getSuggestedMajor().getName(),
                majorPercentages != null ? majorPercentages : Collections.emptyList()
        );
    }

    private record MajorScore(Major major, double percentage) {}

    private List<MajorScore> calculateAllMajorScores(ExamSubmissionDTO submission, Exam exam) {
        List<ExamQuestion> questions = examQuestionRepository.findByExamId(exam.getId());
        if (questions.isEmpty()) {
            throw new ExamValidationException("No questions found for exam with ID: " + exam.getId());
        }


        Map<Major, Double> majorScores = new HashMap<>();
        Map<Major, Integer> majorQuestionCounts = new HashMap<>();

        for (int i = 0; i < questions.size(); i++) {
            ExamQuestion question = questions.get(i);
            int answerScore = getScoreForAnswer(submission.getUserAnswers().get(i));

            Major firstMajor = question.getFirstMajor();
            majorScores.merge(firstMajor, (double) answerScore, Double::sum);
            majorQuestionCounts.merge(firstMajor, 1, Integer::sum);

            if (question.getSecondMajor() != null) {
                Major secondMajor = question.getSecondMajor();
                majorScores.merge(secondMajor, (double) answerScore, Double::sum);
                majorQuestionCounts.merge(secondMajor, 1, Integer::sum);
            }
        }

        return majorScores.entrySet().stream()
                .map(entry -> {
                    Major major = entry.getKey();
                    double totalScore = entry.getValue();
                    int questionCount = majorQuestionCounts.get(major);
                    double percentage = (totalScore / (questionCount * 5.0)) * 100.0;
                    return new MajorScore(major, percentage);
                })
                .sorted(Comparator.comparingDouble(MajorScore::percentage).reversed())
                .collect(Collectors.toList());
    }


    private int getScoreForAnswer(FeedbackChoice feedbackChoice) {
        return switch (feedbackChoice) {
            case STRONGLY_AGREE -> 5;
            case AGREE -> 4;
            case NEUTRAL -> 3;
            case DISAGREE -> 2;
            case STRONGLY_DISAGREE -> 1;
            default -> throw new ExamValidationException("Invalid feedback choice: " + feedbackChoice);
        };
    }
}



////





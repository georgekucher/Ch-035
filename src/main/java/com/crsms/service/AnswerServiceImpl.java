package com.crsms.service;

import com.crsms.dao.AnswerDao;
import com.crsms.domain.Answer;
import com.crsms.domain.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Andriets Petro
 */

public class AnswerServiceImpl implements AnswerService {
    private static Logger logger = LogManager.getLogger(AnswerServiceImpl.class);

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionService questionService;

    public AnswerServiceImpl() {}

    @Override
    @Transactional
    public void createAnswer(Long questionId, Answer answer) {
        logger.info("AnswerService. Creating a new answer.");
        Question question = questionService.getQuestionById(questionId);
        answer.setQuestion(question);
        answerDao.saveAnswer(answer);
        logger.info("AnswerService. Creating a new answer successfully.");
    }

    @Override
    @Transactional
    public Answer getAnswerById(Long id) {
        logger.info("AnswerService. Reading answer by ID: " + id + ".");
        Answer answer = answerDao.getAnswerById(id);
        logger.info("AnswerService. Reading answer by ID: " + id + " successfully.");
        return answer;
    }

    @Override
    @Transactional
    public List<Answer> getAnswersByQuestionId(Long questionId) {
        logger.info("AnswerService. Reading all answers by Question ID.");
        return answerDao.getAnswersByQuestionId(questionId);
    }

    @Override
    @Transactional
    public void editAnswer(Answer answer) {
        logger.info("AnswerService. Editing answer.");
        Answer existingAnswer = answerDao.getAnswerById(answer.getId());
        existingAnswer.setText(answer.getText());
        existingAnswer.setCorrect(answer.getCorrect());
        answerDao.updateAnswer(existingAnswer);
        logger.info("AnswerService. Editing answer successfully.");
    }

    @Override
    @Transactional
    public void deleteAnswerById(Long id) {
        logger.info("AnswerService. Deleting answer by ID: " + id + ".");
        answerDao.deleteAnswerById(id);
        logger.info("AnswerService. Deleting answer by ID: " + id + " successfully.");
    }
}

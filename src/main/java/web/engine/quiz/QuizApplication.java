package web.engine.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@SpringBootApplication
public class QuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizApplication.class, args);
	}
}

@RestController
@Validated
class QuizController {
	Answer positiveAnswer = new Answer(true, "Congratulations, you're right!");
	Answer negativeAnswer = new Answer(false, "Wrong answer! Please, try again.");

	List<Question> questions = new ArrayList<>();

	int identifier = 1;

	@PostMapping("/api/quizzes")
	public Question getQuizzes(@Valid @RequestBody Question q) {
		Question question = new Question(identifier, q.getTitle(), q.getText(), q.getOptions(), q.getAnswer());
		questions.add(question);
		identifier++;
		return question;
	}

	@GetMapping("/api/quizzes/{id}")
	public Question getQuestion(@PathVariable int id) {
		for (Question question : questions) {
			if (question.getId() == id) {
				return question;
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/api/quizzes")
	public List<Question> getQuestions() {
		return questions;
	}

	@PostMapping("/api/quizzes/{id}/solve")
	public Answer getAnswer(@PathVariable int id, @RequestBody Map<String, List<Integer>> answer) {
		for (Question question : questions) {
			if (question.getId() == id) {
				List<Integer> answers = answer.get("answer");
				Collections.sort(answers);
				Collections.sort(question.getAnswer());
				if (answers.isEmpty() && question.getAnswer().isEmpty()) {
					return positiveAnswer;
				} else if (answers.isEmpty() && !question.getAnswer().isEmpty() || !answers.isEmpty() && question.getAnswer().isEmpty()) {
					return negativeAnswer;
				} else {
					for (int i : answers) {
						for (int j : question.getAnswer()) {
							return i == j ? positiveAnswer : negativeAnswer;
						}
					}
				}
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}
}

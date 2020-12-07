package pl.bl.document;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pl.bl.organization.User;

public class DocumentDao {

	public List<Document> getAllDocumentsInDatabase(){
		List<Document> documents = new ArrayList<>();
		
		 Questionnaire questionnaire = new Questionnaire();
		 
		 questionnaire.setCreationDate(getRandomPastDate());
		 questionnaire.setRemoved(false);
		 questionnaire.setTitle("Ankieta kulinarna");
		 
		 Question question = new Question();
		 question.setQuestionText("Które z dañ wybra³byœ na jutrzejszy obiad?");
		 question.getPossibleAnswers().add("Szasz³yki drobiowe");
		 question.getPossibleAnswers().add("Falafele w tortilli");
		 question.getPossibleAnswers().add("Jajko sadzone ze szpinakiem i ziemniakami");
		 questionnaire.getQuestions().add(question);
		 
		 question = new Question();
		 question.setQuestionText("Jakie warzywo uwa¿asz za najzdrowsze?");
		 question.getPossibleAnswers().add("Kalafior");
		 question.getPossibleAnswers().add("Ziemniak");
		 question.getPossibleAnswers().add("Rzodkiewka");
		 question.getPossibleAnswers().add("Marchewka");
		 questionnaire.getQuestions().add(question);
		 
		 documents.add(questionnaire);
		 
		 //**************************************************************************************
		 
		 questionnaire = new Questionnaire();
		 
		 questionnaire.setCreationDate(getRandomPastDate());
		 questionnaire.setRemoved(false);
		 questionnaire.setTitle("Ankieta komputerowa");
		 
		 question = new Question();
		 question.setQuestionText("Które procesory twoim zdaniem "
		 		+ "maj¹ najlepsz¹ relacjê jakoœci do ceny?");
		 question.getPossibleAnswers().add("AMD");
		 question.getPossibleAnswers().add("Intel");
		 question.getPossibleAnswers().add("IBM Power");
		 questionnaire.getQuestions().add(question);
		 
		 question = new Question();
		 question.setQuestionText("Jaka jest minimalna iloœæ ramu "
		 		+ "która wymagana jest aby wygodnie programowaæ w Javie?");
		 question.getPossibleAnswers().add("2GB");
		 question.getPossibleAnswers().add("4GB");
		 question.getPossibleAnswers().add("8GB");
		 question.getPossibleAnswers().add("16GB");
		 question.getPossibleAnswers().add("32GB");
		 question.getPossibleAnswers().add("64GB");
		 questionnaire.getQuestions().add(question);
		 
		 documents.add(questionnaire);
		 
		 //**************************************************************************************
		 
		 ApplicationForHolidays applicationForHolidays = new ApplicationForHolidays();
		 applicationForHolidays.setSince(getRandomPastDate());
		 applicationForHolidays.setTo(getRandomPastDate());
		 User user = new User();
		 user.setName("Jan");
		 user.setSurname("Kowalski");
		 user.setLogin("jankowalski60");
		 applicationForHolidays.setUserWhoRequestAboutHolidays(user);
		 documents.add(applicationForHolidays);
		 
		 applicationForHolidays = new ApplicationForHolidays();
		 applicationForHolidays.setSince(getRandomPastDate());
		 applicationForHolidays.setTo(getRandomPastDate());
		 user = new User();
		 user.setName("Agnieszka");
		 user.setSurname("Nowak");
		 user.setLogin("nowaczka");
		 applicationForHolidays.setUserWhoRequestAboutHolidays(user);
		 documents.add(applicationForHolidays);
		 
		 applicationForHolidays = new ApplicationForHolidays();
		 applicationForHolidays.setSince(getRandomPastDate());
		 applicationForHolidays.setTo(getRandomPastDate());
		 user = new User();
		 user.setName("Roman");
		 user.setSurname("Andrzejczyk");
		 user.setLogin("romuœ1999");
		 applicationForHolidays.setUserWhoRequestAboutHolidays(user);
		 documents.add(applicationForHolidays);
		 
		 return documents;
	}
	
	private Date getRandomPastDate() {
		Random random = new Random();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, random.nextInt(20) * (-1));
		return calendar.getTime();
	}
	
}

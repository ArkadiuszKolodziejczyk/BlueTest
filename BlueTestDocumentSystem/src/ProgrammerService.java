import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import pl.bl.document.*;
import pl.bl.organization.User;

public class ProgrammerService {
	private static final Pattern PATTERN = Pattern.compile(".*[•π∆Ê Í£≥—Ò”ÛåúèüØø].*");
	
	public void execute(DocumentDao documentDao) {
		List<Document> documents = documentDao.getAllDocumentsInDatabase();
		List<ApplicationForHolidays> applicationsForHolidays = filterTypeOfDocument(documents, ApplicationForHolidays.class);
		List<Questionnaire> questionnaires = filterTypeOfDocument(documents, Questionnaire.class);
		execute(applicationsForHolidays, questionnaires);
	}
	
	public void execute(List<ApplicationForHolidays> applicationsForHolidays, List<Questionnaire> questionnaires)  {
		System.out.println("årednia liczba moøliwych odpowiedzi na pytania ze wszystkich kwestionariuszy wynosi: " 
							+ getAverageOfPossibleAnswers(questionnaires));
		
		List<User> usersAppliedForHolidays = createListOfUsersAppliedForHolidays(applicationsForHolidays);
		printUsersAppliedForHolidays(usersAppliedForHolidays);
		
		checkPolishCharacters(usersAppliedForHolidays);
		
		checkDates(applicationsForHolidays);
		
		// Salary reduced to 80% of base salary - for all users which applied for holidays
		double salaryChange = 0.8; 
		changeSalaryAllUsers(usersAppliedForHolidays, salaryChange);
		 
		// Salary reduced to 500 - for specific user which applied for holidays
		double newSalary = 500; 
		changeSalaryOneUser(usersAppliedForHolidays, newSalary, "nowaczka"); 
		
		if (!questionnaires.isEmpty()) {
			QuestionnaireToFile.saveToFile(questionnaires.get(0));
		}	
	}
	
	private <T> List<T> filterTypeOfDocument(List<Document> input, Class<T> classType) {
		return input.stream()
				.filter(d -> classType.isInstance(d))
				.map(d -> classType.cast(d) )
				.collect(Collectors.toList());
	}

	private double getAverageOfPossibleAnswers(List<Questionnaire> questionnaires) {
		if (questionnaires.isEmpty()) {
			return 0;
		}
		
		List<Question> questions = new ArrayList<>();	
		double sum = 0;
		double count = 0;	
		for (Questionnaire questionnaire : questionnaires) {
			questions = questionnaire.getQuestions();
			for (Question question : questions) {
				sum += question.getPossibleAnswers().size();
				count++;		
			}
		}
		
		return sum/count;	
	}
	
	private List<User> createListOfUsersAppliedForHolidays(List<ApplicationForHolidays> applicationsForHolidays) {
		return applicationsForHolidays.stream()
				.map(u -> u.getUserWhoRequestAboutHolidays())
				.collect(Collectors.toList());								
	}
	
	private void printUsersAppliedForHolidays(List<User> usersAppliedForHolidays) {	
		System.out.println("Lista uøytkownikÛw, ktÛrzy z≥oøyli wniosek o urlop: ");
		
		if (usersAppliedForHolidays.isEmpty()) {
			System.out.println("Øaden uøytkownik nie z≥oøy≥ takiego wniosku.");
			return;
		}
		
		for (User user : usersAppliedForHolidays) {
			System.out.println("\t" + user.getLogin());
		}		
	}
	
	private void checkPolishCharacters(List<User> userList) {
		userList.stream()
			.map(User::getLogin)
			.filter(login -> PATTERN.matcher(login).matches())
			.forEach(login -> System.out.println("Login " + login + " zawiera polskie znaki"));
	}
	
	private void checkDates(List<ApplicationForHolidays> applicationsForHolidays) {	
		for (ApplicationForHolidays user : applicationsForHolidays) {
			if (user.getSince().after(user.getTo())) {
				System.out.println("Niepoprawnie wprowadzona data (koniec urlopu przed jego poczπtkiem) dla uøytkownika: " 
									+ user.getUserWhoRequestAboutHolidays().getLogin());
			}
		}
	}
	
	private void changeSalaryAllUsers(List<User> users, double salaryChange) {
		for (User user : users) {
			try {
				Class<?> cl = Class.forName(user.getClass().getName());
				Field salaryField = cl.getDeclaredField("salary");
				salaryField.setAccessible(true);
				salaryField.set(user, salaryField.getDouble(user) * salaryChange);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Pensja uøytkownika " + user.getLogin() + " zosta≥a zmieniona na: " + user.getSalary());
		}
	}
	
	private void changeSalaryOneUser(List<User> users, double newSalary, String login) {	
		User user = findUser(users, login);
		
		if (user.getLogin() == null) {
			System.out.println("Uøytkownika " + login + " nie ma na liúcie osÛb, ktÛrzy z≥oøyli wniosek o urlop. Obniøenie pensji nie powiod≥o siÍ.");
			return;
		}
		
		try {
			Class<?> cl = Class.forName(user.getClass().getName());
			Field salaryField = cl.getDeclaredField("salary");
			salaryField.setAccessible(true);
			salaryField.set(user, newSalary);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Pensja uøytkownika " + login + " zosta≥a zmieniona na: " + user.getSalary());
	}

	private User findUser(List<User> users, String login) {
		User foundUser = new User();
		
		for (User user : users) {
			if (user.getLogin().equals(login)) {
				foundUser = user;
				break;
			}
		}
		
		return foundUser;		
	}
}

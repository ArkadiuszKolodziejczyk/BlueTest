import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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
	
	public void execute(List<ApplicationForHolidays> applicationsForHolidays, List<Questionnaire> questionnaires) {
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
					.filter(classType::isInstance)
					.map(classType::cast)
					.collect(Collectors.toList());
	}
	
	private double getAverageOfPossibleAnswers(List<Questionnaire> questionnaires) {
		if (questionnaires.isEmpty()) {
			return 0d;
		}
		AtomicReference<Double> sum = new AtomicReference<>((double) 0);
		AtomicReference<Double> count = new AtomicReference<>((double) 0);
		questionnaires.stream()
					   .flatMap(q -> q.getQuestions()
					   .stream())
					   .forEach(q -> {
						   sum.accumulateAndGet((double) q.getPossibleAnswers().size(), Double::sum);
						   count.updateAndGet(previous -> previous++);
					   	});
		
		return Optional.of(count.get())
					   .filter(denominator -> 0 != denominator)
					   .map(denominator -> sum.get()/denominator)
					   .orElse(0d);
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
	
	private List<User> changeSalaryAllUsers(List<User> users, double salaryChange) {
		return users.stream()
					.map(u -> changeUserSalary(salaryChange, u, new User()))
					.collect(Collectors.toList());
	}
	
	private User changeUserSalary(double salaryChange, User oldUser, User user) {
		try {
			Class<?> cl = Class.forName(user.getClass().getName());
			setFieldValue(cl, "login", oldUser.getLogin(), user);
			setFieldValue(cl, "name", oldUser.getName(), user);
			setFieldValue(cl, "surname", oldUser.getSurname(), user);
			setFieldValue(cl, "salary", oldUser.getSalary() * salaryChange, user);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to find class: " + user.getClass().getName());
		}
		System.out.println("Pensja uøytkownika " + user.getLogin() + " zosta≥a zmieniona na: " + user.getSalary());
		return oldUser;
	}
	
	private void setFieldValue(Class<?> cl, String name, Object value, User user) {
		try {
			Field field = cl.getDeclaredField(name);
			field.setAccessible(true);
			field.set(user, value);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException("Failed to set field '" + name + "' value: " + value);
		}
	}
	
	private Optional<User> changeSalaryOneUser(List<User> users, double newSalary, String login) {
		Optional<User> optionalUser = findUser(users, login);
		if (optionalUser.isEmpty()) {
			System.out.println("Uøytkownika " + login + " nie ma na liúcie osÛb, ktÛrzy z≥oøyli wniosek o urlop. Obniøenie pensji nie powiod≥o siÍ.");
			return Optional.empty();
		}
		final User newUser = changeUserSalary(newSalary, optionalUser.get(), new User());
		System.out.println("Pensja uøytkownika " + login + " zosta≥a zmieniona na: " + newUser.getSalary());
		return Optional.of(newUser);
		}
		
	private Optional<User> findUser(List<User> users, String login) {
		return users.stream().filter(user -> user.getLogin().equals(login)).findAny();
	}
}


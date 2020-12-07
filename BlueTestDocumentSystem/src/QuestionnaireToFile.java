import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import pl.bl.document.Question;
import pl.bl.document.Questionnaire;

public class QuestionnaireToFile {
	private static final String QUESTIONARE = "Kwestionariusz";
	
	private QuestionnaireToFile() {		
	}
	
	public static void saveToFile(Questionnaire questionnaire) {
		String title = getTitle(questionnaire);	
		try {
			String textToSave = createQuestionnaireBuilder(questionnaire).toString();		
			if (fileNotExist(title)) {
				saveTextToFile(title, textToSave);
			} else {
				saveTextToFile(title + getFileUniqueNumber(title), textToSave);
			}           
        } catch (IOException exception) {
            exception.printStackTrace();
        }
	}
	
	private static boolean fileNotExist(String title) {
		if (Files.notExists(Path.of(title + ".txt"))) {
			return true;
		} 
		return false;
	}
	
	private static void saveTextToFile(String title, String textToSave) throws IOException {
		Files.write(Paths.get(title + ".txt"), textToSave.getBytes());
	}
	
	private static int getFileUniqueNumber(String title) {
		int addNumberToFileIfExist = 1;
		while (Files.exists(Path.of(title + addNumberToFileIfExist + ".txt"))) {
        	addNumberToFileIfExist++;
        }
		return addNumberToFileIfExist;
	}
	
	private static StringBuilder createQuestionnaireBuilder(Questionnaire questionnaire) {
		List<Question> questions = questionnaire.getQuestions();
		String title = getTitle(questionnaire);
		StringBuilder questionnaireBuilder = new StringBuilder(title);	
		
		for (Question question : questions) {		
			questionnaireBuilder.append("\n\nPytanie: ").append(question.getQuestionText());			
			List<String> answers = question.getPossibleAnswers();	
			for (int i = 1; i <= answers.size(); i++) {
				questionnaireBuilder.append("\n    ").append(i).append(". ").append(answers.get(i-1));
			}
		}
				
		return questionnaireBuilder;		
	}
	
	private static String getTitle(Questionnaire questionnaire) {
		return Optional.ofNullable(questionnaire.getTitle()).orElse(QUESTIONARE);
	}
}

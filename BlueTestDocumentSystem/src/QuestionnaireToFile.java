import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import pl.bl.document.Question;
import pl.bl.document.Questionnaire;

public class QuestionnaireToFile {
	public static void saveToFile(Questionnaire questionnaire) {
		String title = getTitle(questionnaire);	
		try {
			String textToSave = createQuestionnaireBuilder(questionnaire).toString();		
			if (Files.notExists(Path.of(title + ".txt"))) {
				Files.write(Paths.get(title + ".txt"), textToSave.getBytes());
			} else {
				int addNumberToFileIfExist = 1;
				while (Files.exists(Path.of(title + addNumberToFileIfExist + ".txt"))) {
	            	addNumberToFileIfExist++;
	            }
				Files.write(Paths.get(title + addNumberToFileIfExist + ".txt"), textToSave.getBytes());
			}           
        } catch (IOException exception) {
            exception.printStackTrace();
        }
	}
	
	private static StringBuilder createQuestionnaireBuilder(Questionnaire questionnaire) {
		List<Question> questions = questionnaire.getQuestions();
		String title = getTitle(questionnaire);
		StringBuilder questionnaireBuilder = new StringBuilder(title);	
		
		for (Question question : questions) {		
			questionnaireBuilder.append("\n\nPytanie: " + question.getQuestionText());			
			List<String> answers = question.getPossibleAnswers();	
			for (int i = 1; i <= answers.size(); i++) {
				questionnaireBuilder.append("\n    " + i + ". " + answers.get(i-1));
			}
		}
				
		return questionnaireBuilder;		
	}
	
	private static String getTitle(Questionnaire questionnaire) {
		String title = questionnaire.getTitle();
		
		if (title == null) {
			title = "Kwestionariusz";
		}
		
		return title;
	}
}

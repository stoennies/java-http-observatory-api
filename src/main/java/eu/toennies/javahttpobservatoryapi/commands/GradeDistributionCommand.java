package eu.toennies.javahttpobservatoryapi.commands;

/**
 * This returns each possible grade in the HTTP Observatory, as well as how many
 * scans have fallen into that grade.
 * 
 * Example result:
 * 
 * { "A+": 3, "A": 6, "A-": 2, "B+": 8, "B": 76, "B-": 79, "C+": 80, "C": 88,
 * "C-": 86, "D+": 60, "D": 110, "D-": 215, "E": 298, "F": 46770 }
 * 
 * @author Sascha Tönnies <https://github.com/stoennies>
 *
 */
public class GradeDistributionCommand extends GetCommandWithoutParameter {

	public GradeDistributionCommand() {
		super("getGradeDistribution", "gradeDistribution", "g", "Grade distribution",
				"Returns each possible grade in the HTTP Observatory, as well as how many scans have fallen into that grade.");
	}

}
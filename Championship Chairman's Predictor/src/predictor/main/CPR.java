package predictor.main;

import predictor.wrappers.Award;
import predictor.wrappers.Team;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CPR {

    private static final double rcaPoint = 1, eiPoint = .75, dcaPoint = .5, deiPoint = .35;

    /**
     * Calculates the CPR (Chairman's Power Rating) of a Team.
     *
     * @param t The Team.
     * @param d The Date that this CPR is based off of. Awards won at Events after this Date are ignored.
     */
    public static void calculateCPR(Team t, Date d) {
        int rcaWins = 0;
        int dcaWins = 0;
        int eiWins = 0;
        int deiWins = 0;
        int streakLength = 0;
        double streakPoints = 0;
        List<Award> awards;
        if (d == null) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                d = format.parse("2016-12-31"); //TODO: change this to use current system year
            } catch (Exception ignore) {}
        }
        final int year = Utils.getYear(d);
        awards = t.getAwardsBefore(d);
        for (Award a : awards) {
            if (a.year <= year && a.year >= (year - 3)) { //If the award is within a four year range
                if (a.type == Award.CHAIRMANS) {
                    if (a.district) dcaWins++;
                    else rcaWins++;
                } else if (a.type == Award.ENGINEERING_INSPIRATION) {
                    if (a.district) deiWins++;
                    else eiWins++;
                }
            }
        }
        //TODO: include the current year in the streak IF there has been an EI or CA win (?)
        int currYear = year - 1;
        boolean streakContinued = true;
        while (streakContinued) {
            streakContinued = false;
            for (Award a : awards) {
                if (a.year == currYear) {
                    if (a.type == Award.CHAIRMANS && !a.district) {
                        streakPoints += rcaPoint;
                        streakLength++;
                        streakContinued = true;
                    }
                    if (a.type == Award.ENGINEERING_INSPIRATION && !a.district) {
                        streakPoints += eiPoint;
                        streakLength++;
                        streakPoints += .75;
                        streakContinued = true;
                    }
                }
            }
            currYear--;
        }
        //System.out.println(t.number + " has " + (rcaWins + dcaWins) + " Chairman's wins and " + (eiWins + deiWins) + " EI wins.");
        //if (streakLength != 0) System.out.println(t.number + " has a Chairman/EI streak of " + streakLength + ", which started in " + (currYear + 2) + ".");
        t.cpr = (rcaWins * rcaPoint) + (eiWins * eiPoint) + (dcaWins * dcaPoint) + (deiWins * deiPoint) + streakPoints;
    }

    public static final Comparator<Team> cprComp = (t1, t2) -> {
        Double data = t2.cpr - t1.cpr;
        data *= 1000;
        return data.intValue();
    };
}

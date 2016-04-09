package predictor.main;

import predictor.tba.Award;
import predictor.tba.Event;
import predictor.tba.Team;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CPR {

    private static final double rcaPoint = 1, eiPoint = .75, dcaPoint = .5, deiPoint = .35;
    private static final double goodCPR = 3;

    /**
     * Calculates the simple CPR (Chairman's Power Rating) of a Team.
     *
     * @param t The Team.
     * @param d The Date that this CPR is based off of. Awards won at Events after this Date are ignored.
     */
    public static void calculateSimpleCPR(Team t, Date d) {
        t.cpr = 0;
        int rcaWins = 0;
        int dcaWins = 0;
        int eiWins = 0;
        int deiWins = 0;
        int streakLength = 0;
        double thisYearBonus = 0;
        double streakPoints = 0;
        List<Award> awards;
        d = Utils.makeSafe(d);
        final int year = Utils.getYear(d);
        awards = t.getAwardsBefore(d);
        //Awards gained this year are worth double points
        for (Award a : awards) {
            if (a.year <= year && a.year >= (year - Main.yearsBackwards)) { //If the award is within a four year range
                if (a.type == Award.CHAIRMANS) {
                    if (a.district) {
                        dcaWins++;
                        //if (a.year == year) thisYearBonus += dcaPoint;
                    } else {
                        rcaWins++;
                        if (a.year == year) thisYearBonus += rcaPoint;
                    }
                } else if (a.type == Award.ENGINEERING_INSPIRATION) {
                    if (a.district) {
                        deiWins++;
                        //if (a.year == year) thisYearBonus += deiPoint;
                    } else {
                        eiWins++;
                        if (a.year == year) thisYearBonus += eiPoint;
                        if (a.champs) thisYearBonus += eiPoint; //Not really a "this year bonus", but champs E.I. gets a bonus
                    }
                }
            }
        }

        int currYear = year - 1;
        boolean streakContinued = true;
        while (streakContinued) {
            if (currYear >= (year - Main.yearsBackwards)) {
                streakContinued = false;
                for (Award a : awards) {
                    if (a.year == currYear && a.type == Award.CHAIRMANS && !a.district && !streakContinued) {
                        streakPoints += rcaPoint;
                        streakLength++;
                        streakContinued = true;
                    }
                }
                if (!streakContinued) {
                    for (Award a : awards) {
                        if (a.year == currYear && a.type == Award.ENGINEERING_INSPIRATION && !a.district && !streakContinued) {
                            streakPoints += eiPoint;
                            streakLength++;
                            streakContinued = true;
                        }
                    }
                }
            } else {
                streakContinued = false;
            }
            currYear--;
        }

        //System.out.println(t.number + " has " + (rcaWins + dcaWins) + " Chairman's wins and " + (eiWins + deiWins) + " EI wins.");
        //if (streakLength != 0) System.out.println(t.number + " has a Chairman/EI streak of " + streakLength + ", which started in " + (currYear + 2) + ".");
        t.cpr = (rcaWins * rcaPoint) + (eiWins * eiPoint) + (dcaWins * dcaPoint) + (deiWins * deiPoint) + streakPoints + thisYearBonus;
    }

    /**
     * Calculates the complex CPR (Chairman's Power Rating) of a Team. Complex CPR takes into account the CPR of the
     * opponents this Team has defeated.
     *
     * @param t The Team.
     * @param d The Date that this CPR is based off of. Awards won at Events after this Date are ignored.
     * @param redoSimple If true, this will first call calculateSimpleCPR() on the Team.
     */
    public static void calculateComplexCPR(Team t, Date d, boolean redoSimple) {
        if (redoSimple) calculateSimpleCPR(t, d);
        d = Utils.makeSafe(d);
        int wins = 0;
        int goodWins = 0;
        double defeatedOpponentBonus = 0;
        final int year = Utils.getYear(d);

        for (Event e : t.getEventsBefore(d)) {
            if (e.year <= year && e.year >= (year - Main.yearsBackwards) && !e.district) {
                boolean caWin = false, eiWin = false;
                List<Integer> caWinners = e.getWinnersOf(Award.CHAIRMANS).stream().map(team -> team.number).collect(Collectors.toList());
                List<Integer> eiWinners = e.getWinnersOf(Award.ENGINEERING_INSPIRATION).stream().map(team -> team.number).collect(Collectors.toList());
                if (caWinners.contains(t.number)) caWin = true;
                else if (eiWinners.contains(t.number)) eiWin = true;
                eiWin = false;
                if (caWin || eiWin) {
                    Processing.processTeams(e.getTeams(), (Team team) -> {
                        CPR.calculateSimpleCPR(team, e.date);
                    }, Main.threadsToUse, false);

                    Team biggest = null;
                    for (Team team : e.getTeams()) {
                        if (team.number != t.number) {
                            if ((caWin && !caWinners.contains(team.number)) || (eiWin && !caWinners.contains(team.number) && !eiWinners.contains(team.number))) {
                                if (biggest == null) {
                                    biggest = team;
                                } else {
                                    if (team.cpr > biggest.cpr) biggest = team;
                                }
                            }
                        }
                    }
                    if (biggest != null) {
                        wins++;
                        double oppCpr = biggest.cpr;
                        if (oppCpr >= goodCPR) {
                            goodWins++;
                            int yearDiff = year - e.year;
                            double points = 0;
                            if (yearDiff == 0) points = 2;
                            else if (yearDiff == 1) points = 1;
                            else if (yearDiff == 2) points = 0.5;
                            else if (yearDiff == 3) points = 0.25;
                            if (eiWin) points /= 2;
                            defeatedOpponentBonus += points;
                        }
                    }
                }
            }
        }
        //if (goodWins > 0) Utils.log(t.number + " has a defeated opponent bonus of \"" + defeatedOpponentBonus + "\". They have " + goodWins + " good wins and " + wins + " overall wins.");
        t.cpr += defeatedOpponentBonus;
    }

    public static final Comparator<Team> cprComp = (t1, t2) -> {
        Double data = t2.cpr - t1.cpr;
        data *= 1000;
        return data.intValue();
    };
}

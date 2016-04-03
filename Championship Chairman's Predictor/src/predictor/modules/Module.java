package predictor.modules;

import predictor.wrappers.Team;
import java.util.List;

public interface Module {

    /**
     * Gets all the Teams this Module is dealing with.
     *
     * @return All the Teams this Module is dealing with.
     */
    List<Team> getTeams();

    /**
     * Processes one of the Teams and determines if they should be kept or not.
     *
     * @param t The Team being processed.
     * @return If the Team should be kept or not.
     */
    boolean processTeam(Team t);

    /**
     * Runs the Module's handling code for the list of Teams that passed processing.
     * @param relevant The list of Teams that passed processing.
     */
    void finish(List<Team> relevant);

}

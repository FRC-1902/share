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
     * Processes one of the Teams.
     *
     * @param t The Team being processed.
     */
    void processTeam(Team t);

    /**
     * Runs the Module's handling code for the list of Teams that passed processing. The Module should internally
     * keep track of the Teams it cares about.
     */
    void finish();

}

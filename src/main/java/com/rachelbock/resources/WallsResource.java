package com.rachelbock.resources;

import com.rachelbock.data.Message;
import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to set up the database connection for Walls
 */

@Path("/user/{username}/walls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WallsResource {


    /**
     * Retrieves wallSection data by wallid. It will return an empty list if unable to retrieve wallSections.
     * @param id - the wallId used to pull up wallSections
     * @return - a list of wallSections from the Clamber Database
     */
    @GET
    @Path("{wall_id}/wall_sections")
    public List<WallSection> getWallById(@PathParam("wall_id") int id) {
        ArrayList<WallSection> wallSections = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM wall_sections WHERE wall_segment = " + id);

            while (resultSet.next()){
                WallSection wallSection = new WallSection();
                wallSection.setId(resultSet.getInt("id"));
                wallSection.setTopOut(resultSet.getBoolean("top_out"));
                wallSection.setDateLastUpdated(resultSet.getDate("date_last_updated"));
                wallSection.setName(resultSet.getString("name"));
                wallSections.add(wallSection);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return wallSections;
    }

    protected static final String WALL_SECTIONS_BY_ID_QUERY = "SELECT * FROM climbs \n" +
            "LEFT OUTER JOIN projects " +
            "   ON projects.climb_id = climbs.climb_id AND projects.user_name = ?\n" +
            "LEFT OUTER JOIN completed_climbs " +
            "   ON completed_climbs.climb_id = climbs.climb_id AND completed_climbs.user_name = ?\n" +
            "WHERE climbs.wall_id = ? AND climbs.removed = false \n" +
            "ORDER BY climbs.gym_rating";

    /**
     * Retrieves Message data for all climbs on a specific wall section. It also uses the UserName to check
     * if any of the climbs are marked as projects or completed by the user so that the information can
     * be populated on the application side. It will return an empty List of Climbs if unable to find any
     * for the wall Section.
     * @param wall_id - wall section id number used to populate climbs
     * @param username - username used in query to determine project/completed status
     * @return - List of climbs from the Clamber Database
     */
    @GET
    @Path("{wall_id}/wall_sections/{wall_section_id}/climbs")
    public List<Message> getWallSectionById(@PathParam("wall_section_id") int wall_id, @PathParam("username") String username){
        List<Message> messages = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(WALL_SECTIONS_BY_ID_QUERY);
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setInt(3, wall_id);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Message message = new Message();
                message.setClimbId(resultSet.getInt("messages.climb_id"));
                message.setGymRating(resultSet.getInt("gym_rating"));
                message.setTapeColor(resultSet.getString("tape_color"));
                message.setType(resultSet.getString("climb_type"));
                message.setWallId(wall_id);
                if (resultSet.getString("projects.user_name") != null){
                    message.setProject(true);
                }
                else {
                    message.setProject(false);
                }
                if (resultSet.getString("completed_climbs.user_name") != null) {
                    message.setCompleted(true);
                }
                else {
                    message.setCompleted(false);
                }
                messages.add(message);
            }


        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }
        return messages;
    }

    public static final String LAST_UPDATED_WALL_QUERY = "SELECT * FROM wall_sections\n" +
            "ORDER BY date_last_updated DESC LIMIT 3";

    /**
     * Retrieves the wallSections that were most recently updated. Have to provide it with the date of the most recent
     * update.
     * @param username
     * @return - the wallSections that were most recently updated in an array list. If none match the query it will
     * return an empty list.
     */
    @GET
    @Path("wall_sections/last_updated")
    public List<WallSection> getLastUpdatedWallSections(@PathParam("username") String username) {
        List<WallSection> wallSections = new ArrayList<>();
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(LAST_UPDATED_WALL_QUERY);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                WallSection wallSection = new WallSection();
                wallSection.setDateLastUpdated(resultSet.getDate("date_last_updated"));
                wallSection.setId(resultSet.getInt("id"));
                wallSection.setMainWallId(resultSet.getInt("wall_segment"));
                wallSections.add(wallSection);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e);
        }

        return wallSections;
    }
}

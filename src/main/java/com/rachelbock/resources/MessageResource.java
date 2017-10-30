package com.rachelbock.resources;

import com.rachelbock.data.Message;
import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to set up the database connection for users.
 */
@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

    @GET
    @Path("/open_messages")
    public List<Message> getOpenMessages() {
        List<Message> messages = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM message WHERE received_date IS NULL")) {

            while (resultSet.next()) {
                Message message = new Message();
                message.setMessageId(resultSet.getInt("id"));
                message.setRecepName(resultSet.getString("recep_name"));
                message.setNotes(resultSet.getString("notes"));
                message.setMessageDate(resultSet.getDate("message_date"));

                messages.add(message);
            }
            return messages;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new NotFoundException("Could not find any open messages");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        throw new NotFoundException("Could not find any open messages");
    }


//    @GET
//    @Path("/all_mesages")
//    public List<Message> getAllMessages() {
//
//    }
//
//    @POST
//    @Path("new_message")
//    public Message addNewMessage() {
//
//    }
//
//    @PUT
//    @Path("update_message")
//    public Message updateMessage(@PathParam("messageId") String messageId) {
//
//    }

//    /**
//     * Retrieves User information for a given username. If no User exists with the username a NotFound
//     * Exception is thrown.
//     * @param userName - used in query to match user by username
//     * @return - if there is a matching User, the User is returned.
//     */
//    @GET
//    @Path("/{messageId}")
//    public Message getUserByUserName(@PathParam("messageId") String userName) {
//
//
//        try (Connection conn = ConnectionPool.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet resultSet = stmt.executeQuery("SELECT * FROM user_information WHERE user_name ='" + userName + "'")) {
//
//            if (resultSet.next()) {
//                User user = new User();
//                user.setUserName(userName);
//                user.setName(resultSet.getString("name"));
//                user.setHeight(resultSet.getInt("height"));
//                user.setSkillLevel(resultSet.getInt("skill_level"));
//
//                return user;
//            }
//        } catch (SQLIntegrityConstraintViolationException e) {
//            throw new BadRequestException("Could not find user with username " + userName );
//        } catch (SQLException e1) {
//            e1.printStackTrace();
//        }
//
//        throw new NotFoundException("Could not find user with username " + userName);
//    }
//
//
//    /**
//     * Method to add a new User to the Clamber Database. If it is unable to add the User, it throws an
//     * exception.
//     * @param request - json for the User
//     * @return - if successful it will return the User.
//     */
//    @POST
//    public User addUserToDatabase(NewUserRequest request) {
//        try (Connection conn = ConnectionPool.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            stmt.execute("INSERT INTO user_information (user_name, height, skill_level) VALUES " +
//                    "('" + request.getUsername() + "', " + request.getHeight() + ", " + request.getSkill() + ")");
//            return getUserByUserName(request.getUsername());
//
//        } catch (SQLIntegrityConstraintViolationException e) {
//            throw new BadRequestException("Username already exists");
//        } catch (SQLException e1) {
//            e1.printStackTrace();
//        }
//        throw new InternalServerErrorException("Could not create new user " + request.getUsername());
//    }
//
//    public static final String UPDATE_USER_QUERY = "UPDATE user_information SET height = ?, skill_level = ? \n" +
//            "WHERE user_name = ?";
//
//    /**
//     * Method to update an existing user in the database.
//     * @param request - json for the User
//     * @return - updated User
//     */
//    @Path("/{username}/update")
//    @POST
//    public User updateExistingUser(@PathParam("username") String username, NewUserRequest request) {
//        try(Connection conn = ConnectionPool.getConnection()){
//            PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_QUERY);
//            stmt.setInt(1, request.getHeight());
//            stmt.setInt(2, request.getSkill());
//            stmt.setString(3, username);
//
//            stmt.execute();
//
//            return getUserByUserName(request.getUsername());
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        throw new InternalServerErrorException("Could not create new user");
//    }

    //Table for device tokens - when the app is opened, hit api to give us a deviceId.
    //

    /**
     * Defines how we expect the json in the body to look
     */
    public static class NewMessageRequest {
        protected String recepName;
        protected String notes;

        public String getRecepName() {
            return recepName;
        }

        public void setRecepName(String recepName) {
            this.recepName = recepName;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class updateMessageRequest {
        protected int messageId;
        protected String claimedBy;

        public int getMessageId() {
            return messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }

        public String getClaimedBy() {
            return claimedBy;
        }

        public void setClaimedBy(String claimedBy) {
            this.claimedBy = claimedBy;
        }
    }
}

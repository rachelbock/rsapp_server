package com.rachelbock.resources;

import com.rachelbock.data.Message;
import com.rachelbock.db.ConnectionPool;
import com.rachelbock.mobilepush.PushNotificationHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Class to set up the database connection for messages.
 */
@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {
    private static final String NEW_SOS_MESSAGE = "New SOS Message!";
    private static final String UPDATED_SOS_MESSAGE = "SOS Message Claimed";
    private PushNotificationHandler pushNotificationHandler = new PushNotificationHandler();

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
                message.setUrgent(resultSet.getBoolean("urgent"));
                message.setMessageDate(resultSet.getTimestamp("message_date"));
                message.setLocation(resultSet.getString("location"));
                message.setReceivedDate(resultSet.getTimestamp("received_date"));
                message.setReceivedBy(resultSet.getString("received_by"));
                message.setTeamName(resultSet.getString("team_name"));
                message.setClaimed(resultSet.getBoolean("claimed"));

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

    @GET
    @Path("/all_messages")
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM message")) {

            while (resultSet.next()) {
                Message message = new Message();
                message.setMessageId(resultSet.getInt("id"));
                message.setRecepName(resultSet.getString("recep_name"));
                message.setUrgent(resultSet.getBoolean("urgent"));
                message.setMessageDate(resultSet.getTimestamp("message_date"));
                message.setLocation(resultSet.getString("location"));
                message.setReceivedDate(resultSet.getTimestamp("received_date"));
                message.setReceivedBy(resultSet.getString("received_by"));
                message.setTeamName(resultSet.getString("team_name"));
                message.setClaimed(resultSet.getBoolean("claimed"));

                messages.add(message);
            }
            return messages;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new NotFoundException("Could not find any messages");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        throw new NotFoundException("Could not find any messages");
    }

    @POST
    @Path("new_message")
    public Response addNewMessage(NewMessageRequest newMessageRequest) {
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO message (recep_name, team_name, " +
                    "location, urgent, message_date) VALUES (?,?,?,?,?)");

            stmt.setString(1, newMessageRequest.getRecepName());
            stmt.setString(2, newMessageRequest.getTeamName());
            stmt.setString(3, newMessageRequest.getLocation());
            stmt.setBoolean(4, newMessageRequest.isUrgent());
            stmt.setTimestamp(5, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt.execute();

            pushNotificationHandler.publishMessage(NEW_SOS_MESSAGE);

            return Response.ok("Successfully created new message").build();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BadRequestException("Duplicate Message");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        throw new InternalServerErrorException("Internal Error - could not create new message");
    }

    @PUT
    @Path("update_message")
    public Response updateMessage(UpdateMessageRequest updateMessageRequest) {
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE message SET received_by = ?, received_date = ?, " +
                    "claimed = ? WHERE id = ?");

            stmt.setString(1, updateMessageRequest.getReceivedBy());
            stmt.setTimestamp(2, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt.setBoolean(3, true);
            stmt.setInt(4, updateMessageRequest.getMessageId());
            stmt.execute();

            pushNotificationHandler.publishMessage(UPDATED_SOS_MESSAGE);

            return Response.ok("Successfully updated message").build();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new NotFoundException("Could not find the message to update");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        throw new InternalServerErrorException("Internal Error - could not update message");
    }


    /**
     * Defines how we expect the json in the body to look
     */
    public static class NewMessageRequest {
        protected String recepName;
        protected boolean urgent;
        protected String teamName;
        protected String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getRecepName() {
            return recepName;
        }

        public void setRecepName(String recepName) {
            this.recepName = recepName;
        }

        public boolean isUrgent() {
            return urgent;
        }

        public void setUrgent(boolean urgent) {
            this.urgent = urgent;
        }
    }

    public static class UpdateMessageRequest {
        protected int messageId;
        protected String receivedBy;

        public int getMessageId() {
            return messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }

        public String getReceivedBy() {
            return receivedBy;
        }

        public void setReceivedBy(String receivedBy) {
            this.receivedBy = receivedBy;
        }
    }
}

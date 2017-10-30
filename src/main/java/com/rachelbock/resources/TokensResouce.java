package com.rachelbock.resources;

import com.rachelbock.data.Token;
import com.rachelbock.db.ConnectionPool;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource for device tokens for notifications.
 */
@Path("/tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TokensResouce {

    @POST
    public Response registerToken(Token token) {
        try (Connection conn = ConnectionPool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO tokens (token) VALUES (?)");
            stmt.setString(1, token.getToken());
            stmt.execute();
            return Response.ok("Successfully registered token").build();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new BadRequestException("Duplicate Token");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        throw new InternalServerErrorException("Internal Error - could not add token");
    }

    @GET
    public List<Token> getAllTokens() {
        List<Token> tokens = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery("SELECT * FROM tokens")) {

            while (resultSet.next()) {
                Token token = new Token();
                token.setToken(resultSet.getString("token"));

                tokens.add(token);
            }
            return tokens;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new NotFoundException("Could not find any tokens");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        throw new NotFoundException("Could not find any tokens");
    }
}

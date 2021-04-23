package handlers;

import dataaccess.DataAccess;
import payloads.EmptyPayload;
import utils.Answer;

import java.util.Map;

public class StrainsGetOneHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public StrainsGetOneHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, Map<String, String> requestParams) {
        String sequenceJson = dataAccess.getStrain(requestParams.get(":id"));
        if (sequenceJson.isEmpty()) return Answer.notFound();
        return new Answer(200, sequenceJson);
    }
}
package handlers.sequences;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.TrimRequestResult;
import utils.Answer;

import java.util.Map;

public class TrimmedSequencesPostHandler extends AbstractHandler<TrimRequestResult> {
    private final DataAccess dataAccess;

    public TrimmedSequencesPostHandler(DataAccess dataAccess) {
        super(TrimRequestResult.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(TrimRequestResult result, Map<String, String> requestParams) {
        if (result.getStatusCode() == 5) {
            if (dataAccess.setSequenceTrimToFalse(result.getSequenceToken())) {
                return new Answer(200, "Sequence trimmed files field updated to false");
            }
            return new Answer(404, "Could not find the sequence with the given token");
        }
        switch (dataAccess.uploadTrimmedFile(result)) {
            case OK:
                return new Answer(200, okJson());
            case NOT_FOUND:
                return new Answer(404, errorJson("Could not find the sequence with the given token"));
            default:
                return new Answer(500, errorJson("The upload encountered a fatal error"));
        }
    }

    private String okJson() {
        return "{\"message\":\"Trimmed sequence uploaded\"}";
    }

    private String errorJson(String message) {
        return "{\"message\":\"" + message + "\"}";
    }
}
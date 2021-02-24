package handlers;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
import payloads.TrimRequestResult;
import utils.Answer;

public class TrimmedSequencesPostHandler extends AbstractHandler<TrimRequestResult> {
    private final DataAccess dataAccess;

    public TrimmedSequencesPostHandler(DataAccess dataAccess) {
        super(TrimRequestResult.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(TrimRequestResult result) {
        return switch (dataAccess.uploadTrimmedFile(result)) {
            case OK -> new Answer(200, okJson());
            case NOT_FOUND -> new Answer(404, errorJson("Could not find the sequence with the given token"));
            case WRITE_FAILED -> new Answer(500, errorJson("The upload encountered a fatal error"));
        };
    }

    private String okJson() {
        return "{\"message\":\"Trimmed sequence uploaded\"}";
    }

    private String errorJson(String message) {
        return "{\"message\":\"" + message + "\"}";
    }
}

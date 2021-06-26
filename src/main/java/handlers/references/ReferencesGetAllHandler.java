package handlers.references;

import dataaccess.DataAccess;
import handlers.AbstractHandler;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

public class ReferencesGetAllHandler extends AbstractHandler<EmptyPayload> {
    private final DataAccess dataAccess;

    public ReferencesGetAllHandler(DataAccess dataAccess) {
        super(EmptyPayload.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(EmptyPayload payload, RequestParams requestParams) {
        return new Answer(200, dataAccess.getAllReferences());
    }
}
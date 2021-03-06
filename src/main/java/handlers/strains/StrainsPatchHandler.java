package handlers.strains;

import dataaccess.DataAccess;
import dataaccess.exceptions.UniquenessViolationException;
import handlers.AbstractHandler;
import payloads.StrainKeys;
import utils.Answer;
import utils.RequestParams;

public class StrainsPatchHandler extends AbstractHandler<StrainKeys> {
    private final DataAccess dataAccess;

    public StrainsPatchHandler(DataAccess dataAccess) {
        super(StrainKeys.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(StrainKeys keys, RequestParams requestParams) {
        try {
            return dataAccess.updateStrainKeys(requestParams.path().get(":id"), keys) ?
                    new Answer(200, "Strain keys updated") :
                    Answer.notFound();
        } catch (UniquenessViolationException e) {
            return new Answer(409, e.getMessage());
        }
    }
}

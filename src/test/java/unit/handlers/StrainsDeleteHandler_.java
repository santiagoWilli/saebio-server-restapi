package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.exceptions.DocumentPointsToStrainException;
import handlers.strains.StrainsDeleteHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.EmptyPayload;
import utils.Answer;
import utils.RequestParams;

import java.util.AbstractMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrainsDeleteHandler_ {
    private static final Map<String, String> PARAMS = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(":id", "1"));

    private StrainsDeleteHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        handler = new StrainsDeleteHandler(dataAccess);
    }

    @Test
    public void ifDataAccessReturnsFalse_returnHttpNotFound() throws DocumentPointsToStrainException {
        when(dataAccess.deleteStrain(PARAMS.get(":id"))).thenReturn(false);
        assertThat(handler.process(new EmptyPayload(), new RequestParams(PARAMS, null))).isEqualTo(Answer.notFound());
    }

    @Test
    public void ifDataAccessReturnsTrue_returnHttpOk() throws DocumentPointsToStrainException {
        when(dataAccess.deleteStrain(PARAMS.get(":id"))).thenReturn(true);
        assertThat(handler.process(new EmptyPayload(), new RequestParams(PARAMS, null)).getCode()).isEqualTo(200);
    }

    @Test
    public void ifDataAccessThrowsException_returnHttpConflict() throws DocumentPointsToStrainException {
        when(dataAccess.deleteStrain(PARAMS.get(":id"))).thenThrow(DocumentPointsToStrainException.class);
        assertThat(handler.process(new EmptyPayload(), new RequestParams(PARAMS, null)).getCode()).isEqualTo(409);
    }
}

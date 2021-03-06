package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.UploadCode;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import utils.Answer;
import handlers.sequences.SequencesPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.Sequence;
import utils.Json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class SequencesPostHandler_ {
    private Sequence sequence;
    private GenomeTool genomeTool;
    private GenomeToolAnswer toolAnswer;
    private SequencesPostHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        sequence = mock(Sequence.class);
        when(sequence.isValid()).thenReturn(true);
        when(sequence.getStrainKey()).thenReturn("kp");
        genomeTool = mock(GenomeTool.class);
        toolAnswer = mock(GenomeToolAnswer.class);
        dataAccess = mock(DataAccess.class);
        handler = new SequencesPostHandler(genomeTool, dataAccess);
        when(genomeTool.requestTrim(sequence)).thenReturn(toolAnswer);
    }

    @Test
    public void serviceUnavailable_if_genomeToolAnswerIsApiDown() {
        when(sequence.isTrimmed()).thenReturn(false);
        when(dataAccess.strainExists("kp")).thenReturn(true);
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.API_DOWN);
        assertThat(handler.process(sequence, null)).isEqualTo(Answer.serviceUnavailable("Genome reporter tool is down"));
        verify(dataAccess, times(1)).strainExists("kp");
        verify(dataAccess, times(1)).sequenceAlreadyExists(sequence);
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void badGateway_if_genomeToolAnswerIsServerError() {
        when(sequence.isTrimmed()).thenReturn(false);
        when(dataAccess.strainExists("kp")).thenReturn(true);
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.SERVER_ERROR);
        assertThat(handler.process(sequence, null)).isEqualTo(Answer.badGateway("Genome reporter tool encountered an internal error"));
        verify(dataAccess, times(1)).strainExists("kp");
        verify(dataAccess, times(1)).sequenceAlreadyExists(sequence);
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void if_genomeToolAnswerIsOk_return_httpAccepted_and_sequenceId() {
        when(sequence.isTrimmed()).thenReturn(false);
        when(dataAccess.strainExists("kp")).thenReturn(true);
        String id = "507f1f77bcf86cd799439011";
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.OK);
        when(toolAnswer.getMessage()).thenReturn(token());
        when(dataAccess.createSequence(sequence, token())).thenReturn(id);
        assertThat(handler.process(sequence, null)).isEqualTo(new Answer(202, Json.id(id)));
        verify(dataAccess, times(1)).createSequence(sequence, token());
    }

    @Test
    public void serverError_if_genomeToolAnswerIsExceptionEncountered() {
        when(sequence.isTrimmed()).thenReturn(false);
        when(dataAccess.strainExists("kp")).thenReturn(true);
        String exception = "Error";
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.EXCEPTION_ENCOUNTERED);
        when(toolAnswer.getMessage()).thenReturn(exception);
        assertThat(handler.process(sequence, null)).isEqualTo(Answer.serverError(exception));
        verify(dataAccess, times(1)).strainExists("kp");
        verify(dataAccess, times(1)).sequenceAlreadyExists(sequence);
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void if_sequenceStrainKeyDoesNotExist_return_httpBadRequest() {
        when(sequence.isTrimmed()).thenReturn(false);
        when(dataAccess.strainExists("kp")).thenReturn(false);
        assertThat(handler.process(sequence, null).getCode()).isEqualTo(400);
        verifyNoInteractions(genomeTool);
        verify(dataAccess, times(1)).strainExists("kp");
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void if_sequenceIsTrimmed_andFilesUploaded_return_httpOk_and_sequenceId() {
        when(sequence.isTrimmed()).thenReturn(true);
        when(dataAccess.strainExists("kp")).thenReturn(true);
        String id = "507f1f77bcf86cd799439011";
        when(dataAccess.createSequenceAlreadyTrimmed(sequence)).thenReturn(id);
        assertThat(handler.process(sequence, null)).isEqualTo(new Answer(200, Json.id(id)));
        verifyNoInteractions(genomeTool);
        verify(dataAccess, times(1)).createSequenceAlreadyTrimmed(sequence);
    }

    @Test
    public void if_writeExceptionWhenUploadingTheTrimmedSequence_return_httpServerError() {
        when(sequence.isTrimmed()).thenReturn(true);
        when(dataAccess.strainExists("kp")).thenReturn(true);
        String id = "507f1f77bcf86cd799439011";
        when(dataAccess.createSequenceAlreadyTrimmed(sequence)).thenReturn(null);
        assertThat(handler.process(sequence, null).getCode()).isEqualTo(500);
        verifyNoInteractions(genomeTool);
        verify(dataAccess, times(1)).createSequenceAlreadyTrimmed(sequence);
    }

    @Test
    public void if_sequenceExists_return_http409() {
        when(dataAccess.strainExists("kp")).thenReturn(true);
        when(dataAccess.sequenceAlreadyExists(sequence)).thenReturn(true);
        assertThat(handler.process(sequence, null).getCode()).isEqualTo(409);
        verifyNoInteractions(genomeTool);
        verify(dataAccess, times(1)).strainExists("kp");
        verify(dataAccess, times(1)).sequenceAlreadyExists(sequence);
        verifyNoMoreInteractions(dataAccess);
    }

    private static String token() {
        return "123e4567-e89b-12d3-a456-556642440000";
    }
}

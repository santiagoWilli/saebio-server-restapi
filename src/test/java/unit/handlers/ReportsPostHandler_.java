package unit.handlers;

import dataaccess.DataAccess;
import genome.GenomeTool;
import genome.GenomeToolAnswer;
import handlers.reports.ReportsPostHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.ReportRequest;
import utils.Answer;
import utils.Json;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ReportsPostHandler_ {
    private ReportRequest reportRequest;
    private GenomeTool genomeTool;
    private GenomeToolAnswer toolAnswer;
    private ReportsPostHandler handler;
    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() {
        reportRequest = mock(ReportRequest.class);
        when(reportRequest.isValid()).thenReturn(true);
        HashSet<String> sequences = new HashSet<>(Collections.singletonList("1, 2, 3"));
        when(reportRequest.getSequences()).thenReturn(sequences);
        when(reportRequest.getReference()).thenReturn("1");
        genomeTool = mock(GenomeTool.class);
        toolAnswer = mock(GenomeToolAnswer.class);
        dataAccess = mock(DataAccess.class);
        handler = new ReportsPostHandler(genomeTool, dataAccess);
        when(genomeTool.requestToSendAnalysisFiles()).thenReturn(toolAnswer);
    }

    @Test
    public void if_sequencesAndReferenceDoNotShareTheSameStrain_return_httpConflict() {
        when(dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences())).thenReturn(false);
        assertThat(handler.process(reportRequest, null).getCode()).isEqualTo(409);
        verify(dataAccess, times(1)).referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences());
        verifyNoMoreInteractions(dataAccess);
        verifyNoInteractions(genomeTool);
    }

    @Test
    public void serviceUnavailable_if_genomeToolAnswerIsApiDown() {
        when(dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences())).thenReturn(true);
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.API_DOWN);
        assertThat(handler.process(reportRequest, null).getCode()).isEqualTo(Answer.serviceUnavailable("").getCode());
        verify(dataAccess, times(1)).referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences());
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void badGateway_if_genomeToolAnswerIsServerError() {
        when(dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences())).thenReturn(true);
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.SERVER_ERROR);
        assertThat(handler.process(reportRequest, null).getCode()).isEqualTo(Answer.badGateway("").getCode());
        verify(dataAccess, times(1)).referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences());
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void serverError_if_genomeToolAnswerIsExceptionEncountered() {
        when(dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences())).thenReturn(true);
        final String exception = "Error";
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.EXCEPTION_ENCOUNTERED);
        when(toolAnswer.getMessage()).thenReturn(exception);
        assertThat(handler.process(reportRequest, null)).isEqualTo(Answer.serverError(exception));
        verify(dataAccess, times(1)).referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences());
        verifyNoMoreInteractions(dataAccess);
    }

    @Test
    public void if_genomeToolAnswerIsOk_return_httpAccepted_and_reportId() throws IOException {
        when(dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences())).thenReturn(true);
        final String token = "123e4567-e89b-12d3-a456-556642440000";
        final String id = "507f1f77bcf86cd799439011";
        when(toolAnswer.getStatus()).thenReturn(GenomeToolAnswer.Status.OK);
        when(toolAnswer.getMessage()).thenReturn(token);

        for (String sequenceId : reportRequest.getSequences()) {
            when(dataAccess.getSequenceTrimmedFilesIds(sequenceId)).thenReturn(Arrays.asList("2", "2"));
        }
        when(dataAccess.getReferenceFileId(reportRequest.getReference())).thenReturn("1");

        InputStream referenceStream = new FileInputStream("test/resources/sequences/Kpneu231120_referencia.fa");
        InputStream sequenceStream = new FileInputStream("test/resources/sequences/Kp1_231120_R1.fastq.gz");
        when(dataAccess.getFileStream("2")).thenReturn(sequenceStream);
        when(dataAccess.getFileName("2")).thenReturn("abc");
        when(dataAccess.getFileStream("1")).thenReturn(referenceStream);
        when(dataAccess.getFileName("1")).thenReturn("ref");

        when(genomeTool.sendAnalysisFile(token, sequenceStream, "abc")).thenReturn(toolAnswer);
        when(genomeTool.sendAnalysisFile(token, referenceStream, "ref")).thenReturn(toolAnswer);
        when(genomeTool.requestToStartAnalysis(token)).thenReturn(toolAnswer);
        when(dataAccess.createReport(reportRequest, token)).thenReturn(id);

        assertThat(handler.process(reportRequest, null)).isEqualTo(new Answer(202, Json.id(id)));
        verify(dataAccess, times(1)).referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences());
        verify(genomeTool, times(1)).requestToSendAnalysisFiles();
        verify(genomeTool, times(2)).sendAnalysisFile(token, sequenceStream, "abc");
        verify(genomeTool, times(1)).sendAnalysisFile(token, referenceStream, "ref");
        verify(genomeTool, times(1)).requestToStartAnalysis(token);
        verify(dataAccess, times(1)).createReport(reportRequest, token);
    }

    @Test
    public void if_AnyOfThePassedIdsDoNotExist_return_httpNotFound() {
        when(dataAccess.referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences())).thenThrow(NullPointerException.class);
        assertThat(handler.process(reportRequest, null).getCode()).isEqualTo(404);
        verify(dataAccess, times(1)).referenceAndSequencesShareTheSameStrain(reportRequest.getReference(), reportRequest.getSequences());
        verifyNoMoreInteractions(dataAccess);
        verifyNoInteractions(genomeTool);
    }
}

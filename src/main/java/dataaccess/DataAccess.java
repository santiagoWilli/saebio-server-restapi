package dataaccess;

import dataaccess.exceptions.*;
import payloads.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface DataAccess {
    String createSequence(Sequence sequence, String genomeToolToken);
    String createSequenceAlreadyTrimmed(Sequence sequence);
    UploadCode uploadTrimmedFiles(TrimRequestResult trimResult);
    boolean setSequenceTrimToFalse(String token);
    String getAllSequencesBySequenceDate(String year, String month);
    String getAllSequencesByUploadDate(String year, String month);
    String getAllSequences(String strainId);
    String getSequence(String id);
    boolean sequenceAlreadyExists(Sequence sequence);

    String getFileName(String id);
    InputStream getFileStream(String id) throws IOException;

    String uploadReference(Reference reference) throws IOException;
    String getAllReferences(String year, String month);
    String getAllReferences(String strainId);
    String getReference(String id);
    boolean referenceAlreadyExists(Reference reference);

    String getAllStrains();
    String createStrain(Strain strain) throws UniquenessViolationException;
    boolean deleteStrain(String id) throws DocumentPointsToStrainException;
    boolean strainExists(String key);
    boolean updateStrainKeys(String id, StrainKeys keys) throws UniquenessViolationException;

    boolean referenceAndSequencesShareTheSameStrain(String reference, Set<String> sequences);
    String createReport(ReportRequest reportRequest, String token);

    List<String> getSequenceTrimmedFilesIds(String sequenceId);
    String getReferenceFileId(String referenceId);

    UploadCode uploadReportFiles(ReportRequestResult reportResult) throws IOException;
    boolean setReportFileToFalse(ReportRequestResult reportResult);
    String getAllReports(String year, String month);
    String getReport(String id);
    String getReportHTMLFileId(String id);
    String getReportFileId(String id, String filename);
    String getReportLogId(String id);

    boolean login(UserAuthentication authentication) throws UserNotFoundException;
}

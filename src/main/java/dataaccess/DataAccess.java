package dataaccess;

import dataaccess.exceptions.*;
import payloads.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface DataAccess {
    String createSequence(Sequence sequence, String genomeToolToken);
    UploadCode uploadTrimmedFile(TrimRequestResult trimResult);
    boolean setSequenceTrimToFalse(String token);
    String getAllSequences();
    String getSequence(String id);
    String getTrimmedFileName(String id);
    InputStream getFileStream(String id) throws IOException;

    String uploadReference(Reference reference) throws IOException;
    String getAllReferences();
    String getReference(String id);

    String getAllStrains();
    String createStrain(Strain strain) throws UniquenessViolationException;
    boolean deleteStrain(String id) throws DocumentPointsToStrainException;
    boolean strainExists(String key);
    boolean updateStrainKeys(String id, StrainKeys keys) throws UniquenessViolationException;

    boolean referenceAndSequencesShareTheSameStrain(String reference, Set<String> sequences);
    String createReport(ReportRequest reportRequest, String token);
}

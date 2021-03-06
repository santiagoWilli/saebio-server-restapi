package unit.payloads;

import org.junit.jupiter.api.Test;
import payloads.Reference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class Reference_ {
    static final String[] VALID_NAMES = {"Kneu123456-referencia.fa", "Kneu24-referencia.fa", "kneu1-referencia.gbf", "kneu1_referencia.gbf"};
    static final String[] INVALID_NAMES = {"Kneu-referencia.fa", "referencia.fa", "kneu123456-referencia.fq", "referencia-kneu123456.fa"};

    @Test
    public void valid_if_fileNameIsCorrect() {
        iterateThroughFileNames(VALID_NAMES, true);
    }

    @Test
    public void invalid_if_fileNameIsNotCorrect() {
        iterateThroughFileNames(INVALID_NAMES, false);
    }

    @Test
    public void invalid_if_noFileGiven() {
        Map<String, File> fileMap = new HashMap<>();
        Reference reference = new Reference(null, fileMap);
        assertThat(reference.isValid()).isEqualTo(false);
    }

    @Test
    public void getName_shouldReturn_fileName() {
        Reference reference = getReferenceFrom("Kneu123456-referencia.fa");
        assertThat(reference.getName()).isEqualTo("Kneu123456-referencia.fa");
    }

    @Test
    public void getStrain_shouldReturn_theKeyInTheFilenameInLowercase() {
        Reference reference = getReferenceFrom("Kneu123456-referencia.fa");
        assertThat(reference.getStrainKey()).isEqualTo("kneu");
    }

    @Test
    public void getCode_shouldReturn_theNumberTheFilename() {
        Reference reference = getReferenceFrom("Kneu123456-referencia.fa");
        assertThat(reference.getIsolateCode()).isEqualTo("123456");
    }

    private static void iterateThroughFileNames(String[] fileNames, boolean expected) {
        for (String fileName : fileNames) {
            Reference reference = getReferenceFrom(fileName);
            assertThat(reference.isValid()).isEqualTo(expected);
        }
    }

    private static Reference getReferenceFrom(String fileName) {
        Map<String, File> fileMap = new HashMap<>();
        File file = mock(File.class);
        fileMap.put(fileName, file);
        return new Reference(null, fileMap);
    }
}

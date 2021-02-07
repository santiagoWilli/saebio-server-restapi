package handlers;

import genome.GenomeTool;
import payloads.Sequence;

public class SequencesPostHandler extends AbstractHandler<Sequence> {
    private final GenomeTool genomeTool;

    public SequencesPostHandler(GenomeTool genomeTool) {
        this.genomeTool = genomeTool;
    }

    @Override
    protected Answer processRequest() {
        return genomeTool.trim() == 404 ? Answer.serviceUnavailable("Genome reporter tool is down") : Answer.badGateway("Genome reporter tool encountered an internal error");
    }
}

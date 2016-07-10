package jp.ryo.informationPump.server.data;


public class SortBoxForDocumentEntry {
    public DocumentEntry entry;
    public Double simirality;
    
    public SortBoxForDocumentEntry(DocumentEntry entry,Double simirality) {
        this.entry = entry;
        this.simirality = simirality;
    }
}
